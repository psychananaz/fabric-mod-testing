package com.psychananaz.psymod;

import java.nio.file.Path;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.fabricmc.loader.api.FabricLoader;

public final class GlobalConfig {
    public static final boolean DEFAULT_ENABLED = true;
    public static final boolean DEFAULT_SHOW_NOTIFICATIONS = true;
    public static final boolean DEFAULT_VERBOSE_LOGGING = false;
    public static final GlobalConfig INSTANCE = new GlobalConfig();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("psymod.toml");

    public volatile boolean enabled = DEFAULT_ENABLED;
    public volatile boolean showNotifications = DEFAULT_SHOW_NOTIFICATIONS;
    public volatile boolean verboseLogging = DEFAULT_VERBOSE_LOGGING;

    private GlobalConfig() {
    }

    public void load() {
        try (CommentedFileConfig config = CommentedFileConfig.builder(CONFIG_PATH).sync().build()) {
            config.load();
            boolean modEnabled = readBoolean(config, "general.enabled", DEFAULT_ENABLED);
            boolean notificationsEnabled = readBoolean(config, "general.showNotifications", DEFAULT_SHOW_NOTIFICATIONS);
            Object value = config.getOrElse("general.verboseLogging", DEFAULT_VERBOSE_LOGGING);
            if (!(value instanceof Boolean enabled)) {
                throw new IllegalStateException("general.verboseLogging must be a boolean in " + CONFIG_PATH);
            }
            verboseLogging = enabled;
            this.enabled = modEnabled;
            showNotifications = notificationsEnabled;
            if (!config.contains("general.verboseLogging") || !config.contains("general.enabled")
                    || !config.contains("general.showNotifications")) {
                writeTo(config);
                config.save();
            }
        }
    }

    public void save() {
        try (CommentedFileConfig config = CommentedFileConfig.builder(CONFIG_PATH).sync().build()) {
            config.load();
            writeTo(config);
            config.save();
        }
    }

    void writeTo(CommentedFileConfig config) {
        config.set("general.enabled", enabled);
        if (config.getComment("general.enabled") == null) {
            config.setComment("general.enabled", " Enable mod features without changing their individual settings.");
        }
        config.set("general.showNotifications", showNotifications);
        if (config.getComment("general.showNotifications") == null) {
            config.setComment("general.showNotifications", " Show brief on-screen confirmations when settings are saved.");
        }
        config.set("general.verboseLogging", verboseLogging);
        if (config.getComment("general.verboseLogging") == null) {
            config.setComment("general.verboseLogging", " Log additional diagnostic details for all mod features.");
        }
    }

    private boolean readBoolean(CommentedFileConfig config, String key, boolean defaultValue) {
        Object value = config.getOrElse(key, defaultValue);
        if (!(value instanceof Boolean setting)) {
            throw new IllegalStateException(key + " must be a boolean in " + CONFIG_PATH);
        }
        return setting;
    }
}
