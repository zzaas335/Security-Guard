# Security-Guard
保护你在启动游戏时电脑的安全

现在网上有很多mod在启动游戏的时候，会打开一个网站并下载文件，这个mod可以从根源解决这个安全隐患

## 概述

本mod是一个 1.20.1 Forge mod ，用于拦截并阻止非游戏核心代码通过 `net.minecraft.Util.OS#openUrl(URL)` 方法打开外部链接。它通过动态堆栈分析，只允许来自游戏核心、标准库、LWJGL 及 Mixin 自身等受信任包的调用，对其他所有外部调用进行静默阻断并记录日志，从而有效防范 Mod 带来的安全风险。

## 功能特性

- **运行时拦截**：在 `openUrl` 方法执行之前插入检测逻辑（`@Inject` 于 `HEAD`，可取消）。
- **堆栈白名单过滤**：自动遍历当前调用堆栈，如果发现调用者不属于以下安全包，则立即取消打开操作：
  - `net.minecraft.*`
  - `com.mojang.*`
  - `java.*` / `javax.*` / `sun.*` / `jdk.*`
  - `org.lwjgl.*`
  - `com.mojang.blaze3d.*`
  - 任何包含 `.mixin.` 的类（Mixin 框架本身）
- **安全日志**：每次拦截都会以 `INFO` 级别记录被阻止的 URL 和触发类的完整路径，便于审计与调试。
- **无侵入性**：不需要修改任何游戏或 Mod 配置文件，仅需将该 Mixin 添加到你的 Mod 项目中即可生效。

## 工作原理

主要 Mixin 目标为 Minecraft 内部工具类 `net.minecraft.Util$OS` 的 `openUrl` 方法。在每次调用该方法时，它会获取当前线程的堆栈轨迹，然后自顶向下检查每个堆栈元素的类名。若某个类不在白名单中，则将其标记为“触发者”，随后调用 `CallbackInfo#cancel()` 中止原方法执行，同时输出日志。

这种设计保证了即使某个 Mod 通过反射或间接方式调用 `openUrl`，只要其最终调用者不是白名单内的类，就会被拦截。

## 使用方法

**将 mod 添加进游戏“mod”文件夹中**：  

## 注意事项

- **性能影响**：堆栈遍历仅在 `openUrl` 被调用时触发，且调用频率通常极低（多为用户点击链接），对整体性能影响可忽略不计。
- **白名单调整**：如果你有特殊需求（例如需要放行自定义包），可以修改主要Mixin中 `onOpenUrl_blockMods` 方法中的白名单条件，添加额外的 `className.startsWith("your.package.")` 判断。
- **兼容性**：此 mod 仅针对 `net.minecraft.Util$OS`，若未来 Minecraft 版本重构了该方法或类路径，可能需要同步更新目标描述符。
- **日志级别**：当前使用 `LOGGER.info` 进行记录，若需改为更细粒度（如 `debug`），可自行调整。
- **安全性**：该 mod 仅作防御层，不能完全替代其他安全措施。

## 示例日志输出

```
[SecurityGuard] Blocked attempt to open URL: https://malicious-site.com from class: com.example.badmod.BadModClass
```

## 许可证

本代码基于 [MIT License](https://opensource.org/licenses/MIT) 发布，允许自由使用、修改和分发，但需保留原始版权声明。

---

*Made with ❤️ for a safer Minecraft experience.*

