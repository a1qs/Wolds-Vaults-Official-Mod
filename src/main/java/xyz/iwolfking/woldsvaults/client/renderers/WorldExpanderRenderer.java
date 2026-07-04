package xyz.iwolfking.woldsvaults.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3d;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import iskallia.vault.client.util.ClientScheduler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import xyz.iwolfking.woldsvaults.blocks.tiles.WorldExpanderTileEntity;
import xyz.iwolfking.woldsvaults.init.ModItems;

public class WorldExpanderRenderer implements BlockEntityRenderer<WorldExpanderTileEntity> {
    private int anim = 0;
    public WorldExpanderRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(WorldExpanderTileEntity blockEntity, float partialTicks, PoseStack matrixStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        Minecraft mc = Minecraft.getInstance();
        BakedModel model = mc.getBlockRenderer().getBlockModel(blockEntity.getBlockState());






        float f = blockEntity.animationTick + partialTicks;
        float f1 = (blockEntity.animationTick + partialTicks) * -0.025F * (180F / (float)Math.PI);
        float f2 = Mth.sin(f * 0.1F) / 2.0F + 0.5F;
        f2 = f2 * f2 + f2;
        matrixStack.pushPose();
        matrixStack.translate(0.125D, 0.125D, 0.125D);
        Vector3f vector3f = new Vector3f(0.5F, 0.5F, 0.5F);
        vector3f.normalize();
        matrixStack.mulPose(vector3f.rotationDegrees(f1));
        matrixStack.scale(0.75F, 0.75F, 0.75F);
        mc.getBlockRenderer().getModelRenderer().renderModel(
                matrixStack.last(),
                bufferSource.getBuffer(RenderType.solid()),
                blockEntity.getBlockState(),
                model,
                1.0F,
                1.0F,
                1.0F,
                combinedLight,
                combinedOverlay
        );
        matrixStack.popPose();


        if (!blockEntity.shouldAnimate()) {
            return;  // If it's not animating, don't render anything.
        }

        Level world = blockEntity.getLevel();
        if (world != null) {
            ItemStack itemStack = new ItemStack(ModItems.POWER_SHARD);

            matrixStack.pushPose();
            this.renderItem(matrixStack, bufferSource, combinedLight, combinedOverlay, 1.5F, 0.65F, itemStack, blockEntity, partialTicks);
            matrixStack.popPose();
        }


    }

    private void renderItem(PoseStack matrixStack, MultiBufferSource buffer, int lightLevel, int overlay, float yOffset, float scale, ItemStack itemStack, WorldExpanderTileEntity blockEntity, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        matrixStack.pushPose();

        matrixStack.translate(0.5, yOffset, 0.5);
        matrixStack.scale(scale, scale, scale);

        // Use ticks for smooth animation
        float lerp = Mth.lerp(partialTicks, blockEntity.ticksSpinningOld, blockEntity.ticksSpinning);

        // Progress of the animation (0.0 at start, 1.0 at end)
        float progress = lerp / (float) blockEntity.maxSpinTicks;
        float acceleratedProgress = (0.1f + progress) * progress * progress;
        float baseRotationSpeed = 20.0F;
        float rotationDegrees = (acceleratedProgress * baseRotationSpeed * lerp) % 360;

        matrixStack.mulPose(Quaternion.fromXYZ(0.0F, (float) Math.toRadians(rotationDegrees), 0.0F));

        BakedModel bakedModel = minecraft.getItemRenderer().getModel(itemStack, null, null, 0);
        minecraft.getItemRenderer().render(itemStack, ItemTransforms.TransformType.FIXED, true, matrixStack, buffer, lightLevel, overlay, bakedModel);

        matrixStack.popPose();
    }
}
