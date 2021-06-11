package backend.util;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Slf4j
public class DownloadUtil {
    public static void download(@NonNull String url, @NonNull File destFile) throws IOException {
        log.info("downloading from url: {}", url);
        if (!destFile.exists()) {
            destFile.createNewFile();
        }
//        try (BufferedInputStream is = new BufferedInputStream(new URL(url).openStream())) {
//            Files.copy(is, destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
//        }
        ReadableByteChannel webChannel = Channels.newChannel(new URL(url).openStream());
        FileChannel fileChannel = new FileOutputStream(destFile).getChannel();
        fileChannel.transferFrom(webChannel, 0, Long.MAX_VALUE);
    }
}
