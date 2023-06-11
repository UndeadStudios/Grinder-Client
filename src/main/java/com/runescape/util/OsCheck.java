package com.runescape.util;

import com.grinder.client.util.Log;
import com.runescape.cache.AccessFile;
import com.runescape.cache.BufferedFile;
import com.runescape.cache.OsCache;
import com.runescape.io.Buffer;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public final class OsCheck {

  public static String companyName = "grinder";
  public static String mainCacheName = "GrinderScapeCache";
  public static String subCacheName = "oldschool";

  public static String osName;
  public static String osNameLowerCase;
  public static String userHomeDirectory;
  public static String[] cacheDirectoryLocations;
  public static String[] __ao_s;
  public static File clDat;

  static {
    try {
      osName = System.getProperty("os.name");
    } catch (Exception var28) { 
      osName = "Unknown";
      var28.printStackTrace();
    }

    osNameLowerCase = osName.toLowerCase();

    try {
      userHomeDirectory = System.getProperty("user.home");
      if(userHomeDirectory != null) {
        userHomeDirectory = userHomeDirectory + "/";
      }
    } catch (Exception var27) {
      var27.printStackTrace();
    }

    try {
      if(osNameLowerCase.startsWith("win")) {
        if(userHomeDirectory == null) {
          userHomeDirectory = System.getenv("USERPROFILE");
        }
      } else if(userHomeDirectory == null) {
        userHomeDirectory = System.getenv("HOME");
      }

      if(userHomeDirectory != null) {
        userHomeDirectory = userHomeDirectory + "/";
      }
    } catch (Exception var26) {
      var26.printStackTrace();
    }

    if(userHomeDirectory == null) {
      userHomeDirectory = "~/";
    }

    cacheDirectoryLocations = new String[]{"c:/rscache/", "/rscache/", "c:/windows/", "c:/winnt/", "c:/", userHomeDirectory, "/tmp/", ""};
    __ao_s = new String[]{"."+companyName+"_cache_0", ".file_store_0"};
    int var17 = 0;
    label259:
    while(var17 < 4) {
      String var37 = var17 == 0?"":"" + var17;
      System.out.println(var17+" -> "+var37);
      clDat = new File(userHomeDirectory, companyName+"_cl_"+subCacheName+"_" + var37 + ".dat");
      String cacheDirectoryName1 = null;
      String cacheDirectoryName2 = null;
      boolean var19 = false;
      File cacheDirectory1;
      if(clDat.exists()) {
        try {
          AccessFile clDatFile = new AccessFile(clDat, "rw", 10000L);

          int var11;
          Buffer buffer;
          for(buffer = new Buffer((int)clDatFile.length()); buffer.index < buffer.array.length; buffer.index += var11) {
            var11 = clDatFile.read(buffer.array, buffer.index, buffer.array.length - buffer.index);
            if(var11 == -1) {
              throw new IOException();
            }
          }

          buffer.index = 0;
          var11 = buffer.getUnsignedByte();
          if(var11 < 1 || var11 > 3) {
            throw new IOException("" + var11);
          }

          int var12 = 0;
          if(var11 > 1) {
            var12 = buffer.getUnsignedByte();
          }

          if(var11 <= 2) {
            cacheDirectoryName1 = buffer.readStringCp1252NullCircumfixed();
            if(var12 == 1) {
              cacheDirectoryName2 = buffer.readStringCp1252NullCircumfixed();
            }
          } else {
            cacheDirectoryName1 = buffer.__aw_304();
            if(var12 == 1) {
              cacheDirectoryName2 = buffer.__aw_304();
            }
          }

          clDatFile.close();
        } catch (IOException var30) {
          var30.printStackTrace();
        }

        if(cacheDirectoryName1 != null) {
          cacheDirectory1 = new File(cacheDirectoryName1);
          if(!cacheDirectory1.exists()) {
            cacheDirectoryName1 = null;
          }
        }

        if(cacheDirectoryName1 != null) {
          cacheDirectory1 = new File(cacheDirectoryName1, "test.dat");
          if(!method847(cacheDirectory1, true)) {
            cacheDirectoryName1 = null;
          }
        }
      }

      if(cacheDirectoryName1 == null && var17 == 0) {
        label234:
        for (String ao_ : __ao_s) {
          for (String cacheDirectoryLocation : cacheDirectoryLocations) {
            File var22 = new File(cacheDirectoryLocation + ao_ + File.separatorChar + mainCacheName + File.separatorChar + subCacheName + File.separator);
            if (var22.exists() && method847(new File(var22, "test.dat"), true)) {
              cacheDirectoryName1 = var22.toString();
              Log.info("New cache directory name is "+cacheDirectoryName1);
              var19 = true;
              break label234;
            }
          }
        }
      }

      if(cacheDirectoryName1 == null) {
        cacheDirectoryName1 = userHomeDirectory + File.separatorChar + mainCacheName + File.separatorChar + subCacheName + + File.separatorChar;
        var19 = true;
      }

      if(cacheDirectoryName2 != null) {
        File cacheDirectory2 = new File(cacheDirectoryName2);
        cacheDirectory1 = new File(cacheDirectoryName1);

        try {
          File[] files = cacheDirectory2.listFiles();
          for (File var14 : files) {
            File var15 = new File(cacheDirectory1, var14.getName());
            boolean var16 = var14.renameTo(var15);
            if (!var16) {
              throw new IOException();
            }
          }
        } catch (Exception var29) {
          var29.printStackTrace();
        }

        var19 = true;
      }

      if(var19) {
        method167(new File(cacheDirectoryName1), null);
      }

      OsCache.directory = new File(cacheDirectoryName1);
      if(!OsCache.directory.exists()) {
        OsCache.directory.mkdirs();
        Log.info("Created new cache directory at "+cacheDirectoryName1);
      }

      File[] files = OsCache.directory.listFiles();
      if(files != null) {
        for (File file : files) {
          if (!method847(file, false)) {
            ++var17;
            continue label259;
          }
        }
      }
      break;
    }
  }
  public static void createOrLoadRandomDat() {
    try {
      File randomFile = new File(userHomeDirectory, "random.dat");
      int i;
      if(randomFile.exists()) {
        OsCache.randomDat = new BufferedFile(new AccessFile(randomFile, "rw", 25L), 24, 0);
      } else {
        label38:
        for (String ao_ : __ao_s) {
          for (i = 0; i < cacheDirectoryLocations.length; ++i) {
            File var3 = new File(cacheDirectoryLocations[i] + ao_ + File.separatorChar + "random.dat");
            if (var3.exists()) {
              OsCache.randomDat = new BufferedFile(new AccessFile(var3, "rw", 25L), 24, 0);
              break label38;
            }
          }
        }
      }

      if(OsCache.randomDat == null) {
        RandomAccessFile randomAccessFile = new RandomAccessFile(randomFile, "rw");
        i = randomAccessFile.read();
        randomAccessFile.seek(0L);
        randomAccessFile.write(i);
        randomAccessFile.seek(0L);
        randomAccessFile.close();
        OsCache.randomDat = new BufferedFile(new AccessFile(randomFile, "rw", 25L), 24, 0);
      }
    } catch (IOException exception) {
      Log.error("Failed to create ........dat", exception);
    }
  }

  public static boolean method847(File var0, boolean var1) {
    try {
      RandomAccessFile var2 = new RandomAccessFile(var0, "rw");
      int var3 = var2.read();
      var2.seek(0L);
      var2.write(var3);
      var2.seek(0L);
      var2.close();
      if(var1) {
        var0.delete();
      }

      return true;
    } catch (Exception var4) {
      var4.printStackTrace();
      return false;
    }
  }
  public static void method167(File var0, File var1) {
    try {
      AccessFile var2 = new AccessFile(clDat, "rw", 10000L);
      Buffer var3 = new Buffer(500);
      var3.writeByte(3);
      var3.writeByte(var1 != null?1:0);
      var3.writeCharSequence(var0.getPath());
      if(var1 != null) {
        var3.writeCharSequence("");
      }

      var2.write(var3.array, 0, var3.index);
      var2.close();
    } catch (IOException var4) {
      var4.printStackTrace();
    }

  }


}