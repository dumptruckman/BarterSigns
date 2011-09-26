package com.dumptruckman.bartersigns.config;

/**
 * @author dumptruckman
 */
public enum ConfigPath {
    LANGUAGE_FILE("settings.languagefile", "english.yml"),
    DATA_SAVE("settings.datasavetimer", 15),
    USE_PERMS("settings.usepermissions", true),

    SIGN_STORAGE_LIMIT("signs.storagelimit", 0),
    SIGN_INDESTRUCTIBLE("signs.indestructible", true),
    SIGN_ENFORCE_MAX_STACK_SIZE("signs.enforcemaxstacksize", true),
    SIGN_USE_NUM_STACKS("signs.storagelimitisnumberofstacks", true),
    SIGN_DROPS_ITEMS("signs.dropitemsonbreak", true),

    PLUGINS_OVERRIDE("plugins.override", true),
    ;

    private String path;
    private Object def;

    ConfigPath(String path, Object def) {
        this.path = path;
        this.def = def;
    }

    public String getPath() {
        return path;
    }

    public Object getDefault() {
        return def;
    }
}
