/**
 * 2013 Foxykeep (http://www.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */
package com.foxykeep.parcelablecodegenerator.generator;

import com.foxykeep.parcelablecodegenerator.model.FieldData;
import com.foxykeep.parcelablecodegenerator.model.Type;
import com.foxykeep.parcelablecodegenerator.utils.FileCache;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public final class ParcelableGenerator {

    private static final String TAB = "    ";

    private static final String IMPORT = "import %1$s.%2$s;\n";
    private static final String EXTENDS = " extends %1$s";

    private ParcelableGenerator() {}

    public static void generate(String dirPath, String classPackage, String className,
            String superClassPackage, String superClassName, boolean isSuperClassParcelable,
            boolean hasSubClasses, boolean isAbstract, ArrayList<FieldData> fieldDataList) {

        Set<String> importSet = new HashSet<String>();
        StringBuilder sbImports = new StringBuilder();
        String extendsString = "";
        StringBuilder sbFields = new StringBuilder();
        StringBuilder sbConstructor = new StringBuilder();
        StringBuilder sbWriteToParcel = new StringBuilder();

        String classModifiers, constructorProtection = null;
        if (hasSubClasses) {
            if (isAbstract) {
                classModifiers = "abstract ";
            } else {
                classModifiers = "";
            }
            constructorProtection = "protected";
        } else {
            classModifiers = "final ";
            constructorProtection = "private";
        }

        String parcelableText, parcelableCreatorText;
        try {
            StringBuilder sb = new StringBuilder();
            String line;
            File file = new File("res/parcelable.txt");
            BufferedReader br = new BufferedReader(new FileReader(file));
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            parcelableText = sb.toString();
            sb.setLength(0);
            file = new File("res/parcelable_creator.txt");
            br = new BufferedReader(new FileReader(file));
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            parcelableCreatorText = sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        if (superClassName != null) {
            extendsString = String.format(EXTENDS, superClassName);

            if (superClassPackage != null) {
                importSet.add(String.format(IMPORT, superClassPackage, superClassName));
            }

            if (isSuperClassParcelable) {
                sbConstructor.append(TAB + TAB + "super(in);\n");
                sbWriteToParcel.append(TAB + TAB + "super.writeToParcel(dest, flags);\n");
            }
        }

        if (fieldDataList.size() > 0) {
            if (isSuperClassParcelable) {
                sbConstructor.append("\n");
                sbWriteToParcel.append("\n");
            }
            sbFields.append("\n");
        }

        for (FieldData fieldData : fieldDataList) {
            Type type = fieldData.type;
            String name = fieldData.name;
            String parcelableClassName = fieldData.parcelableClassName;

            // Check if it's a known type
            if (type.packageName != null) {
                importSet.add(String.format(IMPORT, type.packageName, type.name));
            }
            if (type.needsArrayListImport) {
                importSet.add(String.format(IMPORT, "java.util", "ArrayList"));
            }
            if (fieldData.parcelableClassName != null && fieldData.parcelableClassPackage != null) {
                importSet.add(String.format(IMPORT, fieldData.parcelableClassPackage,
                        parcelableClassName));
            }

            if (fieldData.isGroupStart) {
                sbFields.append("\n");
                sbConstructor.append("\n");
                sbWriteToParcel.append("\n");
            }
            sbFields.append(type.getField(name, fieldData.defaultValue, parcelableClassName));
            sbConstructor.append(type.getConstructorString(name, parcelableClassName));
            sbWriteToParcel.append(type.getWriteToParcelString(name));
        }

        boolean firstImport = true;
        for (String importString : importSet) {
            if (firstImport) {
                sbImports.append("\n");
                firstImport = false;
            }
            sbImports.append(importString);
        }

        String creator = "";
        if (!isAbstract) {
            creator = String.format(parcelableCreatorText, className);
        }

        String output = String.format(parcelableText, classPackage, sbImports.toString(), className,
                extendsString, sbFields.toString(), sbConstructor.toString(),
                sbWriteToParcel.toString(), classModifiers, constructorProtection, creator);

        FileCache.saveFile(createOutputFilePath(dirPath, classPackage, className), output);

    }

    private static String createOutputFilePath(String dirPath, String classPackage,
            String className) {
        String rootPath = dirPath == null ? "ROOT_FOLDER" : dirPath;
        return "output/" + rootPath + "/" + classPackage.replace(".", "/") + "/" + className +
                ".java";
    }
}
