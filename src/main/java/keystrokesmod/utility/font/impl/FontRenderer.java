package keystrokesmod.utility.font.impl;

import keystrokesmod.utility.Utils;
import keystrokesmod.utility.font.CenterMode;
import keystrokesmod.utility.font.IFont;
import keystrokesmod.utility.render.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author TejasLamba2006
 * @since 28/07/2024
 */
public class FontRenderer extends CharRenderer implements IFont {
    private final int[] colorCode = new int[32];
    private static final String colorcodeIdentifiers = "0123456789abcdefklmnor";

    public FontRenderer(Font font) {
        super(font, true, true);
        this.setupMinecraftColorcodes();
        this.setupBoldItalicIDs();
    }

    public void drawString(String text, double x, double y, @NotNull CenterMode centerMode, boolean dropShadow, int color) {
        switch (centerMode) {
            case X:
                if (dropShadow) {
                    this.drawString(text, x - this.getStringWidth(text) / 2 + 0.5, y + 0.5, color, true);
                }
                this.drawString(text, x - this.getStringWidth(text) / 2, y, color, false);
                return;
            case Y:
                if (dropShadow) {
                    this.drawString(text, x + 0.5, y - this.getHeight() / 2 + 0.5, color, true);
                }
                this.drawString(text, x, y - this.getHeight() / 2, color, false);
                return;
            case XY:
                if (dropShadow) {
                    this.drawString(text, x - this.getStringWidth(text) / 2 + 0.5, y - this.getHeight() / 2 + 0.5, color, true);
                }
                this.drawString(text, x - this.getStringWidth(text) / 2, y - this.getHeight() / 2, color, false);
                return;
            default:
            case NONE:
                if (dropShadow) {
                    this.drawString(text, x + 0.5, y + 0.5, color, true);
                }
                this.drawString(text, x, y, color, false);
        }
    }

    @Override
    public boolean isTextSupported(@NotNull String text) {
        return text.chars().noneMatch(c -> c >= 256);
    }

    public void drawString(String text, double x, double y, int color, boolean shadow) {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());

        if (text == null) {
            return;
        }

        if (shadow) {
            drawString(text, x + 1, y + 1, (color & 0xFCFCFC) >> 2 | color & 0xFF000000, false);
        }

//        FontManager.init();

        double alpha = (color >> 24 & 255) / 255f;
        x = (x - 1) * sr.getScaleFactor();
        y = (y - 3) * sr.getScaleFactor() - 0.2;
        GL11.glPushMatrix();
        GL11.glScaled((double) 1 / sr.getScaleFactor(), 1 / (double) sr.getScaleFactor(), 1 / (double) sr.getScaleFactor());
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        ColorUtils.setColor(color);
        GlStateManager.enableTexture2D();
        GlStateManager.bindTexture(this.tex.getGlTextureId());

        for (int index = 0; index < text.length(); index++) {
            char character = text.charAt(index);

            if (character == '§') {
                int colorIndex = 21;

                try {
                    colorIndex = colorcodeIdentifiers.indexOf(text.charAt(index + 1));
                } catch (Exception e) {
                    Utils.handleException(e);
                }

                if (colorIndex < 16) {
                    if (colorIndex < 0) {
                        colorIndex = 15;
                    }

                    if (shadow) {
                        colorIndex += 16;
                    }

                    ColorUtils.setColor(this.colorCode[colorIndex], alpha);
                } else {
                    ColorUtils.setColor(color);
                }

                ++index;
            } else if (character < charData.length) {
                GL11.glBegin(4);
                this.drawChar(charData, character, x, y);
                x += charData[character].width - 8.3 + 0;
                GL11.glEnd();
            }
        }
        GlStateManager.disableBlend();
        GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_DONT_CARE);
        GL11.glPopMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void drawString(String text, double x, double y, int color) {
        drawString(text, x, y, color, false);
    }

    @Override
    public double width(String text) {
        return getStringWidth(text);
    }

    @Override
    public void drawCenteredString(String text, double x, double y, int color) {
        drawString(text, x, y, CenterMode.X, false, color);
    }

    @Override
    public double height() {
        return getHeight();
    }

    public double getStringWidth(String text) {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());

        if (text == null) {
            return 0;
        }

        double width = 0;

        for (int index = 0; index < text.length(); index++) {
            char character = text.charAt(index);

            if (character == '§') {
                index++;
            } else if (character < charData.length) {
                width += charData[character].width - 8.3f + 0;
            }
        }

        return width / (double) sr.getScaleFactor();
    }

    public double getHeight() {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());

        return (this.fontHeight - 8) / (double) sr.getScaleFactor();
    }

    @Override
    public void setFont(Font font) {
        super.setFont(font);
        this.setupBoldItalicIDs();
    }

    @Override
    public void setAntiAlias(boolean antiAlias) {
        super.setAntiAlias(antiAlias);
        this.setupBoldItalicIDs();
    }

    @Override
    public void setFractionalMetrics(boolean fractionalMetrics) {
        super.setFractionalMetrics(fractionalMetrics);
        this.setupBoldItalicIDs();
    }

    private void setupBoldItalicIDs() {
    }

    @SuppressWarnings("unused")
    public void wrapText(@NotNull String text, double x, double y, CenterMode centerMode, boolean shadow, int color, double width) {
        List<String> lines = new ArrayList<>();
        String[] words = text.trim().split(" ");
        StringBuilder line = new StringBuilder();

        for (String word : words) {
            double totalWidth = getStringWidth(line + " " + word);

            if (x + totalWidth >= x + width) {
                lines.add(line.toString());
                line = new StringBuilder(word).append(" ");
                continue;
            }

            line.append(word).append(" ");
        }
        lines.add(line.toString());

        double newY = y - (centerMode == CenterMode.XY || centerMode == CenterMode.Y ? ((lines.size() - 1) * (getHeight() + 5)) / 2 : 0);
        // add x centermode support never !!!!
        for (String s : lines) {
            ColorUtils.resetColor();
            drawString(s, x, newY, centerMode, shadow, color);
            newY += getHeight() + 5;
        }
    }

    private void setupMinecraftColorcodes() {
        int index = 0;

        while (index < 32) {
            int noClue = (index >> 3 & 1) * 85;
            int red = (index >> 2 & 1) * 170 + noClue;
            int green = (index >> 1 & 1) * 170 + noClue;
            int blue = (index & 1) * 170 + noClue;

            if (index == 6) {
                red += 85;
            }

            if (index >= 16) {
                red /= 4;
                green /= 4;
                blue /= 4;
            }

            this.colorCode[index] = (red & 255) << 16 | (green & 255) << 8 | blue & 255;
            ++index;
        }
    }

}