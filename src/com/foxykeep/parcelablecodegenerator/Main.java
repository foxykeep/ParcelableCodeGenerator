/**
 * 2013 Foxykeep (http://www.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */
package com.foxykeep.parcelablecodegenerator;

import com.foxykeep.parcelablecodegenerator.generator.ParcelableGenerator;
import com.foxykeep.parcelablecodegenerator.model.FieldData;
import com.foxykeep.parcelablecodegenerator.utils.JsonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public final class Main {

    public static void main(String[] args) {

        File fileInputDir = new File("input");
        if (!fileInputDir.exists() || !fileInputDir.isDirectory()) {
            return;
        }

        ArrayList<FileInfo> fileInfoList = new ArrayList<FileInfo>();
        findJsonFiles(fileInputDir, null, fileInfoList);

        StringBuilder sb = new StringBuilder();

        // For each file in the input folder
        for (FileInfo fileInfo : fileInfoList) {
            String fileName = fileInfo.file.getName();
            System.out.println("Generating code for " + fileName);

            char[] buffer = new char[2048];
            sb.setLength(0);
            Reader in;
            try {
                in = new InputStreamReader(new FileInputStream(fileInfo.file), "UTF-8");
                int read;
                do {
                    read = in.read(buffer, 0, buffer.length);
                    if (read != -1) {
                        sb.append(buffer, 0, read);
                    }
                } while (read >= 0);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return;
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            String content = sb.toString();
            if (content.length() == 0) {
                System.out.println("file is empty.");
                return;
            }

            try {
                JSONObject root = new JSONObject(content);

                // Classes generation
                String classPackage, className, superClassPackage, superClassName;
                boolean isSuperClassParcelable, hasSubClasses, isAbstract;

                classPackage = root.getString("package");
                className = root.getString("name");
                superClassPackage = JsonUtils.getStringFixFalseNull(root, "superClassPackage");
                superClassName = JsonUtils.getStringFixFalseNull(root, "superClassName");
                isSuperClassParcelable = root.optBoolean("isSuperClassParcelable");
                hasSubClasses = root.optBoolean("hasSubClasses");
                if (hasSubClasses) {
                    isAbstract = root.optBoolean("isAbstract");
                } else {
                    isAbstract = false;
                }

                JSONArray fieldJsonArray = root.optJSONArray("fields");
                ArrayList<FieldData> fieldDataList;
                if (fieldJsonArray != null) {
                    fieldDataList = FieldData.getFieldsData(fieldJsonArray);
                } else {
                    fieldDataList = new ArrayList<FieldData>();
                }

                // Parcelable generation
                ParcelableGenerator.generate(fileInfo.dirPath, classPackage, className,
                        superClassPackage, superClassName, isSuperClassParcelable, hasSubClasses,
                        isAbstract, fieldDataList);
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
        }

    }

    private static void findJsonFiles(File fileInputDir, String parentPath,
            ArrayList<FileInfo> fileInfoList) {
        if (!fileInputDir.isDirectory()) {
            throw new IllegalArgumentException("fileInputDir must be a directory");
        }

        if (fileInfoList == null) {
            throw new NullPointerException("foundFileList cannot be null");
        }

        for (File file : fileInputDir.listFiles()) {
            if (file.isDirectory()) {
                String newParentPath;
                if (parentPath == null) {
                    newParentPath = file.getName();
                } else {
                    newParentPath = parentPath + "/" + file.getName();
                }
                findJsonFiles(file, newParentPath, fileInfoList);
            } else if (file.getName().matches(".+\\.json")) {
                fileInfoList.add(new FileInfo(file, parentPath));
            }
        }
    }

    private static final class FileInfo {
        File file;
        String dirPath;

        public FileInfo(File file, String dirPath) {
            this.file = file;
            this.dirPath = dirPath;
        }
    }
}
