package se.popcorn_time.model.config;

public final class VpnConfig {

    private String[] providers;
    private boolean checkVpnOptionEnabled;
    private boolean checkVpnOptionDefault;
    private boolean alertEnabled;
    private Alert alert;
    private Notice notice;

    public String[] getProviders() {
        return providers;
    }

    public void setProviders(String[] providers) {
        this.providers = providers;
    }

    public boolean isCheckVpnOptionEnabled() {
        return checkVpnOptionEnabled;
    }

    public void setCheckVpnOptionEnabled(boolean checkVpnOptionEnabled) {
        this.checkVpnOptionEnabled = checkVpnOptionEnabled;
    }

    public boolean isCheckVpnOptionDefault() {
        return checkVpnOptionDefault;
    }

    public void setCheckVpnOptionDefault(boolean checkVpnOptionDefault) {
        this.checkVpnOptionDefault = checkVpnOptionDefault;
    }

    public boolean isAlertEnabled() {
        return alertEnabled;
    }

    public void setAlertEnabled(boolean alertEnabled) {
        this.alertEnabled = alertEnabled;
    }

    public Alert getAlert() {
        return alert;
    }

    public void setAlert(Alert alert) {
        this.alert = alert;
    }

    public Notice getNotice() {
        return notice;
    }

    public void setNotice(Notice notice) {
        this.notice = notice;
    }

    public static final class Alert {

        private String title;
        private Text[] texts;

        public Alert(String title, Text[] texts) {
            this.title = title;
            this.texts = texts;
        }

        public Alert() {
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Text[] getTexts() {
            return texts;
        }

        public void setTexts(Text[] texts) {
            this.texts = texts;
        }

        public static final class Text {

            public String text;
            public int lines;

            public Text(String text, int lines) {
                this.text = text;
                this.lines = lines;
            }
        }
    }

    public static final class Notice {

        private String iconUrl;
        private String title;
        private String text;

        public Notice(String title, String text) {
            this.title = title;
            this.text = text;
        }

        public Notice() {
        }

        public String getIconUrl() {
            return iconUrl;
        }

        public void setIconUrl(String iconUrl) {
            this.iconUrl = iconUrl;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}