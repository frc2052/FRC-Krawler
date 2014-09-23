package com.team2052.frckrawler.database.serializers;

import java.util.ArrayList;

/**
 * @author Adam
 */
public class StringArrayDeserializer {
    public static String deserialize(String[] arrStrings){
        String returnString = new String();
        for (int i = 0; i < arrStrings.length; i++) {
            returnString += arrStrings[i] + ":";
        }
        return returnString;
    }

    public static String[] deserialize(String string){
        String currentValsString = "";
        ArrayList<String> valuesList = new ArrayList<>();
        for (int character = 0; character < string.length(); character++) {
            if (string.charAt(character) != ':')
                currentValsString += string.charAt(character);
            else {
                valuesList.add(currentValsString);
                currentValsString = "";
            }
        }
        return (String[]) valuesList.toArray();
    }
}