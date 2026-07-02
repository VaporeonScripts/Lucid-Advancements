package com.niixlabs.lucidadvancements.client.gui;

import com.niixlabs.lucidadvancements.Constants;
import com.niixlabs.lucidadvancements.utils.LucidConfig;
import com.niixlabs.lucidadvancements.utils.TranslationUtils;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LucidAdvancementsOverlay {

    private static final Map<String, CachedOverlayBox> OVERLAY_CACHE = new HashMap<>();
    private static String lastLang = "";

    public static void render(GuiGraphics guiGraphics, float partialTick) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.options.hideGui || mc.player == null || LucidAdvancementsScreen.TRACKED_ADVANCEMENTS.isEmpty()) return;
        if (mc.getConnection() == null || mc.getConnection().getAdvancements() == null) return;

        String currentLang = mc.getLanguageManager().getSelected();
        if (!currentLang.equals(lastLang)) {
            OVERLAY_CACHE.clear();
            lastLang = currentLang;
        }

        double vanillaScale = mc.getWindow().getGuiScale();
        double targetScale = getTargetScale(mc);

        float scaleMod = (float) (targetScale / vanillaScale);

        int screenWidth = (int) (mc.getWindow().getScreenWidth() / targetScale);
        int screenHeight = (int) (mc.getWindow().getScreenHeight() / targetScale);

        Font font = mc.font;
        int boxWidth = 135;
        int startX = screenWidth - boxWidth - 8;
        int startY = 55;
        int spacing = 5;

        Map<AdvancementHolder, AdvancementProgress> vanillaProgressMap = ((AdvancementProgressAccess) mc.getConnection().getAdvancements()).lucid$getProgressMap();

        guiGraphics.pose().pushPose();

        if (scaleMod != 1.0f) {
            guiGraphics.pose().scale(scaleMod, scaleMod, 1.0f);
        }

        for (String idStr : LucidAdvancementsScreen.TRACKED_ADVANCEMENTS) {
            ResourceLocation loc = ResourceLocation.tryParse(idStr);
            if (loc == null) continue;

            AdvancementNode node = mc.getConnection().getAdvancements().getTree().get(loc);
            if (node == null || node.holder().value().display().isEmpty()) continue;

            DisplayInfo display = node.holder().value().display().get();
            AdvancementProgress progress = vanillaProgressMap.get(node.holder());

            if (progress == null || progress.isDone()) {
                continue;
            }

            List<String> rawRemaining = (List<String>) progress.getRemainingCriteria();

            CachedOverlayBox cachedBox = OVERLAY_CACHE.get(idStr);
            if (cachedBox == null || !cachedBox.rawRemaining.equals(rawRemaining)) {
                boolean isChallenge = display.getType() == net.minecraft.advancements.AdvancementType.CHALLENGE;
                int titleColor = isChallenge ? LucidConfig.overlayChallengeTitleColor : LucidConfig.overlayTitleColor;
                String titleText = font.plainSubstrByWidth(display.getTitle().getString(), boxWidth - 32);

                List<String> critLines = new ArrayList<>();
                int maxVisibleCriteria = Math.min(3, rawRemaining.size());

                for (int i = 0; i < maxVisibleCriteria; i++) {
                    String resolved = TranslationUtils.resolveDisplayCriterion(loc, rawRemaining.get(i));
                    String critLine = font.plainSubstrByWidth("🔒 " + resolved, boxWidth - 10);
                    critLines.add(critLine);
                }

                int boxHeight = 24 + ((rawRemaining.size() > 3 ? maxVisibleCriteria + 1 : maxVisibleCriteria) * 10);
                if (maxVisibleCriteria > 0) boxHeight += 2;

                cachedBox = new CachedOverlayBox(new ArrayList<>(rawRemaining), titleText, critLines, boxHeight, titleColor);
                OVERLAY_CACHE.put(idStr, cachedBox);
            }

            guiGraphics.fill(startX, startY, startX + boxWidth, startY + cachedBox.boxHeight, LucidConfig.overlayBgColor);

            boolean isChallenge = display.getType() == net.minecraft.advancements.AdvancementType.CHALLENGE;
            int accentColor = isChallenge ? LucidConfig.overlayChallengeAccent : LucidConfig.overlayNormalAccent;
            guiGraphics.fill(startX, startY, startX + 2, startY + cachedBox.boxHeight, accentColor);

            ItemStack iconStack = display.getIcon();
            guiGraphics.pose().pushPose();
            guiGraphics.renderItem(iconStack, startX + 6, startY + 4);
            guiGraphics.pose().popPose();

            guiGraphics.drawString(font, cachedBox.titleText, startX + 26, startY + 5, cachedBox.titleColor, true);

            int textY = startY + 22;
            for (String line : cachedBox.critLines) {
                guiGraphics.drawString(font, line, startX + 8, textY, 0xFFAAAAAA, true);
                textY += 10;
            }

            if (rawRemaining.size() > 3) {
                int hiddenCount = rawRemaining.size() - 3;
                guiGraphics.drawString(font, Component.translatable(Constants.MOD_ID + ".overlay.hidden_count", hiddenCount), startX + 8, textY, 0xFF555555, true);
            }

            startY += cachedBox.boxHeight + spacing;

            if (startY > screenHeight - 35) {
                break;
            }
        }

        guiGraphics.pose().popPose();
    }

    private static double getTargetScale(Minecraft mc) {
        final double MIN_VIRTUAL_WIDTH = 550.0;
        final double MIN_VIRTUAL_HEIGHT = 300.0;

        double screenWidth = mc.getWindow().getScreenWidth();
        double screenHeight = mc.getWindow().getScreenHeight();

        double maxPossibleScaleX = screenWidth / MIN_VIRTUAL_WIDTH;
        double maxPossibleScaleY = screenHeight / MIN_VIRTUAL_HEIGHT;

        double maxSafeScale = Math.max(1.0, Math.floor(Math.min(maxPossibleScaleX, maxPossibleScaleY)));
        double vanillaScale = mc.getWindow().getGuiScale();

        if (LucidConfig.customGuiScale == 0) {
            return Math.min(vanillaScale, maxSafeScale);
        }

        return Mth.clamp((double) LucidConfig.customGuiScale, 1.0, maxSafeScale);
    }

    private static class CachedOverlayBox {
        final List<String> rawRemaining;
        final String titleText;
        final List<String> critLines;
        final int boxHeight;
        final int titleColor;

        CachedOverlayBox(List<String> rawRemaining, String titleText, List<String> critLines, int boxHeight, int titleColor) {
            this.rawRemaining = rawRemaining;
            this.titleText = titleText;
            this.critLines = critLines;
            this.boxHeight = boxHeight;
            this.titleColor = titleColor;
        }
    }
}