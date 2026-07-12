package xyz.iwolfking.woldsvaults.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import org.spongepowered.asm.mixin.Mutable;
import xyz.iwolfking.woldsvaults.api.util.ComponentUtils;
import xyz.iwolfking.woldsvaults.blocks.tiles.StatisticsTileEntity;
import xyz.iwolfking.woldsvaults.blocks.tiles.StatisticsTileEntity;
import xyz.iwolfking.woldsvaults.init.ModItems;

import java.util.ArrayList;
import java.util.List;

public class StatisticsTileRenderer implements BlockEntityRenderer<StatisticsTileEntity> {



    public StatisticsTileRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(
            StatisticsTileEntity tile,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int light,
            int overlay
    ) {

        List<MutableComponent> lines = new ArrayList<>();

        if (tile.hasReachedMaxLevelCap()) {
            lines.add(new TextComponent("Total Contributions: ").withStyle(Style.EMPTY.withColor(0xfffaa3))
                    .append(ComponentUtils.wavingComponent(new TextComponent(String.valueOf(tile.getTotalContributions())), 0xF7C707, 0.1F, 0.4F)));

            lines.add(new TextComponent(""));
            lines.add(ComponentUtils.wavingComponent(new TextComponent("Max Level Cap Reached"), 0xF7C707, 0.1F, 0.4F));

        } else {
            lines.add(
                    new TextComponent(String.valueOf(tile.getTotalContributions())).withStyle(ChatFormatting.WHITE)
                            .append(new TextComponent(" / ").withStyle(Style.EMPTY.withColor(0xfffaa3)))
                            .append(new TextComponent(String.valueOf(tile.getToNextThreshold())).withStyle(ChatFormatting.GOLD)));
            lines.add(new TextComponent("Progress to next threshold:").withStyle(Style.EMPTY.withColor(0xfffaa3)));

            lines.add(new TextComponent("Current Level cap: ").withStyle(Style.EMPTY.withColor(0xfffaa3))
                    .append(new TextComponent(String.valueOf(tile.getGlobalLevelCap())).withStyle(Style.EMPTY.withColor(ChatFormatting.GOLD))));
        }

        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;

        poseStack.pushPose();
        poseStack.translate(0.5, 1.7, 0.5);
        poseStack.mulPose(mc.getEntityRenderDispatcher().cameraOrientation());
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(180));

        float baseScale = 0.02F;
        poseStack.scale(baseScale, baseScale, baseScale);

        // Render floating text lines
        for (int i = 0; i <= lines.size() - 1; i++) {
            MutableComponent line = lines.get(i);

            float y = (lines.size() - i - 1) * 10;
            float x = -font.width(line) / 2f;

            font.drawInBatch(line, x, y, 0xFFFFFF, true,
                    poseStack.last().pose(), buffer, false, 0, light);
        }
        poseStack.popPose();

        // Render border
        if (mc.player != null && mc.player.getMainHandItem().getItem() == ModItems.CONFIGURABLE_FLOATING_TEXT) {
            double position = ((mc.player.tickCount * 20 + (int)(partialTicks * 20.0F)) % 2000) / 2000.0;
            int red = (int)(Math.cos(position * Math.PI * 2.0) * 127.0 + 128.0);
            int green = (int)(Math.cos((position + 0.3333333333333333) * Math.PI * 2.0) * 127.0 + 128.0);
            int blue = (int)(Math.cos((position + 0.6666666666666666) * Math.PI * 2.0) * 127.0 + 128.0);

            VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.lines());
            LevelRenderer.renderLineBox(poseStack, vertexconsumer, 0.1, 0.1, 0.1, 0.9, 0.9, 0.9, red, green, blue, 1.0F, red, green, blue);
        }

    }
}
