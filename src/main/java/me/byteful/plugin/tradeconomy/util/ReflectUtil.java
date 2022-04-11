package me.byteful.plugin.tradeconomy.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

public final class ReflectUtil {
  @Nullable
  public static Field getDeclaredField(@NotNull String key, @NotNull Class<?> clazz) {
    try {
      return clazz.getDeclaredField(key);
    } catch (NoSuchFieldException e) {
      return null;
    }
  }
}