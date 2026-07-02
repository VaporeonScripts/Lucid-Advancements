package com.niixlabs.lucidadvancements.client.gui;

import com.niixlabs.lucidadvancements.Constants;
import net.minecraft.network.chat.Component;

public enum SortMode {
    ALL(Component.translatable(Constants.MOD_ID + ".gui.sort.mode.all").getString()),
    COMPLETED(Component.translatable(Constants.MOD_ID + ".gui.sort.mode.completed").getString()),
    INCOMPLETE(Component.translatable(Constants.MOD_ID + ".gui.sort.mode.incomplete").getString()),
    CHALLENGES(Component.translatable(Constants.MOD_ID + ".gui.sort.mode.challenges").getString());

    private final String displayName;

    SortMode(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public SortMode next() {
        return values()[(this.ordinal() + 1) % values().length];
    }
}
