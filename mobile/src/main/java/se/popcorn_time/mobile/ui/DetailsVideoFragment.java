package se.popcorn_time.mobile.ui;

import android.Manifest;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.AdapterViewFlipper;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.player.dialog.FileChooserDialog;
import com.player.dialog.ListItemEntity;
import com.player.subtitles.SubtitlesRenderer;
import com.player.subtitles.SubtitlesUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import se.popcorn_time.HeaderBehavior;
import se.popcorn_time.IUseCaseManager;
import se.popcorn_time.VibrantUtils;
import se.popcorn_time.base.IPopcornApplication;
import se.popcorn_time.base.database.tables.Downloads;
import se.popcorn_time.base.model.DownloadInfo;
import se.popcorn_time.base.model.WatchInfo;
import se.popcorn_time.base.model.video.Cinema;
import se.popcorn_time.base.model.video.info.Episode;
import se.popcorn_time.base.model.video.info.Season;
import se.popcorn_time.base.model.video.info.Torrent;
import se.popcorn_time.base.model.video.info.TvShowsInfo;
import se.popcorn_time.base.model.video.info.VideoInfo;
import se.popcorn_time.base.prefs.PopcornPrefs;
import se.popcorn_time.base.prefs.Prefs;
import se.popcorn_time.base.storage.StorageUtil;
import se.popcorn_time.base.subtitles.SubtitlesLanguage;
import se.popcorn_time.base.torrent.TorrentState;
import se.popcorn_time.base.torrent.client.DownloadsClient;
import se.popcorn_time.base.utils.Logger;
import se.popcorn_time.base.utils.NetworkUtil;
import se.popcorn_time.mobile.PopcornApplication;
import se.popcorn_time.mobile.R;
import se.popcorn_time.mobile.model.content.ContentRepository;
import se.popcorn_time.mobile.ui.adapter.VideoPosterAdapter;
import se.popcorn_time.mobile.ui.dialog.OptionDialog;
import se.popcorn_time.mobile.ui.dialog.VpnDialog;
import se.popcorn_time.mobile.ui.dialog.WatchDialog;
import se.popcorn_time.mobile.ui.widget.ItemSelectButton;
import se.popcorn_time.model.config.VpnConfig;
import se.popcorn_time.model.details.IDetailsUseCase;
import se.popcorn_time.model.subtitles.Subtitles;
import se.popcorn_time.mvp.IViewRouter;
import se.popcorn_time.ui.IBrowserView;
import se.popcorn_time.ui.details.IDetailsPresenter;
import se.popcorn_time.ui.details.IDetailsView;
import se.popcorn_time.utils.PermissionsUtils;

public abstract class DetailsVideoFragment<T extends VideoInfo, V extends IDetailsView<T>, P extends IDetailsPresenter<T, V>> extends Fragment implements IDetailsView<T> {

    private static final String TAG_VPN_DIALOG = "vpn_dialog";

    private static final float RATING_MULTIPLIER = 5f / 10; // start count / max rating

    private static final int REQUEST_CODE_DOWNLOAD_EXTERNAL_STORAGE_PERMISSION = 100;
    private static final int REQUEST_CODE_WATCH_EXTERNAL_STORAGE_PERMISSION = 101;

    private P detailsPresenter;

    protected DownloadsClient downloadsClient;
    protected IDetailsUseCase detailsUseCase;

    protected ImageView poster;
    protected AdapterViewFlipper backdrops;
    protected RatingBar ratingbar;
    protected TextView ratingbarText;
    protected TextView title;
    protected TextView genre;
    protected TextView year;
    protected TextView duration;
    protected TextView description;

    protected FloatingActionButton playBtn;
    protected FloatingActionButton downloadBtn;

    protected ToggleButton watchedButton;
    protected Button imdbBtn;
    protected AppCompatButton trailerBtn;
    protected TextView additionalDescription;
    protected TextView additionalReleaseDate;
    protected View additionalControls;
    protected ItemSelectButton subtitlesBtn;
    protected ItemSelectButton dubbingBtn;
    protected ItemSelectButton torrentsBtn;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        downloadsClient = new DownloadsClient(getContext());
        final IUseCaseManager useCaseManager = (IUseCaseManager) getActivity().getApplication();
        detailsPresenter = onCreateDetailsPresenter(useCaseManager);
        detailsUseCase = useCaseManager.getDetailsUseCase();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((AppCompatActivity) getActivity()).setSupportActionBar((Toolbar) view.findViewById(R.id.toolbar));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        poster = (ImageView) view.findViewById(R.id.poster);
        backdrops = (AdapterViewFlipper) view.findViewById(R.id.backdrops);
        ratingbar = (RatingBar) view.findViewById(R.id.ratingbar);
        ratingbarText = (TextView) view.findViewById(R.id.ratingbar_text);
        title = (TextView) view.findViewById(R.id.title);
        genre = (TextView) view.findViewById(R.id.genre);
        year = (TextView) view.findViewById(R.id.year);
        duration = (TextView) view.findViewById(R.id.duration);
        description = (TextView) view.findViewById(R.id.description);
        playBtn = (FloatingActionButton) view.findViewById(R.id.play);
        playBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final Torrent torrent = detailsUseCase.getTorrentChoiceProperty().getItem();
                if (torrent != null) {
                    watch(torrent);
                }
            }
        });
        downloadBtn = (FloatingActionButton) view.findViewById(R.id.download);

        final View additionalDetails = view.findViewById(R.id.additional_details);
        ((HeaderBehavior) ((CoordinatorLayout.LayoutParams) view.findViewById(R.id.appbar).getLayoutParams()).getBehavior()).setBottomContent(additionalDetails);

        watchedButton = (ToggleButton) additionalDetails.findViewById(R.id.watched);
        imdbBtn = (Button) additionalDetails.findViewById(R.id.imdb);
        trailerBtn = (AppCompatButton) additionalDetails.findViewById(R.id.trailer);
        additionalDescription = (TextView) additionalDetails.findViewById(R.id.additional_description);
        additionalReleaseDate = (TextView) additionalDetails.findViewById(R.id.additional_release_date);
        additionalControls = additionalDetails.findViewById(R.id.additional_controls);
        subtitlesBtn = (ItemSelectButton) additionalDetails.findViewById(R.id.subtitles);
        subtitlesBtn.setFragmentManager(getActivity().getSupportFragmentManager());
        subtitlesBtn.setPrompt(R.string.subtitles);
        dubbingBtn = (ItemSelectButton) additionalDetails.findViewById(R.id.dubbing);
        dubbingBtn.setFragmentManager(getActivity().getSupportFragmentManager());
        dubbingBtn.setPrompt(R.string.dubbing);
        torrentsBtn = (ItemSelectButton) additionalDetails.findViewById(R.id.torrents);
        torrentsBtn.setFragmentManager(getActivity().getSupportFragmentManager());
        torrentsBtn.setPrompt(R.string.torrents);

        LayerDrawable stars = (LayerDrawable) ratingbar.getProgressDrawable();
        DrawableCompat.setTint(DrawableCompat.wrap(stars.getDrawable(0)), Color.WHITE);
        DrawableCompat.setTint(DrawableCompat.wrap(stars.getDrawable(1)), VibrantUtils.getAccentColor());
        DrawableCompat.setTint(DrawableCompat.wrap(stars.getDrawable(2)), VibrantUtils.getAccentColor());

        final ColorStateList list = ColorStateList.valueOf(VibrantUtils.getAccentColor());
        ratingbarText.setTextColor(list);
        trailerBtn.setSupportBackgroundTintList(list);
        playBtn.setBackgroundTintList(list);
        downloadBtn.setBackgroundTintList(list);
    }

    @Override
    public void onStart() {
        super.onStart();
        downloadsClient.bind();
    }

    public void onResume() {
        super.onResume();
        getDetailsPresenter().attach((V) this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getDetailsPresenter().detach((V) this);
        if (backdrops != null) {
            backdrops.stopFlipping();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        downloadsClient.unbind();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (REQUEST_CODE_DOWNLOAD_EXTERNAL_STORAGE_PERMISSION == requestCode || REQUEST_CODE_WATCH_EXTERNAL_STORAGE_PERMISSION == requestCode) {
            if (PermissionsUtils.isPermissionsGranted(permissions, grantResults)) {
                if (StorageUtil.getCacheDir() == null) {
                    StorageUtil.init(getContext(), ((PopcornApplication) getActivity().getApplication()).getSettingsUseCase());
                }
                if (REQUEST_CODE_DOWNLOAD_EXTERNAL_STORAGE_PERMISSION == requestCode) {
                    download(detailsUseCase.getTorrentChoiceProperty().getItem());
                } else {
                    watch(detailsUseCase.getTorrentChoiceProperty().getItem());
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onVideoInfo(@NonNull T videoInfo) {
        if (Configuration.ORIENTATION_PORTRAIT == getResources().getConfiguration().orientation && poster != null) {
            Picasso.with(getContext()).load(videoInfo.getPosterBig()).placeholder(android.R.color.black).into(poster);
        }
        if (backdrops != null) {
            final String[] urls = videoInfo.getBackdrops() != null ? videoInfo.getBackdrops() : new String[]{videoInfo.getPosterBig()};
            if (backdrops.getAdapter() == null) {
                backdrops.setAdapter(new VideoPosterAdapter(urls));
                backdrops.setFlipInterval(5000);
                backdrops.setInAnimation(getActivity(), android.R.animator.fade_in);
                backdrops.setOutAnimation(getActivity(), android.R.animator.fade_out);
            } else {
                ((VideoPosterAdapter) backdrops.getAdapter()).setUrls(urls);
            }
            if (backdrops.getCount() > 1) {
                backdrops.startFlipping();
            } else {
                backdrops.stopFlipping();
            }
        }

        ratingbar.setRating(RATING_MULTIPLIER * videoInfo.getRating());
        final SpannableString rating = new SpannableString(String.format(Locale.ENGLISH, "%.1f/10", videoInfo.getRating()));
        rating.setSpan(new RelativeSizeSpan(0.67f), 3, rating.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        ratingbarText.setText(rating);
        title.setText(Html.fromHtml(videoInfo.getTitle()));
        final int genreRes = videoInfo.getGenres() != null && videoInfo.getGenres().length > 0 ? getGenreRes(videoInfo.getGenres()[0]) : 0;
        if (genreRes != 0) {
            genre.setText(genreRes);
            genre.setVisibility(View.VISIBLE);
        } else {
            genre.setVisibility(View.GONE);
        }
        year.setText(String.format(Locale.ENGLISH, "%d", videoInfo.getYear()));
        duration.setText(String.format(Locale.ENGLISH, "%dm", videoInfo.getDurationMinutes()));
        description.setText(Html.fromHtml(videoInfo.getDescription()));
        if (!isCinema(videoInfo) || TextUtils.isEmpty(videoInfo.getImdb())) {
            imdbBtn.setVisibility(View.GONE);
        } else {
            imdbBtn.setVisibility(View.VISIBLE);
            imdbBtn.setOnClickListener(new OnImdbClickListener(videoInfo.getImdb()));
        }
        if (TextUtils.isEmpty(videoInfo.getTrailer())) {
            trailerBtn.setVisibility(View.GONE);
        } else {
            trailerBtn.setVisibility(View.VISIBLE);
            trailerBtn.setOnClickListener(new OnTrailerClickListener(videoInfo.getTrailer()));
        }
    }

    @Override
    public void onDubbing(@Nullable String[] languages, int position) {
        if (languages != null && languages.length > 0) {
            final List<ListItemEntity> items = new ArrayList<>();
            for (String language : languages) {
                ListItemEntity.addItemToList(items, new ListItemEntity<String>(language) {

                    @Override
                    public String getName() {
                        switch (getValue()) {
                            case "":
                                return "No dubbing";
                            case "it":
                                return getString(R.string.lang_italian);
                            case "ru":
                                return getString(R.string.lang_russian);
                            case "es":
                                return getString(R.string.lang_spanish);
                            case "fr":
                                return getString(R.string.lang_french);
                            case "pb":
                            case "br":
                                return getString(R.string.lang_brazilian_portuguese);
                            default:
                                return getValue();
                        }
                    }

                    @Override
                    public CharSequence getControlText() {
                        switch (getValue()) {
                            case "":
                                return "Select dubbing";
                            default:
                                return super.getControlText();
                        }
                    }

                    @Override
                    public void onItemChosen() {
                        detailsUseCase.getDubbingChoiceProperty().setPosition(getPosition());
                    }
                });
            }
            dubbingBtn.setItems(items, position);
        }
    }

    @Override
    public void onTorrents(@Nullable Torrent[] torrents, int position) {
        if (torrents != null && torrents.length > 0) {
            final List<ListItemEntity> items = new ArrayList<>();
            for (Torrent torrent : torrents) {
                ListItemEntity.addItemToList(items, new ListItemEntity<Torrent>(torrent) {

                    @Override
                    public String getName() {
                        return String.format(
                                Locale.ENGLISH, "%s (%s)  %s %s,  %s %s",
                                getValue().getQuality(),
                                StorageUtil.getSizeText(getValue().getSize()),
                                getValue().getSeeds(), getString(R.string.seeds),
                                getValue().getPeers(), getString(R.string.peers)
                        );
                    }

                    @Override
                    public void onItemChosen() {
                        detailsUseCase.getTorrentChoiceProperty().setPosition(getPosition());
                    }
                });
            }
            torrentsBtn.setItems(items, position);
            final Torrent t = torrents[position];
            if (t != null) {
                final Season season = detailsUseCase.getSeasonChoiceProperty().getItem();
                final Episode episode = detailsUseCase.getEpisodeChoiceProperty().getItem();
                if (Downloads.isDownloads(getContext(), t, season != null ? season.getNumber() : -1, episode != null ? episode.getNumber() : -1)) {
                    showOpenBtn(t);
                } else {
                    showDownloadBtn(t);
                }
                playBtn.setVisibility(PopcornApplication.isFullVersion() ? View.VISIBLE : View.GONE);
                downloadBtn.setVisibility(PopcornApplication.isFullVersion() ? View.VISIBLE : View.GONE);
                additionalControls.setVisibility(PopcornApplication.isFullVersion() ? View.VISIBLE : View.INVISIBLE);
                return;
            }
        }
        playBtn.setVisibility(View.GONE);
        downloadBtn.setVisibility(View.GONE);
        additionalControls.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onLangSubtitles(@Nullable String[] languages, int position) {
        final List<ListItemEntity> items = new ArrayList<>();
        ListItemEntity.addItemToList(items, new ListItemEntity<String>(SubtitlesUtils.WITHOUT_SUBTITLES) {

            @Override
            public String getName() {
                return getString(R.string.without_subtitle);
            }

            @Override
            public void onItemChosen() {
                detailsUseCase.getCustomSubtitlesProperty().setValue(null);
                detailsUseCase.getLangSubtitlesChoiceProperty().setPosition(-1);
            }
        });
        ListItemEntity.addItemToList(items, new ListItemEntity<String>(SubtitlesUtils.CUSTOM_SUBTITLES) {

            @Override
            public String getName() {
                return getString(R.string.custom_subtitle);
            }

            @Override
            public void onItemChosen() {
                final String tag = "custom_subs_dialog";
                FileChooserDialog dialog = (FileChooserDialog) getFragmentManager().findFragmentByTag(tag);
                if (dialog == null) {
                    dialog = new FileChooserDialog();
                }
                if (!dialog.isAdded()) {
                    dialog.setTitle(R.string.select_subtitle);
                    dialog.setChooserListener(new FileChooserDialog.OnChooserListener() {

                        @Override
                        public void onChooserSelected(File file) {
                            final Subtitles sub = new Subtitles();
                            sub.setUrl(Uri.fromFile(file).toString());
                            detailsUseCase.getCustomSubtitlesProperty().setValue(sub);
                            subtitlesBtn.showSelectedItem(1);
                        }

                        @Override
                        public void onChooserCancel() {
                            detailsUseCase.getCustomSubtitlesProperty().setValue(null);
                        }
                    });
                    dialog.setAcceptExtensions(SubtitlesRenderer.SUPPORTED_EXTENSIONS);
                    dialog.setDenyFolderNames(new String[]{
                            StorageUtil.ROOT_FOLDER_NAME
                    });
                    dialog.show(getFragmentManager(), StorageUtil.getSDCardFolder(getActivity()));
                }
            }
        });
        if (languages != null) {
            for (String lang : languages) {
                ListItemEntity.addItemToList(items, new ListItemEntity<String>(lang) {

                    @Override
                    public String getName() {
                        return SubtitlesLanguage.subtitlesNameToNative(SubtitlesLanguage.subtitlesIsoToName(getValue()));
                    }

                    @Override
                    public void onItemChosen() {
                        detailsUseCase.getCustomSubtitlesProperty().setValue(null);
                        detailsUseCase.getLangSubtitlesChoiceProperty().setPosition(getPosition() - 2);
                    }
                });
            }
        }
        if (detailsUseCase.getCustomSubtitlesProperty().getValue() != null) {
            position = -2;
        }
        subtitlesBtn.setItems(items, position >= 0 ? position + 2 : (position == -2 ? 1 : 0));
    }

    @NonNull
    protected final P getDetailsPresenter() {
        return detailsPresenter;
    }

    private void download(@NonNull Torrent torrent) {
        if (!isReadyToAction(torrent, REQUEST_CODE_DOWNLOAD_EXTERNAL_STORAGE_PERMISSION)) {
            return;
        }
        if (isShowVpnDialog()) {
            final Torrent t = torrent;
            VpnDialog.showDialog(getFragmentManager(), TAG_VPN_DIALOG, new VpnDialog.VpnDialogListener() {

                @Override
                public void onContinue() {
                    addDownload(t);
                }
            });
            return;
        }
        addDownload(torrent);
    }

    private void open(@NonNull Torrent torrent) {
        startActivity(new Intent(getActivity(), DownloadsActivity.class).putExtra(DownloadsActivity.VIDEO_URL, torrent.getUrl()));
    }

    private void watch(@NonNull Torrent torrent) {
        if (!isReadyToAction(torrent, REQUEST_CODE_WATCH_EXTERNAL_STORAGE_PERMISSION)) {
            return;
        }
        if (isShowVpnDialog()) {
            final Torrent t = torrent;
            VpnDialog.showDialog(getFragmentManager(), TAG_VPN_DIALOG, new VpnDialog.VpnDialogListener() {

                @Override
                public void onContinue() {
                    addWatch(t);
                }
            });
            return;
        }
        addWatch(torrent);
    }

    @NonNull
    protected DownloadInfo buildDownloadInfo(@NonNull Torrent torrent) {
        DownloadInfo info = new DownloadInfo();
        info.type = detailsUseCase.getVideoInfoProperty().getValue().getType();
        info.imdb = detailsUseCase.getVideoInfoProperty().getValue().getImdb();
        info.torrentUrl = torrent.getUrl();
        info.torrentMagnet = torrent.getMagnet();
        info.fileName = torrent.getFile();
        info.posterUrl = detailsUseCase.getVideoInfoProperty().getValue().getPoster();
        info.title = detailsUseCase.getVideoInfoProperty().getValue().getTitle();
        info.state = TorrentState.DOWNLOADING;
        info.size = torrent.getSize();
        info.torrentHash = torrent.getHash();
        return info;
    }

    @NonNull
    private WatchInfo buildWatchInfo(@NonNull Torrent torrent) {
        WatchInfo watchInfo = new WatchInfo();
        watchInfo.imdb = detailsUseCase.getVideoInfoProperty().getValue().getImdb();
        watchInfo.type = detailsUseCase.getVideoInfoProperty().getValue().getType();
        watchInfo.watchDir = StorageUtil.getCacheDirPath();
        watchInfo.torrentUrl = torrent.getUrl();
        watchInfo.torrentMagnet = torrent.getMagnet();
        watchInfo.fileName = torrent.getFile();
        watchInfo.posterUrl = detailsUseCase.getVideoInfoProperty().getValue().getPosterBig();
        if (detailsUseCase.getVideoInfoProperty().getValue() instanceof TvShowsInfo) {
            watchInfo.season = detailsUseCase.getSeasonChoiceProperty().getItem().getNumber();
            watchInfo.episode = detailsUseCase.getEpisodeChoiceProperty().getItem().getNumber();
        }
//        watchInfo.subtitles = subtitles;
        return watchInfo;
    }

    private boolean isReadyToAction(@NonNull Torrent torrent, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && PermissionsUtils.requestPermissions(this, requestCode, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            return false;
        }
        if (StorageUtil.getDownloadsDir() == null) {
            Toast.makeText(getActivity(), R.string.cache_folder_not_selected, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!NetworkUtil.hasAvailableConnection(getActivity())) {
            final String tag = "option_dialog";
            OptionDialog fragment = (OptionDialog) getFragmentManager().findFragmentByTag(tag);
            if (fragment == null) {
                fragment = new OptionDialog();
            }
            if (!fragment.isAdded()) {
                fragment.setArguments(OptionDialog.createArguments(getString(R.string.download), getString(R.string.disable_wifi_message)));
                fragment.setListener(new OptionDialog.SimpleOptionListener() {

                    @Override
                    public boolean positiveShow() {
                        return true;
                    }

                    @Override
                    public String positiveButtonText() {
                        return getString(android.R.string.ok);
                    }
                });
                fragment.show(getFragmentManager(), tag);
            }
            return false;
        }
        if (StorageUtil.getAvailableSpaceInBytes(StorageUtil.getDownloadsDirPath()) < torrent.getSize()) {
            final String tag = "option_dialog";
            OptionDialog fragment = (OptionDialog) getFragmentManager().findFragmentByTag(tag);
            if (fragment == null) {
                fragment = new OptionDialog();
            }
            if (!fragment.isAdded()) {
                fragment.setArguments(OptionDialog.createArguments(getString(R.string.application_name), getString(R.string.no_free_space)));
                fragment.setListener(new OptionDialog.SimpleOptionListener() {

                    @Override
                    public boolean positiveShow() {
                        return true;
                    }

                    @Override
                    public String positiveButtonText() {
                        return getString(android.R.string.ok);
                    }

                    @Override
                    public void positiveAction() {
                        StorageUtil.clearCacheDir();
                    }
                });
                fragment.show(getFragmentManager(), tag);
            }
            return false;
        }
        return true;
    }

    private boolean isShowVpnDialog() {
        final PopcornApplication app = (PopcornApplication) getActivity().getApplication();
        final VpnConfig vpnConfig = app.getConfigUseCase().getConfig().getVpnConfig();
        if (app.getVpnUseCase().isVpnConnected()) {
            return false;
        }
        if (vpnConfig.getProviders().length == 0) {
            return false;
        }
        if (vpnConfig.isCheckVpnOptionEnabled()) {
            final Boolean checkVpn = app.getSettingsUseCase().isDownloadsCheckVpn();
            return checkVpn != null ? checkVpn : vpnConfig.isCheckVpnOptionDefault();
        } else {
            return vpnConfig.isAlertEnabled();
        }
    }

    private void addDownload(@NonNull Torrent torrent) {
        final DownloadInfo info = buildDownloadInfo(torrent);
        String uuid = UUID.randomUUID().toString();
        String directoryPath = StorageUtil.getDownloadsDirPath() + "/" + uuid;
        info.directory = new File(directoryPath);
        if (info.directory.exists()) {
            StorageUtil.clearDir(info.directory);
        } else {
            if (!info.directory.mkdirs()) {
                Logger.error("VideoBaseFragment: Cannot crate dir - " + info.directory.getAbsolutePath());
                return;
            }
        }
        downloadsClient.downloadsAdd(info);
        showOpenBtn(torrent);
    }

    private void addWatch(@NonNull Torrent torrent) {
        final String lastTorrent = Prefs.getPopcornPrefs().get(PopcornPrefs.LAST_TORRENT, "");
        if (!TextUtils.isEmpty(lastTorrent) && !(lastTorrent.equals(torrent.getUrl()) || lastTorrent.equals(torrent.getMagnet()))) {
            downloadsClient.removeTorrent(lastTorrent);
            Prefs.getPopcornPrefs().put(PopcornPrefs.LAST_TORRENT, "");
            StorageUtil.clearCacheDir();
        }
        showWatchDialog(buildWatchInfo(torrent));
    }

    private void showWatchDialog(@NonNull WatchInfo watchInfo) {
        final String tag = "watch_view";
        WatchDialog dialog = (WatchDialog) getFragmentManager().findFragmentByTag(tag);
        if (dialog == null) {
            dialog = new WatchDialog();
        }
        if (!dialog.isAdded()) {
            dialog.show(getFragmentManager(), watchInfo, tag);
        }
    }

    private void showDownloadBtn(@NonNull final Torrent torrent) {
        downloadBtn.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_item_download_resume));
        downloadBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                download(torrent);
            }
        });
    }

    private void showOpenBtn(@NonNull final Torrent torrent) {
        downloadBtn.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_folder));
        downloadBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                open(torrent);
            }
        });
    }

    @NonNull
    protected abstract P onCreateDetailsPresenter(@NonNull IUseCaseManager useCaseManager);

    @StringRes
    private int getGenreRes(@NonNull String genre) {
        switch (genre) {
            case ContentRepository.GENRE_NONE:
            case ContentRepository.GENRE_POPULAR:
                return R.string.popular;
            case ContentRepository.GENRE_ACTION:
                return R.string.action;
            case ContentRepository.GENRE_ADVENTURE:
                return R.string.adventure;
            case ContentRepository.GENRE_ANIMATION:
                return R.string.animation;
            case ContentRepository.GENRE_BIOGRAPHY:
                return R.string.biography;
            case ContentRepository.GENRE_COMEDY:
                return R.string.comedy;
            case ContentRepository.GENRE_CRIME:
                return R.string.crime;
            case ContentRepository.GENRE_DOCUMENTARY:
                return R.string.documentary;
            case ContentRepository.GENRE_DRAMA:
                return R.string.drama;
            case ContentRepository.GENRE_FAMILY:
                return R.string.family;
            case ContentRepository.GENRE_FANTASY:
                return R.string.fantasy;
            case ContentRepository.GENRE_FILM_NOIR:
                return R.string.film_noir;
            case ContentRepository.GENRE_HISTORY:
                return R.string.history;
            case ContentRepository.GENRE_HORROR:
                return R.string.horror;
            case ContentRepository.GENRE_MUSIC:
                return R.string.music;
            case ContentRepository.GENRE_MUSICAL:
                return R.string.musical;
            case ContentRepository.GENRE_MYSTERY:
                return R.string.mystery;
            case ContentRepository.GENRE_ROMANCE:
                return R.string.romance;
            case ContentRepository.GENRE_SCI_FI:
                return R.string.sci_fi;
            case ContentRepository.GENRE_SHORT:
                return R.string.short_;
            case ContentRepository.GENRE_SPORT:
                return R.string.sport;
            case ContentRepository.GENRE_THRILLER:
                return R.string.thriller;
            case ContentRepository.GENRE_WAR:
                return R.string.war;
            case ContentRepository.GENRE_WESTERN:
                return R.string.western;
            default:
                return 0;
        }
    }

    private boolean isCinema(@NonNull T videoInfo) {
        return Cinema.TYPE_MOVIES.equals(videoInfo.getType()) || Cinema.TYPE_TV_SHOWS.equals(videoInfo.getType());
    }

    private final class OnImdbClickListener implements View.OnClickListener {

        private final String imdb;

        private OnImdbClickListener(@NonNull String imdb) {
            this.imdb = imdb;
        }

        @Override
        public void onClick(View v) {
            final String url = ((IPopcornApplication) getActivity().getApplication()).getConfigUseCase().getConfig().getImdbUrl();
            if (!TextUtils.isEmpty(url)) {
                ((IViewRouter) getActivity()).onShowView(IBrowserView.class, url + imdb);
            }
        }
    }

    private final class OnTrailerClickListener implements View.OnClickListener {

        private final String trailer;

        private OnTrailerClickListener(@NonNull String trailer) {
            this.trailer = trailer;
        }

        @Override
        public void onClick(View v) {
            TrailerActivity.start(v.getContext(), trailer);
        }
    }
}
