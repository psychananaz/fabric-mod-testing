package com.psychananaz.psymod;

import java.nio.file.Path;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.fabricmc.loader.api.FabricLoader;

public final class GlobalConfig {
    public static final boolean DEFAULT_VERBOSE_LOGGING = false;
    public static final GlobalConfig INSTANCE = new GlobalConfig();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("psymod.toml");

    public volatile boolean verboseLogging = DEFAULT_VERBOSE_LOGGING;

    private GlobalConfig() {
    }

    public void load() {
        try (CommentedFileConfig config = CommentedFileConfig.builder(CONFIG_PATH).sync().build()) {
            config.load();
            Object value = config.getOrElse("general.verboseLogging", DEFAULT_VERBOSE_LOGGING);
            if (!(value instanceof Boolean enabled)) {
                throw new IllegalStateException("general.verboseLogging must be a boolean in " + CONFIG_PATH);
            }
            verboseLogging = enabled;
            if (!config.contains("general.verboseLogging")) {
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
        config.set("general.verboseLogging", verboseLogging);
        if (config.getComment("general.verboseLogging") == null) {
            config.setComment("general.verboseLogging", " Log additional diagnostic details for all mod features.");
        }
    }
}
