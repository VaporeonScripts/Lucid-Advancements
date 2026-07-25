package com.niixlabs.lucidadvancements.config.category;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public final class CategoryTextureManager {
    private static final String NAMESPACE = "lucidadvancements";
    private static final Map<String, ResourceLocation> CACHE = new HashMap<>();

    private static Path assetsDir() {
        return Minecraft.getInstance().gameDirectory.toPath().resolve("config/lucid-advancements/assets");
    }

    public static ResourceLocation load(String modId, String fileName) {
        String cacheKey = modId + "/" + fileName;
        if (CACHE.containsKey(cacheKey)) {
            return CACHE.get(cacheKey);
        }

        Path imagePath = assetsDir().resolve(modId).resolve(fileName);
        if (!Files.exists(imagePath)) {
            CACHE.put(cacheKey, null);
            return null;
        }

        try (InputStream stream = Files.newInputStream(imagePath)) {
            NativeImage image = NativeImage.read(stream);
            DynamicTexture texture = new DynamicTexture(image);

            String sanitized = cacheKey.replaceAll("[^a-z0-9/._-]", "_").toLowerCase();
            ResourceLocation location = ResourceLocation.fromNamespaceAndPath(NAMESPACE, "category_icon/" + sanitized);

            Minecraft.getInstance().getTextureManager().register(location, texture);
            CACHE.put(cacheKey, location);
            return location;
        } catch (IOException e) {
            e.printStackTrace();
            CACHE.put(cacheKey, null);
            return null;
        }
    }

    public static void clearCache() {
        Minecraft minecraft = Minecraft.getInstance();
        for (ResourceLocation location : CACHE.values()) {
            if (location != null) {
                minecraft.getTextureManager().release(location);
            }
        }
        CACHE.clear();
    }
}