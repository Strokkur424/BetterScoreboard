package net.strokkur.betterscoreboard.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardEntry;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.scoreboard.number.NumberFormat;
import net.minecraft.scoreboard.number.StyledNumberFormat;
import net.minecraft.text.Text;
import net.strokkur.betterscoreboard.records.SidebarEntry;

import net.strokkur.betterscoreboard.util.ConfigUtil;
import org.spongepowered.asm.mixin.*;

import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;

@Mixin(InGameHud.class)
public abstract class ScoreboardMixin {
    @Unique
    private static final int offset = 5;

    @Shadow
    @Final
    private static Comparator<ScoreboardEntry> SCOREBOARD_ENTRY_COMPARATOR;

    @Shadow
    private int scaledHeight;

    @Shadow
    private int scaledWidth;

    @Final
    @Shadow
    private MinecraftClient client;

    @Shadow public abstract TextRenderer getTextRenderer();

    @Overwrite
    @SuppressWarnings({"SpellCheckingInspection", "deprecation"})
    private void renderScoreboardSidebar(DrawContext context, ScoreboardObjective objective) {
        if (ConfigUtil.isScoreboardDisabled()) return;
        Scoreboard scoreboard = objective.getScoreboard();
        NumberFormat numberFormat = objective.getNumberFormatOr(StyledNumberFormat.RED);
        TextRenderer textRenderer = getTextRenderer();
        AtomicInteger index = new AtomicInteger(1);
        SidebarEntry[] sidebarEntrys;
        if (ConfigUtil.hideContent()) {
            sidebarEntrys = new SidebarEntry[0];
        } else {
            sidebarEntrys = scoreboard.getScoreboardEntries(objective)
                    .stream()
                    .filter((score) -> !score.hidden()).sorted(SCOREBOARD_ENTRY_COMPARATOR).limit(ConfigUtil.maxEntries()).map((scoreboardEntry) -> {

                        Team team = scoreboard.getScoreHolderTeam(scoreboardEntry.owner());

                        Text name = scoreboardEntry.name();
                        Text nameDecorated = ConfigUtil.overrideContentText(Team.decorateName(team, name), index.get());

                        Text number = ConfigUtil.overrideScoreNumber(scoreboardEntry.formatted(numberFormat), index.get());
                        int numberWidth = textRenderer.getWidth(number);

                        index.set(index.addAndGet(1));
                        if (ConfigUtil.hideScoreNumber())
                            return new SidebarEntry(nameDecorated, Text.literal(""), 0);
                        return new SidebarEntry(nameDecorated, number, numberWidth);
                    })
                    .toArray(SidebarEntry[]::new);
        }

        Text title = ConfigUtil.getTitleText(objective.getDisplayName().copy());

        int width = 0;

        if (!ConfigUtil.hideTitle())
            width = textRenderer.getWidth(title);

        final int originalWidth = width;
        int defaultWidth = textRenderer.getWidth(": ");

        for (SidebarEntry sidebarEntry : sidebarEntrys) {
            width = Math.max(width,
                    textRenderer.getWidth(sidebarEntry.name()) + (sidebarEntry.scoreWidth() > 0 ? defaultWidth + sidebarEntry.scoreWidth() : 0));
        }

        final int requiredWidth = width;
        context.draw(() -> {

            int elementCount = sidebarEntrys.length;
            int contentBoxPixels = elementCount * 9;

            int heightPixelsScaled = scaledHeight / 2 + contentBoxPixels / 3;
            int x = this.scaledWidth - requiredWidth - 3;
            int xRight = this.scaledWidth - 3 + 2;

            int backgroundColor = client.options.getTextBackgroundColor(0.3F);
            int backgroundColorTitle = client.options.getTextBackgroundColor(0.4F);

            int scoresHeightPixels = heightPixelsScaled - elementCount * 9;
            int xLeft = x - 2;

            // Draw title box
            if (!ConfigUtil.hideTitle()) {
                if (ConfigUtil.hideContent()) {
                    context.fill(xLeft, scoresHeightPixels - 9 - 1 + offset, xRight, scoresHeightPixels - 1 + offset, backgroundColorTitle);
                }
                else {
                    context.fill(xLeft, scoresHeightPixels - 9 - 1, xRight, scoresHeightPixels - 1, backgroundColorTitle);
                }
            }

            // Draw content box
            if (!ConfigUtil.hideContent()) {
                if (ConfigUtil.hideTitle()) {
                    context.fill(x - 2, scoresHeightPixels - 1 - offset, xRight, heightPixelsScaled - offset, backgroundColor);
                }
                else {
                    context.fill(x - 2, scoresHeightPixels - 1, xRight, heightPixelsScaled, backgroundColor);
                }
            }

            int titleX = x + requiredWidth / 2 - originalWidth / 2;

            // Draw title text
            if (!ConfigUtil.hideTitle()) {
                if (ConfigUtil.hideContent()) {
                    context.drawText(textRenderer, title, titleX, scoresHeightPixels  - 9 + offset, -1, ConfigUtil.textShadow());
                    return;
                }
                context.drawText(textRenderer, title, titleX, scoresHeightPixels - 9, -1, ConfigUtil.textShadow());
            }

            if (ConfigUtil.hideContent()) return;

            // Draw content texts
            for (int i = 0; i < elementCount; i++) {
                SidebarEntry sidebarEntry = sidebarEntrys[i];
                xLeft = elementCount - i;

                int y;
                if (ConfigUtil.hideTitle()) {
                    y = heightPixelsScaled - xLeft * 9 - offset;
                }
                else {
                    y = heightPixelsScaled - xLeft * 9;
                }
                context.drawText(textRenderer, sidebarEntry.name(), x, y, -1, ConfigUtil.textShadow());
                context.drawText(textRenderer, sidebarEntry.score(), xRight - sidebarEntry.scoreWidth(), y, -1, ConfigUtil.textShadow());
            }

        });
    }

}
