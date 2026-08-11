package xyz.iwolfking.woldsvaults.mixins.vaulthunters.gear;

import iskallia.vault.gear.crafting.VaultGearCraftingHelper;
import iskallia.vault.gear.modification.GearModification;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Random;

@Mixin(value = GearModification.class, remap = false)
public class MixinGearModification {

    /**
     * @author alyx
     * @reason lazy
     */
    @Overwrite
    public GearModification.Result canApply(ItemStack stack, ItemStack materialStack, Player player, Random rand) {
        GearModification instance = (GearModification) (Object) this;
        VaultGearCraftingHelper.reducePotential(stack.copy(), player, instance);
        return instance.doModification(stack.copy(), materialStack, player, rand);
    }
}
