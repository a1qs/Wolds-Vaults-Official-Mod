package xyz.iwolfking.woldsvaults.blocks;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.iwolfking.woldsvaults.WoldsVaults;
import xyz.iwolfking.woldsvaults.api.data.level.WorldExpansionData;
import xyz.iwolfking.woldsvaults.api.util.ComponentUtils;
import xyz.iwolfking.woldsvaults.blocks.tiles.WorldExpanderTileEntity;
import xyz.iwolfking.woldsvaults.init.ModBlocks;
import xyz.iwolfking.woldsvaults.items.world.PowerShardItem;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class WorldExpanderBlock extends BaseEntityBlock {
    private static final VoxelShape SHAPE = Shapes.box(.1, .1, .1, .9, .9, .9);
    private static final int OVERWORLD_MULTIPLIER = 1;
    private static final int NETHER_MULTIPLIER = 8;
    private static final int END_MULTIPLIER = 4;


    public WorldExpanderBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable BlockGetter pLevel, List<Component> pTooltip, @NotNull TooltipFlag pFlag) {
        pTooltip.add(new TextComponent("Allows for expansion of the World Border").withStyle(ChatFormatting.YELLOW));
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter getter, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState pState, Level pLevel, @NotNull BlockPos pPos, @NotNull Player pPlayer, @NotNull InteractionHand pHand, @NotNull BlockHitResult pHit) {
        if (pLevel.isClientSide() || pHand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }

        // Handle non-crystal interactions
        if (!(pPlayer.getMainHandItem().getItem() instanceof PowerShardItem shardMain)) {
            handleWorldBorderInfo(pPlayer);
            return InteractionResult.SUCCESS;
        }

        MinecraftServer srv = ServerLifecycleHooks.getCurrentServer();
        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);


        List<ServerLevel> validDimensions = Stream.of(
                srv.getLevel(Level.OVERWORLD),
                srv.getLevel(Level.NETHER),
                srv.getLevel(Level.END)
        ).filter(Objects::nonNull).toList();


        if(blockEntity instanceof WorldExpanderTileEntity expanderEntity) {
            if (expanderEntity.getLevel() == null) return InteractionResult.PASS;
            if (expanderEntity.shouldAnimate()) return InteractionResult.PASS;

            WorldExpansionData data = WorldExpansionData.get(srv);

            double powerCrystalIncrease = shardMain.getIncrease();
            int handCount = pPlayer.getMainHandItem().getCount();


            for (ServerLevel dimension : validDimensions) {
                WorldBorder dimensionBorder = dimension.getWorldBorder();

                double blocksExpanded = calculateDimensionSpecificExpansion(dimension, powerCrystalIncrease, handCount);
                double newSize = dimensionBorder.getSize() + blocksExpanded;
                if(newSize >= WorldBorder.MAX_SIZE) {
                    WoldsVaults.LOGGER.error("Cannot increase border in {}, size would be {} but the max allowed value is {}", dimension.dimension(), newSize, 5.9999968E7);
                    pPlayer.displayClientMessage(new TextComponent("Please report this to a server admin, see logs for more information.").withStyle(ChatFormatting.RED), true);
                    return InteractionResult.FAIL;
                }
                dimensionBorder.lerpSizeBetween(dimensionBorder.getSize(), newSize, 1000);
            }

            expanderEntity.setBroadcastMessage(pPlayer.getDisplayName().getString(), powerCrystalIncrease * handCount);


            if (!pPlayer.getAbilities().instabuild) {
                pPlayer.getMainHandItem().setCount(0);
            }

            data.addContribution(pPlayer.getUUID(), handCount);
            expanderEntity.getLevel().playSound(null, pPos, SoundEvents.BEACON_POWER_SELECT, SoundSource.BLOCKS, 0.75F, 0.9F);

            expanderEntity.resetSpinTime();
            expanderEntity.setChanged();
            pLevel.sendBlockUpdated(pPos, expanderEntity.getBlockState(), expanderEntity.getBlockState(), Block.UPDATE_ALL);

        }



        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if (pLevel.isClientSide() && pBlockEntityType == ModBlocks.WORLD_EXPANDER_TILE_ENTITY) {
            return WorldExpanderTileEntity::clientTick;
        }
        return pLevel.isClientSide() ? WorldExpanderTileEntity::clientTick : WorldExpanderTileEntity::serverTick;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        return new WorldExpanderTileEntity(pPos, pState);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.INVISIBLE;
    }

    private double calculateDimensionSpecificExpansion(ServerLevel dimension, double baseIncrease, int itemCount) {
        if (dimension.dimension() == Level.OVERWORLD) {
            return itemCount * baseIncrease * OVERWORLD_MULTIPLIER;
        } else if (dimension.dimension() == Level.NETHER) {
            return itemCount * baseIncrease * NETHER_MULTIPLIER;
        } else if (dimension.dimension() == Level.END) {
            return itemCount * baseIncrease * END_MULTIPLIER;
        }
        return itemCount * baseIncrease;
    }

    private static void handleWorldBorderInfo(Player player) {
        double worldBorderSize = player.getLevel().getWorldBorder().getSize();
        player.displayClientMessage(new TextComponent("Current World Border size diameter: " + worldBorderSize), true);
    }

    @Override
    public @NotNull MutableComponent getName() {
        return ComponentUtils.wavingComponent(super.getName(), 0xF7C707, 0.1F, 0.4F);
    }
}
