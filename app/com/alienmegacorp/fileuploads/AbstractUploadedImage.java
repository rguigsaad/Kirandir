package com.alienmegacorp.fileuploads;

import com.google.gson.JsonObject;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import javax.imageio.ImageIO;
import javax.persistence.*;
import org.apache.commons.lang.time.DateFormatUtils;
import play.Logger;
import play.Play;
import play.data.validation.Match;
import play.data.validation.MaxSize;
import play.data.validation.Min;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.Model;
import play.libs.*;
import play.libs.WS.*;
import static com.alienmegacorp.fileuploads.Helper.*;

/**
 * Image uploads.
 *
 * <p>In production, images are compressed with the Smush.it service. The service
 * does not reduce quality.
 *
 * <p>Models that extend this class must define their own {@link Variant}s (probably as an enum).
 *
 * <p>application.conf must set the properties "aws.accessKey", "aws.secretKey",
 * and "application.amazonS3Bucket" must also be set.
 * If amazonS3Bucket is "false", the local file system will be used instead of S3.
 * If not "false", the value will be the name of the S3 bucket.
 *
 * @author Michael Boyd <michael@alienmegacorp.com>
 * @version r1
 */
@MappedSuperclass
abstract public class AbstractUploadedImage extends Model {
    /**
     * Name of the uploaded file. May be null.
     */
    public String originalFilename;

    /**
     * Location of the copied file, if {@link #setURLtoCopy(java.lang.String)}
     * or {@link #setURLtoCopy(java.net.URL)} was used.
     */
    @Column(name = "original_url")
    public String originalURL;

    /**
     * String.format() this with the variant name.
     */
    @Column(name = "file_path")
    @Required
    private String path;

    /**
     * The file's extension, without the leading dot, and not null.
     */
    @Required
    @Match(value = "^[a-zA-Z0-9]+$", message = "File extension is invalid")
    @MinSize(2)
    @MaxSize(4)
    public String extension;

    /**
     * Width in pixels.
     */
    @Required
    public int width;

    /**
     * Height in pixels.
     */
    @Required
    public int height;

    /**
     * File size in bytes.
     */
    @Required
    @Min(value = 0)
    public long fileSize;

    public int bytesSavedBySmushItForFullSize;

    public int bytesSavedBySmushItForVariants;

    @Temporal(value = TemporalType.TIMESTAMP)
    public Date createdAt;

    protected abstract String getFolder();

    public String getPath(final Variant variant) {
        if (variant == null) {
            throw new IllegalArgumentException("Variant is null");
        } else if (path == null) {
            throw new IllegalStateException("Path is null");
        }
        return String.format(path, variant.name().toLowerCase());
    }

    public File getLocalFile(final Variant variant) {
        return new File(DIR_ROOT.getPath() + "/" + getPath(variant));
    }

    public String getURL(final Variant variant) {
        return Helper.getBaseURL() + getPath(variant);
    }

    public void setInputStreamToCopy(String filename, InputStream is) {
        try {
            // Copy the InputStream to a local temporary file, and continue with that file.
            final File tmpFile = File.createTempFile("copy-image-", null);
            IO.write(is, tmpFile);

            this.originalFilename = filename;
            this.fileSize = tmpFile.length();
            this.extension = filename.substring(filename.lastIndexOf('.') + 1);

            putFileInCorrectPlaceAndDoEverythingElse(tmpFile);

            tmpFile.delete();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void setFixtureFile(final String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("Fixture filename must not be null");
        }

        final File file = play.Play.getFile("public/fixtures/" + filename);
        if (!file.exists()) {
            throw new IllegalArgumentException("Fixture file doesn't exist: " + filename);
        }

        this.originalFilename = filename;
        this.fileSize = file.length();
        this.extension = filename.substring(filename.lastIndexOf('.') + 1);

        putFileInCorrectPlaceAndDoEverythingElse(file);
    }

    public final void setURLtoCopy(final String url) {
        try {
            setURLtoCopy(new URL(url));
        } catch (MalformedURLException ex) {
            throw new RuntimeException("Malformed URL: " + url, ex);
        }
    }

    public final void setURLtoCopy(final URL url) {
        try {
            // Copy the URL file to a local temporary file, and continue with that file.
            final File tmpFile = File.createTempFile("copy-image-", null);
            IO.copy(url.openStream(), new FileOutputStream(tmpFile));

            this.originalURL = url.toString();
            this.fileSize = tmpFile.length();
            this.extension = url.toString().substring(url.toString().lastIndexOf('.') + 1);

            putFileInCorrectPlaceAndDoEverythingElse(tmpFile);

            tmpFile.delete();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void setFileToCopy(File file) {
        if (!file.exists()) {
            throw new IllegalArgumentException("File doesn't exist: " + file);
        }

        originalFilename = file.getName();
        extension = originalFilename.substring(originalFilename.lastIndexOf('.') + 1);
        fileSize = file.length();

        putFileInCorrectPlaceAndDoEverythingElse(file);
    }

    @PrePersist
    protected void prePersist() {
        if (createdAt == null) {
            createdAt = new Date();
        }
    }

    /**
     * Delete all variant files from the local file system and S3.
     * This is called automatically if the {@link Logo} is deleted, and can be called manually
     * e.g. if a rollback must be done.
     */
    @PostRemove
    public void deleteAllFiles() {
        for (Variant variant: getVariants()) {
            getLocalFile(variant).delete();
        }
    }

    private void putFileInCorrectPlaceAndDoEverythingElse(File file) {
        putFileInCorrectPlace(file);
        setDimensions();
        generateVariants();

        if (Play.mode.isProd()) {
            optimizeFiles();
        }
    }

    private void putFileInCorrectPlace(File file) {
        if (file == null) {
            throw new IllegalArgumentException("File must not be null");
        } else if (!file.exists()) {
            throw new IllegalArgumentException("File doesn't exist: " + file);
        }

        if (this.path != null) {
            throw new IllegalStateException("Using an AbstractUploadedFile that already has a path (create a new object instead of reusing");
        }

        // Generate a path name.
        this.path = getFolder()
                + "/" + DateFormatUtils.format(Calendar.getInstance(), "MMMy")
                + "/" + UUID.randomUUID().toString() + "-%s." + extension;

        // Copy the file to its target path, and put it on S3 if enabled.
        final File target = getLocalFile(getVariantFull());
        target.getParentFile().mkdirs();
        Files.copy(file, target);

        Logger.info("Uploaded " + getFolder() + " image: " + getPath(getVariantFull()));
    }

    private void setDimensions() {
        BufferedImage image;
        try {
            image = ImageIO.read(getLocalFile(getVariantFull()));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        width = image.getWidth();
        height = image.getHeight();
        image.flush();
        image = null;
    }

    private void generateVariants() {
        for (Variant v: getVariants()) {
            if (v != getVariantFull()) {
                generateVariant(v);
            }
        }
    }

    /**
     * Generates the specified variant of this image and uploads it to Amazon S3 storage.
     * @param variant Should not be the FULL variant.
     */
    final protected void generateVariant(final Variant variant) {
        final int variantWidth = variant.getMaxWidth();
        final int variantHeight = variant.getMaxHeight();
        final File full = getLocalFile(getVariantFull());
        final File target = getLocalFile(variant);

        if (target.exists()) {
            Logger.info("Skipping variant because it already exists: " + getPath(variant));
        }

        // Square images are cropped, so that the dimensions aren't scewed.
        if (variantWidth == variantHeight && variantWidth > 0) {
            int x1, y1, x2, y2;

            if (width > height) {
                y1 = 0;
                y2 = height;

                int removeFromEachXSide = (width - height) / 2;
                x1 = removeFromEachXSide;
                x2 = width - removeFromEachXSide;
            } else {
                x1 = 0;
                x2 = width;

                int removeFromEachYSide = (height - width) / 2;
                y1 = removeFromEachYSide;
                y2 = height - removeFromEachYSide;
            }

            Images.crop(full, target, x1, y1, x2, y2);
            Images.resize(target, target, variantWidth, variantHeight);
        } else if ((variantWidth > 0) && (width >= variantWidth)) {
            int newHeight = (int) (height * ((double) variantWidth / (double) width));
            Images.resize(full, target, variantWidth, newHeight);
        } else if ((variantHeight > 0) && (height >= variantHeight)) {
            int newWidth = (int) (width * ((double) variantHeight / (double) height));
            Images.resize(full, target, width, newWidth);
        } // Resize to make the image bigger.
        else if ((variantWidth > 0) && (width < variantWidth)) {
            Images.resize(full, target, variantWidth, -1);
        } // Resize to make the image bigger.
        else if ((variantHeight > 0) && (height < variantHeight)) {
            Images.resize(full, target, -1, variantHeight);
        } else {
            throw new RuntimeException("Unexpected resize: "
                    + width + ", " + height + ", " + variantWidth + ", " + variantHeight);
        }

    }

    private void optimizeFiles() {
        for (Variant v: getVariants()) {
            if (v == getVariantFull()) {
                bytesSavedBySmushItForFullSize += optimizeFile(getLocalFile(v));
            } else {
                bytesSavedBySmushItForVariants += optimizeFile(getLocalFile(v));
            }
        }
    }

    /**
     * Overwrites the file with a better compressed version.
     * Will not do anything if the file is not a JPEG.
     *
     * Compression is done with the Yahoo SmushIt API.
     *
     * @return Number of bytes saved.
     */
    private int optimizeFile(File file) {
        if (!file.exists()) {
            throw new IllegalArgumentException("File does not exist: " + file.getAbsolutePath());
        }

        // Build the API request.
        FileParam[] files = new FileParam[] { new FileParam(file, "files"), new FileParam(file, "2") };
        WSRequest req = WS.url("http://www.smushit.com/ysmush.it/ws.php?img=");
        req.fileParams = files;
        HttpResponse resp = req.post();
        JsonObject json = resp.getJson().getAsJsonObject();

        if (json.has("dest")) {
            try {
                URL url = new URL(json.get("dest").getAsString());
                IO.write(url.openStream(), file);
            } catch (IOException ex) {
                Logger.error(ex, ex.getMessage());
            }

            int savings = json.get("src_size").getAsInt() - json.get("dest_size").getAsInt();
            return savings;
        } else if (json.has("error")) {
            String error = json.get("error").getAsString();
            if (error.equals("No savings")) {
                return 0;
            } else {
                Logger.error("Error: " + error);
            }
        } else {
            Logger.error("Unexpected JSON: " + json.getAsString());
        }

        return 0;
    }

    /**
     * The variants of size this image type can have.
     */
    public interface Variant {
        /**
         * Get the variant's maximum width dimension. If it has no maximum width, -1 is returned.
         */
        public int getMaxWidth();

        /**
         * Get the variant's maximum height dimension. If it has no maximum height, -1 is returned.
         */
        public int getMaxHeight();

        /**
         * Used in file names. (This method doesn't need implemented - it's part of Java).
         */
        public String name();
    }

    abstract protected Variant[] getVariants();

    abstract protected Variant getVariantFull();
}