package com.alienmegacorp.fileuploads;

import java.io.File;
import play.Logger;
import play.Play;

class Helper {
    final static File DIR_ROOT = Play.getFile("public/uploads");

        static String getBaseURL() {
        final StringBuilder sb = new StringBuilder(100);

        final String protocol = ((play.mvc.Http.Request.current() != null && play.mvc.Http.Request.current().secure)
                ? "https://"
                : "http://");

            String baseUrl = play.Play.configuration.getProperty("application.baseUrl");
            baseUrl = baseUrl.replace("http://", protocol);
            sb.append(baseUrl);
            sb.append("uploads/");

        return sb.toString();
    }

    private Helper() {
    }
}