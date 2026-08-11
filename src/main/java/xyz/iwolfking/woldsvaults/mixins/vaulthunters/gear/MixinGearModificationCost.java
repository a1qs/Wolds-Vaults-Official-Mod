package xyz.iwolfking.woldsvaults.mixins.vaulthunters.gear;

import iskallia.vault.config.gear.VaultGearModificationConfig;
import iskallia.vault.gear.modification.GearModification;
import iskallia.vault.gear.modification.GearModificationCost;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModGearModifications;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = GearModificationCost.class, remap = false)
public class MixinGearModificationCost {
    /**
     * @author
     * @reason
     */
    @Overwrite
    public static GearModificationCost getCost(int gearPotential, float maxPotential, GearModification modification, int level) {
        VaultGearModificationConfig.OperationConfig cfg = ModConfigs.VAULT_GEAR_MODIFICATION_CONFIG.getOperationConfig(modification);
        if (cfg == null) {
            return new GearModificationCost(Integer.MAX_VALUE, Integer.MAX_VALUE);
        } else {
            float maxPotentialMultiplier = Math.max(maxPotential / 200.0F, 1.0F);
            float multiplier = cfg.getBaseCostMultiplier() * maxPotentialMultiplier;
            float basePlating = Math.max(4.0F * multiplier, 1.0F);
            float baseBronze = 35.0F * multiplier;
            if (modification == ModGearModifications.IMPROVE_GEARLEVEL) {
                basePlating = level * 1.0F;
                baseBronze = level * cfg.getBaseCostMultiplier();



                if (gearPotential < 0) {
                    int absPotential = Math.abs(gearPotential);
                    float negativeMultiplier = cfg.getNegativePotentialCostMultiplier();
                    float potentialPercent = Mth.clamp(Math.max(0.01F, maxPotential / Math.max(gearPotential, 1.0F)), 1.0F, 25.0F) * (1 + ((float) absPotential * negativeMultiplier/ 10));

                    float negativeCostPlating = Math.max(Math.round(basePlating * potentialPercent), 1);
                    float negativeCostBronze = Math.max(Math.round(baseBronze * potentialPercent), 1);

                    return new GearModificationCost((int)negativeCostPlating, (int)negativeCostBronze);
                }


                float potentialPercent = Mth.clamp(Math.max(0.01F, maxPotential / Math.max(gearPotential, 1.0F)), 1.0F, 25.0F);


                float positiveCostPlating = Math.max(Math.round(basePlating * potentialPercent), 1);
                float positiveCostBronze = Math.max(Math.round(baseBronze * potentialPercent), 1);
                return new GearModificationCost((int)positiveCostPlating, (int)positiveCostBronze);

            } else {
                if (gearPotential < 0) {
                    int absPotential = Math.abs(gearPotential);
                    float negativeMultiplier = cfg.getNegativePotentialCostMultiplier();
                    float potentialPercent = Mth.clamp(Math.max(0.01F, 1.0F - gearPotential / maxPotential), 0.01F, 1.0F) * (1 + ((float) absPotential * negativeMultiplier / 10));


                    float negativeCostPlating = Math.max(Math.round(basePlating * potentialPercent ), 1);
                    float negativeCostBronze = Math.max(Math.round(baseBronze * potentialPercent), 1);

                    return new GearModificationCost((int)negativeCostPlating, (int)negativeCostBronze);
                }

                float potentialPercent = Mth.clamp(Math.max(0.01F, 1.0F - gearPotential / maxPotential), 0.01F, 1.0F);

                return new GearModificationCost(Math.max(Math.round(basePlating), 1), Math.max(3 + Math.round((baseBronze - 3.0F) * potentialPercent), 1), cfg.getAdditionalItemCost() == -1.0F ? -1 : Math.round(cfg.getAdditionalItemCost()));
            }
        }
    }
}
