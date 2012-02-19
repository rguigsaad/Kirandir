package ext;

import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import jj.play.org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import jj.play.org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder;
import jj.play.org.eclipse.mylyn.wikitext.textile.core.TextileLanguage;
import play.templates.JavaExtensions;

public class TextileExtension extends JavaExtensions {

	public static String textile(String text){
		StringWriter writer = new StringWriter();
		HtmlDocumentBuilder builder = new HtmlDocumentBuilder(writer);
		// Empêche la génération des balises html et body.
		builder.setEmitAsDocument(false);

		// Création du parser Textile
		MarkupParser parser = new MarkupParser(new TextileLanguage());
		parser.setBuilder(builder);
		parser.parse(text);
		
		return bootstrapFormater(writer.toString());

	}

	public static String bootstrapFormater(String text) {
		StringBuffer thumbnailPart1 = new StringBuffer(); 
		thumbnailPart1.append("<ul class=\"thumbnails\">");
		thumbnailPart1.append("<li class=\"span3\">");
		thumbnailPart1.append("<a class=\"thumbnail\" data-toggle=\"modal\" href=\"#myModal");
		
		String thumbnailPart2 = "\"><img";
		String thumbnailPart3 = "/></a></li><div id=\"myModal";
		
		StringBuffer thumbnailPart4 = new StringBuffer();
		thumbnailPart4.append("\" class=\"modal hide fade\">");
		thumbnailPart4.append("<div class=\"modal-body\">");
		thumbnailPart4.append("<a class=\"close\" data-dismiss=\"modal\" >&times;</a>");
		
		String thumbnailPart5 = "</div></div></ul>";
		
		String regexp = "<img(.*?)/>";
		
		// Set Bootstrap thumbnail for img tag by url ( !http://mypic.jpg(mylabel)!)
		 Pattern p = Pattern.compile(regexp);
		 Matcher m = p.matcher(text);
		 StringBuffer sb = new StringBuffer();
		 int imgIndex = 0;
		 while (m.find()) {
			 String imgTag = StringUtils.substring(text, m.start(), m.end());
			 String newImgTag = new String(imgTag);
			 StringBuffer thumbnailBegin = new StringBuffer(thumbnailPart1.toString());
			 thumbnailBegin.append(imgIndex);
			 thumbnailBegin.append(thumbnailPart2);
			 newImgTag = StringUtils.replace(newImgTag, "<img", thumbnailBegin.toString());
			 StringBuffer thumbnailEnd = new StringBuffer(thumbnailPart3);
			 thumbnailEnd.append(imgIndex);
			 thumbnailEnd.append(thumbnailPart4);
			 thumbnailEnd.append(imgTag);
			 thumbnailEnd.append(thumbnailPart5);			 
			 newImgTag = StringUtils.replace(newImgTag, "/>", thumbnailEnd.toString());
		     m.appendReplacement(sb, newImgTag);
		     imgIndex++;
		 }
		 m.appendTail(sb);
		 
		 // Set BootStrap Code prettyprint to block Code
		 text = StringUtils.replace(sb.toString(),"!@","<pre class=\"prettyprint linenums\">");
		 text = StringUtils.replace(text,"@!","</pre>");
//		 text = sb.toString();
		 
		// String regexp2 = "$pic.(.*?)\!$";
		 
		 //$pic.5!$
		 
		 return text;
	}
}