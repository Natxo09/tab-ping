package net.natxo.pingtabmod.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerListHud.class)
public class PlayerListHudMixin {

    @Inject(method = "renderLatencyIcon", at = @At("HEAD"), cancellable = true)
    private void renderLatencyIcon(DrawContext context, int width, int x, int y, PlayerListEntry entry, CallbackInfo ci) {
        // Cancel the default renderLatencyIcon method to replace it with our own
        ci.cancel();

        // Get the ping of the player
        int ping = entry.getLatency();

        // Determine the color of the ping text based on the ping value
        Formatting pingColor;
        if (ping < 70) {
            pingColor = Formatting.GREEN;
        } else if (ping < 100) {
            pingColor = Formatting.YELLOW;
        } else if (ping < 200) {
            pingColor = Formatting.GOLD;
        } else {
            pingColor = Formatting.RED;
        }

        // Create the ping text with the appropriate color
        String pingText = pingColor + String.valueOf(ping);

        // Get the TextRenderer from the Minecraft client
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.textRenderer == null) {
            return; // Ensure client and necessary fields are not null
        }

        TextRenderer textRenderer = client.textRenderer;

        // Calculate the new x position based on the width of the tab
        int pingTextWidth = textRenderer.getWidth(pingText);
        int newX = x + width - pingTextWidth; // Calculate position from the right edge of the tab

        // Draw the ping text at the new position
        context.drawText(textRenderer, pingText, newX, y, 0xFFFFFF, false);
    }
}
