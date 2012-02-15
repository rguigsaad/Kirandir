package controllers;
 
import java.io.StringWriter;

import ext.TextileExtension;

import jj.play.org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import jj.play.org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder;
import jj.play.org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder.Stylesheet;
import jj.play.org.eclipse.mylyn.wikitext.textile.core.TextileLanguage;
import play.mvc.Controller;
 
public class Wikitext extends Controller {
 
    // Controller classique Play! qui sera appelé par l'iframe Preview de MarkItUp.
    public static void renderPreview(String wiki) {
        StringWriter writer = new StringWriter();
        HtmlDocumentBuilder builder = new HtmlDocumentBuilder(writer);
 
        // Ajout de mes 2 css.
        Stylesheet css = new Stylesheet("/public/stylesheets/main.css");
        builder.addCssStylesheet(css);
        css = new Stylesheet("/public/stylesheets/wiki.css");
        builder.addCssStylesheet(css);
 
        // Création du parser Textile
        MarkupParser parser = new MarkupParser(new TextileLanguage());
        parser.setBuilder(builder);
        parser.parse(wiki);
 
        String htmlContent = TextileExtension.bootstrapFormater(writer.toString());
        renderText(htmlContent);
    }
 
    // Méthode qui sera appelée par les templates Groovy de Play!.
    public static String render(String wiki) {
        StringWriter writer = new StringWriter();
        HtmlDocumentBuilder builder = new HtmlDocumentBuilder(writer);
        // Empêche la génération des balises html et body.
        builder.setEmitAsDocument(false);
 
        // Création du parser Textile
        MarkupParser parser = new MarkupParser(new TextileLanguage());
        parser.setBuilder(builder);
        parser.parse(wiki);
 
        return TextileExtension.bootstrapFormater(writer.toString());
    }
}