package com.security_guard.security_guard.mixin;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.URL;   // 改为 URL

/**
 * 阻止非游戏核心类调用 Util.OS#openUrl 打开外部链接
 */
@Mixin(targets = "net.minecraft.Util$OS")
public class MixinUtilOS {

    @Unique
    private static final Logger LOGGER = LogUtils.getLogger();

    @Inject(method = "openUrl", at = @At("HEAD"), cancellable = true)
    private void onOpenUrl_blockMods(URL url, CallbackInfo ci) {  // 参数改为 URL
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        String blockedClass = null;

        for (StackTraceElement element : stack) {
            String className = element.getClassName();
            // 放行游戏核心、Java标准库、LWJGL、Mixin自身等
            if (className.startsWith("net.minecraft.") ||
                    className.startsWith("com.mojang.") ||
                    className.startsWith("java.") ||
                    className.startsWith("javax.") ||
                    className.startsWith("sun.") ||
                    className.startsWith("jdk.") ||
                    className.startsWith("org.lwjgl.") ||
                    className.startsWith("com.mojang.blaze3d.") ||
                    className.contains(".mixin.")) {
                continue;
            }
            blockedClass = className;
            break;
        }

        if (blockedClass != null) {
            LOGGER.info("Blocked attempt to open URL: {} from class: {}", url, blockedClass);  // 变量名改为 url
            ci.cancel();
        }
    }
}