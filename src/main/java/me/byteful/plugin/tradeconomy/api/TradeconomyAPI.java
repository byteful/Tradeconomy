package me.byteful.plugin.tradeconomy.api;

import me.byteful.plugin.tradeconomy.TradeconomyPlugin;
import org.jetbrains.annotations.NotNull;

public final class TradeconomyAPI {
  private static TradeconomyAPI instance;

  public static TradeconomyAPI getInstance() {
    return instance;
  }

//  @NotNull
//  private final DataHandler dataHandler;

  private TradeconomyAPI(@NotNull TradeconomyPlugin plugin) {

    // TODO read config and load appropriate data handler
  }

  public static void init(@NotNull TradeconomyPlugin plugin) {
    if(instance != null) {
      throw new IllegalStateException("Cannot initialize API if it's already initialized!");
    }

    instance = new TradeconomyAPI(plugin);
  }
}