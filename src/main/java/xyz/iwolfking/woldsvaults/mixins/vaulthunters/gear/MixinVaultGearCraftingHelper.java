package xyz.iwolfking.woldsvaults.mixins.vaulthunters.gear;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import iskallia.vault.gear.crafting.VaultGearCraftingHelper;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.gear.modification.GearModification;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModGearAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(value = VaultGearCraftingHelper.class, remap = false)
public class MixinVaultGearCraftingHelper {

    @ModifyReturnValue(method = "reducePotential", at = @At("RETURN"))
    private static boolean infinitePotential(boolean original, ItemStack stack, Player player, GearModification action)  {
        if (!stack.isEmpty() && stack.getItem() instanceof VaultGearItem) {
            if (!original) {
                VaultGearData data = VaultGearData.read(stack);
                int potentialReduction = ModConfigs.VAULT_GEAR_MODIFICATION_CONFIG.getPotentialUsed(action);
                int potential = (Integer)data.getFirstValue(ModGearAttributes.CRAFTING_POTENTIAL).orElse(0);
                data.createOrReplaceAttributeValue(ModGearAttributes.CRAFTING_POTENTIAL, potential - potentialReduction);
                data.write(stack);
                return true;
            }
        }
        return false;
    }
}
