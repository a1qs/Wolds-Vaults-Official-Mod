package xyz.iwolfking.woldsvaults.modifiers.vault;

import com.google.gson.annotations.Expose;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.modifier.spi.ModifierContext;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.core.world.data.entity.EntityPredicate;
import iskallia.vault.core.world.storage.VirtualWorld;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.EventPriority;
import xyz.iwolfking.woldsvaults.WoldsVaults;

import java.util.Random;

public class InfernalMobModifier extends VaultModifier<InfernalMobModifier.Properties> {
    public InfernalMobModifier(ResourceLocation id, Properties properties, Display display) {
        super(id, properties, display);
        this.setDescriptionFormatter((t, p, s) -> t.formatted((int)Math.abs(p.getChance() * s * 100.0)));
    }

    @Override
    public void initServer(VirtualWorld world, Vault vault, ModifierContext context) {
        CommonEvents.ENTITY_SPAWN.register(context.getUUID(), EventPriority.HIGHEST, event -> {
            if (event.getEntity().level == world) {
                Entity spawnedEntity = event.getEntity();
                if (spawnedEntity instanceof LivingEntity entity) {
                    if ((this.properties).filter.test(entity)) {
                        Random random = entity.level.getRandom();
                       if(random.nextDouble() < this.properties.getChance()) {
                           WoldsVaults.LOGGER.error("Tried to add an entity because of modifier: {}", this.getId());
                       }
                    }

                }
            }
        });
    }

    public static class Properties {
        @Expose
        private final EntityPredicate filter;
        @Expose
        private final double chance;

        @Expose
        private final int amount;

        public Properties(EntityPredicate filter, double chance, int amount) {
            this.filter = filter;
            this.chance = chance;
            this.amount = amount;
        }

        public EntityPredicate getFilter() {
            return this.filter;
        }


        public double getChance() {
            return this.chance;
        }
        public int getAmount() {
            return this.amount;
        }
    }
}
