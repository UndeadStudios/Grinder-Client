package com.runescape.util;

import java.io.*;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import javax.swing.JOptionPane;

import com.grinder.client.util.Log;
import com.grinder.net.CacheDownloader;
import com.runescape.Client;
import com.grinder.Configuration;
import com.grinder.GrinderScape;

public class Unzip {
	public static void unzip(String zipFile, String outputFolder, boolean deleteAfter)
	{
		//Open the file
		try(ZipFile file = new ZipFile(zipFile))
		{
			FileSystem fileSystem = FileSystems.getDefault();
			//Get file entries
			Enumeration<? extends ZipEntry> entries = file.entries();

			//We will unzip files in this folder
			final File folder = fileSystem.getPath(outputFolder).toFile();
			folder.mkdir();


			//Iterate over entries
			while (entries.hasMoreElements())
			{
				ZipEntry entry = entries.nextElement();
				File destinationPath = new File(outputFolder, entry.getName());
				//create parent directories
				destinationPath.getParentFile().mkdirs();

				// if the entry is a file extract it
				if (entry.isDirectory()) {
					continue;
				}else {

					System.out.println("Extracting file: " + destinationPath);

					BufferedInputStream bis = new BufferedInputStream(file.getInputStream(entry));

					int b;
					byte buffer[] = new byte[1024];

					FileOutputStream fos = new FileOutputStream(destinationPath);

					BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);

					while ((b = bis.read(buffer, 0, 1024)) != -1) {
						bos.write(buffer, 0, b);
					}

					bos.close();
					bis.close();

				}
//				System.out.println("parsing "+entry.getName()+", "+entry.getComment());
//				//If directory then create a new directory in uncompressed folder
//				if (entry.isDirectory())
//				{
////					folder = new File(uncompressedDirectory + entry.getName());
////					if(folder.exists()){
////						folder.delete();
////					}
//					System.out.println("Creating Directory:" + uncompressedDirectory + entry.getName());
//					Files.createDirectories(fileSystem.getPath(uncompressedDirectory + entry.getName()));
//				}
//				//Else create the file
//				else
//				{
//					InputStream is = file.getInputStream(entry);
//					BufferedInputStream bis = new BufferedInputStream(is);
//					String uncompressedFileName = uncompressedDirectory + entry.getName();
//					Path uncompressedFilePath = fileSystem.getPath(uncompressedFileName);
//
//					File f = uncompressedFilePath.toFile();
//					if(f.exists())
//						f.delete();
//					Files.createFile(uncompressedFilePath);
//					FileOutputStream fileOutput = new FileOutputStream(uncompressedFileName);
//					while (bis.available() > 0)
//					{
//						fileOutput.write(bis.read());
//					}
//					fileOutput.close();
//					System.out.println("Written :" + entry.getName()+" "+uncompressedFileName);
//				}
			}
		}
		catch(Exception e)
		{
			Log.error("Error during cache extraction", e);
			JOptionPane.showMessageDialog(null, "We had an error with unzipping the cache, Re-downloading...");
			final File cacheDirectory = new File(Configuration.CACHE_DIRECTORY);
			cacheDirectory.delete();
			CacheDownloader.downloading = false;
			Client.instance.cleanUpForQuit();
			Client.instance = null;
			GrinderScape.main(new String[]{});
		}

		if(deleteAfter){
			Log.info("Deleting "+zipFile+"!");
			new File(zipFile).delete();
		}
	}

	/**
	 * Unzip it
	 * @param zipFile input zip file
	 * @param output zip file output folder
	 * @param deleteAfter		Should the zip file be deleted afterwards?
	 */
	public static void unZipIt(String zipFile, String outputFolder, boolean deleteAfter) {

		byte[] buffer = new byte[4096];

		try{

			//create output directory is not exists
			File folder = new File(outputFolder);
			if(!folder.exists()){
				folder.mkdir();
			}

			//get the zip file content
			ZipInputStream zis =
					new ZipInputStream(new FileInputStream(zipFile));
			//get the zipped file list entry
			ZipEntry ze = zis.getNextEntry();

			while(ze!=null){

				String fileName = ze.getName();
				File newFile = new File(outputFolder + File.separator + fileName);

				System.out.println("file unzip : "+ newFile.getAbsoluteFile());

				//create all non exists folders
				//else you will hit FileNotFoundException for compressed folder
				new File(newFile.getParent()).mkdirs();

				FileOutputStream fos = new FileOutputStream(newFile);

				int len;
				while ((len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}

				fos.close();
				ze = zis.getNextEntry();
			}

			zis.closeEntry();
			zis.close();
			
			if(deleteAfter) {
				new File(zipFile).delete();
			}

		}catch(Exception ex){
			Log.error("Error during cache extraction", ex);
			JOptionPane.showMessageDialog(null, "We had an error with unzipping the cache, Re-downloading...");
			final File cacheDirectory = new File(Configuration.CACHE_DIRECTORY);
			cacheDirectory.delete();
			CacheDownloader.downloading = false;
			Client.instance.cleanUpForQuit();
			Client.instance = null;
			GrinderScape.main(new String[]{});
			ex.printStackTrace();

		}
	}

}
