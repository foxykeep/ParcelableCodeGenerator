/**
 * 2013 Foxykeep (http://www.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */
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
