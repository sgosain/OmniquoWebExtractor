import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import org.xml.sax.SAXException;

public class Scraper {
 
   //The url of the website
   private static String webSiteURL ="";
 
 
   public static void main(String[] args) throws UnknownHostException, URISyntaxException, SAXException {
 
   Scanner in = new Scanner(System.in);
    
   System.out.print("Please enter web page: ");
   final String siteURL = in.nextLine();
   webSiteURL = siteURL;
   
   Pattern pattern = Pattern.compile(".*mp3\\s*:\\s*[\"|']\\[([^\\]]+)\\].*");

   
   //in.close();
     
   
   ArrayList<PageVideo> pageVideos = new ArrayList<PageVideo>();
   
   try {
 
           Document doc = Jsoup.connect(webSiteURL ).get();
           String title = doc.title();
           System.out.println("Title: " + title);
 
           //Get all elements with video tag ,
           Elements vid = doc.getElementsByTag("video");
           
           
           for (Element el : vid) {
        	  
                              
               //get all source elements
               Elements vidSrc = el.getElementsByAttribute("src"); 
               for (Element vs : vidSrc) {
            	   
            	   pageVideos.add(new PageVideo(vs.absUrl("src"), "video" ));
               }
               
             
           }
          
           
           Elements metaVideo = doc.select("meta[property=og:video]");

           for (Element src : metaVideo) {
               if (src.tagName().equals("meta")) {
            	   System.out.println(" content: " + src.attr("content"));
            	   pageVideos.add(new PageVideo(src.attr("content"), "META-content" ));
               }
                else
            	   System.out.println("TAG: "+ src.tagName());
           }
           
           // extract Youtube and Vimeo videos from the post
           Elements ytVimVideo = doc.select("iframe[src~=(youtube\\.com|vimeo\\.com)], object[data~=(youtube\\.com|vimeo\\.com)], embed[src~=(youtube\\.com|vimeo\\.com)]");
                    
           String provider = "";
           for (Element video : ytVimVideo ) {
               String vidurl = video.attr("src");
               
               if (vidurl == null)
                   vidurl = video.attr("data");
               if (vidurl == null || vidurl.trim().equals(""))
                   continue;
               
               if (vidurl.toLowerCase().contains("vimeo.com"))
                   provider= "VIMEO";
               else
                   provider="YOUTUBE";
               
               pageVideos.add(new PageVideo(vidurl, provider ));
           }
                    
           for (PageVideo pageVideo : pageVideos)
           {
              
              System.out.println("Source: " + pageVideo.vidSrc + " Provider: " + pageVideo.vidProvider ); 
           }
           
 
   } catch (IOException ex) {
             System.err.println("There was an error: " + ex);
       }
   
 }
 
   
}
   
   

