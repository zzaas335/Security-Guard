package com.security_guard.security_guard;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod(Security_guard.MODID)
public class Security_guard {
    public static final String MODID = "security_guard";
    private static final Logger LOGGER = LogUtils.getLogger();

    public Security_guard() {
        LOGGER.info("Security Guard loaded - all mod links will be blocked.");
    }
}