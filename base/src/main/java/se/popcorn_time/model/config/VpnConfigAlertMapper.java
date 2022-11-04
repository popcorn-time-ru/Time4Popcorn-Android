package se.popcorn_time.model.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

import se.popcorn_time.utils.GsonUtils;

public final class VpnConfigAlertMapper implements JsonDeserializer<VpnConfig.Alert>, JsonSerializer<VpnConfig.Alert> {

    private static final String KEY_TITLE = "title";
    private static final String KEY_TEXTS = "texts";

    private static final String KEY_TEXT_TEXT = "text";
    private static final String KEY_TEXT_LINES = "lines";

    @Override
    public VpnConfig.Alert deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject jsonAlert = (JsonObject) json;
        if (jsonAlert.has(KEY_TEXTS)) {
            final String title = GsonUtils.getAsString(jsonAlert, KEY_TITLE);
            final JsonArray jsonTexts = jsonAlert.getAsJsonArray(KEY_TEXTS);
            final VpnConfig.Alert.Text[] texts = new VpnConfig.Alert.Text[jsonTexts.size()];
            for (int i = 0; i < jsonTexts.size(); i++) {
                final JsonObject jsonText = jsonTexts.get(i).getAsJsonObject();
                texts[i] = new VpnConfig.Alert.Text(GsonUtils.getAsString(jsonText, KEY_TEXT_TEXT), GsonUtils.getAsInt(jsonText, KEY_TEXT_LINES));
            }
            return new VpnConfig.Alert(title, texts);
        }
        return null;
    }

    @Override
    public JsonElement serialize(VpnConfig.Alert src, Type typeOfSrc, JsonSerializationContext context) {
        final JsonObject json = new JsonObject();
        json.addProperty(KEY_TITLE, src.getTitle());
        final JsonArray jsonTexts = new JsonArray();
        for (VpnConfig.Alert.Text text : src.getTexts()) {
            final JsonObject jsonText = new JsonObject();
            jsonText.addProperty(KEY_TEXT_TEXT, text.text);
            jsonText.addProperty(KEY_TEXT_LINES, text.lines);
            jsonTexts.add(jsonText);
        }
        json.add(KEY_TEXTS, jsonTexts);
        return json;
    }
}
