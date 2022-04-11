package me.byteful.plugin.tradeconomy.api.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface DataHandler {
  @Nullable
  Model get(@NotNull String[] indexes);

  void set(@NotNull Model model);
}
