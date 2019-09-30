package es.rafaco.inappdevtools.library.view.notifications;

import android.app.NotificationManager;

public enum IadtChannel {

    CHANNEL_PRIORITY("es.rafaco.iadt.priority", "Iadt Priority", NotificationManager.IMPORTANCE_HIGH,
            "Notifications pop up over other screens"),
    CHANNEL_STANDARD("es.rafaco.iadt.standard", "Iadt Standard", NotificationManager.IMPORTANCE_DEFAULT,
            "Non intrusive notifications, they use device settings for sound and vibrate"),
    CHANNEL_SILENT("es.rafaco.iadt.silent", "Iadt Silent", NotificationManager.IMPORTANCE_LOW,
            "Non intrusive and always silent notifications");

    private String id;
    private String name;
    private String description;
    private int priority;

    IadtChannel(String id, String name, int priority, String description) {
        this.id = id;
        this.name = name;
        this.priority = priority;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPriority() {
        return priority;
    }

    public String getDescription() {
        return description;
    }
}
