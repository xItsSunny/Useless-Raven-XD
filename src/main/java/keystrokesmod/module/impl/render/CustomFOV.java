package keystrokesmod.module.impl.render;


import keystrokesmod.event.render.FOVUpdateEvent;
import keystrokesmod.event.world.WorldChangeEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.eventbus.annotations.EventListener;


public class CustomFOV extends Module {


    private float savedFOVSetting;

    private static final SliderSetting setFOV = new SliderSetting("FOV:", 70, 1, 179, 1);
    public final ButtonSetting forceStaticFOV = new ButtonSetting("Static", false);

    public CustomFOV() {
        super("CustomFOV", category.render);
        //this.registerSetting(new DescriptionSetting("Currently very broken"));
        this.registerSetting(setFOV);
        this.registerSetting(forceStaticFOV);
    }

    @Override
    public void onEnable() {
        savedFOVSetting = mc.gameSettings.fovSetting;

        mc.gameSettings.fovSetting = (float) setFOV.getInput();
    }

    @Override
    public void onDisable() {
        mc.gameSettings.fovSetting = savedFOVSetting;
    }

    @EventListener
    public void onWorldChangeEvent(WorldChangeEvent event) {
        mc.gameSettings.fovSetting = (float) setFOV.getInput();
    }

    @EventListener
    public void onFOVUpdateEvent(FOVUpdateEvent event) {
        mc.gameSettings.fovSetting = (float) setFOV.getInput();
    }

    public static float getDesiredFOV() {
        return (float) setFOV.getInput();
    }

}
