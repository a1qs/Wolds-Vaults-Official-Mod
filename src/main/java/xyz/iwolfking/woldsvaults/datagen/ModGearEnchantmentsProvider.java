
package xyz.iwolfking.woldsvaults.datagen;

import cofh.core.init.CoreEnchantments;
import com.cursedcauldron.wildbackport.common.registry.WBEnchantments;
import com.github.alexthe666.alexsmobs.enchantment.AMEnchantmentRegistry;
import com.simibubi.create.Create;
import de.castcrafter.travel_anchors.ModEnchantments;
import iskallia.vault.init.ModItems;
import net.mehvahdjukaar.supplementaries.setup.ModRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import xyz.iwolfking.vhapi.api.datagen.AbstractGearEnchantmentProvider;
import xyz.iwolfking.woldsvaults.WoldsVaults;


public class ModGearEnchantmentsProvider extends AbstractGearEnchantmentProvider {
    protected ModGearEnchantmentsProvider(DataGenerator generator) {
        super(generator, WoldsVaults.MOD_ID);
    }

    @Override
    public void registerConfigs() {
        add("wild_backport", builder -> {
            builder.addEnchantment(WBEnchantments.SWIFT_SNEAK.get(), 20, 1);
        });

        add("draconic_evolution", builder -> {
            builder.addEnchantment(ResourceLocation.fromNamespaceAndPath("draconicevolution", "reaper_enchantment"), 48, 10);
        });

        add("alexsmobs", builder -> {
            builder.addEnchantment(AMEnchantmentRegistry.STRADDLE_BOARDRETURN, 5, 1);
            builder.addEnchantment(AMEnchantmentRegistry.STRADDLE_JUMP, 5, 1);
            builder.addEnchantment(AMEnchantmentRegistry.STRADDLE_LAVAWAX, 5, 1);
            builder.addEnchantment(AMEnchantmentRegistry.STRADDLE_SERPENTFRIEND, 5, 1);
        });

        add("supplementaries", builder -> {
            builder.addEnchantment(ModRegistry.STASIS_ENCHANTMENT.get(), 5, 1);
        });

        add("cofh_core", builder -> {
            builder.addEnchantment(CoreEnchantments.HOLDING.get(), 5, 1);
        });

        add("create", builder -> {
            builder.addEnchantment(ResourceLocation.fromNamespaceAndPath(Create.ID, "potato_recovery"), 5, 1);
            builder.addEnchantment(ResourceLocation.fromNamespaceAndPath(Create.ID, "capacity"), 5, 1);
        });

        add("travel_anchors", builder -> {
            builder.addEnchantment(ModEnchantments.range, 5, 1);
        });
    }
}
