package com.lab.server.util;

import java.lang.reflect.Type;


import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import com.lab.common.data.Chapter;
import com.lab.common.exception.IncorrectData;

public class ChapterDeserializer implements JsonDeserializer<Chapter> {

    @Override
    public Chapter deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        Chapter chapter = new Chapter();
        try {
            chapter.setName(jsonObject.get("name").getAsString());
            chapter.setParentLegion(jsonObject.get("parentLegion").getAsString());
            chapter.setMarinesCount(jsonObject.get("marinesCount").getAsLong());
            chapter.setWorld(jsonObject.get("world").getAsString());
        } catch (IncorrectData e) {
            return null;
        }
        return chapter;
    }
}
