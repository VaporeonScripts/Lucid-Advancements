package com.niixlabs.lucidadvancements.config.category;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

public final class CategoryAssetInitializer {
    private static final String NAMESPACE = "lucidadvancements";
    private static final String GLOBAL_ICON_FILE = "category.png";

    // assets/lucidadvancements/textures/gui/default_category_icon.png
    private static final ResourceLocation BUNDLED_DEFAULT_ICON =
            ResourceLocation.fromNamespaceAndPath(NAMESPACE, "textures/gui/default_category_icon.png");

    private CategoryAssetInitializer() {}

    private static Path assetsDir() {
        return Minecraft.getInstance().gameDirectory.toPath().resolve("config/lucid-advancements/assets");
    }

    public static void ensureGlobalCategoryIcon() {
        Path targetDir = assetsDir().resolve(NAMESPACE);
        Path targetFile = targetDir.resolve(GLOBAL_ICON_FILE);

        if (Files.exists(targetFile)) {
            return;
        }

        try {
            Files.createDirectories(targetDir);
            Optional<Resource> bundled = Minecraft.getInstance().getResourceManager().getResource(BUNDLED_DEFAULT_ICON);
            if (bundled.isPresent()) {
                try (InputStream stream = bundled.get().open()) {
                    Files.copy(stream, targetFile, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}