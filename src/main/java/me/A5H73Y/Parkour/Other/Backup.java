package me.A5H73Y.Parkour.Other;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import me.A5H73Y.Parkour.Parkour;
import me.A5H73Y.Parkour.Utilities.Utils;

public class Backup {

	private static List<String> fileList;
	private static final String OUTPUT_ZIP_FILE = Parkour.getPlugin().getDataFolder().toString() + File.separator + "[" + Utils.getDate() + "] Backup.zip";
	private static final String SOURCE_FOLDER = Parkour.getPlugin().getDataFolder().toString();

	public static void backupNow(){
		backupNow(true);
	}

	/**
	 * Create a backup of all the configuration files
	 * A new zip will be generated with the current date containing all parkour files
	 * @param message
	 */
	public static void backupNow(boolean message){
		if (message)
			Utils.log("Beginning backup...");
		fileList = new ArrayList<>();

		generateFileList(new File(SOURCE_FOLDER));
		zipIt();
		if(message)
			Utils.log("Backup completed!");
	}

	private static void zipIt(){
		byte[] buffer = new byte[1024];
		try{
			FileOutputStream fos = new FileOutputStream(OUTPUT_ZIP_FILE);
			ZipOutputStream zos = new ZipOutputStream(fos);

			for(String file : fileList){
				ZipEntry ze= new ZipEntry(file);
				zos.putNextEntry(ze);

				FileInputStream in = 
						new FileInputStream(SOURCE_FOLDER + File.separator + file);

				int len;
				while ((len = in.read(buffer)) > 0) {
					zos.write(buffer, 0, len);
				}
				in.close();
			}
			zos.closeEntry();
			//remember close it
			zos.close();
		}catch(IOException ex){
			ex.printStackTrace();   
		}
	}

	/**
	 * Traverse a directory and get all files,
	 * and add the file into fileList  
	 * @param node file or directory
	 */
	private static void generateFileList(File node){
		//add file only
		if(node.isFile()){
			if (!node.getName().contains(".zip"))
				fileList.add(generateZipEntry(SOURCE_FOLDER + File.separator + node.getName()));
		}

		if(node.isDirectory()){
			String[] subNote = node.list();
			for(String filename : subNote){
				generateFileList(new File(node, filename));
			}
		}

	}

	/**
	 * Format the file path for zip
	 * @param file file path
	 * @return Formatted file path
	 */
	private static String generateZipEntry(String file){
		return file.substring(SOURCE_FOLDER.length()+1, file.length());
	}

}