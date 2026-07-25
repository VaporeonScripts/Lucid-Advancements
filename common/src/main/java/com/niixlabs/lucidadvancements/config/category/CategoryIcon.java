package com.niixlabs.lucidadvancements.config.category;

public record CategoryIcon(CategoryIconType type, String value) {
    public static CategoryIcon ofItem(String itemId) {
        return new CategoryIcon(CategoryIconType.ITEM, itemId);
    }

    public static CategoryIcon ofTexture(String fileName) {
        return new CategoryIcon(CategoryIconType.TEXTURE, fileName);
    }
}