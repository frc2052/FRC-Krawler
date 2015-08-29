package com.team2052.frckrawler.database.converters;

import com.google.gson.JsonElement;
import com.team2052.frckrawler.tba.JSON;

import de.greenrobot.dao.converter.PropertyConverter;

public class JsonPropertyConverter implements PropertyConverter<JsonElement, String> {

    @Override
    public JsonElement convertToEntityProperty(String databaseValue) {
        return JSON.getParser().parse(databaseValue);
    }

    @Override
    public String convertToDatabaseValue(JsonElement entityProperty) {
        return JSON.getGson().toJson(entityProperty);
    }
}
