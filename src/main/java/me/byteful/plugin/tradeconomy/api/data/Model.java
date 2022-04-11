package me.byteful.plugin.tradeconomy.api.data;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class Model {
  @NotNull
  private final String storedGroup;
  @NotNull
  private final ModelProperty id;
  @NotNull
  private final List<ModelProperty> indexedProperties, uniqueProperties, properties;

  public Model(@NotNull String storedGroup, @NotNull ModelProperty id, @NotNull List<ModelProperty> indexedProperties, @NotNull List<ModelProperty> uniqueProperties, @NotNull List<ModelProperty> properties) {
    this.storedGroup = storedGroup;
    this.id = id;
    this.indexedProperties = indexedProperties;
    this.uniqueProperties = uniqueProperties;
    this.properties = properties;
  }

  public @NotNull String getStoredGroup() {
    return storedGroup;
  }

  public @NotNull ModelProperty getId() {
    return id;
  }

  public @NotNull List<ModelProperty> getIndexedProperties() {
    return indexedProperties;
  }

  public @NotNull List<ModelProperty> getUniqueProperties() {
    return uniqueProperties;
  }

  public @NotNull List<ModelProperty> getProperties() {
    return properties;
  }

  public enum ModelPropertyType {
    UUID,
    STRING,
    INTEGER,
    DOUBLE,
    FLOAT,
    DECIMAL
  }

  public static class ModelProperty {
    @NotNull
    private final ModelPropertyType type;
    @NotNull
    private final String key;
    @NotNull
    private final Object value;

    public ModelProperty(@NotNull ModelPropertyType type, @NotNull String key, @NotNull Object value) {
      this.type = type;
      this.key = key;
      this.value = value;
    }

    public @NotNull ModelPropertyType getType() {
      return type;
    }

    public @NotNull String getKey() {
      return key;
    }

    public @NotNull Object getValue() {
      return value;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      ModelProperty that = (ModelProperty) o;
      return type == that.type && key.equals(that.key) && value.equals(that.value);
    }

    @Override
    public int hashCode() {
      return Objects.hash(type, key, value);
    }

    @Override
    public String toString() {
      return "ModelProperty{" +
        "type=" + type +
        ", key='" + key + '\'' +
        ", value=" + value +
        '}';
    }
  }
}