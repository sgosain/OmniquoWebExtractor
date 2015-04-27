package net.danburfoot.mbcu;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileUtils {
	private final static String PREFIX_BATCH = "batch";
	private final static String PREFIX_NODE  = "node";
	private final static int BUFFER = 8192;
	
	/**
	 * Get the node of hashed url
	 * @param url The url
	 * @return hashed url as {@link String}
	 */
	public static String getNode(String url){
		int hc = url.hashCode();
		hc = Math.abs(hc); // Make sure it's positive
		hc = (hc % 100000000); //Eight zeroes, maximum value is 99,999,999
		 
		// Now to get the STRING form, pad with leading zeroes:
		String hashstr = String.valueOf(hc);
		 
		while(hashstr.length() < 8) { 
			hashstr = "0" + hashstr; 
		}
		
		return hashstr;
	}
		
	/**
	 * Get the batch of hashed url
	 * @param url The url
	 * @return hashed url as {@link String}
	 */
	public static String getBatch(String url){		
		int hc = url.hashCode();
		hc = (hc < 0 ? -hc : hc); 
		hc = (hc % 1000); 
		 
		// Now to get the STRING form, pad with leading zeroes:
		String hashstr = String.valueOf(hc);
		 		
		while(hashstr.length() < 3) { 
			hashstr = "0" + hashstr; 
		}
		return hashstr;
	}
	
	/**
	 * Return the path of gzipped file of html
	 * @param corpFolder The corpus root folder
	 * @param url The url of the html
	 * @return Path of the gzipped html
	 */
	public static String getHtmlFilePath(String corpFolder, String url){
		String batchFolder = PREFIX_BATCH + getBatch(url);
		String file = PREFIX_NODE + getNode(url);		
		return (corpFolder + File.separator + batchFolder + File.separator +  file + ".html.z");
	}
	
	/**
	 * Create a {@link File} with folder structure
	 * @param path The target path
	 * @return
	 */
	public static boolean makeFile(String path){
		boolean res = false;
		File file = new File(path);
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			try {
				file.createNewFile();
				res = true;
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}			
		return res;
	}
	
	/**
	 * Compress raw html into zipped file
	 * @param path The path for target file
	 * @param html The raw html content
	 */
	public static void gzipHtml(String path, String html){
		
		if (makeFile(path)){
			Writer out = null;
			try {		
		        GZIPOutputStream zip = new GZIPOutputStream(new FileOutputStream(new File(path)));
				out = new BufferedWriter(new OutputStreamWriter(zip, "UTF-8"));
				out.write(html);
				out.close();			
				System.out.println("Zipped : " + path);
			} catch (IOException e) {
				e.printStackTrace();
			}finally{           
		        if(out != null)
					try {
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
		    }
		}	
	}		
	
	/**
	 * Save the extracted html into txt file
	 * @param path The taret txt file
	 * @param content The content
	 */
	public static void saveTxt(String path, String content){
		BufferedWriter bw = null;
		try{
			File file = new File(path);		 
		if (!file.exists())
			makeFile(path);

		bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
		bw.write(content);
		bw.close();
		}catch (IOException e){
			e.printStackTrace();
		}finally{
			if (bw != null){
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		
	}
	
	/**
	 * Recursively delete files with ext extension
	 * @param path Root folder where recursive delete starts
	 * @param ext The file extension of to be deleted files
	 * @return Number of deleted files
	 */
	public static int deleteRecursive(String path, String ext){
		int count = 0;
		ArrayList<File> batchList = FileUtils.listFolders(path);
		for (File batch : batchList){		
			for (File file : batch.listFiles()){
				if (file.isFile() && file.getAbsolutePath().endsWith(ext)){
					file.delete();
					count++;
				}				
			}					
		}	
		return count;
	}
	
	/**
	 * Decompress gzipped html back into {@link String}
	 * @param path Path of target file
	 * @return raw html from the zip
	 */
	public static String gunzipHtml(String path){
		String res = "";		
		GZIPInputStream gzis = null;
		BufferedReader bf = null;
		try {
			gzis = new GZIPInputStream(new FileInputStream(path));		
			bf = new BufferedReader(new InputStreamReader(gzis, "UTF-8"));
	        String line;
	        while ((line=bf.readLine())!=null) {
	          res += line;
	        }	            
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (gzis != null){
				try {
					gzis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}if (bf != null){
				try{
					bf.close();
				}catch (IOException e){
					e.printStackTrace();
				}
			}
			
		}
		return res;
		
	}
	
	/**
	 * List folders inside of path
	 * @param path the path
	 * @return {@link List} of folders as {@link File} 
	 */
	public static ArrayList<File> listFolders(String path) {
		ArrayList<File> res = new ArrayList<File>();
	    File directory = new File(path);
	    File[] fList = directory.listFiles();
	    for (File file : fList) {
	        if (file.isDirectory()) {
	            res.add(file);
	        } 
	    }
	    return res;
	}
	
	/**
	 * Archive directory structure with corpus folder as root
	 * @param path
	 * Corpus absolute path
	 */
	public static void batchArchiver(String path){
		byte[] buffer = new byte[BUFFER];
		ArrayList<File> folders = FileUtils.listFolders(path);
		for (File batch : folders){
			File batchZip = new File(batch.getAbsolutePath() + ".zip");					
		    try{		    	 
		    	FileOutputStream fos = new FileOutputStream(batchZip);
		    	ZipOutputStream zos = new ZipOutputStream(fos);
		 		    	
			    File[] nodeList = batch.listFiles();
			    for (File node : nodeList) {
			        if (node.isFile()) {		
			        	/*
			        	 * DCB request: zip structure starts from the corpus folder
			        	 */
			    		ZipEntry ze= new ZipEntry(node.getPath().substring(Config.PATH_BASE.length())); // this is the intended file's path
			        	zos.putNextEntry(ze);			 
			        	FileInputStream in =  new FileInputStream(node.getPath());		// this is the source        	
			        	int len;
			        	while ((len = in.read(buffer)) > 0) {
			        		zos.write(buffer, 0, len);
			        	}			 
			        	in.close();
			        } 
			    }	 
		    	zos.closeEntry();
		    	zos.close();		
		    }catch(IOException ex){
		       ex.printStackTrace();   
		    }		
		}		
	}

	/**
	 * List all files in a directory
	 * @param path
	 * @return {@link File} list of files
	 */
	public static ArrayList<File> listFiles(String path) {
		ArrayList<File> res = new ArrayList<File>();
	    File directory = new File(path);
	    File[] fList = directory.listFiles();
	    for (File file : fList) {
	        if (file.isFile()) {
	            res.add(file);
	        } 
	    }
	    return res;
	}
	
	
}
