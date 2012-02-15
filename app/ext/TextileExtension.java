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
		String thumbnailBegin = "<ul class=\"thumbnails\"><li class=\"span3\"><a href=\"#\" class=\"thumbnail\"><img";
		String thumbnailEnd = "/></a></li></ul>";
		String regexp = "<img(.*?)/>";
		
		 Pattern p = Pattern.compile(regexp);
		 Matcher m = p.matcher(text);
		 StringBuffer sb = new StringBuffer();
		 while (m.find()) {
			 String substring = StringUtils.substring(text, m.start(), m.end());
			 substring = StringUtils.replace(substring, "<img", thumbnailBegin);
			 substring = StringUtils.replace(substring, "/>", thumbnailEnd);
		     m.appendReplacement(sb, substring);
		 }
		 m.appendTail(sb);
		 
		 text = StringUtils.replace(sb.toString(),"!@","<pre class=\"prettyprint linenums\">");
		 text = StringUtils.replace(text,"@!","</pre>");
//		 text = sb.toString();
		 return text;
	}
}