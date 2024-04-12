package net.strokkur.betterscoreboard.config;

import io.wispforest.owo.config.annotation.*;

import java.util.List;

@Modmenu(modId = "betterscoreboard")
@Config(name = "scoreboard-config", wrapperName = "SConfig")
public class SModelConfig {

    public boolean enableScoreboard = true;
    public boolean textShadow = true;

    @SectionHeader("Score Configs")
    public boolean hideNumbers = false;
    public NumberColorEnum numberColor = NumberColorEnum.RED;
    public boolean useHexColor = false;
    @RegexConstraint("[A-Fa-f0-9]{6}")
    public String hexValue = "FF5555";
    @Nest
    public Style scoreNumberStyle = new Style();

    @Nest
    public ScoreOverrides scoreOverrides = new ScoreOverrides();

    @SectionHeader("Scoreboard Title Configs")
    public boolean hideTitle = false;

    public boolean overrideTitleBackground = false;
    @RegexConstraint("((100)|([0-9])|[1-9][0-9])%")
    public String titleBackgroundOpacity = "40%";
    @RegexConstraint("[A-Fa-f0-9]{6}")
    public String titleBackgroundColor = "000000";

    public boolean overrideTitleText = false;
    public String titleText = "&6Custom Title";

    @Nest
    public Color titleColor = new Color();
    @Nest
    public Style titleStyle = new Style();

    @SectionHeader("Scoreboard Content Configs")
    public boolean hideContent = false;

    public boolean overrideContentBackground = false;
    @RegexConstraint("((100)|([0-9])|[1-9][0-9])%")
    public String contentBackgroundOpacity = "30%";
    @RegexConstraint("[A-Fa-f0-9]{6}")
    public String contentBackgroundColor = "000000";

    @Nest
    public Color contentColor = new Color();
    @Nest
    public Style contentStyle = new Style();

    @Nest
    public EntryOverrides contentOverrides = new EntryOverrides();


    public static class ScoreOverrides {
        public int maxEntries = 15;
        public boolean enableOverrides = false;
        public List<String> overrides = List.of(
                "score;1;&eScore number 1",
                "placement;1;&eFirst place!"
        );
    }

    public static class EntryOverrides {
        public boolean enableOverrides = false;
        public List<String> overrides = List.of(
                "content;Test Content;&eNew Content",
                "placement;1;&eFirst line!"
        );
    }

    public static class Color {
        public boolean overrideColor = false;
        public NumberColorEnum overrideEnum = NumberColorEnum.WHITE;
        public boolean overrideWithHex = false;
        @RegexConstraint("[A-Fa-f0-9]{6}")
        public String overrideHex = "FF5555";
    }

    public static class Style {
        public boolean overrideStyle = false;
        public boolean obfuscated = false;
        public boolean bold = false;
        public boolean strikethrough = false;
        public boolean underline = false;
        public boolean italic = false;
    }

    public enum NumberColorEnum {
        DARK_RED(0xAA0000), RED(0xFF5555), GOLD(0xFFAA00), YELLOW(0xFFFF55), DARK_GREEN(0x00AA00), GREEN(0x55FF55), AQUA(0x55FFFF), DARK_AQUA(0x00AAAA), DARK_BLUE(0x0000AA), BLUE(0x5555FF), LIGHT_PURPLE(0xFF55FF), DARK_PURPLE(0xAA00AA), WHITE(0xFFFFFF), GRAY(0xAAAAAA), DARK_GRAY(0x555555), BLACK(0);

        public final int hex;

        NumberColorEnum(int hex) {
            this.hex = hex;
        }
    }
}
