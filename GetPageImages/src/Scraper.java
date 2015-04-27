import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

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
   
   //in.close();
     
   
   ArrayList<PageImage> pageImages = new ArrayList<PageImage>();
   
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
	 
	               int indexname = src.lastIndexOf("/");
	               
	               if (indexname == src.length()) {
	                   src = src.substring(1, indexname);
	               }
	               
	               indexname = src.lastIndexOf("/");
	               String fileName = src.substring(indexname, src.length());
	               fileName= fileName.replaceAll("[^a-zA-Z0-9_.]", "_"); 
	               //Open a URL Stream
	               URL url = new URL(src);
	               
	               //Gestimate size, we should take the largest size
	               URLConnection yc = url.openConnection();
	               long fileSize = yc.getContentLengthLong();
	               pageImages.add(new PageImage(src, fileName, fileSize, el.attr("title"), el.attr("alt"), el.attr("height"), el.attr("width"), el.attr("tabindex")));
	               
 
               }
               
   		}
           
          long maxImgSize = 0;
          String maxImgSrc = "";
          String maxImgName = "";
          String maxImgTitle = "";
          String maxImgAlt = "";
          String maxImgHeight = "";
          String maxImgWidth = "" ;
          String maxImgTabIndex = "" ;
          
          
           for (PageImage pageImage : pageImages)
           {
              if (pageImage.imgSize > maxImgSize ) {
            	  maxImgSize = pageImage.imgSize;
                  maxImgSrc = pageImage.imgSrc;
                  maxImgName = pageImage.imgName;
                  maxImgTitle = pageImage.imgTitle;
                  maxImgAlt = pageImage.imgAlt;
                  if (pageImage.imgHeight != null ) {
                	  maxImgHeight = pageImage.imgHeight;
                  }
                  if (pageImage.imgWidth != null ) {
                	  maxImgWidth = pageImage.imgWidth;
                  }
                  if (pageImage.imgHeight != null ) {
                	  maxImgTabIndex = pageImage.imgTabIndex;
                  }
              }
              System.out.println("Source: " + pageImage.imgSrc + " Name: " + pageImage.imgName + " Size: " + pageImage.imgSize + " Title: " + pageImage.imgTitle + "  Alt: " + pageImage.imgAlt + "  Height: " + pageImage.imgHeight + "  Width: " + pageImage.imgWidth + "  TabIndex: " + pageImage.imgTabIndex ); 
           }
           System.out.println("LARGEST Source: " + maxImgSrc + " Name: " + maxImgName + " Size: " + maxImgSize + " Title: " + maxImgTitle + " Alt: " + maxImgAlt + "Height: " + maxImgHeight + " Width: " + maxImgWidth + " TabIndex: " + maxImgTabIndex); 
 
   } catch (IOException ex) {
             System.err.println("There was an error: " + ex);
       }
   
 }
 
   
}
   
   
