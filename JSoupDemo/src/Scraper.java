import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

//import org.apache.commons.httpclient.util.URIUtil.encodeQuery

import java.net.URLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.HttpHeaders;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaMetadataKeys;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class Scraper {
 
   //The url of the website
   private static String webSiteURL ="";
 
   //The path of the folder that you want to save the images to
   private static String folderPath = "";
   static Integer lastPage = 0;
   static Integer LP = 0;
 
   public static void main(String[] args) throws UnknownHostException, URISyntaxException, SAXException, TikaException {
 
   Scanner in = new Scanner(System.in);
   System.out.print("Please enter the folder path: ");
   final String path = in.nextLine();
   folderPath = path;
 
   System.out.print("Please enter web page: ");
   final String siteURL = in.nextLine();
   webSiteURL = siteURL;
   
   //in.close();
   
   try {
 
           Document doc = Jsoup.connect(webSiteURL ).get();
           String title = doc.title();
           System.out.println("Title: " + title);
 
           //Get all elements with img tag ,
           Elements img = doc.getElementsByTag("img");
 
           for (Element el : img) {
 
               //for each element get the srs url
               String src = el.absUrl("src");
               
               if (src.length() > 0 ) {
	 
	               System.out.println("Image Found!");
	               System.out.println("src attribute is : " + src);
               
                   //URI uri encode querystring of url
	              
	               URL url= new URL(src);
	               String qStr = url.getQuery();
	               if (qStr!= null) {
	            	   qStr = URLEncoder.encode(qStr, "UTF-8");
	            	   System.out.println(qStr);
	            	   URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), qStr, url.getRef());
	            	   src=uri.toASCIIString();
	               }
	               
	 
	               getImages(src);
 
               }
   		}
 
   } catch (IOException ex) {
             System.err.println("There was an error: " + ex);
       }
   
 }
 
   private static void getImages(String src) throws IOException, SAXException, TikaException {
 
      int indexname = src.lastIndexOf("/");
 
      if (indexname == src.length()) {
          src = src.substring(1, indexname);
      }
          
      
 
      indexname = src.lastIndexOf("/");
      String name = src.substring(indexname, src.length());
      
      //Get rid of all special characters in the filename except / and .
       name= name.replaceAll("[^a-zA-Z0-9/.]", ""); 
      
 
      //Open a URL Stream
      URL url = new URL(src);
      
      //Gestimate size, we should take the largest size
      URLConnection yc = url.openConnection();
      long i = yc.getContentLengthLong();
      System.out.println("Size: " + i);
      
      /* code below is to download the image which is optional */
      InputStream in = url.openStream();
 
      OutputStream out = new BufferedOutputStream(new FileOutputStream(folderPath + name));
 
      for (int b; (b = in.read()) != -1;) {
           out.write(b);
      }
      out.close();
      in.close();
      
      /* optional, check mimetype to see what type of file it is */
      System.out.println("MIME TYPE: " + getMimetype(folderPath + name));
      
  }
   
   private static String getMimetype(String filePath) throws IOException, SAXException, TikaException {
	   
	   File file = new File(filePath);

       AutoDetectParser parser = new AutoDetectParser();
       parser.setParsers(new HashMap<MediaType, Parser>());

       Metadata metadata = new Metadata();
       metadata.add(TikaMetadataKeys.RESOURCE_NAME_KEY, file.getName());

       InputStream stream = new FileInputStream(file);
       parser.parse(stream, new DefaultHandler(), metadata, new ParseContext());
       stream.close();

       String mimeType = metadata.get(HttpHeaders.CONTENT_TYPE);
       System.out.println(mimeType);
       return mimeType;
	   
   }
   
}