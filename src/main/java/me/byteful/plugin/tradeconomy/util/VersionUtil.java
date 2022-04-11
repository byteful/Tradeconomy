package me.byteful.plugin.tradeconomy.util;

import me.lucko.helper.reflect.MinecraftVersion;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

public final class VersionUtil {
  @NotNull
  private static final Comparator<MinecraftVersion> COMPARATOR = Comparator
    .comparingInt(MinecraftVersion::getMajor)
    .thenComparingInt(MinecraftVersion::getMinor)
    .thenComparingInt(MinecraftVersion::getBuild);

  public static boolean isBefore(@NotNull MinecraftVersion first, @NotNull MinecraftVersion second) {
    return COMPARATOR.compare(first, second) < 0;
  }
}