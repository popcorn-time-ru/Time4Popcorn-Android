package se.popcorn_time.model.share;

public class ShareData implements IShareData {

    private String type;
    private String text;
    private boolean show;
    private String dialogTitle;
    private String dialogText1;
    private String dialogText2;
    private String dialogText3;
    private String dialogText4;
    private String dialogButton;

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public boolean isShow() {
        return show;
    }

    @Override
    public String getDialogTitle() {
        return dialogTitle;
    }

    @Override
    public String getDialogText1() {
        return dialogText1;
    }

    @Override
    public String getDialogText2() {
        return dialogText2;
    }

    @Override
    public String getDialogText3() {
        return dialogText3;
    }

    @Override
    public String getDialogText4() {
        return dialogText4;
    }

    @Override
    public String getDialogButton() {
        return dialogButton;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public void setDialogTitle(String dialogTitle) {
        this.dialogTitle = dialogTitle;
    }

    public void setDialogText1(String dialogText1) {
        this.dialogText1 = dialogText1;
    }

    public void setDialogText2(String dialogText2) {
        this.dialogText2 = dialogText2;
    }

    public void setDialogText3(String dialogText3) {
        this.dialogText3 = dialogText3;
    }

    public void setDialogText4(String dialogText4) {
        this.dialogText4 = dialogText4;
    }

    public void setDialogButton(String dialogButton) {
        this.dialogButton = dialogButton;
    }
}
