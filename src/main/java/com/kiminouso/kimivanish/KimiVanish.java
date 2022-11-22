package com.kiminouso.kimivanish;

import com.kiminouso.kimivanish.commands.HideManager;
import com.kiminouso.kimivanish.commands.VanishCommand;
import com.kiminouso.kimivanish.commands.subcommands.settings.*;
import lombok.Getter;
import me.tippie.tippieutils.guis.GuiManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class KimiVanish extends JavaPlugin {
    @Getter
    private Storage storage;
    @Getter
    private VanishManager vanishManager;
    @Getter
    private HideManager hideManager;
    private VanishCommand vanishCommand;
    private ItemSettingCommand itemSettingCommand;
    private NotifySettingCommand notifySettingCommand;
    private FlySettingCommand flySettingCommand;
    private InteractSettingCommand interactSettingCommand;
    private NightvisionSettingCommand nightvisionSettingCommand;
    private LocationSettingCommand locationSettingCommand;
    private VanishListeners vanishListeners;
    @Getter private GuiManager guiManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        vanishManager = new VanishManager();
        vanishCommand = new VanishCommand();
        hideManager = new HideManager();
        vanishListeners = new VanishListeners();
        itemSettingCommand = new ItemSettingCommand();
        notifySettingCommand = new NotifySettingCommand();
        flySettingCommand = new FlySettingCommand();
        interactSettingCommand = new InteractSettingCommand();
        locationSettingCommand = new LocationSettingCommand();
        nightvisionSettingCommand = new NightvisionSettingCommand();
        guiManager = new GuiManager(this);
        storage = new Storage(this);

        Bukkit.getPluginCommand("vanish").setExecutor(this.vanishCommand);
        Bukkit.getPluginCommand("vanish").setTabCompleter(this.vanishCommand);
        Bukkit.getServer().getPluginManager().registerEvents(this.vanishListeners, this);
        Bukkit.getServer().getPluginManager().registerEvents(this.itemSettingCommand, this);
        Bukkit.getServer().getPluginManager().registerEvents(this.notifySettingCommand, this);
        Bukkit.getServer().getPluginManager().registerEvents(this.interactSettingCommand, this);
        Bukkit.getServer().getPluginManager().registerEvents(this.nightvisionSettingCommand, this);
        Bukkit.getServer().getPluginManager().registerEvents(this.flySettingCommand, this);
        Bukkit.getServer().getPluginManager().registerEvents(this.locationSettingCommand, this);
        Bukkit.getServer().getPluginManager().registerEvents(this.hideManager, this);

        if (Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            Bukkit.getServer().getLogger().log(Level.INFO, "PlaceholderAPI has been found! Enabling placeholder expansion...");
            new VanishPlaceholder(this).register();
        } else {
            Bukkit.getServer().getLogger().log(Level.SEVERE, "Could not find PlaceholderAPI! Placeholder expansion is disabled.");
        }

        if (Bukkit.getServer().getPluginManager().getPlugin("Essentials") != null) {
            Bukkit.getServer().getLogger().log(Level.INFO, "Essentials has been found! Enabling Essentials vanish hook...");
        } else {
            Bukkit.getServer().getLogger().log(Level.SEVERE, "Could not find Essentials! Essentials hook is disabled.");
        }

        Bukkit.getScheduler().runTaskTimer(this, () -> vanishManager.canVanish.forEach(player -> {
            int level = hideManager.checkLevel(Bukkit.getPlayer(player));
            vanishManager.vanishLevels.tailMap(level, true)
                    .values()
                    .forEach(sublist -> sublist.forEach(p -> Bukkit.getPlayer(player).showPlayer(this, p))
                    );
        }), 0L, 1L);

        if (KimiVanish.getPlugin(KimiVanish.class).getConfig().getBoolean("settings.vanish.actionbar")) {
            hideManager.start();
        }
    }

    @Override
    public void onDisable() {
        hideManager.unhideAll();
        if (hideManager.isActive()) {
            hideManager.end();
        }
    }
}
