package xyz.iwolfking.woldsvaults.mixins.sophisticatedstorage;

import net.p3pp3rf1y.sophisticatedstorage.upgrades.compression.CompressionInventoryPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.iwolfking.woldsvaults.WoldsVaults;

import java.util.HashMap;
import java.util.Map;

@Mixin(value = CompressionInventoryPart.class, remap = false)
public abstract class MixinCompressionInventoryPart {
    @Shadow
    private Map<Integer, ?> slotDefinitions = new HashMap<>();

    @Inject(method = "getPrevSlotMultiplier", at = @At("HEAD"), cancellable = true)
    private void getPrevSlotMultiplier(int slot, CallbackInfoReturnable<Integer> cir) {
        Object slotDef = slotDefinitions.get(slot);
        if (slotDef == null) {
            WoldsVaults.LOGGER.error("SlotDefinition is null in some soph storage barrel, MODS, GET THEM");
            cir.setReturnValue(1);
        }
    }

}
