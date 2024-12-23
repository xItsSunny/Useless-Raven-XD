package keystrokesmod.module.impl.render;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Reflection;
import keystrokesmod.utility.Utils;
import keystrokesmod.utility.render.ColorUtils;
import keystrokesmod.utility.render.RenderUtils;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import keystrokesmod.event.world.EntityJoinWorldEvent;
import keystrokesmod.eventbus.annotations.EventListener;
import keystrokesmod.event.render.Render2DEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Iterator;
import java.util.Set;

public class Indicators extends Module {
    private final ButtonSetting renderArrows;
    private final ButtonSetting renderPearls;
    private final ButtonSetting renderFireballs;
    private final ButtonSetting renderPlayers;
    private final SliderSetting radius;
    private final ButtonSetting itemColors;
    private final ButtonSetting arrowColor;
    private final ButtonSetting renderItem;
    private final ButtonSetting threatsOnly;
    private final Set<Entity> threats = new ObjectOpenHashSet<>();
    private final int pearlColor = new Color(173, 12, 255).getRGB();
    private final int fireBallColor = new Color(255, 109, 0).getRGB();

    public Indicators() {
        super("Indicators", category.render);
        this.registerSetting(renderArrows = new ButtonSetting("Render arrows", true));
        this.registerSetting(renderPearls = new ButtonSetting("Render ender pearls", true));
        this.registerSetting(renderFireballs = new ButtonSetting("Render fireballs", true));
        this.registerSetting(renderPlayers = new ButtonSetting("Render players", true));
        this.registerSetting(radius = new SliderSetting("Circle radius", 50, 5, 250, 2));
        this.registerSetting(itemColors = new ButtonSetting("Item colors", true));
        this.registerSetting(arrowColor = new ButtonSetting("Arrow color", true));
        this.registerSetting(renderItem = new ButtonSetting("Render item", true));
        this.registerSetting(threatsOnly = new ButtonSetting("Render only threats", true));
    }

    public void onDisable() {
        this.threats.clear();
    }

    @EventListener
    public void onRenderTick(Render2DEvent event) {
        if (mc.currentScreen != null || !Utils.nullCheck()) {
            return;
        }
        if (threats.isEmpty()) {
            return;
        }
        try {
            Iterator<Entity> iterator = threats.iterator();
            while (iterator.hasNext()) {
                Entity e = iterator.next();
                if (e == null || !mc.theWorld.loadedEntityList.contains(e) || !canRender(e) || (e instanceof EntityArrow && Reflection.inGround.getBoolean(e))) {
                    iterator.remove();
                    continue;
                }
                float yaw = Utils.getYaw(e) - mc.thePlayer.rotationYaw;
                ScaledResolution sr = new ScaledResolution(mc);
                float x = (float) sr.getScaledWidth() / 2;
                float y = (float) sr.getScaledHeight() / 2;
                GL11.glPushMatrix();
                GL11.glTranslated(x, y, 0.0);
                GL11.glPopMatrix();
                int color = arrowColor.isToggled() ? ColorUtils.getColorFromCode(e.getDisplayName().getFormattedText()).getRGB() : -1;
                if (renderItem.isToggled()) {
                    ItemStack entityItem = null;
                    if (e instanceof EntityEnderPearl) {
                        color = pearlColor;
                        entityItem = new ItemStack(Items.ender_pearl);
                    } else if (e instanceof EntityArrow) {
                        entityItem = new ItemStack(Items.arrow);
                    } else if (e instanceof EntityFireball) {
                        color = fireBallColor;
                        entityItem = new ItemStack(Items.fire_charge);
                    }
                    if (entityItem != null) {
                        GL11.glPushMatrix();
                        float[] position = getPositionForCircle(yaw, radius.getInput());
                        GL11.glTranslated(x + position[0], y + position[1], 0.0);
                        if (e instanceof EntityArrow) {
                            GL11.glRotatef(yaw, 0.0f, 0.0f, 1.0f);
                            GL11.glTranslatef(-7, 0, 0);
                            GL11.glRotatef(-45.0f, 0.0f, 0.0f, 1.0f);
                        }
                        mc.getRenderItem().renderItemAndEffectIntoGUI(entityItem, -8, 0);
                        GL11.glPopMatrix();
                    }
                }

                GL11.glPushMatrix();
                float[] position = getPositionForCircle(yaw, radius.getInput() + 21);
                GL11.glTranslated(x + position[0], y + position[1], 0.0);
                String distanceStr = (int) mc.thePlayer.getDistanceToEntity(e) + "m";
                float textWidth = mc.fontRendererObj.getStringWidth(distanceStr);
                mc.fontRendererObj.drawStringWithShadow(distanceStr, -textWidth / 2, (float) -mc.fontRendererObj.FONT_HEIGHT / 2, -1);
                GL11.glPopMatrix();

                GL11.glPushMatrix();
                GL11.glTranslated(x, y, 0.0);
                GL11.glRotatef(yaw, 0.0f, 0.0f, 1.0f);
                RenderUtils.drawArrow(-5f, (float) -radius.getInput() - 38, itemColors.isToggled() ? color : -1, 3, 5);
                GL11.glPopMatrix();
            }
        } catch (Exception ignored) {
        }
    }

    @EventListener
    public void onEntityJoin(EntityJoinWorldEvent e) {
        if (!Utils.nullCheck()) {
            return;
        }
        if (e.getEntity() == mc.thePlayer) {
            this.threats.clear();
        } else if (canRender(e.getEntity()) && (mc.thePlayer.getDistanceSqToEntity(e.getEntity()) > 16.0 || !threatsOnly.isToggled())) {
            this.threats.add(e.getEntity());
        }
    }

    private boolean canRender(Entity entity) {
        try {
            if (entity instanceof EntityArrow && !Reflection.inGround.getBoolean(entity) && renderArrows.isToggled()) {
                return true;
            } else if (entity instanceof EntityFireball && renderFireballs.isToggled()) {
                return true;
            } else if (entity instanceof EntityEnderPearl && renderPearls.isToggled()) {
                return true;
            } else if (entity instanceof EntityPlayer && renderPlayers.isToggled()) {
                return true;
            }
        } catch (IllegalAccessException e) {
            Utils.sendMessage("&cIssue checking entity.");
            return false;
        }
        return false;
    }

    public float[] getPositionForCircle(float angle, double radius) {
        angle = angle % 360;
        if (angle < 0) {
            angle += 360;
        }
        float wrappedAngle = MathHelper.wrapAngleTo180_float(angle);
        float absAngle = Math.abs(wrappedAngle);
        float[] position = null;
        if (absAngle >= 0 && absAngle <= 90) {
            position = new float[]{(float) (Math.cos(Math.toRadians(90 - absAngle)) * radius), (float) (Math.sin(Math.toRadians(90 - absAngle)) * radius)};
        } else if (absAngle > 90 && absAngle <= 180) {
            position = new float[]{(float) (Math.cos(Math.toRadians(absAngle - 90)) * radius), (float) -(Math.sin(Math.toRadians(absAngle - 90)) * radius)};
        }
        if (position != null) {
            if (wrappedAngle <= 0) {
                position[0] = -position[0];
            }
            position[1] = -position[1];
        }
        return position;
    }
}