package com.psychananaz.psymod;

import java.nio.file.Path;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.fabricmc.loader.api.FabricLoader;

public final class ModConfig {
    public static final boolean DEFAULT_ENABLED = true;
    public static final boolean DEFAULT_SHOW_NOTIFICATIONS = true;
    public static final boolean DEFAULT_VERBOSE_LOGGING = false;
    public static final boolean DEFAULT_USE_AUTO_TOOL = false;
    public static final boolean DEFAULT_ENABLE_BYPASS = false;
    public static final ModConfig INSTANCE = new ModConfig();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("psymod.toml");

    public boolean enabled = DEFAULT_ENABLED;
    public boolean showNotifications = DEFAULT_SHOW_NOTIFICATIONS;
    public boolean verboseLogging = DEFAULT_VERBOSE_LOGGING;
    public boolean useAutoTool = DEFAULT_USE_AUTO_TOOL;
    public boolean enableBypass = DEFAULT_ENABLE_BYPASS;

    private ModConfig() {
    }

    public void load() {
        try (CommentedFileConfig config = openConfig()) {
            config.load();
            enabled = readBoolean(config, "general.enabled", DEFAULT_ENABLED);
            showNotifications = readBoolean(config, "general.showNotifications", DEFAULT_SHOW_NOTIFICATIONS);
            verboseLogging = readBoolean(config, "general.verboseLogging", DEFAULT_VERBOSE_LOGGING);
            useAutoTool = readBoolean(config, "useAutoTool", DEFAULT_USE_AUTO_TOOL);
            enableBypass = readBoolean(config, "enableBypass", DEFAULT_ENABLE_BYPASS);

            if (!config.contains("general.enabled") || !config.contains("general.showNotifications")
                    || !config.contains("general.verboseLogging") || !config.contains("useAutoTool")
                    || !config.contains("enableBypass")) {
                write(config);
            }
        }
    }

    public void save() {
        try (CommentedFileConfig config = openConfig()) {
            config.load();
            write(config);
        }
    }

    private CommentedFileConfig openConfig() {
        return CommentedFileConfig.builder(CONFIG_PATH).sync().build();
    }

    private void write(CommentedFileConfig config) {
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

        config.set("useAutoTool", useAutoTool);
        if (config.getComment("useAutoTool") == null) {
            config.setComment("useAutoTool", " Automatically switch to the most efficient tool when breaking blocks.");
        }

        config.set("enableBypass", enableBypass);
        if (config.getComment("enableBypass") == null) {
            config.setComment("enableBypass", " Enable bypass functionality.");
        }

        config.save();
    }

    private boolean readBoolean(CommentedFileConfig config, String key, boolean defaultValue) {
        Object value = config.getOrElse(key, defaultValue);
        if (!(value instanceof Boolean setting)) {
            throw new IllegalStateException(key + " must be a boolean in " + CONFIG_PATH);
        }
        return setting;
    }
}
