package com.niixlabs.lucidadvancements;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod(Constants.MOD_ID)
public class LucidAdvancements {
    public LucidAdvancements(IEventBus eventBus) {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            CommonClass.init();
        }
    }
}