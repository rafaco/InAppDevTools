package es.rafaco.inappdevtools;

import java.util.ArrayList;
import java.util.List;

public class DevToolsConfig {

    public List<String> reportEmails;

    public boolean enabled;
    public boolean strictModeEnabled;
    public boolean crashHandlerEnabled;
    public boolean crashHandlerCallDefaultHandler;
    public boolean anrLoggerEnabled;
    public boolean activityLoggerEnabled;

    public boolean notificationUiEnabled;

    public boolean overlayUiEnabled;
    public boolean overlayUiServiceSticky;
    public boolean overlayUiOverOtherApps;
    public boolean overlayUiIconEnabled;

    public static Builder newBuilder() {
        return new Builder();
    }

    private DevToolsConfig(Builder builder) {
        reportEmails = builder.reportEmails;
        enabled = builder.enabled;
        strictModeEnabled = builder.strictModeEnabled;
        crashHandlerEnabled = builder.crashHandlerEnabled;
        crashHandlerCallDefaultHandler = builder.crashHandlerCallDefaultHandler;
        anrLoggerEnabled = builder.anrLoggerEnabled;
        activityLoggerEnabled = builder.activityLoggerEnabled;
        notificationUiEnabled = builder.notificationUiEnabled;
        overlayUiEnabled = builder.overlayUiEnabled;
        overlayUiServiceSticky = builder.overlayUiServiceSticky;
        overlayUiOverOtherApps = builder.overlayUiOverOtherApps;
        overlayUiIconEnabled = builder.overlayUiIconEnabled;
    }

    public static final class Builder {
        private List<String> reportEmails = new ArrayList<>();
        private boolean enabled = true;
        private boolean strictModeEnabled = false;
        private boolean crashHandlerEnabled = true;
        private boolean crashHandlerCallDefaultHandler = false;
        private boolean anrLoggerEnabled = true;
        private boolean activityLoggerEnabled = true;
        private boolean notificationUiEnabled = true;
        private boolean overlayUiEnabled = true;
        private boolean overlayUiServiceSticky = false;
        private boolean overlayUiOverOtherApps;
        private boolean overlayUiIconEnabled = false;

        private Builder() {
        }

        public Builder reportEmails(List<String> val) {
            reportEmails = val;
            return this;
        }

        public Builder enabled(boolean val) {
            enabled = val;
            return this;
        }

        public Builder strictModeEnabled(boolean val) {
            strictModeEnabled = val;
            return this;
        }

        public Builder crashHandlerEnabled(boolean val) {
            crashHandlerEnabled = val;
            return this;
        }

        public Builder crashHandlerCallDefaultHandler(boolean val) {
            crashHandlerCallDefaultHandler = val;
            return this;
        }

        public Builder anrLoggerEnabled(boolean val) {
            anrLoggerEnabled = val;
            return this;
        }

        public Builder activityLoggerEnabled(boolean val) {
            activityLoggerEnabled = val;
            return this;
        }

        public Builder notificationUiEnabled(boolean val) {
            notificationUiEnabled = val;
            return this;
        }

        public Builder overlayUiEnabled(boolean val) {
            overlayUiEnabled = val;
            return this;
        }

        public Builder overlayUiServiceSticky(boolean val) {
            overlayUiServiceSticky = val;
            return this;
        }

        public Builder overlayUiOverOtherApps(boolean val) {
            overlayUiOverOtherApps = val;
            return this;
        }

        public Builder overlayUiIconEnabled(boolean val) {
            overlayUiIconEnabled = val;
            return this;
        }


        //Actions
        public Builder addEmail(String val) {
            if (reportEmails == null) reportEmails = new ArrayList<>();
            reportEmails.add(val);
            return this;
        }


        public DevToolsConfig build() {
            //TODO: prepare meta configurations
            return new DevToolsConfig(this);
        }
    }
}
