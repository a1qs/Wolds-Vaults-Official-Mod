package xyz.iwolfking.woldsvaults.blocks.tiles;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;
import xyz.iwolfking.woldsvaults.init.ModBlocks;

import javax.annotation.Nullable;

public class WorldExpanderTileEntity extends BlockEntity {
    public int ticksSpinning = 0;
    public int ticksSpinningOld = 0;
    public final int maxSpinTicks = 65;
    private boolean isAnimating = false;
    private String playerName;
    private  double blocksExpanded;

    public int animationTick;

    public WorldExpanderTileEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.WORLD_EXPANDER_TILE_ENTITY, pos, state);
    }

    public static <T extends BlockEntity> void clientTick(Level world, BlockPos pos, BlockState state, T tile) {
        if (tile instanceof WorldExpanderTileEntity entity) {
            if (entity.isAnimating && entity.ticksSpinning < entity.maxSpinTicks) {
                entity.ticksSpinningOld = entity.ticksSpinning;
                entity.ticksSpinning++;
            }
            entity.animationTick++;
        }
    }


    public static <T extends BlockEntity> void serverTick(Level world, BlockPos pos, BlockState state, T tile) {
        if (tile instanceof WorldExpanderTileEntity expanderEntity) {
            if (expanderEntity.isAnimating && expanderEntity.ticksSpinning < expanderEntity.maxSpinTicks) {
                expanderEntity.ticksSpinningOld = expanderEntity.ticksSpinning;
                expanderEntity.ticksSpinning++;
                expanderEntity.setChanged();
            } else if (expanderEntity.ticksSpinning >= expanderEntity.maxSpinTicks) {
                expanderEntity.stopAnimation();
                world.playSound(null, pos, SoundEvents.ENDER_EYE_DEATH, SoundSource.BLOCKS, 1.0f, 1.25f);
                BlockPos crystalPos = new BlockPos(pos.getX(), pos.getY() + 1.5, pos.getZ());
                world.levelEvent(LevelEvent.PARTICLES_EYE_OF_ENDER_DEATH, crystalPos,0);

                ServerLifecycleHooks.getCurrentServer().getPlayerList()
                        .broadcastMessage(new TextComponent(expanderEntity.playerName + " has increased the World Border by " + expanderEntity.blocksExpanded + " blocks!").withStyle(ChatFormatting.YELLOW), ChatType.GAME_INFO, Util.NIL_UUID);
            }
        }
    }

    public void resetSpinTime() {
        this.ticksSpinningOld = 0;
        this.ticksSpinning = 0;
        this.isAnimating = true;
        this.setChanged();
    }

    public void stopAnimation() {
        this.isAnimating = false;
        this.ticksSpinning = 0;
        this.ticksSpinningOld = 0;


        this.setChanged();
        if (this.level != null && !this.level.isClientSide()) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
        }
    }

    public boolean shouldAnimate() {
        return isAnimating;
    }

    public void setBroadcastMessage(String playerName, double blocksExpanded) {
        this.playerName = playerName;
        this.blocksExpanded = blocksExpanded;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);

        tag.putBoolean("IsAnimating", this.isAnimating);
        tag.putInt("TicksSpinning", this.ticksSpinning);
        tag.putInt("TicksSpinningOld", this.ticksSpinningOld);
    }

    public void load(@NotNull CompoundTag tag) {
        super.load(tag);

        // Read animation state and ticks from NBT
        this.isAnimating = tag.getBoolean("IsAnimating");
        this.ticksSpinning = tag.getInt("TicksSpinning");
        this.ticksSpinningOld = tag.getInt("TicksSpinningOld");
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Override
    @Nullable
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
