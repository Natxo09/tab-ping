package net.natxo.pingtabmod.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerTabOverlay.class)
public class PlayerListHudMixin {

    @ModifyArg(
        method = "render",
        at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(II)I", ordinal = 0),
        index = 0
    )
    private int increaseEntryWidth(int originalWidth) {
        return originalWidth + 30;
    }

    @Inject(method = "renderPingIcon", at = @At("HEAD"), cancellable = true)
    protected void renderPingAsNumber(GuiGraphics context, int width, int x, int y, PlayerInfo entry, CallbackInfo ci) {
        ci.cancel();

        Minecraft client = Minecraft.getInstance();
        if (client == null) return;

        Font font = client.font;
        if (font == null) return;

        int ping = net.natxo.pingtabmod.DevPingManager.getPingForPlayer(entry.getLatency());

        int color;
        if (ping < 150) {
            color = 0xFF00FF00;
        } else if (ping < 300) {
            color = 0xFFFFFF00;
        } else if (ping < 600) {
            color = 0xFFFF5555;
        } else {
            color = 0xFFAA0000;
        }

        String pingText = ping > 9999 ? "9999+" : String.valueOf(ping);

        int textWidth = font.width(pingText);
        int pingX = x + width - textWidth - 2;

        context.drawString(font, pingText, pingX, y, color, false);
    }
}
