package io.github.ghjj34.Warper;

import org.bukkit.plugin.java.JavaPlugin;

public class WarperPlugin extends JavaPlugin {

    private static WarperPlugin instance;


    @Override
    public void onEnable() {
        instance = this;
        getServer().getPluginManager().registerEvents(new WarperListener(), this);
    }

    public static WarperPlugin getInstance() {
        return instance;
    }

}
