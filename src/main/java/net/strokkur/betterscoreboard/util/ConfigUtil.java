package net.strokkur.betterscoreboard.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.strokkur.betterscoreboard.config.SConfig;
import net.strokkur.betterscoreboard.config.SModelConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static net.strokkur.betterscoreboard.client.MainClient.config;

public class ConfigUtil {

    public static boolean isScoreboardDisabled() {
        return !config.enableScoreboard();
    }

    public static boolean textShadow() {
        return config.textShadow();
    }

    // Score Configs
    public static boolean hideScoreNumber() {
        return config.hideNumbers();
    }

    public static MutableText applyNumberFormat(MutableText text) {
        Style style = getStyle(config.scoreNumberStyle);

        style = style.withColor(config.useHexColor()
                ? Integer.parseInt(config.hexValue(), 16)
                : config.numberColor().hex);

        text = text.setStyle(style);
        return text;
    }

    public static MutableText overrideScoreNumber(MutableText number, int index) {
        SConfig.ScoreOverrides overrides = config.scoreOverrides;
        if (!overrides.enableOverrides()) return applyNumberFormat(number);
        if (index >= overrides.maxEntries()) throw new NumberFormatException("Okay enough entries :3");

        MutableText out = replace(number, index, overrides.overrides());
        if (out != null) return out;
        return applyNumberFormat(number);
    }
    public static int maxEntries() {
        return config.scoreOverrides.maxEntries();
    }

    // Title Configs
    public static boolean hideTitle() {
        return config.hideTitle();
    }

    public static int getTitleBackground(@NotNull MinecraftClient client) {
        //if (!config.overrideTitleBackground())
        return client.options.getTextBackgroundColor(0.3F);
    }

    public static MutableText getTitleText(MutableText title) {
        if (config.overrideTitleText())
            return Text.literal(config.titleText().replaceAll("&", "§"));

        return colorAndStyle(title, config.titleColor, config.titleStyle);
    }

    // Content Configs
    public static boolean hideContent() {
        return config.hideContent();
    }

    public static int getContentBackground(@NotNull MinecraftClient client) {
        return client.options.getTextBackgroundColor(0.4F);
    }

    public static MutableText overrideContentText(MutableText text, int index) {
        SConfig.EntryOverrides overrides = config.contentOverrides;
        if (!overrides.enableOverrides()) return text;

        MutableText out = replace(text, index, overrides.overrides());
        if (out != null) return out;

        return colorAndStyle(text, config.contentColor, config.contentStyle);
    }

    // Util
    private static int getColor(SConfig.@NotNull Color color) {
        return color.overrideWithHex()
                ? Integer.parseInt(config.hexValue(), 16)
                : config.numberColor().hex;
    }

    private static Style getStyle(SConfig.Style configStyle) {
        Style style = Style.EMPTY;

        style = style.withObfuscated(configStyle.obfuscated());
        style = style.withBold(configStyle.bold());
        style = style.withStrikethrough(configStyle.strikethrough());
        style = style.withUnderline(configStyle.underline());
        style = style.withItalic(configStyle.italic());

        return style;
    }

    private static @Nullable MutableText replace(MutableText original, int index, @NotNull List<String> replaceContent) {
        for (String str : replaceContent) {
            String[] contents = str.split(";");
            if (contents.length != 3) continue;

            switch (contents[0].toLowerCase()) {
                case "score", "content" -> {
                    if (!original.getString().equals(contents[1]))
                        continue;

                    if (contents[2].contains("&"))
                        return Text.literal(contents[2].replaceAll("&", "§"));
                    return applyNumberFormat(Text.literal(contents[2]));
                }

                case "placement" -> {
                    try {
                        if (index != Integer.parseInt(contents[1])) continue;

                        if (contents[2].contains("&"))
                            return Text.literal(contents[2].replaceAll("&", "§"));
                        return applyNumberFormat(Text.literal(contents[2]));
                    }
                    catch (NumberFormatException e) { /* */ }
                }
            }
        }

        return null;
    }

    private static MutableText colorAndStyle(MutableText text, SConfig.Color color, SConfig.@NotNull Style titleStyle) {
        if (titleStyle.overrideStyle()) {
            text = text.setStyle(getStyle(titleStyle));
        }

        if (color.overrideColor()) {
            text = text.withColor(getColor(color));
        }

        return text;
    }
}
