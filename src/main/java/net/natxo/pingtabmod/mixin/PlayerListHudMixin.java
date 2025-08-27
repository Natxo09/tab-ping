package net.natxo.pingtabmod.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerListHud.class)
public class PlayerListHudMixin {
    
    private static final int PING_WIDTH = 40; // Espacio reservado para el ping numérico
    
    /**
     * Modifica el ancho calculado para cada entrada del tab para dar más espacio al ping
     */
    @ModifyArg(
        method = "render",
        at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(II)I", ordinal = 0),
        index = 0
    )
    private int increaseEntryWidth(int originalWidth) {
        // Añadir espacio extra para el ping numérico
        return originalWidth + 30; // Añadir 30 píxeles más de ancho
    }
    
    /**
     * Replace ONLY the ping icon rendering with numeric ping display
     */
    @Inject(method = "renderLatencyIcon", at = @At("HEAD"), cancellable = true)
    private void renderPingAsNumber(DrawContext context, int width, int x, int y, PlayerListEntry entry, CallbackInfo ci) {
        // Cancel the default ping icon rendering
        ci.cancel();
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return;
        
        TextRenderer textRenderer = client.textRenderer;
        if (textRenderer == null) return;
        
        // Get the ping value
        int ping = net.natxo.pingtabmod.DevPingManager.getPingForPlayer(entry.getLatency());
        
        // Determine color based on ping
        int color;
        if (ping < 150) {
            color = 0x00FF00; // Green
        } else if (ping < 300) {
            color = 0xFFFF00; // Yellow
        } else if (ping < 600) {
            color = 0xFF5555; // Light Red
        } else if (ping < 1000) {
            color = 0xAA0000; // Dark Red
        } else {
            color = 0xAA0000; // Dark Red for very high ping
        }
        
        // Format ping text
        String pingText;
        if (ping > 9999) {
            pingText = "9999+";
        } else if (ping > 999) {
            pingText = String.valueOf(ping);
        } else {
            pingText = String.valueOf(ping);
        }
        
        // Draw the ping text with more space
        int textWidth = textRenderer.getWidth(pingText);
        int pingX = x + width - textWidth - 2;
        
        // Draw the ping text
        context.drawText(textRenderer, pingText, pingX, y, color, false);
    }
}