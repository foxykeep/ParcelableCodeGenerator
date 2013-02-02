/**
 * 2013 Foxykeep (http://www.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */
package com.foxykeep.parcelablecodegenerator.model;

import com.foxykeep.parcelablecodegenerator.utils.JsonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public final class FieldData {

    public String name;
    public Type type;
    public String defaultValue;

    public String parcelableClassName;
    public String parcelableClassPackage;

    public boolean isGroupStart;

    public FieldData(JSONObject json) throws JSONException {
        name = json.getString("name");
        String typeName = json.getString("type");
        type = Type.getTypeFromName(typeName);
        if (type == null) {
            throw new IllegalArgumentException(
                    "The field '" + name + "' is of unknown type '" + typeName + "'.");
        }

        defaultValue = JsonUtils.getStringFixFalseNull(json, "defaultValue");

        parcelableClassName = JsonUtils.getStringFixFalseNull(json, "parcelableClassName");
        parcelableClassPackage = JsonUtils.getStringFixFalseNull(json, "parcelableClassPackage");

        isGroupStart = json.optBoolean("isGroupStart", false);
    }

    public static ArrayList<FieldData> getFieldsData(JSONArray jsonArray) throws JSONException {
        ArrayList<FieldData> fieldDataList = new ArrayList<FieldData>();

        final int jsonArrayLength = jsonArray.length();
        for (int i = 0; i < jsonArrayLength; i++) {
            fieldDataList.add(new FieldData(jsonArray.getJSONObject(i)));
        }

        return fieldDataList;
    }
}
