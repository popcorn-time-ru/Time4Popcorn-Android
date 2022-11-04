package se.popcorn_time.mobile.ui;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceFragmentCompat;

import java.io.File;
import java.util.Locale;

import se.popcorn_time.ActionPreference;
import se.popcorn_time.FolderChooserPreference;
import se.popcorn_time.NumberPickerPreference;
import se.popcorn_time.base.storage.StorageUtil;
import se.popcorn_time.base.subtitles.SubtitlesFontColor;
import se.popcorn_time.base.subtitles.SubtitlesFontSize;
import se.popcorn_time.base.subtitles.SubtitlesLanguage;
import se.popcorn_time.mobile.BuildConfig;
import se.popcorn_time.mobile.Language;
import se.popcorn_time.mobile.PlayerHardwareAcceleration;
import se.popcorn_time.mobile.PopcornApplication;
import se.popcorn_time.mobile.R;
import se.popcorn_time.mobile.StartPage;
import se.popcorn_time.mvp.IViewRouter;
import se.popcorn_time.ui.IBrowserView;
import se.popcorn_time.ui.settings.ISettingsPresenter;
import se.popcorn_time.ui.settings.ISettingsView;
import se.popcorn_time.ui.settings.SettingsPresenter;
import se.popcorn_time.utils.PermissionsUtils;

public final class SettingsFragment extends PreferenceFragmentCompat
        implements IViewRouter, ISettingsView, Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {

    private static final int REQUEST_CODE_EXTERNAL_STORAGE_PERMISSIONS = 101;

    private static final String KEY_LANGUAGE = "language";
    private static final String KEY_START_PAGE = "start_page";
    private static final String KEY_PLAYER_HARDWARE_ACCELERATION = "player_hardware_acceleration";
    private static final String KEY_SUBTITLES_LANGUAGE = "subtitles_language";
    private static final String KEY_SUBTITLES_FONT_SIZE = "subtitles_font_size";
    private static final String KEY_SUBTITLES_FONT_COLOR = "subtitles_font_color";
    private static final String KEY_DOWNLOADS_CHECK_VPN = "downloads_check_vpn";
    private static final String KEY_DOWNLOADS_WIFI_ONLY = "downloads_wifi_only";
    private static final String KEY_DOWNLOADS_CONNECTIONS_LIMIT = "downloads_connections_limit";
    private static final String KEY_DOWNLOADS_DOWNLOAD_SPEED = "downloads_download_speed";
    private static final String KEY_DOWNLOADS_UPLOAD_SPEED = "downloads_upload_speed";
    private static final String KEY_DOWNLOADS_CACHE_FOLDER = "downloads_cache_folder";
    private static final String KEY_DOWNLOADS_CLEAR_CACHE_FOLDER = "downloads_clear_cache_folder";
    private static final String KEY_ABOUT_VISIT_SITE = "about_visit_site";
    private static final String KEY_ABOUT_VISIT_FORUM = "about_visit_forum";
    private static final String KEY_ABOUT_VERSION = "about_version";

    private ListPreference languagePreference;
    private ListPreference startPagePreference;
    private ListPreference playerHardwareAccelerationPreference;
    private ListPreference subtitlesLanguagePreference;
    private ListPreference subtitlesFontSizePreference;
    private ListPreference subtitlesFontColorPreference;
    private CheckBoxPreference downloadsCheckVpnPreference;
    private CheckBoxPreference downloadsWifiOnlyPreference;
    private NumberPickerPreference downloadsConnectionsLimitPreference;
    private ListPreference downloadsDownloadSpeedPreference;
    private ListPreference downloadsUploadSpeedPreference;
    private FolderChooserPreference downloadsCacheFolderPreference;
    private CheckBoxPreference downloadsClearCacheFolderPreference;
    private ActionPreference aboutVisitSitePreference;
    private ActionPreference aboutVisitForumPreference;
    private Preference aboutVersionPreference;

    private ISettingsPresenter presenter;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings);
        languagePreference = (ListPreference) findPreference(KEY_LANGUAGE);
        languagePreference.setOnPreferenceChangeListener(SettingsFragment.this);
        startPagePreference = (ListPreference) findPreference(KEY_START_PAGE);
        startPagePreference.setOnPreferenceChangeListener(SettingsFragment.this);
        playerHardwareAccelerationPreference = (ListPreference) findPreference(KEY_PLAYER_HARDWARE_ACCELERATION);
        playerHardwareAccelerationPreference.setOnPreferenceChangeListener(SettingsFragment.this);
        subtitlesLanguagePreference = (ListPreference) findPreference(KEY_SUBTITLES_LANGUAGE);
        subtitlesLanguagePreference.setOnPreferenceChangeListener(SettingsFragment.this);
        subtitlesFontSizePreference = (ListPreference) findPreference(KEY_SUBTITLES_FONT_SIZE);
        subtitlesFontSizePreference.setOnPreferenceChangeListener(SettingsFragment.this);
        subtitlesFontColorPreference = (ListPreference) findPreference(KEY_SUBTITLES_FONT_COLOR);
        subtitlesFontColorPreference.setOnPreferenceChangeListener(SettingsFragment.this);
        downloadsCheckVpnPreference = (CheckBoxPreference) findPreference(KEY_DOWNLOADS_CHECK_VPN);
        downloadsCheckVpnPreference.setOnPreferenceChangeListener(SettingsFragment.this);
        downloadsWifiOnlyPreference = (CheckBoxPreference) findPreference(KEY_DOWNLOADS_WIFI_ONLY);
        downloadsWifiOnlyPreference.setOnPreferenceChangeListener(SettingsFragment.this);
        downloadsConnectionsLimitPreference = (NumberPickerPreference) findPreference(KEY_DOWNLOADS_CONNECTIONS_LIMIT);
        downloadsConnectionsLimitPreference.setOnPreferenceChangeListener(SettingsFragment.this);
        downloadsDownloadSpeedPreference = (ListPreference) findPreference(KEY_DOWNLOADS_DOWNLOAD_SPEED);
        downloadsDownloadSpeedPreference.setOnPreferenceChangeListener(SettingsFragment.this);
        downloadsUploadSpeedPreference = (ListPreference) findPreference(KEY_DOWNLOADS_UPLOAD_SPEED);
        downloadsUploadSpeedPreference.setOnPreferenceChangeListener(SettingsFragment.this);
        downloadsCacheFolderPreference = (FolderChooserPreference) findPreference(KEY_DOWNLOADS_CACHE_FOLDER);
        downloadsCacheFolderPreference.setOnPreferenceChangeListener(SettingsFragment.this);
        downloadsClearCacheFolderPreference = (CheckBoxPreference) findPreference(KEY_DOWNLOADS_CLEAR_CACHE_FOLDER);
        downloadsClearCacheFolderPreference.setOnPreferenceChangeListener(SettingsFragment.this);
        aboutVisitSitePreference = (ActionPreference) findPreference(KEY_ABOUT_VISIT_SITE);
        aboutVisitSitePreference.setOnPreferenceClickListener(SettingsFragment.this);
        aboutVisitForumPreference = (ActionPreference) findPreference(KEY_ABOUT_VISIT_FORUM);
        aboutVisitForumPreference.setOnPreferenceClickListener(SettingsFragment.this);
        aboutVersionPreference = findPreference(KEY_ABOUT_VERSION);
        presenter = new SettingsPresenter(
                ((PopcornApplication) getActivity().getApplication()).getConfigUseCase(),
                ((PopcornApplication) getActivity().getApplication()).getSettingsUseCase(),
                String.format(Locale.ENGLISH, "%s.%d", BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE)
        );

        setVisibility((PreferenceCategory) findPreference("category_player"), PopcornApplication.isFullVersion());
        setVisibility((PreferenceCategory) findPreference("category_subtitles"), PopcornApplication.isFullVersion());
        setVisibility((PreferenceCategory) findPreference("category_downloads"), PopcornApplication.isFullVersion());
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        if (preference instanceof NumberPickerPreference) {
            final DialogFragment f = NumberPickerPreference.Dialog.newInstance(preference.getKey());
            f.setTargetFragment(this, 0);
            f.show(getFragmentManager(), "android.support.v7.preference.PreferenceFragment.DIALOG");
        } else if (preference instanceof FolderChooserPreference) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && PermissionsUtils.requestPermissions(SettingsFragment.this, REQUEST_CODE_EXTERNAL_STORAGE_PERMISSIONS, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                return;
            }
            final DialogFragment f = FolderChooserPreference.Dialog.newInstance(preference.getKey());
            f.setTargetFragment(this, 0);
            f.show(getFragmentManager(), "android.support.v7.preference.PreferenceFragment.DIALOG");
        } else {
            super.onDisplayPreferenceDialog(preference);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (REQUEST_CODE_EXTERNAL_STORAGE_PERMISSIONS == requestCode) {
            if (PermissionsUtils.isPermissionsGranted(permissions, grantResults)) {
                if (StorageUtil.getCacheDir() == null) {
                    StorageUtil.init(getContext(), ((PopcornApplication) getActivity().getApplication()).getSettingsUseCase());
                }
                onDisplayPreferenceDialog(downloadsCacheFolderPreference);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.attach(SettingsFragment.this);
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.detach(SettingsFragment.this);
    }

    @Override
    public boolean onShowView(@NonNull Class<?> view, Object... args) {
        if (getActivity() instanceof IViewRouter) {
            return ((IViewRouter) getActivity()).onShowView(view, args);
        } else if (getActivity().getApplication() instanceof IViewRouter) {
            return ((IViewRouter) getActivity().getApplication()).onShowView(view, args);
        }
        return false;
    }

    @Override
    public void onLanguages(@NonNull String[] languages) {
        final String[] names = new String[languages.length];
        for (int i = 0; i < languages.length; i++) {
            names[i] = Language.getName(getResources(), languages[i]);
        }
        languagePreference.setEntries(names);
        languagePreference.setEntryValues(languages);
    }

    @Override
    public void onLanguage(@NonNull String language) {
        languagePreference.setValue(language);
        languagePreference.setSummary(Language.getName(getResources(), language));
    }

    @Override
    public void onStartPages(@NonNull Integer[] startPages) {
        final String[] names = new String[startPages.length];
        final String[] values = new String[startPages.length];
        for (int i = 0; i < startPages.length; i++) {
            names[i] = StartPage.getName(getResources(), startPages[i]);
            values[i] = Integer.toString(startPages[i]);
        }
        startPagePreference.setEntries(names);
        startPagePreference.setEntryValues(values);
    }

    @Override
    public void onStartPage(@NonNull Integer startPage) {
        startPagePreference.setValue(Integer.toString(startPage));
        startPagePreference.setSummary(StartPage.getName(getResources(), startPage));
    }

    @Override
    public void onPlayerHardwareAccelerations(@NonNull Integer[] playerHardwareAccelerations) {
        final String[] names = new String[playerHardwareAccelerations.length];
        final String[] values = new String[playerHardwareAccelerations.length];
        for (int i = 0; i < playerHardwareAccelerations.length; i++) {
            names[i] = PlayerHardwareAcceleration.getName(getResources(), playerHardwareAccelerations[i]);
            values[i] = Integer.toString(playerHardwareAccelerations[i]);
        }
        playerHardwareAccelerationPreference.setEntries(names);
        playerHardwareAccelerationPreference.setEntryValues(values);
    }

    @Override
    public void onPlayerHardwareAcceleration(@NonNull Integer playerHardwareAcceleration) {
        playerHardwareAccelerationPreference.setValue(Integer.toString(playerHardwareAcceleration));
        playerHardwareAccelerationPreference.setSummary(PlayerHardwareAcceleration.getName(getResources(), playerHardwareAcceleration));
    }

    @Override
    public void onSubtitlesLanguages(@NonNull String[] subtitlesLanguages) {
        final String[] names = new String[subtitlesLanguages.length];
        for (int i = 0; i < subtitlesLanguages.length; i++) {
            names[i] = SubtitlesLanguage.subtitlesNameToNative(subtitlesLanguages[i]);
        }
        subtitlesLanguagePreference.setEntries(names);
        subtitlesLanguagePreference.setEntryValues(subtitlesLanguages);
    }

    @Override
    public void onSubtitlesLanguage(@NonNull String subtitlesLanguage) {
        subtitlesLanguagePreference.setValue(subtitlesLanguage);
        subtitlesLanguagePreference.setSummary(SubtitlesLanguage.subtitlesNameToNative(subtitlesLanguage));
    }

    @Override
    public void onSubtitlesFontSizes(@NonNull Float[] subtitlesFontSizes) {
        final String[] names = new String[subtitlesFontSizes.length];
        final String[] values = new String[subtitlesFontSizes.length];
        for (int i = 0; i < subtitlesFontSizes.length; i++) {
            names[i] = SubtitlesFontSize.getName(getResources(), subtitlesFontSizes[i]);
            values[i] = Float.toString(subtitlesFontSizes[i]);
        }
        subtitlesFontSizePreference.setEntries(names);
        subtitlesFontSizePreference.setEntryValues(values);
    }

    @Override
    public void onSubtitlesFontSize(@NonNull Float subtitlesFontSize) {
        subtitlesFontSizePreference.setValue(Float.toString(subtitlesFontSize));
        subtitlesFontSizePreference.setSummary(SubtitlesFontSize.getName(getResources(), subtitlesFontSize));
    }

    @Override
    public void onSubtitlesFontColors(@NonNull String[] subtitlesFontColors) {
        final String[] names = new String[subtitlesFontColors.length];
        for (int i = 0; i < subtitlesFontColors.length; i++) {
            names[i] = SubtitlesFontColor.getName(getResources(), subtitlesFontColors[i]);
        }
        subtitlesFontColorPreference.setEntries(names);
        subtitlesFontColorPreference.setEntryValues(subtitlesFontColors);
    }

    @Override
    public void onSubtitlesFontColor(@NonNull String subtitlesFontColor) {
        subtitlesFontColorPreference.setValue(subtitlesFontColor);
        subtitlesFontColorPreference.setSummary(SubtitlesFontColor.getName(getResources(), subtitlesFontColor));
    }

    @Override
    public void onDownloadsCheckVpn(@NonNull Boolean downloadsCheckVpn, @NonNull Boolean enabled) {
        downloadsCheckVpnPreference.setChecked(downloadsCheckVpn);
        downloadsCheckVpnPreference.setVisible(PopcornApplication.isFullVersion() && enabled);
    }

    @Override
    public void onDownloadsWifiOnly(@NonNull Boolean downloadsWifiOnly) {
        downloadsWifiOnlyPreference.setChecked(downloadsWifiOnly);
    }

    @Override
    public void onDownloadsConnectionsLimits(@NonNull Integer minDownloadsConnectionsLimit, @NonNull Integer maxDownloadsConnectionsLimit) {
        downloadsConnectionsLimitPreference.setMinValue(minDownloadsConnectionsLimit);
        downloadsConnectionsLimitPreference.setMaxValue(maxDownloadsConnectionsLimit);
    }

    @Override
    public void onDownloadsConnectionsLimit(@NonNull Integer downloadsConnectionsLimit) {
        downloadsConnectionsLimitPreference.setValue(downloadsConnectionsLimit);
        downloadsConnectionsLimitPreference.setSummary(Integer.toString(downloadsConnectionsLimit));
    }

    @Override
    public void onDownloadsDownloadSpeeds(@NonNull Integer[] downloadsDownloadSpeeds) {
        final String[] names = new String[downloadsDownloadSpeeds.length];
        final String[] values = new String[downloadsDownloadSpeeds.length];
        for (int i = 0; i < downloadsDownloadSpeeds.length; i++) {
            names[i] = getReadableSpeed(downloadsDownloadSpeeds[i]);
            values[i] = Integer.toString(downloadsDownloadSpeeds[i]);
        }
        downloadsDownloadSpeedPreference.setEntries(names);
        downloadsDownloadSpeedPreference.setEntryValues(values);
    }

    @Override
    public void onDownloadsDownloadSpeed(@NonNull Integer downloadsDownloadSpeed) {
        downloadsDownloadSpeedPreference.setValue(Integer.toString(downloadsDownloadSpeed));
        downloadsDownloadSpeedPreference.setSummary(getReadableSpeed(downloadsDownloadSpeed));
    }

    @Override
    public void onDownloadsUploadSpeeds(@NonNull Integer[] downloadsUploadSpeeds) {
        final String[] names = new String[downloadsUploadSpeeds.length];
        final String[] values = new String[downloadsUploadSpeeds.length];
        for (int i = 0; i < downloadsUploadSpeeds.length; i++) {
            names[i] = getReadableSpeed(downloadsUploadSpeeds[i]);
            values[i] = Integer.toString(downloadsUploadSpeeds[i]);
        }
        downloadsUploadSpeedPreference.setEntries(names);
        downloadsUploadSpeedPreference.setEntryValues(values);
    }

    @Override
    public void onDownloadsUploadSpeed(@NonNull Integer downloadsUploadSpeed) {
        downloadsUploadSpeedPreference.setValue(Integer.toString(downloadsUploadSpeed));
        downloadsUploadSpeedPreference.setSummary(getReadableSpeed(downloadsUploadSpeed));
    }

    @Override
    public void onDownloadsCacheFolder(@Nullable File downloadsCacheFolder) {
        downloadsCacheFolderPreference.setFolder(downloadsCacheFolder);
        downloadsCacheFolderPreference.setSummary(
                downloadsCacheFolder == null ? getString(R.string.cache_folder_not_selected) : downloadsCacheFolder.getAbsolutePath()
        );
    }

    @Override
    public void onDownloadsClearCacheFolder(@NonNull Boolean downloadsClearCacheFolder) {
        downloadsClearCacheFolderPreference.setChecked(downloadsClearCacheFolder);
    }

    @Override
    public void onAboutSite(@NonNull String site) {
        aboutVisitSitePreference.setValue(site);
        aboutVisitSitePreference.setSummary(site);
    }

    @Override
    public void onAboutForum(@NonNull String forum) {
        aboutVisitForumPreference.setValue(forum);
        aboutVisitForumPreference.setSummary(forum);
    }

    @Override
    public void onAboutVersion(@NonNull String version) {
        aboutVersionPreference.setSummary(version);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()) {
            case KEY_ABOUT_VISIT_SITE:
            case KEY_ABOUT_VISIT_FORUM:
                onShowView(IBrowserView.class, ((ActionPreference) preference).getValue());
                return true;
        }
        return false;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        switch (preference.getKey()) {
            case KEY_LANGUAGE:
                presenter.setLanguage((String) newValue);
                return true;
            case KEY_START_PAGE:
                presenter.setStartPage(Integer.parseInt((String) newValue));
                return true;
            case KEY_PLAYER_HARDWARE_ACCELERATION:
                presenter.setPlayerHardwareAcceleration(Integer.parseInt((String) newValue));
                return true;
            case KEY_SUBTITLES_LANGUAGE:
                presenter.setSubtitlesLanguage((String) newValue);
                return true;
            case KEY_SUBTITLES_FONT_SIZE:
                presenter.setSubtitlesFontSize(Float.parseFloat((String) newValue));
                return true;
            case KEY_SUBTITLES_FONT_COLOR:
                presenter.setSubtitlesFontColor((String) newValue);
                return true;
            case KEY_DOWNLOADS_CHECK_VPN:
                presenter.setDownloadsCheckVpn((Boolean) newValue);
                return true;
            case KEY_DOWNLOADS_WIFI_ONLY:
                presenter.setDownloadsWifiOnly((Boolean) newValue);
                return true;
            case KEY_DOWNLOADS_CONNECTIONS_LIMIT:
                presenter.setDownloadsConnectionsLimit((Integer) newValue);
                return true;
            case KEY_DOWNLOADS_DOWNLOAD_SPEED:
                presenter.setDownloadsDownloadSpeed(Integer.parseInt((String) newValue));
                return true;
            case KEY_DOWNLOADS_UPLOAD_SPEED:
                presenter.setDownloadsUploadSpeed(Integer.parseInt((String) newValue));
                return true;
            case KEY_DOWNLOADS_CACHE_FOLDER:
                presenter.setDownloadsCacheFolder((File) newValue);
                return true;
            case KEY_DOWNLOADS_CLEAR_CACHE_FOLDER:
                presenter.setDownloadsClearCacheFolder((Boolean) newValue);
                return true;
        }
        return false;
    }

    @NonNull
    private String getReadableSpeed(@NonNull Integer speed) {
        return speed == 0 ? getString(R.string.unlimited) : speed / 1000 + " KB/s";
    }

    private void setVisibility(PreferenceCategory category, boolean visible) {
        category.setVisible(visible);
        for (int i = 0; i < category.getPreferenceCount(); i++) {
            category.getPreference(i).setVisible(visible);
        }
    }
}
