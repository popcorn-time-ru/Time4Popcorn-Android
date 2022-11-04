package se.popcorn_time.model.share;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;
import se.popcorn_time.utils.GsonUtils;

public final class ShareRemoteRepository implements IShareRemoteRepository {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static final String KEY_BODY_ID = "id";
    private static final String KEY_BODY_LANGUAGE = "language";
    private static final String KEY_BODY_LAUNCHES_AFTER_INSTALL = "launchesAfterInstall";
    private static final String KEY_BODY_LAUNCHES_AFTER_LAST_SHARE = "launchesAfterLastShare";
    private static final String KEY_BODY_FOCUSES_AFTER_LAST_SHARE = "focusesAfterLastShare";
    private static final String KEY_BODY_TIME_COUNTER_AFTER_LAST_SHARE = "timeCounterAfterLastShare";
    private static final String KEY_BODY_WAS_SHARED_FROM_DIALOG = "wasSharedFromDialog";
    private static final String KEY_BODY_SHARE_DIALOGS_AFTER_LAUNCH = "shareDialogsAfterLaunch";
    private static final String KEY_BODY_LAUNCHES_AFTER_SHARE_DIALOG = "launchesAfterShareDialog";
    private static final String KEY_BODY_FOCUSES_AFTER_LAUNCH = "focusesAfterLaunch";
    private static final String KEY_BODY_IMDB = "imdb";
    private static final String KEY_BODY_VIDEO_LENGTH = "videoLength";
    private static final String KEY_BODY_WATCHED = "watched";

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(ShareData.class, new ShareDataGsonMapper())
            .registerTypeAdapter(VideoShareData.class, new VideoShareDataGsonMapper())
            .create();

    private final String id;

    private Api api;

    public ShareRemoteRepository(@NonNull String id) {
        this.id = id;
    }

    @Override
    public void setUrl(@NonNull String url) {
        if (TextUtils.isEmpty(url)) {
            this.api = new Api() {
                @Override
                public Observable<ShareData> getShareData(@Body RequestBody body) {
                    return Observable.just(new ShareData());
                }

                @Override
                public Observable<ShareData> getFocusShareData(@Body RequestBody body) {
                    return Observable.just(new ShareData());
                }

                @Override
                public Observable<VideoShareData> getVideoShareData(@Body RequestBody body) {
                    return Observable.just(new VideoShareData());
                }
            };
        } else {
            this.api = new Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .baseUrl(url)
                    .build()
                    .create(Api.class);
        }
    }

    @NonNull
    @Override
    public Observable<IShareData> getShare(int launchesAfterInstall,
                                           int launchesAfterLastShare,
                                           long timeCounterAfterLastShare,
                                           boolean wasSharedFromDialog,
                                           int launchesAfterShareDialog) {
        final JsonObject body = new JsonObject();
        body.addProperty(KEY_BODY_ID, id);
        body.addProperty(KEY_BODY_LANGUAGE, Locale.getDefault().getLanguage());
        body.addProperty(KEY_BODY_LAUNCHES_AFTER_INSTALL, launchesAfterInstall);
        body.addProperty(KEY_BODY_LAUNCHES_AFTER_LAST_SHARE, launchesAfterLastShare);
        body.addProperty(KEY_BODY_TIME_COUNTER_AFTER_LAST_SHARE, timeCounterAfterLastShare);
        body.addProperty(KEY_BODY_WAS_SHARED_FROM_DIALOG, wasSharedFromDialog);
        body.addProperty(KEY_BODY_LAUNCHES_AFTER_SHARE_DIALOG, launchesAfterShareDialog);
        return api.getShareData(RequestBody.create(JSON, body.toString()))
                .observeOn(AndroidSchedulers.mainThread())
                .map(new ShareDataRxMapper(IShareData.TYPE_SHARE));
    }

    @NonNull
    @Override
    public Observable<IShareData> getFocusShare(int launchesAfterInstall,
                                                int focusesAfterLastShare,
                                                long timeCounterAfterLastShare,
                                                boolean wasSharedFromDialog,
                                                int shareDialogsAfterLaunch,
                                                int launchesAfterShareDialog,
                                                int focusesAfterLaunch) {
        final JsonObject body = new JsonObject();
        body.addProperty(KEY_BODY_ID, id);
        body.addProperty(KEY_BODY_LANGUAGE, Locale.getDefault().getLanguage());
        body.addProperty(KEY_BODY_LAUNCHES_AFTER_INSTALL, launchesAfterInstall);
        body.addProperty(KEY_BODY_FOCUSES_AFTER_LAST_SHARE, focusesAfterLastShare);
        body.addProperty(KEY_BODY_TIME_COUNTER_AFTER_LAST_SHARE, timeCounterAfterLastShare);
        body.addProperty(KEY_BODY_WAS_SHARED_FROM_DIALOG, wasSharedFromDialog);
        body.addProperty(KEY_BODY_SHARE_DIALOGS_AFTER_LAUNCH, shareDialogsAfterLaunch);
        body.addProperty(KEY_BODY_LAUNCHES_AFTER_SHARE_DIALOG, launchesAfterShareDialog);
        body.addProperty(KEY_BODY_FOCUSES_AFTER_LAUNCH, focusesAfterLaunch);
        return api.getFocusShareData(RequestBody.create(JSON, body.toString()))
                .observeOn(AndroidSchedulers.mainThread())
                .map(new ShareDataRxMapper(IShareData.TYPE_FOCUS_SHARE));
    }

    @NonNull
    @Override
    public Observable<IVideoShareData> getVideoShare(@NonNull String imdb) {
        final JsonObject body = new JsonObject();
        body.addProperty(KEY_BODY_ID, id);
        body.addProperty(KEY_BODY_LANGUAGE, Locale.getDefault().getLanguage());
        body.addProperty(KEY_BODY_IMDB, imdb);
//        body.addProperty(KEY_BODY_VIDEO_LENGTH, length);
//        body.addProperty(KEY_BODY_WATCHED, position);
        return api.getVideoShareData(RequestBody.create(JSON, body.toString()))
                .observeOn(AndroidSchedulers.mainThread())
                .map(new VideoShareDataRxMapper(imdb));
    }

    private interface Api {

        @POST("/")
        Observable<ShareData> getShareData(@Body RequestBody body);

        @POST("/focus.php")
        Observable<ShareData> getFocusShareData(@Body RequestBody body);

        @POST("/imdb")
        Observable<VideoShareData> getVideoShareData(@Body RequestBody body);
    }

    private static class ShareDataGsonMapper<T extends ShareData> implements JsonDeserializer<T> {

        private static final String KEY_TEXT = "shareText";
        private static final String KEY_SHOW = "showShare";
        private static final String KEY_DIALOG = "dialogText";
        private static final String KEY_DIALOG_TITLE = "title";
        private static final String KEY_DIALOG_TEXT_1 = "text1";
        private static final String KEY_DIALOG_TEXT_2 = "text2";
        private static final String KEY_DIALOG_TEXT_3 = "text3";
        private static final String KEY_DIALOG_TEXT_4 = "text4";
        private static final String KEY_DIALOG_BUTTON = "buttonText";

        @Override
        public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            final JsonObject jsonShareData = (JsonObject) json;
            final T shareData;
            try {
                shareData = ((Class<T>) typeOfT).newInstance();
            } catch (Exception e) {
                throw new JsonParseException(e);
            }
            shareData.setText(GsonUtils.getAsString(jsonShareData, KEY_TEXT));
            shareData.setShow(GsonUtils.getAsBoolean(jsonShareData, KEY_SHOW));
            final JsonObject jsonDialog = jsonShareData.getAsJsonObject(KEY_DIALOG);
            shareData.setDialogTitle(GsonUtils.getAsString(jsonDialog, KEY_DIALOG_TITLE));
            shareData.setDialogText1(GsonUtils.getAsString(jsonDialog, KEY_DIALOG_TEXT_1));
            shareData.setDialogText2(GsonUtils.getAsString(jsonDialog, KEY_DIALOG_TEXT_2));
            shareData.setDialogText3(GsonUtils.getAsString(jsonDialog, KEY_DIALOG_TEXT_3));
            shareData.setDialogText4(GsonUtils.getAsString(jsonDialog, KEY_DIALOG_TEXT_4));
            shareData.setDialogButton(GsonUtils.getAsString(jsonDialog, KEY_DIALOG_BUTTON));
            return shareData;
        }
    }

    private static class VideoShareDataGsonMapper extends ShareDataGsonMapper<VideoShareData> {

        private static final String KEY_RATE = "shareRate";

        @Override
        public VideoShareData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            final JsonObject jsonVideoShareData = (JsonObject) json;
            final VideoShareData videoShareData = super.deserialize(json, typeOfT, context);
            videoShareData.setRate(GsonUtils.getAsDouble(jsonVideoShareData, KEY_RATE));
            return videoShareData;
        }
    }

    private static class ShareDataRxMapper implements Function<ShareData, IShareData> {

        private final String type;

        private ShareDataRxMapper(@NonNull String type) {
            this.type = type;
        }

        @Override
        public IShareData apply(@io.reactivex.annotations.NonNull ShareData shareData) throws Exception {
            shareData.setType(type);
            return shareData;
        }
    }

    private static class VideoShareDataRxMapper implements Function<VideoShareData, IVideoShareData> {

        private final String imdb;

        private VideoShareDataRxMapper(@NonNull String imdb) {
            this.imdb = imdb;
        }

        @Override
        public IVideoShareData apply(@io.reactivex.annotations.NonNull VideoShareData videoShareData) throws Exception {
            videoShareData.setType(IShareData.TYPE_VIDEO_SHARE);
            videoShareData.setImdb(imdb);
            return videoShareData;
        }
    }
}
