package xyz.iwolfking.woldsvaults.mixins.vaulthunters.objectives;

import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.objective.ScavengerBingoObjective;
import iskallia.vault.item.BasicScavengerItem;
import iskallia.vault.world.data.ServerVaults;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ScavengerBingoObjective.class, remap = false)
public class MixinScavengerBingoObjective {
    @Shadow
    private boolean hasValidVaultId(ItemStack stack, Vault vault) {
        throw new AssertionError(); // dummy
    }

    @Inject(method = "countItemFromWorld", at = @At(value = "INVOKE", target = "Liskallia/vault/core/vault/objective/scavenger/ScavengerBingoTile;isCompleted()Z"))
    private void alwaysApplyTagWorld(ItemStack stack, CallbackInfo ci) {
        if (stack.getItem() instanceof BasicScavengerItem) stack.getOrCreateTag().putBoolean("ScavBingoCounted", true);

    }

    @Inject(method = "countItem", at = @At(value = "INVOKE", target = "Liskallia/vault/core/vault/objective/ScavengerBingoObjective;get(Liskallia/vault/core/data/key/GenericFieldKey;)Ljava/lang/Object;", ordinal = 1))
    private void alwaysApplyTag(ItemStack stack, Vault vault, CallbackInfo ci) {
        stack.getOrCreateTag().putBoolean("ScavBingoCounted", true);
    }
}
