package me.byteful.plugin.tradeconomy;

import me.byteful.plugin.tradeconomy.api.TradeconomyAPI;
import me.byteful.plugin.tradeconomy.util.VersionUtil;
import me.lucko.helper.config.ConfigurationNode;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import me.lucko.helper.reflect.MinecraftVersion;
import me.lucko.helper.reflect.MinecraftVersions;
import me.lucko.helper.text3.Text;
import me.lucko.helper.utils.Log;
import org.bukkit.Bukkit;
import redempt.redlib.commandmanager.CommandParser;

public final class TradeconomyPlugin extends ExtendedJavaPlugin {
  private ConfigurationNode settings;
  private boolean fullyStarted = false;

  @Override
  protected void enable() {
    final long startTime = System.currentTimeMillis();
    if (!checkVersion()) {
      Bukkit.getPluginManager().disablePlugin(this);

      return;
    }

    colorLog("");
    colorLog("&b    _________");
    colorLog("&b   /_|_____|_\\       &eTradeconomy &6v" + getDescription().getVersion());
    colorLog("&b   '. \\   / .'       &8Running on &7" + getServer().getName() + " &8- &7" + getServer().getBukkitVersion());
    colorLog("&b     '.\\ /.'         &aCreated by &2byteful");
    colorLog("&b       '.'");
    colorLog("");

    saveDefaultConfig();
    settings = loadConfigNode("config.yml");
    Log.info("Loaded configuration...");

    new CommandParser(getResource("command.rdcml")).parse().register(this, "tradeconomy");
    Log.info("Loaded commands...");

    Log.info("Now initializing API... This may take a while.");
    try {
      TradeconomyAPI.init(this);
    } catch (Exception e) {
      Bukkit.getPluginManager().disablePlugin(this);

      throw new RuntimeException("Failed to initialize API for Tradeconomy!", e);
    }

    Log.info("Tradeconomy has successfully started! (took " + (System.currentTimeMillis() - startTime) + "ms)");
    fullyStarted = true;
  }

  @Override
  protected void disable() {
    if(!fullyStarted) {
      colorLog("&8----------------------------------------------------------------------------------------------");
      colorLog("&6 There was an error during startup that has prevented Tradeconomy from starting successfully.");
      colorLog("&6 Usual shutdown methods will not be executed and data may not save.");
      colorLog("&8----------------------------------------------------------------------------------------------");
      Log.severe("Tradeconomy has forcefully stopped.");

      return;
    }

    Log.info("Tradeconomy has successfully stopped.");
  }

  private boolean checkVersion() {
    final MinecraftVersion v = MinecraftVersion.getRuntimeVersion();
    Log.info("Server version " + v.getVersion() + " detected.");

    if(VersionUtil.isBefore(v, MinecraftVersion.parse("1.8.8"))) {
      colorLog("&8------------------------------------------------------------------------------------------");
      colorLog("&c Tradeconomy is not available for versions below 1.8.8!");
      colorLog("&c");
      colorLog("&c For more information, visit the GitHub page here: https://github.com/byteful/Tradeconomy");
      colorLog("&c Tradeconomy will now shut down.");
      colorLog("&8------------------------------------------------------------------------------------------");

      return false;
    }

    if(VersionUtil.isBefore(v, MinecraftVersions.v1_13)) {
      colorLog("&8-------------------------------------------------------------------------------------");
      colorLog("&6 Tradeconomy may not work well on versions below 1.13.");
      colorLog("&6 The author of Tradeconomy recommends you update to the latest version of Minecraft.");
      colorLog("&6 Support will not be given to users using old Minecraft versions.");
      colorLog("&8-------------------------------------------------------------------------------------");
    }

    return true;
  }

  private void colorLog(String str) {
    Bukkit.getConsoleSender().sendMessage(Text.colorize(str));
  }

  public ConfigurationNode getSettings() {
    return settings;
  }
}