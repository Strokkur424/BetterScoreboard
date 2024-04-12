package net.strokkur.betterscoreboard.client;

import net.fabricmc.api.ClientModInitializer;
import net.strokkur.betterscoreboard.config.SConfig;

public class MainClient implements ClientModInitializer {

    public static final SConfig config = SConfig.createAndLoad();

    @Override
    public void onInitializeClient() {

    }
}
