package com.rerurate.keephotbar;

import com.mojang.logging.LogUtils;
import com.rerurate.keephotbar.handler.PlayerDeathHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(Keephotbar.MODID)
public class Keephotbar {
    public static final String MODID = "keephotbar";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Keephotbar() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    }

    private void setup(final FMLCommonSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(new PlayerDeathHandler());
        LOGGER.info("[HotbarKeep] Mod initialized - Hotbar will be preserved on death!");
    }
}
