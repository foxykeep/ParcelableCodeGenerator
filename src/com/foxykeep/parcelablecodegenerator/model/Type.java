/**
 * 2013 Foxykeep (http://www.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */
package com.foxykeep.parcelablecodegenerator.model;

public enum Type {
    INT("int", 0, "Int"),
    LONG("long", 0, "Long"),
    BYTE("byte", 0, "Byte"),
    FLOAT("float", 0, "Float"),
    DOUBLE("double", 0, "Double"),
    BOOLEAN("boolean", 1, null),
    STRING("String", 0, "String"),
    BUNDLE("Bundle", 0, "Bundle", "Bundle", "android.os"),
    INT_ARRAY("int[]", 2, "IntArray"),
    LONG_ARRAY("long[]", 2, "LongArray"),
    BYTE_ARRAY("byte[]", 2, "ByteArray"),
    CHAR_ARRAY("char[]", 2, "CharArray"),
    FLOAT_ARRAY("float[]", 2, "FloatArray"),
    DOUBLE_ARRAY("double[]", 2, "DoubleArray"),
    BOOLEAN_ARRAY("boolean[]", 2, "BooleanArray"),
    STRING_ARRAY("String[]", 2, "StringArray"),
    STRING_ARRAY_LIST("ArrayList<String>", 3, "StringArrayList", "StringList", null),
    PARCELABLE("Parcelable", 4, null),
    PARCELABLE_ARRAY("Parcelable[]", 5, "TypedArray"),
    PARCELABLE_ARRAY_LIST("ArrayList<Parcelable>", 6, "TypedArrayList", "TypedList", null),
    PARCELABLE_SPARSE_ARRAY("SparseArray<Parcelable>", 7, null);

    public String name;
    public String readName;
    public String writeName;
    public String packageName;
    public int methodType;
    public boolean needsArrayListImport;
    public boolean needsSparseArrayImport;

    private Type(String name, int methodType, String readWriteName) {
        this(name, methodType, readWriteName, readWriteName);
    }

    private Type(String name, int methodType, String readName, String writeName) {
        this(name, methodType, readName, writeName, null);
    }

    private Type(String name, int methodType, String readName, String writeName,
            String packageName) {
        this.name = name;
        this.readName = readName;
        this.writeName = writeName;
        this.packageName = packageName;
        this.methodType = methodType;
        this.needsArrayListImport = methodType == 3 || methodType == 6;
        this.needsSparseArrayImport = methodType == 7;
    }

    public static Type getTypeFromName(String name) {
        for (Type type : Type.values()) {
            if (name.equals(type.name)) {
                return type;
            }
        }
        return null;
    }

    private static final String TAB = "    ";

    private static final String FIELD = TAB + "public %1$s %2$s%3$s;\n";
    private static final String FIELD_PARCELABLE_ARRAY = TAB + "public %1$s[] %2$s%3$s;" +
            "\n";
    private static final String FIELD_PARCELABLE_ARRAY_LIST = TAB + "public ArrayList<%1$s> " +
            "%2$s%3$s;\n";
    private static final String FIELD_PARCELABLE_SPARSE_ARRAY = TAB + "public SparseArray<%1$s>" +
            " " + "%2$s%3$s;\n";
    private static final String VALUE = " = %1$s";
    private static final String DEFAULT_VALUE_PARCELABLE_SPARSE_ARRAY = " = new " +
            "SparseArray<%1$s>()";

    public String getField(String fieldName, String defaultValue, String parcelableClassName) {
        String value = "";
        if (defaultValue != null) {
            value = String.format(VALUE, defaultValue);
        }

        switch (methodType) {
            case 0: // standard
            case 1: // boolean
            case 2: // Array
            case 3: // Array List
                return String.format(FIELD, name, fieldName, value);
            case 4: // Parcelable
                return String.format(FIELD, parcelableClassName, fieldName, value);
            case 5: // Parcelable Array
                return String.format(FIELD_PARCELABLE_ARRAY, parcelableClassName, fieldName, value);
            case 6: // Parcelable ArrayList
                return String.format(FIELD_PARCELABLE_ARRAY_LIST, parcelableClassName, fieldName,
                        value);
            case 7: // Parcelable SparseArray
                if (value.equals("")) {
                    value = String.format(DEFAULT_VALUE_PARCELABLE_SPARSE_ARRAY,
                            parcelableClassName);
                }
                return String.format(FIELD_PARCELABLE_SPARSE_ARRAY, parcelableClassName, fieldName,
                        value);
        }
        return null;
    }


    private static final String CONSTRUCTOR = TAB + TAB + "%1$s = in.%2$s%3$s();\n";
    private static final String CONSTRUCTOR_BOOLEAN = TAB + TAB + "%1$s = in.readInt() == 1;\n";
    private static final String CONSTRUCTOR_PARCELABLE = TAB + TAB + "%1$s = %2$s.CREATOR" +
            ".createFromParcel(in);\n";
    private static final String CONSTRUCTOR_PARCELABLE_ARRAY = TAB + TAB + "%1$s = in" +
            ".create%2$s(%3$s.CREATOR);\n";
    private static final String CONSTRUCTOR_PARCELABLE_SPARSE_ARRAY = TAB + TAB + "%1$s = new " +
            "SparseArray<%2$s>();\n" + TAB + TAB + "final int %1$sSize = in.readInt();\n" + TAB +
            TAB + "for (int i = 0; i < %1$sSize; i++) {\n" + TAB + TAB + TAB + "%1$s.append(in" +
            ".readInt(), %2$s.CREATOR.createFromParcel(in));\n" + TAB + TAB + "}\n";

    public String getConstructorString(String fieldName, String parcelableClassName) {
        switch (methodType) {
            case 0: // standard
                return String.format(CONSTRUCTOR, fieldName, "read", readName);
            case 1: // boolean
                return String.format(CONSTRUCTOR_BOOLEAN, fieldName);
            case 2: // Array
            case 3: // ArrayList
                return String.format(CONSTRUCTOR, fieldName, "create", readName);
            case 4: // Parcelable
                return String.format(CONSTRUCTOR_PARCELABLE, fieldName, parcelableClassName);
            case 5: // Parcelable Array
            case 6: // Parcelable ArrayList
                return String.format(CONSTRUCTOR_PARCELABLE_ARRAY, fieldName, readName,
                        parcelableClassName);
            case 7: // Parcelable SparseArray
                return String.format(CONSTRUCTOR_PARCELABLE_SPARSE_ARRAY, fieldName,
                        parcelableClassName);
        }
        return null;
    }

    private static final String WRITE_TO_PARCEL = TAB + TAB + "dest.write%1$s(%2$s);\n";
    private static final String WRITE_TO_PARCEL_BOOLEAN = TAB + TAB + "dest.writeInt(%1$s ? 1 : " +
            "0);\n";
    private static final String WRITE_TO_PARCEL_PARCELABLE = TAB + TAB + "%1$s" +
            ".writeToParcel(dest, flags);\n";
    private static final String WRITE_TO_PARCEL_PARCELABLE_ARRAY = TAB + TAB + "dest" +
            ".write%1$s(%2$s, flags);\n";
    private static final String WRITE_TO_PARCEL_SPARSE_ARRAY = TAB + TAB + "final int %1$sSize = " +
            "%1$s.size();\n" + TAB + TAB + "dest.writeInt(%1$sSize);\n" + TAB + TAB +
            "for (int i = 0; i < %1$sSize; i++) {\n" + TAB + TAB + TAB + "dest.writeInt(%1$s" +
            ".keyAt(i));\n" + TAB + TAB + TAB + "%1$s.valueAt(i).writeToParcel(dest, flags);\n" +
            TAB + TAB + "}\n";

    public String getWriteToParcelString(String fieldName) {
        switch (methodType) {
            case 0: // standard
            case 2: // Array
            case 3: // ArrayList
            case 6: // Parcelable ArrayList
                return String.format(WRITE_TO_PARCEL, writeName, fieldName);
            case 1: // boolean
                return String.format(WRITE_TO_PARCEL_BOOLEAN, fieldName);
            case 4: // Parcelable
                return String.format(WRITE_TO_PARCEL_PARCELABLE, fieldName);
            case 5: // Parcelable Array
                return String.format(WRITE_TO_PARCEL_PARCELABLE_ARRAY, writeName, fieldName);
            case 7: // Parcelable SparseArray
                return String.format(WRITE_TO_PARCEL_SPARSE_ARRAY, fieldName);
        }
        return null;
    }
}