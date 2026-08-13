package xyz.iwolfking.woldsvaults.mixins.mekanism;

import iskallia.vault.integration.IntegrationCurios;
import iskallia.vault.network.message.OpenCardDeckMessage;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = OpenCardDeckMessage.class, remap = false)
public class MixinOpenCardDeckMessage {

    @Redirect(method = "lambda$handle$2", at = @At(value = "INVOKE", target = "Lmekanism/common/integration/curios/CuriosIntegration;getCurioStack(Lnet/minecraft/world/entity/LivingEntity;Ljava/lang/String;I)Lnet/minecraft/world/item/ItemStack;"))
    private static ItemStack yappa(LivingEntity entity, String slotType, int slot) {
        return IntegrationCurios.getCurioItemStack(entity, slotType, slot);
    }
}
