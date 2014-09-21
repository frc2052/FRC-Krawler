package com.team2052.frckrawler.database.serializers;

import com.activeandroid.serializer.TypeSerializer;

import java.util.ArrayList;

/**
 * @author Adam
 */
public class ObjectArraySerializer extends TypeSerializer {
    @Override
    public Class<?> getDeserializedType() {
        return Object[].class;
    }

    @Override
    public Class<?> getSerializedType() {
        return String.class;
    }

    @Override
    public Object serialize(Object o) {
        String rangeInput = "";
        for (Object obj : (Object[]) o) {
            rangeInput += obj.toString() + ":";
        }
        return rangeInput;
    }

    @Override
    public Object deserialize(Object o) {
        String range = (String) o;
        String currentRangeValString = "";
        ArrayList<Object> rangeArrList = new ArrayList<>();
        for (int character = 0; character < range.length(); character++) {
            if (range.charAt(character) != ':')
                currentRangeValString += range.charAt(character);
            else {
                rangeArrList.add(currentRangeValString);
                currentRangeValString = new String();
            }
        }
        return rangeArrList.toArray();
    }
}
