package com.niixlabs.lucidadvancements.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class TranslationExporter {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static void exportCurrentAdvancements(Map<AdvancementNode, AdvancementProgress> progressMap) {
        Map<String, Map<String, String>> exports = new HashMap<>();

        for (Map.Entry<AdvancementNode, AdvancementProgress> entry : progressMap.entrySet()) {
            AdvancementNode node = entry.getKey();
            AdvancementProgress progress = entry.getValue();

            ResourceLocation id = node.holder().id();
            String modId = id.getNamespace();
            String path = id.getPath();

            if (path.startsWith("recipes/")) {
                continue;
            }

            String cleanPath = path.contains("/") ? path.substring(path.lastIndexOf('/') + 1) : path;
            Map<String, String> modMap = exports.computeIfAbsent(modId, k -> new TreeMap<>());

            for (String criterion : progress.getCompletedCriteria()) {
                if (criterion.toLowerCase().contains("recipe") || criterion.contains(":")) {
                    continue;
                }
                String jsonKey = "requirements." + cleanPath + "." + criterion.toLowerCase();
                modMap.put(jsonKey, formatCaps(cleanCriteriaName(criterion)));
            }

            for (String criterion : progress.getRemainingCriteria()) {
                if (criterion.toLowerCase().contains("recipe") || criterion.contains(":")) {
                    continue;
                }
                String jsonKey = "requirements." + cleanPath + "." + criterion.toLowerCase();
                modMap.put(jsonKey, formatCaps(cleanCriteriaName(criterion)));
            }
        }

        File baseDir = new File(Minecraft.getInstance().gameDirectory, "lucid_exported_translations");

        for (Map.Entry<String, Map<String, String>> entry : exports.entrySet()) {
            if (entry.getValue().isEmpty()) continue;

            File modDir = new File(baseDir, entry.getKey());
            if (!modDir.exists()) {
                modDir.mkdirs();
            }

            File file = new File(modDir, "en_us.json");
            try (FileWriter writer = new FileWriter(file)) {
                GSON.toJson(entry.getValue(), writer);
            } catch (IOException ignored) {
            }
        }
    }

    private static String cleanCriteriaName(String raw) {
        if (raw.contains(":")) {
            raw = raw.substring(raw.indexOf(":") + 1);
        }
        return raw.replace("_", " ").toLowerCase();
    }

    private static String formatCaps(String c) {
        if (c == null || c.isEmpty()) return "";
        String[] words = c.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                sb.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
            }
        }
        return sb.toString().trim();
    }
}