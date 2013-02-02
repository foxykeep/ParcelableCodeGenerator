/**
 * 2013 Foxykeep (http://www.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */
package com.foxykeep.parcelablecodegenerator.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public final class FileCache {

    private static final int BUFFER_SIZE = 2048;
    public static final int ZIP_BUFFER_SIZE = 4 * BUFFER_SIZE;

    private FileCache() {}

    public static void saveFile(String path, String content) {

        try {
            createFileDir(path);

            ByteArrayInputStream bais = new ByteArrayInputStream(content.getBytes());
            FileOutputStream fos = new FileOutputStream(path);
            byte[] buffer = new byte[BUFFER_SIZE];
            int readBytes;
            while ((readBytes = bais.read(buffer)) != -1) {
                fos.write(buffer, 0, readBytes);
            }
            fos.close();
            bais.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    public static void createFileDir(String path) throws IOException {
        File file = new File(path);
        if (file == null || file.exists()) {
            return;
        }

        File parentFile = file.getParentFile();
        if (parentFile != null && !parentFile.exists()) {
            File nomediaFile = new File(parentFile, ".nomedia");
            // Ultra ugly hack to try to evade the Motorola bug. Let's retry after some time
            for (int i = 0; i < 3 && !nomediaFile.mkdirs(); i++) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            nomediaFile.delete();
        }
    }

    public static void copyFile(FileInputStream fromFile, FileOutputStream toFile) throws IOException {
        FileChannel fromChannel = null;
        FileChannel toChannel = null;
        try {
            fromChannel = fromFile.getChannel();
            toChannel = toFile.getChannel();
            fromChannel.transferTo(0, fromChannel.size(), toChannel);
        } finally {
            try {
                if (fromChannel != null) {
                    fromChannel.close();
                }
            } finally {
                if (toChannel != null) {
                    toChannel.close();
                }
            }
        }
    }
}
