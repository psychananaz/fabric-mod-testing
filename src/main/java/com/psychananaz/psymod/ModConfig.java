package com.psychananaz.psymod;

import java.nio.file.Path;

import com.electronwill.nightconfig.core.file.FileConfig;
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
        try (FileConfig config = openConfig()) {
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
        try (FileConfig config = openConfig()) {
            config.load();
            write(config);
        }
    }

    private FileConfig openConfig() {
        return FileConfig.builder(CONFIG_PATH).sync().build();
    }

    private void write(FileConfig config) {
        config.set("general.enabled", enabled);
        config.set("general.showNotifications", showNotifications);
        config.set("general.verboseLogging", verboseLogging);
        config.set("useAutoTool", useAutoTool);
        config.set("enableBypass", enableBypass);
        config.save();
    }

    private boolean readBoolean(FileConfig config, String key, boolean defaultValue) {
        Object value = config.getOrElse(key, defaultValue);
        if (!(value instanceof Boolean setting)) {
            throw new IllegalStateException(key + " must be a boolean in " + CONFIG_PATH);
        }
        return setting;
    }
}
