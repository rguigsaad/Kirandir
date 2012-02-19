package models;
 
import java.util.*;
import javax.persistence.*;

import com.alienmegacorp.fileuploads.AbstractUploadedImage;
import com.alienmegacorp.fileuploads.FullPicture;
import com.alienmegacorp.fileuploads.Thumbnail;


import play.Play;
import play.db.jpa.*;
import play.data.validation.*;


@Entity
public class Picture extends AbstractUploadedImage {
	
	@Override
	protected String getFolder() {
		return Play.configuration.getProperty("dir.upload");
	}

	@Override
	protected Variant[] getVariants() {
		Thumbnail thumbnail = new Thumbnail();
		Variant variant[] = {thumbnail};
		return variant;
	}

	@Override
	protected Variant getVariantFull() {
		FullPicture fullPic = new FullPicture();
		return fullPic;
	}
            
}
