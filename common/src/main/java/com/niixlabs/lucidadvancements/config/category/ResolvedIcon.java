package com.niixlabs.lucidadvancements.config.category;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public sealed interface ResolvedIcon {
    record Item(ItemStack stack) implements ResolvedIcon {}
    record Texture(ResourceLocation location) implements ResolvedIcon {}
}