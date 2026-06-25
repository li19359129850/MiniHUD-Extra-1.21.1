package com.ecaree.minihudextra.mixin.render;

import com.ecaree.minihudextra.config.Configs;
import com.mojang.blaze3d.systems.RenderSystem;
import fi.dy.masa.malilib.config.HudAlignment;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.GuiUtils;
import fi.dy.masa.minihud.event.RenderHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper.Argb;
import org.joml.Matrix4fStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(RenderHandler.class)
public class MixinRenderHandler {
    /**
     * 基于
     * https://github.com/sakura-ryoko/malilib/blob/pre-rewrite/fabric/1.20.6/src/main/java/fi/dy/masa/malilib/render/RenderUtils.java#L367-L441
     */
    @Redirect(
            method = "onRenderGameOverlayPost",
            at = @At(
                    value = "INVOKE",
                    target = "Lfi/dy/masa/malilib/render/RenderUtils;renderText(IIDIILfi/dy/masa/malilib/config/HudAlignment;ZZZLjava/util/List;Lnet/minecraft/client/gui/DrawContext;)I"
            )
    )
    private int onRenderText(int xOff, int yOff, double scale, int textColor, int bgColor, HudAlignment alignment, boolean useBackground, boolean useShadow, boolean useStatusShift, List<String> lines, DrawContext drawContext) {
        if (!Configs.Generic.MODIFY_COLORS.getBooleanValue() && !Configs.Generic.TEXT_OUTLINE.getBooleanValue()) {return RenderUtils.renderText(xOff, yOff, scale, textColor, bgColor, alignment, useBackground, useShadow, useStatusShift, lines, drawContext);}
        TextRenderer fontRenderer = MinecraftClient.getInstance().textRenderer;
        final int scaledWidth = GuiUtils.getScaledWindowWidth();
        final int lineHeight = fontRenderer.fontHeight + 2;
        final int contentHeight = lines.size() * lineHeight - 2;
        final int bgMargin = 2;

        if (scale < 0.0125) {
            return 0;
        }

        Matrix4fStack global4fStack = RenderSystem.getModelViewStack();
        boolean scaled = scale != 1.0;

        if (scaled) {
            if (scale != 0) {
                xOff = (int) (xOff * scale);
                yOff = (int) (yOff * scale);
            }

            global4fStack.pushMatrix();
            global4fStack.scale((float) scale, (float) scale, 1.0f);
            RenderSystem.applyModelViewMatrix();
        }

        double posX = xOff + bgMargin;
        double posY = yOff + bgMargin;

        posY = RenderUtils.getHudPosY((int) posY, yOff, contentHeight, scale, alignment);
        posY += RenderUtils.getHudOffsetForPotions(alignment, scale, MinecraftClient.getInstance().player);

        int[] colorValues = new int[] {
                Configs.Colors.LINE_ONE.getIntegerValue(),
                Configs.Colors.LINE_TWO.getIntegerValue(),
                Configs.Colors.LINE_THREE.getIntegerValue(),
                Configs.Colors.LINE_FOUR.getIntegerValue(),
                Configs.Colors.LINE_FIVE.getIntegerValue(),
                Configs.Colors.LINE_SIX.getIntegerValue(),
                Configs.Colors.LINE_SEVEN.getIntegerValue(),
                Configs.Colors.LINE_EIGHT.getIntegerValue(),
                Configs.Colors.LINE_NINE.getIntegerValue(),
                Configs.Colors.LINE_TEN.getIntegerValue(),
                Configs.Colors.LINE_ELEVEN.getIntegerValue(),
                Configs.Colors.LINE_TWELVE.getIntegerValue(),
                Configs.Colors.LINE_THIRTEEN.getIntegerValue(),
                Configs.Colors.LINE_FOURTEEN.getIntegerValue(),
                Configs.Colors.LINE_FIFTEEN.getIntegerValue(),
                Configs.Colors.LINE_SIXTEEN.getIntegerValue(),
                Configs.Colors.LINE_SEVENTEEN.getIntegerValue(),
                Configs.Colors.LINE_EIGHTEEN.getIntegerValue(),
                Configs.Colors.LINE_NINETEEN.getIntegerValue(),
                Configs.Colors.LINE_TWENTY.getIntegerValue(),
                Configs.Colors.LINE_TWENTYONE.getIntegerValue(),
                Configs.Colors.LINE_TWENTYTWO.getIntegerValue(),
                Configs.Colors.LINE_TWENTYTHREE.getIntegerValue(),
                Configs.Colors.LINE_TWENTYFOUR.getIntegerValue(),
                Configs.Colors.LINE_TWENTYFIVE.getIntegerValue(),
                Configs.Colors.LINE_TWENTYSIX.getIntegerValue(),
                Configs.Colors.LINE_TWENTYSEVEN.getIntegerValue(),
                Configs.Colors.LINE_TWENTYEIGHT.getIntegerValue(),
                Configs.Colors.LINE_TWENTYNINE.getIntegerValue(),
                Configs.Colors.LINE_THIRTY.getIntegerValue(),
                Configs.Colors.LINE_FORTY.getIntegerValue(),
                Configs.Colors.LINE_FORTYONE.getIntegerValue(),
                Configs.Colors.LINE_FORTYTWO.getIntegerValue(),
                Configs.Colors.LINE_FORTYTHREE.getIntegerValue(),
                Configs.Colors.LINE_FORTYFOUR.getIntegerValue(),
                Configs.Colors.LINE_FORTYFIVE.getIntegerValue(),
                Configs.Colors.LINE_FORTYSIX.getIntegerValue(),
                Configs.Colors.LINE_FORTYSEVEN.getIntegerValue(),
                Configs.Colors.LINE_FORTYEIGHT.getIntegerValue(),
                Configs.Colors.LINE_FORTYNINE.getIntegerValue(),
                Configs.Colors.LINE_FIFTY.getIntegerValue()
        };

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            int lineColor;

            if (Configs.Generic.MODIFY_COLORS.getBooleanValue()) {
                if (i < colorValues.length) {
                    lineColor = colorValues[i];
                } else {
                    lineColor = textColor;
                }
            } else {
                lineColor = textColor;
            }

            final int width = fontRenderer.getWidth(line);

            switch (alignment) {
                case TOP_RIGHT:
                case BOTTOM_RIGHT:
                    posX = (scaledWidth / scale) - width - xOff - bgMargin;
                    break;
                case CENTER:
                    posX = (scaledWidth / scale / 2) - (width / 2) - xOff;
                    break;
                default:
            }

            final int x = (int) posX;
            final int y = (int) posY;
            posY += lineHeight;

            if (useBackground) {
                RenderUtils.drawRect(x - bgMargin, y - bgMargin, width + bgMargin, bgMargin + fontRenderer.fontHeight, bgColor);
            }

            if (Configs.Generic.TEXT_OUTLINE.getBooleanValue()) {
                OrderedText orderedText = Text.literal(line).asOrderedText();
                int outlineColor;

                if (Configs.Generic.AUTO_OUTLINE_COLOR.getBooleanValue()) {
                    outlineColor = mhex_autoOutlineColor(lineColor);
                } else {
                    outlineColor = Configs.Colors.OUTLINE_COLOR.getIntegerValue();
                }
                fontRenderer.drawWithOutline(orderedText, x , y, lineColor, outlineColor, drawContext.getMatrices().peek().getPositionMatrix(), drawContext.getVertexConsumers(), 15728880);
                drawContext.getVertexConsumers().draw();
            } else {
                drawContext.drawText(fontRenderer, line, x, y, lineColor, useShadow);
            }
        }

        if (scaled) {
            global4fStack.popMatrix();
            RenderSystem.applyModelViewMatrix();
        }

        return contentHeight + bgMargin * 2;
    }

    @Unique
    private static int mhex_autoOutlineColor(int color) {
        if ((color & 0x00FFFFFF) == 0x000000) {
            return 0xFFFFFFFF;
        } else {
            double brightness = Configs.Generic.OUTLINE_COLOR_BRIGHTNESS.getDoubleValue();
            int r = (int)((double) Argb.getRed(color) * brightness);
            int g = (int)((double) Argb.getGreen(color) * brightness);
            int b = (int)((double) Argb.getBlue(color) * brightness);
            return Argb.getArgb(0, r, g, b);
        }
    }
}