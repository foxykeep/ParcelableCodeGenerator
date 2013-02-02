package com.foxykeep.parcelablecodegenerator.utils;

import org.json.JSONObject;

public final class JsonUtils {

    private JsonUtils() {}

    private static final String JSON_NULL = "null";

    public static String getStringFixFalseNull(JSONObject jsonObject, String key) {
        if (jsonObject.has(key)) {
            String value = jsonObject.optString(key);
            return JSON_NULL.equals(value) ? null : value;
        } else {
            return null;
        }
    }
}
