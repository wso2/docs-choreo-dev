package com.wso2.choreo.integration.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;



@Slf4j
public class FileUtil {
    private static String readFile(String filePath) {
        Path file = Paths.get(filePath);
        StringBuilder stringBuilder = new StringBuilder();
        try (InputStream in = Files.newInputStream(file);
             BufferedReader reader =
                     new BufferedReader(new InputStreamReader(in))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
        } catch (IOException x) {
           log.error(x.getMessage());
        }
        return stringBuilder.toString();
    }

    public static String readFileEncodedContent(String filePath){
        return Base64.getEncoder().encodeToString(readFile(filePath).getBytes());
    }


}
