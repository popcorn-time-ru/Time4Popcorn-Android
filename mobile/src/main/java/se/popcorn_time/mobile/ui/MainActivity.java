package se.popcorn_time.mobile.ui;

import android.Manifest;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import se.popcorn_time.GridSpacingItemDecoration;
import se.popcorn_time.IUseCaseManager;
import se.popcorn_time.UIUtils;
import se.popcorn_time.api.vpn.VpnClient;
import se.popcorn_time.base.IPopcornApplication;
import se.popcorn_time.base.analytics.Analytics;
import se.popcorn_time.base.api.AppApi;
import se.popcorn_time.base.prefs.PopcornPrefs;
import se.popcorn_time.base.prefs.Prefs;
import se.popcorn_time.base.storage.StorageUtil;
import se.popcorn_time.base.torrent.TorrentService;
import se.popcorn_time.base.torrent.client.MainClient;
import se.popcorn_time.base.utils.Logger;
import se.popcorn_time.mobile.BuildConfig;
import se.popcorn_time.mobile.PopcornApplication;
import se.popcorn_time.mobile.R;
import se.popcorn_time.mobile.model.content.ContentProviderView;
import se.popcorn_time.mobile.model.filter.FilterItemView;
import se.popcorn_time.mobile.model.filter.FilterView;
import se.popcorn_time.mobile.ui.adapter.ContentAdapter;
import se.popcorn_time.mobile.ui.dialog.FirebaseMessagingDialog;
import se.popcorn_time.mobile.ui.dialog.ShareDialog;
import se.popcorn_time.model.content.IContentProvider;
import se.popcorn_time.model.content.IContentStatus;
import se.popcorn_time.model.content.IContentUseCase;
import se.popcorn_time.model.filter.IFilter;
import se.popcorn_time.model.filter.IFilterItem;
import se.popcorn_time.model.messaging.IMessagingData;
import se.popcorn_time.model.messaging.IMessagingUseCase;
import se.popcorn_time.model.messaging.PopcornMessagingService;
import se.popcorn_time.model.share.IShareData;
import se.popcorn_time.model.share.IShareUseCase;
import se.popcorn_time.ui.IBrowserView;
import se.popcorn_time.ui.ISystemShareView;
import se.popcorn_time.ui.content.ContentProviderPresenter;
import se.popcorn_time.ui.content.ContentStatusPresenter;
import se.popcorn_time.ui.content.IContentProviderPresenter;
import se.popcorn_time.ui.content.IContentProviderView;
import se.popcorn_time.ui.content.IContentStatusPresenter;
import se.popcorn_time.ui.content.IContentStatusView;
import se.popcorn_time.ui.settings.ISettingsView;
import se.popcorn_time.ui.vpn.IVpnView;
import se.popcorn_time.utils.PermissionsUtils;

public class MainActivity extends UpdateActivity
        implements IContentStatusView,
        IContentProviderView,
        NavigationView.OnNavigationItemSelectedListener,
        TabLayout.OnTabSelectedListener,
        IShareUseCase.Observer,
        IMessagingUseCase.Observer {

    private static final int REQUEST_CODE_EXTERNAL_STORAGE_PERMISSIONS = 1;

    private static final int FILTER_GROUP_ID = 1;

    private static final int INDEX_ITEM_ID = 111;
    private static final int FAVORITES_ITEM_ID = 1;
    private static final int DOWNLOADS_ITEM_ID = 2;
    private static final int SETTINGS_ITEM_ID = 3;
    private static final int VPN_ITEM_ID = 4;

    final int EXIT_DELAY_TIME = 2000;

    private DrawerLayout drawerLayout;
    private ViewGroup drawer;
    private NavigationView navigation;
    private TabLayout tabs;
    private RecyclerView recycler;
    private ProgressBar progress;
    private TextView status;

    private String keywords;

    private final List<ContentProviderView> contentProviderViews = new ArrayList<>();
    private ContentProviderView contentProviderView;

    private ActionBarDrawerToggle drawerToggle;
    private MenuItem searchItem;
    private SearchView searchView;
    private final LoadMoreScrollListener loadMoreScrollListener = new LoadMoreScrollListener();
    private final ContentAdapter contentAdapter = new ContentAdapter();
    private GridLayoutManager gridLayoutManager;

    private boolean doubleBackToExitPressedOnce = false;

    private MainClient mainClient;

    private IContentProviderPresenter contentProviderPresenter;
    private IContentStatusPresenter contentStatusPresenter;

    private int gridSpacingPixels;
    private int contentMargin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UIUtils.transparentStatusBar(this);

        mainClient = new MainClient(getBaseContext());

        setContentView(R.layout.view_main);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer = (ViewGroup) drawerLayout.findViewById(R.id.drawer);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, toolbar, 0, 0);
        drawerLayout.addDrawerListener(drawerToggle);
        navigation = (NavigationView) findViewById(R.id.navigation);
        navigation.setNavigationItemSelectedListener(MainActivity.this);
        final TextView navHeaderVersion = (TextView) navigation.getHeaderView(0).findViewById(R.id.nav_header_version);
        navHeaderVersion.setText(getString(R.string.version) + " " + BuildConfig.VERSION_NAME);
        final Button navHeaderSite = (Button) navigation.getHeaderView(0).findViewById(R.id.nav_header_site);
        navHeaderSite.setText(((IPopcornApplication) getApplication()).getConfigUseCase().getConfig().getSiteUrl());
        navHeaderSite.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onShowView(IBrowserView.class, ((IPopcornApplication) getApplication()).getConfigUseCase().getConfig().getSiteUrl());
            }
        });
        final ImageButton navHeaderFacebook = (ImageButton) navigation.getHeaderView(0).findViewById(R.id.nav_header_facebook);
        navHeaderFacebook.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onShowView(IBrowserView.class, ((IPopcornApplication) getApplication()).getConfigUseCase().getConfig().getFacebookUrl());
            }
        });
        final ImageButton navHeaderTwitter = (ImageButton) navigation.getHeaderView(0).findViewById(R.id.nav_header_twitter);
        navHeaderTwitter.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onShowView(IBrowserView.class, ((IPopcornApplication) getApplication()).getConfigUseCase().getConfig().getTwitterUrl());
            }
        });
        final ImageButton navHeaderYoutube = (ImageButton) navigation.getHeaderView(0).findViewById(R.id.nav_header_youtube);
        navHeaderYoutube.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onShowView(IBrowserView.class, ((IPopcornApplication) getApplication()).getConfigUseCase().getConfig().getYoutubeUrl());
            }
        });
        final Button navShareBtn = (Button) drawer.findViewById(R.id.nav_share_btn);
        navShareBtn.setText(Html.fromHtml("<b>Share</b> Popcorn Time!".toUpperCase()));
        navShareBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final IShareData shareData = ((PopcornApplication) getApplication()).getShareUseCase().getData();
                if (shareData != null) {
                    if (drawerLayout.isDrawerOpen(drawer)) {
                        drawerLayout.closeDrawer(drawer);
                    }
                    onShowView(ISystemShareView.class, shareData.getText());
                }
            }
        });
        tabs = (TabLayout) findViewById(R.id.tabs);

        contentMargin = 2 * getResources().getDimensionPixelSize(R.dimen.action_bar_height);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            final int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
            final int result = resourceId > 0 ? getResources().getDimensionPixelSize(resourceId) : 0;
            if (result > 0) {
                contentMargin += result;
                findViewById(R.id.toolbar_container).setPadding(0, result, 0, 0);
                final ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) tabs.getLayoutParams();
                params.topMargin = params.topMargin + result;
                tabs.setLayoutParams(params);
                navigation.getHeaderView(0).setPadding(0, result, 0, 0);
            }
        }
        final View content = findViewById(R.id.content);
        final ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) content.getLayoutParams();
        params.topMargin = -contentMargin;
        content.setLayoutParams(params);

        recycler = (RecyclerView) findViewById(R.id.recycler);
        gridLayoutManager = new GridLayoutManager(MainActivity.this, getGridSpanCount(getResources().getConfiguration()));
        recycler.setLayoutManager(gridLayoutManager);
        gridSpacingPixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());
        recycler.addItemDecoration(new GridSpacingItemDecoration(gridSpacingPixels) {

            @Override
            protected int getTopOffset(int spanCount, int spanIndex, int position) {
                return (position >= spanCount ? 0 : contentMargin) + super.getTopOffset(spanCount, spanIndex, position);
            }
        });
        recycler.addOnScrollListener(loadMoreScrollListener);
        contentAdapter.setItemSize(getWindowManager().getDefaultDisplay(), gridLayoutManager.getSpanCount(), gridSpacingPixels);
        recycler.setAdapter(contentAdapter);
        progress = (ProgressBar) findViewById(R.id.progress);
        status = (TextView) findViewById(R.id.status);

        ((PopcornApplication) getApplication()).getShareUseCase().onAppBackground(false);

        final IContentUseCase contentUseCase = ((IUseCaseManager) getApplication()).getContentUseCase();
        contentStatusPresenter = new ContentStatusPresenter(contentUseCase);
        contentProviderPresenter = new ContentProviderPresenter(contentUseCase);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
        TorrentService.start(MainActivity.this);
        if (savedInstanceState == null) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && PermissionsUtils.requestPermissions(MainActivity.this, REQUEST_CODE_EXTERNAL_STORAGE_PERMISSIONS, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        return;
                    }
                    start();
                }
            }, 200);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null && intent.getExtras() != null) {
            checkFirebaseExtras(intent.getExtras());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mainClient.bind();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((PopcornApplication) getApplication()).getShareUseCase().subscribe(MainActivity.this);
        ((PopcornApplication) getApplication()).getShareUseCase().onViewResumed(MainActivity.this);
        ((PopcornApplication) getApplication()).getMessagingUseCase().subscribe(MainActivity.this);
        contentStatusPresenter.attach(MainActivity.this);
        contentProviderPresenter.attach(MainActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ((PopcornApplication) getApplication()).getShareUseCase().unsubscribe(MainActivity.this);
        ((PopcornApplication) getApplication()).getMessagingUseCase().unsubscribe(MainActivity.this);
        contentStatusPresenter.detach(MainActivity.this);
        contentProviderPresenter.detach(MainActivity.this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mainClient.unbind();
    }

    @Override
    protected void onDestroy() {
        TorrentService.stop(getBaseContext());
        super.onDestroy();
        AppApi.stop(MainActivity.this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        searchItem = menu.findItem(R.id.main_search);
        MenuItemCompat.setOnActionExpandListener(searchItem, searchExpandListener);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint(getString(R.string.search));
        searchView.setOnQueryTextListener(searchListener);
        onKeywords(keywords);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_share:
                final IShareData shareData = ((PopcornApplication) getApplication()).getShareUseCase().getData();
                if (shareData != null) {
                    onShowView(ISystemShareView.class, shareData.getText());
                    Analytics.event(Analytics.Category.UI, Analytics.Event.MENU_SHARE_BUTTON_IS_CLICKED);
                }
                return true;
            default:
                return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
        gridLayoutManager.setSpanCount(getGridSpanCount(newConfig));
        contentAdapter.setItemSize(getWindowManager().getDefaultDisplay(), gridLayoutManager.getSpanCount(), gridSpacingPixels);
        contentAdapter.notifyItemRangeChanged(0, contentAdapter.getItemCount());
    }

    private int getGridSpanCount(Configuration configuration) {
        return configuration.orientation == Configuration.ORIENTATION_PORTRAIT ? 3 : 5;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(drawer)) {
            drawerLayout.closeDrawer(drawer);
            return;
        }
        if (collapseSearchView()) {
            return;
        }
        if (doubleBackToExitPressedOnce) {
            if (((PopcornApplication) getApplication()).getSettingsUseCase().isDownloadsClearCacheFolder()) {
                mainClient.removeLastOnExit();
                StorageUtil.clearCacheDir();
            }
            mainClient.exitFromApp();
//            finish();
        } else {
            doubleBackToExitPressedOnce = true;
            Toast.makeText(this, R.string.exit_msg, Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, EXIT_DELAY_TIME);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (REQUEST_CODE_EXTERNAL_STORAGE_PERMISSIONS == requestCode) {
            if (PermissionsUtils.isPermissionsGranted(permissions, grantResults)) {
                if (StorageUtil.getCacheDir() == null) {
                    StorageUtil.init(getBaseContext(), ((PopcornApplication) getApplication()).getSettingsUseCase());
                }
            }
            start();
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (FILTER_GROUP_ID == item.getGroupId()) {
            final int filterId = item.getItemId();
            if (INDEX_ITEM_ID == filterId) {
                IndexDialog.showDialog(getSupportFragmentManager(), "index_dialog");
            } else {
                for (IFilter filter : contentProviderView.getFilters()) {
                    if (filterId == ((FilterView) filter).getViewName()) {
                        FilterDialog.showDialog(getSupportFragmentManager(), "filter_dialog", filter);
                        break;
                    }
                }
            }
            return true;
        } else if (Menu.NONE == item.getGroupId()) {
            if (drawerLayout.isDrawerOpen(drawer)) {
                drawerLayout.closeDrawer(drawer);
            }
            switch (item.getItemId()) {
                case FAVORITES_ITEM_ID:
                    startActivity(new Intent(MainActivity.this, FavoritesActivity.class));
                    return true;
                case DOWNLOADS_ITEM_ID:
                    DownloadsActivity.start(MainActivity.this);
                    return true;
                case VPN_ITEM_ID:
                    return onShowView(IVpnView.class);
                case SETTINGS_ITEM_ID:
                    return onShowView(ISettingsView.class);
            }
        }
        return false;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        if (tab.getTag() != null && tab.getTag() instanceof IContentProvider) {
            contentProviderPresenter.setContentProvider((IContentProvider) tab.getTag());
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        contentStatusPresenter.getContent(true);
    }

    @Override
    public void onShowShare(@NonNull IShareData data) {
        ShareDialog.open(getSupportFragmentManager(), "second_launch_dialog");
    }

    @Override
    public void onShowMessagingDialog(@NonNull IMessagingData data) {
        FirebaseMessagingDialog.show(getSupportFragmentManager(), data, "firebase_messaging_dialog");
    }

    @Override
    public void onKeywords(@Nullable String keywords) {
        this.keywords = keywords;
        if (searchItem != null && !TextUtils.isEmpty(keywords)) {
            if (!searchItem.isActionViewExpanded()) {
                searchItem.expandActionView();
            }
            searchView.setQuery(keywords, false);
            searchView.clearFocus();
        }
    }

    @Override
    public void onContentStatus(@NonNull IContentStatus contentStatus) {
        if (contentStatus.isLoading()) {
            loadMoreScrollListener.canLoadMore = false;
            if (contentStatus.getList().isEmpty()) {
                contentAdapter.setContent(null);
                progress.setVisibility(View.VISIBLE);
            } else {
                progress.setVisibility(View.GONE);
            }
            status.setVisibility(View.GONE);
        } else {
            progress.setVisibility(View.GONE);
            if (contentStatus.getError() != null) {
                loadMoreScrollListener.canLoadMore = false;
                if (contentStatus.getList().isEmpty()) {
                    contentAdapter.setContent(null);
                    status.setText(R.string.no_connection);
                    status.setVisibility(View.VISIBLE);
                } else {
                    status.setVisibility(View.GONE);
                }
            } else {
                loadMoreScrollListener.canLoadMore = true;
                if (contentStatus.getList().isEmpty()) {
                    contentAdapter.setContent(null);
                    status.setText(R.string.no_result_found);
                    status.setVisibility(View.VISIBLE);
                } else {
                    contentAdapter.setContent(contentStatus.getList());
                    status.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public void onContentProvider(@NonNull IContentProvider[] contentProviders, @NonNull IContentProvider contentProvider) {
        if (drawerLayout.isDrawerOpen(drawer)) {
            drawerLayout.closeDrawer(drawer);
        }
        contentProviderViews.clear();
        for (IContentProvider cp : contentProviders) {
            if (cp instanceof ContentProviderView) {
                contentProviderViews.add((ContentProviderView) cp);
            }
        }
        contentProviderView = (ContentProviderView) contentProvider;
        onPopulateNavigationView(contentProviderViews, contentProviderView);
        tabs.removeAllTabs();
        tabs.removeOnTabSelectedListener(MainActivity.this);
        for (ContentProviderView cpv : contentProviderViews) {
            if (cpv.getViewCategoryName() == contentProviderView.getViewCategoryName()) {
                tabs.addTab(tabs.newTab().setTag(cpv).setText(cpv.getViewName()), cpv.equals(contentProvider));
            }
        }
        tabs.addOnTabSelectedListener(MainActivity.this);
    }

    @Override
    public void onContentFilterChecked(@NonNull IFilter filter) {
        if (drawerLayout.isDrawerOpen(drawer)) {
            drawerLayout.closeDrawer(drawer);
        }
        if (filter instanceof FilterView) {
            final FilterView filterView = (FilterView) filter;
            final MenuItem menuItem = navigation.getMenu().findItem(filterView.getViewName());
            if (menuItem != null) {
                setFilterMenuItemSubtitle(filterView, menuItem);
            }
        }
    }

    private void start() {
        final String onStartVpnPackage = Prefs.getPopcornPrefs().get(PopcornPrefs.ON_START_VPN_PACKAGE, "");
        if (!TextUtils.isEmpty(onStartVpnPackage)) {
            for (VpnClient client : ((PopcornApplication) getApplication()).getVpnUseCase().getVpnClients()) {
                if (onStartVpnPackage.equals(client.getPackageName()) && VpnClient.STATUS_DISCONNECTED == client.getStatus()) {
                    AppApi.connectVpn(MainActivity.this, client);
                    break;
                }
            }
        }
        ((PopcornApplication) getApplication()).getShareUseCase().checkLaunchShare();
        if (getIntent() != null && getIntent().getExtras() != null) {
            checkFirebaseExtras(getIntent().getExtras());
        }
    }

    private void checkFirebaseExtras(@NonNull Bundle extras) {
        if (extras.containsKey(PopcornMessagingService.KEY_DIALOG)) {
            ((PopcornApplication) getApplication()).getMessagingUseCase().show(PopcornMessagingService.buildDialogData(extras.getString(PopcornMessagingService.KEY_DIALOG), null, null));
        } else if (extras.containsKey(PopcornMessagingService.KEY_DIALOG_HTML)) {
            ((PopcornApplication) getApplication()).getMessagingUseCase().show(PopcornMessagingService.buildDialogHtmlData(extras.getString(PopcornMessagingService.KEY_DIALOG_HTML)));
        }
        for (String s : extras.keySet()) {
            Logger.debug("MainActivity<checkFirebaseExtras> " + s + ": " + extras.get(s));
        }
    }

    private void onPopulateNavigationView(@NonNull List<ContentProviderView> contentProviderViews, @NonNull ContentProviderView contentProviderView) {
        final Menu menu = navigation.getMenu();
        menu.clear();

        final MenuItem indexMenuItem = menu.add(FILTER_GROUP_ID, INDEX_ITEM_ID, Menu.NONE, null);
        indexMenuItem.setActionView(R.layout.item_view_navigation_two_line);
        ((TextView) indexMenuItem.getActionView().findViewById(R.id.icon)).setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_cinema, 0, 0, 0);
        ((TextView) indexMenuItem.getActionView().findViewById(R.id.title)).setText(R.string.index);
        ((TextView) indexMenuItem.getActionView().findViewById(R.id.subtitle)).setText(contentProviderView.getViewCategoryName());

        final IFilter[] filters = contentProviderView.getFilters();
        for (IFilter filter : filters) {
            if (filter instanceof FilterView) {
                final FilterView filterView = (FilterView) filter;
                final MenuItem menuItem = menu.add(FILTER_GROUP_ID, filterView.getViewName(), Menu.NONE, null);
                menuItem.setActionView(R.layout.item_view_navigation_two_line);
                ((TextView) menuItem.getActionView().findViewById(R.id.icon)).setCompoundDrawablesWithIntrinsicBounds(filterView.getViewIcon(), 0, 0, 0);
                ((TextView) menuItem.getActionView().findViewById(R.id.title)).setText(filterView.getViewName());
                setFilterMenuItemSubtitle(filterView, menuItem);
            }
        }

        menu.add(Menu.NONE, FAVORITES_ITEM_ID, Menu.NONE, R.string.favorites).setIcon(R.drawable.ic_heart);
        menu.add(Menu.NONE, DOWNLOADS_ITEM_ID, Menu.NONE, R.string.downloads).setIcon(R.drawable.ic_download).setVisible(PopcornApplication.isFullVersion());
        menu.add(Menu.NONE, SETTINGS_ITEM_ID, Menu.NONE, R.string.settings).setIcon(R.drawable.ic_settings);
        menu.add(Menu.NONE, VPN_ITEM_ID, Menu.NONE, R.string.vpn).setIcon(R.drawable.ic_vpn_option_globe).setVisible(PopcornApplication.isFullVersion());
    }

    private void setFilterMenuItemSubtitle(@NonNull FilterView filterView, @NonNull MenuItem menuItem) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (IFilterItem item : filterView.getItems()) {
            if (filterView.isChecked(item)) {
                if (stringBuilder.length() > 0) {
                    stringBuilder.append(", ");
                }
                if (item instanceof FilterItemView) {
                    stringBuilder.append(getString(((FilterItemView) item).getViewName()));
                } else {
                    stringBuilder.append(item.getValue());
                }
            }
        }
        ((TextView) menuItem.getActionView().findViewById(R.id.subtitle)).setText(stringBuilder.length() > 0 ? stringBuilder.toString() : "None");
    }

     /*
    * Search
    * */

    private boolean collapseSearchView() {
        if (searchItem != null && searchItem.isActionViewExpanded()) {
            searchItem.collapseActionView();
            return true;
        }
        return false;
    }

    private void loadSearchList(String keywords) {
        if (TextUtils.isEmpty(keywords)) {
            return;
        }
        try {
            keywords = URLEncoder.encode(keywords.replaceAll("\\s+", " ").trim(), "UTF-8");
            contentStatusPresenter.setKeywords(keywords);
        } catch (UnsupportedEncodingException e) {
            Logger.error("loadSearchVideoList: " + e.getMessage());
        }
    }

    private MenuItemCompat.OnActionExpandListener searchExpandListener = new MenuItemCompat.OnActionExpandListener() {

        @Override
        public boolean onMenuItemActionExpand(MenuItem item) {
            drawerLayout.closeDrawer(drawer);
            return true;
        }

        @Override
        public boolean onMenuItemActionCollapse(MenuItem item) {
            contentStatusPresenter.setKeywords(null);
            return true;
        }
    };

    private SearchView.OnQueryTextListener searchListener = new SearchView.OnQueryTextListener() {

        @Override
        public boolean onQueryTextSubmit(String s) {
            loadSearchList(s);
            searchView.clearFocus();
            return true;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            return true;
        }
    };

    /*
    *
    * */

    private final class LoadMoreScrollListener extends RecyclerView.OnScrollListener {

        private boolean canLoadMore = true;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (canLoadMore && dy > 0) {
                final GridLayoutManager manager = (GridLayoutManager) recyclerView.getLayoutManager();
                int spanCount = manager.getSpanCount();
                int totalItemCount = manager.getItemCount();
                int visibleItemPosition = manager.findLastVisibleItemPosition() + 1;
                if (totalItemCount - visibleItemPosition <= spanCount) {
                    canLoadMore = false;
                    contentStatusPresenter.getContent(false);
                }
            }
        }
    }
}
