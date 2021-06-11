package backend.util;

import com.google.common.io.Files;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnzipUtil {
    /**
     * unzip a zip file (contains only one single file) to destFile
     */
    public static void unzip(String srcFile, String destFile) throws IOException {
        File src = new File(srcFile);
        if (!src.exists()) {
            throw new FileNotFoundException("no file found");
        }
        try(ZipInputStream zis = new ZipInputStream(new FileInputStream(src))){
            ZipEntry zipEntry = zis.getNextEntry();
            File dest = new File(destFile);
            if (zipEntry.isDirectory()) {
                throw new IOException("should not be directory");
            }
            try(FileOutputStream fos = new FileOutputStream(dest)){
                byte[] buffer = new byte[1024 * 8];
                for (int len; (len = zis.read(buffer))>0; ) {
                    fos.write(buffer,0,len);
                }
            }
            zis.closeEntry();
        }
    }

    public static void main(String[] args){
        try {
            UnzipUtil.unzip("chromedriver.zip", "chromedriver");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
