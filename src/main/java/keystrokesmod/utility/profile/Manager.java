package keystrokesmod.utility.profile;

import keystrokesmod.Client;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.utility.Utils;

import java.awt.*;
import java.io.IOException;

public class Manager extends Module {
    private ButtonSetting loadProfiles, openFolder, createProfile;

    public Manager() {
        super("Manager", category.profiles);
        this.registerSetting(createProfile = new ButtonSetting("Create profile", () -> {
            if (Utils.nullCheck() && Client.profileManager != null) {
                String name = "profile-";
                for (int i = 1; i <= 100; i++) {
                    if (Client.profileManager.getProfile(name + i) != null) {
                        continue;
                    }
                    name += i;
                    Client.profileManager.saveProfile(new Profile(name, 0));
                    Utils.sendMessage("&7Created profile: &b" + name);
                    Client.profileManager.loadProfiles();
                    break;
                }
            }
        }));
        this.registerSetting(loadProfiles = new ButtonSetting("Load profiles", () -> {
            if (Utils.nullCheck() && Client.profileManager != null) {
                Client.profileManager.loadProfiles();
            }
        }));
        this.registerSetting(openFolder = new ButtonSetting("Open folder", () -> {
            try {
                Desktop.getDesktop().open(Client.profileManager.directory);
            }
            catch (IOException ex) {
                Client.profileManager.directory.mkdirs();
                Utils.sendMessage("&cError locating folder, recreated.");
            }
        }));
        ignoreOnSave = true;
        canBeEnabled = false;
    }
}