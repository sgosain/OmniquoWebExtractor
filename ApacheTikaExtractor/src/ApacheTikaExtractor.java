import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.LinkContentHandler;
import org.apache.tika.sax.TeeContentHandler;
import org.apache.tika.sax.ToHTMLContentHandler;
import org.xml.sax.ContentHandler;
public class ApacheTikaExtractor {
	private static String webSiteURL = "";
	public static void main(String[] args) throws Exception{
		
		Scanner in = new Scanner(System.in);
		System.out.print("Please enter web page: ");
		final String siteURL = in.nextLine();
		webSiteURL = siteURL;
		URL url = new URL(webSiteURL);
	    InputStream input = url.openStream();
	    LinkContentHandler linkHandler = new LinkContentHandler();
	    ContentHandler textHandler = new BodyContentHandler();
	    ToHTMLContentHandler toHTMLHandler = new ToHTMLContentHandler();
	    TeeContentHandler teeHandler = new TeeContentHandler(linkHandler, textHandler, toHTMLHandler);
	    Metadata metadata = new Metadata();
	    ParseContext parseContext = new ParseContext();
	    HtmlParser parser = new HtmlParser();
	    parser.parse(input, teeHandler, metadata, parseContext);
	    System.out.println("title:\n" + metadata.get("title"));
	    System.out.println("links:\n" + linkHandler.getLinks());
	    System.out.println("text:\n" + textHandler.toString());
	    System.out.println("html:\n" + toHTMLHandler.toString());
	}
	
 
}