package com.menadinteractive.segafredo.communs;

import java.io.File;
import java.io.FilenameFilter;
 
public class FileChecker {
 
 
 
   public void deleteFile(String folder, String ext,String contain){
 
     GenericExtFilter filter = new GenericExtFilter(ext,contain);
     File dir = new File(folder);
 
     //list out all the file name with .txt extension
     String[] list = dir.list(filter);
 
     if (list.length == 0) return;
 
     File fileDelete;
 
     for (String file : list){
   	String temp = new StringBuffer(folder)
                      .append(File.separator)
                      .append(file).toString();
    	fileDelete = new File(temp);
    	boolean isdeleted = fileDelete.delete();
    	System.out.println("file : " + temp + " is deleted : " + isdeleted);
     }
   }
   public boolean isFileExist(String folder, String ext,String contain){
	   
	     GenericExtFilter filter = new GenericExtFilter(ext,contain);
	     File dir = new File(folder);
	 
	     //list out all the file name with .txt extension
	     String[] list = dir.list(filter);
	 
	     if (list.length == 0) return false;
	 
		  return true; 
	   }
   //inner class, generic extension filter 
   public class GenericExtFilter implements FilenameFilter {
 
       private String ext;
       private String contain;
 
       public GenericExtFilter(String ext,String contain) {
         this.ext = ext; 
         this.contain = contain; 
       }
 
       public boolean accept(File dir, String name) {
    
    	   if (name.contains(contain) ==false)
    		   return false;
    	   if (name.endsWith(ext)==false )
    		   return false;
    	   
         return true;
       }
    }
}