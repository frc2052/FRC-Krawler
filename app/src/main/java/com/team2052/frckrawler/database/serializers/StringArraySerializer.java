package com.team2052.frckrawler.database.serializers;

import com.activeandroid.serializer.TypeSerializer;

import java.util.ArrayList;

/**
 * @author Adam
 */
public class StringArraySerializer extends TypeSerializer {
    @Override
    public Class<?> getDeserializedType() {
        return String[].class;
    }

    @Override
    public Class<?> getSerializedType() {
        return String.class;
    }

    @Override
    public String serialize(Object data) {
        if (data == null)
            return new String();

        String returnString = new String();
        for (int i = 0; i < ((String[]) data).length; i++) {
            returnString += ((String[]) data)[i] + ":";
        }
        return returnString;
    }

    @Override
    public String[] deserialize(Object data) {
        String currentValsString = "";
        String valueString = (String) data;
        ArrayList<String> valuesList = new ArrayList<String>();
        for (int character = 0; character < valueString.length(); character++) {
            if (valueString.charAt(character) != ':')
                currentValsString += valueString.charAt(character);
            else {
                valuesList.add(currentValsString);
                currentValsString = "";
            }
        }
        return (String[]) valuesList.toArray();
    }
}
