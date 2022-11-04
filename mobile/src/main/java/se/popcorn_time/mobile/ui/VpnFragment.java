package se.popcorn_time.mobile.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.view.ContextThemeWrapper;
import android.text.Html;
import android.util.TypedValue;

import java.util.Collection;

import se.popcorn_time.api.vpn.VpnClient;
import se.popcorn_time.base.api.AppApi;
import se.popcorn_time.base.prefs.PopcornPrefs;
import se.popcorn_time.base.prefs.Prefs;
import se.popcorn_time.mobile.PopcornApplication;
import se.popcorn_time.mobile.R;
import se.popcorn_time.ui.vpn.IVpnPresenter;
import se.popcorn_time.ui.vpn.IVpnView;
import se.popcorn_time.ui.vpn.VpnPresenter;

public final class VpnFragment extends PreferenceFragmentCompat
        implements IVpnView, Preference.OnPreferenceClickListener {

    private static final String KEY_VPN_CREATE_ACCOUNT = "vpn_create_account";

    private IVpnPresenter presenter;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferenceScreen(getPreferenceManager().createPreferenceScreen(getContext()));
        presenter = new VpnPresenter(((PopcornApplication) getActivity().getApplication()).getVpnUseCase());
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.attach(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.detach(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()) {
            case KEY_VPN_CREATE_ACCOUNT:
                final String[] providers = ((PopcornApplication) getActivity().getApplication()).getConfigUseCase().getConfig().getVpnConfig().getProviders();
                if (providers != null && providers.length > 0) {
                    WebActivity.show(getContext(), providers[0]);
                }
                return true;
        }
        return false;
    }

    @Override
    public void onVpnClients(@NonNull Collection<VpnClient> vpnClients, @NonNull String connectOnStartVpnPackage) {
        final TypedValue themeTypedValue = new TypedValue();
        getContext().getTheme().resolveAttribute(R.attr.preferenceTheme, themeTypedValue, true);
        final ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(getContext(), themeTypedValue.resourceId);

        getPreferenceScreen().removeAll();

        final Preference vpnCreateAccountPreference = new Preference(contextThemeWrapper);
        vpnCreateAccountPreference.setKey(KEY_VPN_CREATE_ACCOUNT);
        vpnCreateAccountPreference.setPersistent(false);
        vpnCreateAccountPreference.setIcon(R.drawable.ic_create_vpn);
        vpnCreateAccountPreference.setTitle(Html.fromHtml("<b>" + getString(R.string.create_account) + "</b>"));
        vpnCreateAccountPreference.setSummary(R.string.click_to_create_vpn_account);
        vpnCreateAccountPreference.setOnPreferenceClickListener(VpnFragment.this);
        getPreferenceScreen().addPreference(vpnCreateAccountPreference);

        for (VpnClient vpnClient : vpnClients) {
            final PreferenceCategory category = new PreferenceCategory(contextThemeWrapper);
            category.setTitle(vpnClient.getName());
            getPreferenceScreen().addPreference(category);

            final Preference connectPreference = new Preference(contextThemeWrapper);
            connectPreference.setPersistent(false);
            connectPreference.setTitle(VpnClient.STATUS_CONNECTED == vpnClient.getStatus() ? R.string.disconnect : R.string.connect);
            connectPreference.setSummary(VpnClient.STATUS_CONNECTED == vpnClient.getStatus() ? R.string.vpn_connected : R.string.vpn_not_connected);
            connectPreference.setOnPreferenceClickListener(new OnConnectClickListener(vpnClient));
            category.addPreference(connectPreference);

            final CheckBoxPreference connectOnStartPreference = new CheckBoxPreference(contextThemeWrapper);
            connectOnStartPreference.setPersistent(false);
            connectOnStartPreference.setTitle(R.string.connect_on_start);
            connectOnStartPreference.setSummaryOn(R.string.enabled);
            connectOnStartPreference.setSummaryOff(R.string.disabled);
            connectOnStartPreference.setChecked(connectOnStartVpnPackage.equals(vpnClient.getPackageName()));
            connectOnStartPreference.setOnPreferenceChangeListener(new OnConnectOnStartChangedListener(vpnClient));
            category.addPreference(connectOnStartPreference);
        }
    }

    private final class OnConnectClickListener implements Preference.OnPreferenceClickListener {

        private final VpnClient vpnClient;

        private OnConnectClickListener(@NonNull VpnClient vpnClient) {
            this.vpnClient = vpnClient;
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            if (VpnClient.STATUS_CONNECTED == vpnClient.getStatus()) {
                AppApi.disconnectVpn(getContext(), vpnClient);
            } else if (VpnClient.STATUS_DISCONNECTED == vpnClient.getStatus()) {
                AppApi.connectVpn(getContext(), vpnClient);
            }
            return true;
        }
    }

    private final class OnConnectOnStartChangedListener implements Preference.OnPreferenceChangeListener {

        private final VpnClient vpnClient;

        private OnConnectOnStartChangedListener(@NonNull VpnClient vpnClient) {
            this.vpnClient = vpnClient;
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            Prefs.getPopcornPrefs().put(PopcornPrefs.ON_START_VPN_PACKAGE, (boolean) newValue ? vpnClient.getPackageName() : "");
            return true;
        }
    }
}
