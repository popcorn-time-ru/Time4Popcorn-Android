package se.popcorn_time.model.config;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

import se.popcorn_time.utils.GsonUtils;

public final class VpnConfigNoticeMapper implements JsonDeserializer<VpnConfig.Notice>, JsonSerializer<VpnConfig.Notice> {

    private static final String KEY_ICON_URL = "iconUrl";
    private static final String KEY_TITLE = "title";
    private static final String KEY_TEXT = "text";

    @Override
    public VpnConfig.Notice deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject jsonNotice = (JsonObject) json;
        final VpnConfig.Notice notice = new VpnConfig.Notice();
        notice.setIconUrl(GsonUtils.getAsString(jsonNotice, KEY_ICON_URL));
        notice.setTitle(GsonUtils.getAsString(jsonNotice, KEY_TITLE));
        notice.setText(GsonUtils.getAsString(jsonNotice, KEY_TEXT));
        return notice;
    }

    @Override
    public JsonElement serialize(VpnConfig.Notice src, Type typeOfSrc, JsonSerializationContext context) {
        final JsonObject json = new JsonObject();
        json.addProperty(KEY_ICON_URL, src.getIconUrl());
        json.addProperty(KEY_TITLE, src.getTitle());
        json.addProperty(KEY_TEXT, src.getText());
        return json;
    }
}
