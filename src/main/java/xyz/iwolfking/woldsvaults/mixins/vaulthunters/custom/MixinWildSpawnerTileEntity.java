package xyz.iwolfking.woldsvaults.mixins.vaulthunters.custom;

import iskallia.vault.VaultMod;
import iskallia.vault.block.entity.BaseSpawnerTileEntity;
import iskallia.vault.block.entity.WildSpawnerTileEntity;
import iskallia.vault.config.WildSpawnerConfig;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.VaultLevel;
import iskallia.vault.core.vault.objective.Objectives;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.world.data.ServerVaults;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.Nullable;
import java.util.Iterator;

@Mixin(value = WildSpawnerTileEntity.class, remap = false)
public class MixinWildSpawnerTileEntity extends BaseSpawnerTileEntity {
    @Shadow @Nullable private WildSpawnerConfig.SpawnerGroup spawnerGroup;
    @Shadow private int playerCheckCooldown;

    protected MixinWildSpawnerTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    /**
     * @author iwolfking
     * @reason Override method to spawn enhanced wraiths
     */
    @Overwrite
    private static void spawnEntity(Level level, BlockPos blockPos, ServerLevel serverLevel, WildSpawnerConfig.SpawnerGroup spawnerGroup) {
        WildSpawnerConfig.SpawnerEntity spawnerEntity = (WildSpawnerConfig.SpawnerEntity) spawnerGroup.entities.getRandom(level.random);
        if (spawnerEntity == null) {
            VaultMod.LOGGER.warn("Wild Spawner failed to spawn as there was no valid entity found in config for spawn group with minLevel {}", spawnerGroup.minLevel);
        } else {
            if(spawnerEntity.type.toString().equals("quark:wraith")) {
                spawnBuffedWraith(blockPos, serverLevel, spawnerEntity.type, spawnerEntity.nbt, false, () -> {
                    VaultMod.LOGGER.warn("Wild Spawner failed to spawn \"{}\" as it does not exist in entityType registry", spawnerEntity.type);
                });
            }
            else {
                spawnEntity(blockPos, serverLevel, spawnerEntity.type, spawnerEntity.nbt, false, () -> {
                    VaultMod.LOGGER.warn("Wild Spawner failed to spawn \"{}\" as it does not exist in entityType registry", spawnerEntity.type);
                });
            }

        }

    }

    @Unique
    @Nullable
    private static Entity spawnBuffedWraith(BlockPos blockPos, ServerLevel serverLevel, ResourceLocation entityName, @Nullable CompoundTag entityNbt, boolean isPersistent, Runnable logEntityTypeMissing) {
        EntityType<?> entityType = (EntityType) ForgeRegistries.ENTITIES.getValue(entityName);
        if (entityType == null) {
            logEntityTypeMissing.run();
            return null;
        } else {
            Entity entity = entityType.spawn(serverLevel, (ItemStack)null, (Player)null, blockPos, MobSpawnType.SPAWNER, false, false);
            if (entityNbt != null) {
                CompoundTag entityTag = entity.saveWithoutId(new CompoundTag());
                entityTag.merge(entityNbt.copy());
                entity.load(entityTag);
            }

            if (entity instanceof Mob) {
                Mob mob = (Mob)entity;
                if (isPersistent) {
                    mob.setPersistenceRequired();
                }
                mob.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 32567, 10));
            }


            return entity;
        }
    }


    /**
     * @author iwolfking
     * @reason Overwrite wild spawners for Haunted Braziers
     */
    @Overwrite
    private void initSpawnerGroup() {
        if (this.spawnerGroup == null) {
            int vaultLevel = (Integer) ServerVaults.get(this.level).map((vault) -> {
                return ((VaultLevel) vault.get(Vault.LEVEL)).get();
            }).orElse(0);
            Iterator var2 = ModConfigs.WILD_SPAWNER.spawnerGroups.iterator();

            Vault vaultObj = ServerVaults.get(this.level).get();
            String objective = vaultObj.get(Vault.OBJECTIVES).get(Objectives.KEY);
            boolean hasSpooky = false;

            while (true) {
                WildSpawnerConfig.SpawnerGroup sg;
                do {
                    do {
                        if (!var2.hasNext()) {
                            if (this.spawnerGroup != null) {
                                this.playerCheckCooldown = Math.min(this.spawnerGroup.blockCheckRadius / 4, 10);
                            }

                            return;
                        }

                        sg = (WildSpawnerConfig.SpawnerGroup) var2.next();
                    } while (sg.minLevel > vaultLevel);
                } while (this.spawnerGroup != null && sg.minLevel <= this.spawnerGroup.minLevel);

                if(!objective.equals("haunted_braziers")) {
                    hasSpooky = vaultObj.get(Vault.MODIFIERS).getModifiers().stream().anyMatch(vaultModifier -> (
                            vaultModifier.getId().toString().equals("the_vault:spooky")
                    ));
                }

                if(objective.equals("haunted_braziers") || hasSpooky ) {
                    this.spawnerGroup = ModConfigs.WILD_SPAWNER.spawnerGroups.get(3);
                }
                else {
                    this.spawnerGroup = sg;
                }

            }
        }
    }






}
