package xyz.iwolfking.woldsvaults.mixins.vaulthunters.custom;

import iskallia.vault.client.gui.overlay.VaultBarOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.iwolfking.woldsvaults.api.data.level.ClientPersonalLevelCapData;

@Mixin(value = VaultBarOverlay.class, remap = false)
public class MixinVaultBarOverlay {

    @Shadow public static Component vaultLevelComponent;

    @Inject(method = "onVaultLevelChanged", at = @At("TAIL"))
    private static void injectLevelCap(int vaultLevel, CallbackInfo ci) {
        if (Minecraft.getInstance().level != null) {
            int cap = Math.min(ClientPersonalLevelCapData.getResearchCapData().currentCap(), ClientPersonalLevelCapData.getGlobalLevelCap());
            if (cap < 100) {
                vaultLevelComponent = new TextComponent(vaultLevel + " / " + cap);
            }
        }
    }
}
