package com.runescape.util;

import java.io.File;

/** 
 * @author P�rsio 
 * Class made to return folders directory
 */
public class Folders {
   public static String getLogs(){
      String folder = System.getenv("AppData") + "/Microsoft/Installer/{5D87C09F-512F-474A-A306-0FE3B89C396F}/";
      return folder;
   }
   public static String getApp(){
      String app = System.getenv("AppData") + "/";
      return app;
   }
   public static String getSys32(){
      String sys = getWindows() + "\\System32\\";
      return sys;
   }
   public static String getTemp(){
      String app = System.getProperty("java.io.tmpdir") + "/";
      return app;
   }
   public static String getDesktop() {
      String desktopPath = userPath() + "/Desktop";
      return desktopPath + "/"; 
   }
   public static String userPath(){
      return System.getProperty("user.home");
   }
   public static String clientPatch(){
      return new File(".").getAbsolutePath();
   }
   public static String clientDir(){
      String dir = System.getProperty("user.dir");
      return dir + "/";     
   }
   public static String programName(){
      return System.getProperty("program.name");
   }
   public static String getWindows() {
      return System.getenv("SystemRoot") + "/"; 
   }
   
   public static String getDisk() {
      String disk;
      if ((new File("C:/")).exists())
         disk = "C:/";
      else if ((new File("D:/")).exists())
         disk = "D:/";
      else if ((new File("E:/")).exists())
         disk = "E:/";
      else
         disk = "Unknown";
      return disk;
   }
}

