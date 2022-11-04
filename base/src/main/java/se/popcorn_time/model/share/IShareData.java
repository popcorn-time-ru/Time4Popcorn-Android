package se.popcorn_time.model.share;

public interface IShareData {

    String TYPE_SHARE = "share";
    String TYPE_FOCUS_SHARE = "focus_share";
    String TYPE_VIDEO_SHARE = "video_share";

    String getType();

    String getText();

    boolean isShow();

    String getDialogTitle();

    String getDialogText1();

    String getDialogText2();

    String getDialogText3();

    String getDialogText4();

    String getDialogButton();
}