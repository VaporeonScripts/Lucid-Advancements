package com.niixlabs.lucidadvancements;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(Constants.MOD_ID)
public class LucidAdvancements {
    public LucidAdvancements() {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            CommonClass.init();
        }
    }
}
