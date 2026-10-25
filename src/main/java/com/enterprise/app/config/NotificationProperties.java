package com.enterprise.app.config;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

@ConfigMapping(prefix = "notification")
public interface NotificationProperties {
    Attachments attachments();

    interface Attachments {
        @WithDefault("5242880")
        int maxSizeBytes();
    }
}
