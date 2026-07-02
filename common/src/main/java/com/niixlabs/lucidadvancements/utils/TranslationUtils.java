package com.niixlabs.lucidadvancements.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TranslationUtils {
    private static final Gson GSON = new Gson();
    private static final Map<String, Map<String, String>> FILE_CACHE = new HashMap<>();
    private static final Map<String, String> RESOLVED_CRITERIA_CACHE = new HashMap<>();
    private static String lastLanguage = "";

    private static final String[] REGISTRY_PREFIXES = {
            "biome", "entity", "item", "block", "cat_variant", "frog_variant", "wolf_variant", "painting"
    };

    public static String resolveDisplayCriterion(ResourceLocation advancementId, String rawCriterion) {
        String currentLang = Minecraft.getInstance().getLanguageManager().getSelected();

        if (!currentLang.equals(lastLanguage)) {
            clearCache();
            lastLanguage = currentLang;
        }

        String cacheKey = advancementId.toString() + "|" + rawCriterion;
        return RESOLVED_CRITERIA_CACHE.computeIfAbsent(cacheKey, k -> {
            String translated = getTranslatedCriterion(advancementId, rawCriterion, currentLang);
            if (translated == null) {
                translated = tryTranslateRegistryId(rawCriterion);
            }
            if (translated == null) {
                translated = formatCaps(cleanCriteriaName(rawCriterion));
            }
            return translated;
        });
    }

    private static String getTranslatedCriterion(ResourceLocation advancementId, String criterionName, String currentLang) {
        String modId = advancementId.getNamespace();
        String path = advancementId.getPath();
        String cleanPath = path.contains("/") ? path.substring(path.lastIndexOf('/') + 1) : path;
        String jsonKey = "requirements." + cleanPath + "." + criterionName.toLowerCase();

        Map<String, String> modTranslations = FILE_CACHE.computeIfAbsent(modId, k -> loadModTranslations(modId, currentLang));
        return modTranslations.get(jsonKey);
    }

    private static String tryTranslateRegistryId(String rawCriterion) {
        if (!rawCriterion.contains(":")) {
            return null;
        }

        int colonIndex = rawCriterion.indexOf(":");
        int start = colonIndex;
        while (start > 0 && isResourceLocationChar(rawCriterion.charAt(start - 1))) {
            start--;
        }
        int end = colonIndex + 1;
        while (end < rawCriterion.length() && isResourceLocationChar(rawCriterion.charAt(end))) {
            end++;
        }

        if (start >= end) {
            return null;
        }

        String idStr = rawCriterion.substring(start, end);
        int idColon = idStr.indexOf(":");
        String namespace = idStr.substring(0, idColon);
        String path = idStr.substring(idColon + 1);

        for (String prefix : REGISTRY_PREFIXES) {
            String key = prefix + "." + namespace + "." + path;
            if (I18n.exists(key)) {
                return I18n.get(key);
            }
        }

        return null;
    }

    public static String cleanCriteriaName(String raw) {
        if (raw.contains(":")) {
            raw = raw.substring(raw.indexOf(":") + 1);
        }
        return raw.replace("_", " ").toLowerCase();
    }

    public static String formatCaps(String c) {
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

    private static boolean isResourceLocationChar(char c) {
        return (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '_' || c == '-' || c == '/' || c == '.';
    }

    private static Map<String, String> loadModTranslations(String modId, String lang) {
        Map<String, String> translations = new HashMap<>();
        loadJsonFile(modId, "en_us", translations);

        if (!lang.equalsIgnoreCase("en_us")) {
            loadJsonFile(modId, lang, translations);
        }

        return translations;
    }

    private static void loadJsonFile(String modId, String lang, Map<String, String> targetMap) {
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(
                "lucidadvancements",
                "translations/" + modId + "/" + lang.toLowerCase() + ".json"
        );

        Optional<Resource> resourceOpt = Minecraft.getInstance().getResourceManager().getResource(location);

        if (resourceOpt.isPresent()) {
            try (Reader reader = resourceOpt.get().openAsReader()) {
                Map<String, String> loadedData = GSON.fromJson(reader, new TypeToken<Map<String, String>>(){}.getType());
                if (loadedData != null) {
                    targetMap.putAll(loadedData);
                }
            } catch (Exception ignored) {
            }
        }
    }

    public static void clearCache() {
        FILE_CACHE.clear();
        RESOLVED_CRITERIA_CACHE.clear();
    }
}