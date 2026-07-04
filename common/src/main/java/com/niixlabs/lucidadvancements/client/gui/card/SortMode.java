package com.niixlabs.lucidadvancements.client.gui.card;

import com.niixlabs.lucidadvancements.Constants;
import net.minecraft.network.chat.Component;

public enum SortMode {
    ALL(Constants.MOD_ID + ".gui.sort.mode.all"),
    COMPLETED(Constants.MOD_ID + ".gui.sort.mode.completed"),
    INCOMPLETE(Constants.MOD_ID + ".gui.sort.mode.incomplete"),
    CHALLENGES(Constants.MOD_ID + ".gui.sort.mode.challenges");

    private final String translationKey;

    SortMode(String translationKey) {
        this.translationKey = translationKey;
    }

    public String getDisplayName() {
        return Component.translatable(translationKey).getString();
    }

    public SortMode next() {
        SortMode[] values = values();
        return values[(ordinal() + 1) % values.length];
    }
}