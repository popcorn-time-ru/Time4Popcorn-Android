package se.popcorn_time.model.config;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import se.popcorn_time.utils.GsonUtils;

public final class VpnConfigMapper implements JsonDeserializer<VpnConfig> {

    private static final String KEY_PROVIDERS = "vpn_providers";
    private static final String KEY_CHECK_VPN_OPTION_ENABLED = "showCheckVpnPopupOption";
    private static final String KEY_CHECK_VPN_OPTION_DEFAULT = "defOptionState";
    private static final String KEY_ALERT_ENABLED = "showVpnAlert";
    private static final String KEY_ALERT = "vpn_alert";
    private static final String KEY_NOTICE = "vpn_notice";

    @Override
    public VpnConfig deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final VpnConfig vpnConfig = new VpnConfig();
        final JsonObject jsonVpnConfig = (JsonObject) json;
        vpnConfig.setProviders(context.<String[]>deserialize(jsonVpnConfig.get(KEY_PROVIDERS), String[].class));
        vpnConfig.setCheckVpnOptionEnabled(GsonUtils.getAsBoolean(jsonVpnConfig, KEY_CHECK_VPN_OPTION_ENABLED, false));
        vpnConfig.setCheckVpnOptionDefault(GsonUtils.getAsBoolean(jsonVpnConfig, KEY_CHECK_VPN_OPTION_DEFAULT, true));
        vpnConfig.setAlertEnabled(GsonUtils.getAsBoolean(jsonVpnConfig, KEY_ALERT_ENABLED, true));
        vpnConfig.setAlert(context.<VpnConfig.Alert>deserialize(jsonVpnConfig.get(KEY_ALERT), VpnConfig.Alert.class));
        vpnConfig.setNotice(context.<VpnConfig.Notice>deserialize(jsonVpnConfig.get(KEY_NOTICE), VpnConfig.Notice.class));
        return vpnConfig;
    }
}
