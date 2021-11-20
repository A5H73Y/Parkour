package io.github.a5h73y.parkour.other;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.utility.PluginUtils;
import io.github.a5h73y.parkour.utility.time.DateTimeUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Backup the Parkour Data.
 */
public class PluginBackupUtil {

    private static final String SOURCE_FOLDER = Parkour.getInstance().getDataFolder().toString();
    private static final String BACKUPS_FOLDER = "backups";
    private static final String OUTPUT_ZIP_FILE = SOURCE_FOLDER + File.separator + BACKUPS_FOLDER + File.separator
            + "[" + DateTimeUtils.getDisplayDate() + "] Backup.zip";
    private static List<String> fileList;

    public static void backupNow() {
        backupNow(true);
    }

    /**
     * Create a backup of all the configuration files.
     * A new zip will be generated with the current date containing all parkour files
     *
     * @param message output log message
     */
    public static void backupNow(boolean message) {
        if (message) {
            PluginUtils.log("Beginning backup...");
        }
        fileList = new ArrayList<>();

        generateFileList(new File(SOURCE_FOLDER));
        zipIt();
        if (message) {
            PluginUtils.log("Backup completed!");
        }
    }

    private static void zipIt() {
        byte[] buffer = new byte[1024];
        try {
            File outputFolder = new File(SOURCE_FOLDER + File.separator + BACKUPS_FOLDER);
            if (!outputFolder.exists()) {
                outputFolder.mkdirs();
            }

            FileOutputStream fos = new FileOutputStream(OUTPUT_ZIP_FILE);
            ZipOutputStream zos = new ZipOutputStream(fos);

            for (String file : fileList) {
                ZipEntry ze = new ZipEntry(file);
                zos.putNextEntry(ze);

                FileInputStream in = new FileInputStream(SOURCE_FOLDER + File.separator + file);

                int len;
                while ((len = in.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
                in.close();
            }
            zos.closeEntry();
            //remember close it
            zos.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Traverse a directory and get all files.
     * Add each file into fileList.
     *
     * @param node file or directory
     */
    private static void generateFileList(File node) {
        //add file only
        if (node.isFile() && !node.getName().contains(".zip")) {
            fileList.add(generateZipEntry(node.toString()));
        }

        if (node.isDirectory()) {
            String[] subNote = node.list();
            for (String filename : subNote) {
                generateFileList(new File(node, filename));
            }
        }
    }

    /**
     * Generate the file path for zip.
     *
     * @param file file path
     * @return Formatted file path
     */
    private static String generateZipEntry(String file) {
        return file.substring(SOURCE_FOLDER.length() + 1);
    }

}
