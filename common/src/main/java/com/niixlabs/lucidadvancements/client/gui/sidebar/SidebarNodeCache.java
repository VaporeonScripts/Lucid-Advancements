package com.niixlabs.lucidadvancements.client.gui.sidebar;

import com.niixlabs.lucidadvancements.Constants;
import com.niixlabs.lucidadvancements.config.LucidConfig;
import com.niixlabs.lucidadvancements.config.category.CategoryConfigManager;
import com.niixlabs.lucidadvancements.config.category.CategoryDefinition;
import com.niixlabs.lucidadvancements.config.category.CategoryTextureManager;
import com.niixlabs.lucidadvancements.config.category.ResolvedIcon;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

public final class SidebarNodeCache {
    public final AdvancementNode node;
    public final ResolvedIcon icon;
    public final String displayTitle;

    public SidebarNodeCache(@Nullable AdvancementNode node, Font font, int maxTextWidth) {
        this.node = node;
        if (node == null) {
            this.icon = resolveGlobalIcon();
            this.displayTitle = font.plainSubstrByWidth(globalCategoryLabel(), maxTextWidth);
        } else {
            CategoryDefinition def = CategoryConfigManager.resolve(node.holder().id()).orElse(null);
            ItemStack fallbackIcon = node.holder().value().display().map(DisplayInfo::getIcon).orElse(new ItemStack(Items.BOOK));
            this.icon = CategoryConfigManager.resolveIcon(def, fallbackIcon);
            this.displayTitle = truncatedTitle(node, def, font, maxTextWidth);
        }
    }

    private static ResolvedIcon resolveGlobalIcon() {
        ResourceLocation texture = CategoryTextureManager.load(Constants.MOD_ID, "category.png");
        return texture != null
                ? new ResolvedIcon.Texture(texture)
                : new ResolvedIcon.Item(new ItemStack(Items.LECTERN));
    }

    private static String globalCategoryLabel() {
        return Component.translatable(Constants.MOD_ID + ".gui.global_category.tab_name").getString();
    }

    private static String truncatedTitle(AdvancementNode node, @Nullable CategoryDefinition definition, Font font, int maxTextWidth) {
        String rawTitle = (definition == null || definition.title == null || definition.title.isEmpty())
                ? node.holder().value().display()
                .map(DisplayInfo::getTitle)
                .orElse(Component.literal("?"))
                .getString()
                : Component.translatable(definition.title).getString();

        if (font.width(rawTitle) <= maxTextWidth) {
            return rawTitle;
        }
        return font.plainSubstrByWidth(rawTitle, maxTextWidth - font.width(LucidConfig.sidebarTruncationEllipsis)) + LucidConfig.sidebarTruncationEllipsis;
    }
}