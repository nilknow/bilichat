package backend.util;

import lombok.extern.slf4j.Slf4j;
import tool.Live;

import java.io.FileWriter;
import java.io.IOException;

@Slf4j
public class FileUtil {


    /**
     * write room info to file
     */
    public static void writeToFile(String fileName,String content) {
        try (FileWriter fw = new FileWriter(fileName)) {
            fw.write(content);
        } catch (IOException e) {
            log.error("can't write to file",e);
        }
    }
}
