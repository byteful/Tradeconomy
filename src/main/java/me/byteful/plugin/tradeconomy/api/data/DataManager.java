package me.byteful.plugin.tradeconomy.api.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.byteful.plugin.tradeconomy.api.data.annotations.Id;
import me.byteful.plugin.tradeconomy.api.data.annotations.Indexed;
import me.byteful.plugin.tradeconomy.api.data.annotations.StoredGroup;
import me.byteful.plugin.tradeconomy.api.data.annotations.Unique;
import me.byteful.plugin.tradeconomy.util.ReflectUtil;
import me.lucko.helper.utils.Log;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class DataManager<T> {
  @NotNull
  private final Gson gson = new GsonBuilder().serializeNulls().disableHtmlEscaping().create();
  @NotNull
  private final DataHandler handler;
  @NotNull
  private final Class<T> type;

  public DataManager(@NotNull DataHandler handler, @NotNull Class<T> type) {
    this.handler = handler;
    this.type = type;
  }

  @NotNull
  public Optional<T> get(@NotNull String... indexes) {
    if(indexes == null || indexes.length <= 0) {
      throw new IllegalArgumentException("No indexes found! Please provide at least one index.");
    }

    final Model model = handler.get(indexes);

    return model == null ? Optional.empty() : Optional.of(processObjectFromModel(model));
  }

  public void set(@NotNull T t) {
    handler.set(processModelFromObject(t));
  }

  private Model processModelFromObject(@NotNull T obj) {
    final Class<?> c = obj.getClass();
    final StoredGroup storedGroup = c.getAnnotation(StoredGroup.class);

    if(storedGroup == null) {
      throw new IllegalArgumentException("StoredGroup not found for class " + c + "!");
    }

    final List<Model.ModelProperty> index = new ArrayList<>(), unique = new ArrayList<>(), property = new ArrayList<>();
    Model.ModelProperty id = null;
    for (Field field : c.getDeclaredFields()) {
      if(!field.isAccessible() || Modifier.isTransient(field.getModifiers())) {
        continue;
      }

      final Model.ModelProperty processed;
      try {
        processed = processModelPropertyFromField(field, obj);
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
      }

      if(field.getAnnotation(Id.class) != null) {
        id = processed;

        continue;
      }

      if(field.getAnnotation(Indexed.class) != null) {
        index.add(processed);

        continue;
      }

      if(field.getAnnotation(Unique.class) != null) {
        unique.add(processed);

        continue;
      }

      property.add(processed);
    }

    if(index.size() <= 0) {
      throw new IllegalArgumentException("Class " + c + " does not have enough indexes! At least one is required.");
    }

    if(id == null) {
      throw new IllegalArgumentException("Class " + c + " does not have an ID!");
    }

    return new Model(storedGroup.value(), id, index, unique, property);
  }

  private Model.ModelProperty processModelPropertyFromField(@NotNull Field field, @NotNull Object obj) throws IllegalAccessException {
    final Class<?> classType = field.getType();
    final String key = field.getName();
    Object value = field.get(obj);

    final Model.ModelPropertyType type;

    if(classType.isAssignableFrom(String.class)) {
      type = Model.ModelPropertyType.STRING;
    } else if(classType.isAssignableFrom(Integer.class)) {
      type = Model.ModelPropertyType.INTEGER;
    } else if(classType.isAssignableFrom(Double.class)) {
      type = Model.ModelPropertyType.DOUBLE;
    } else if(classType.isAssignableFrom(Float.class)) {
      type = Model.ModelPropertyType.FLOAT;
    } else if(classType.isAssignableFrom(BigDecimal.class)) {
      type = Model.ModelPropertyType.DECIMAL;
    } else if(classType.isAssignableFrom(UUID.class)) {
      type = Model.ModelPropertyType.UUID;
    } else {
      type = Model.ModelPropertyType.STRING;
      value = gson.toJson(value, classType);
    }

    return new Model.ModelProperty(type, key, value);
  }

  private T processObjectFromModel(@NotNull Model model) {
    final List<Model.ModelProperty> properties = new ArrayList<>();
    properties.addAll(model.getIndexedProperties());
    properties.addAll(model.getUniqueProperties());
    properties.addAll(model.getProperties());

    try {
      final T obj = type.getDeclaredConstructor().newInstance();

      final Field idField = ReflectUtil.getDeclaredField(model.getId().getKey(), type);
      if(idField != null) {
        idField.set(obj, model.getId().getValue());
      } else {
        Log.warn("ID field was null! Data structure change has occurred and was not properly applied.");
      }

      for (Model.ModelProperty property : properties) {
        final Field field = ReflectUtil.getDeclaredField(property.getKey(), type);
        if(field == null) {
          continue;
        }

        field.set(obj, property.getValue());
      }

      return obj;
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      throw new RuntimeException("Failed to create object from model.", e);
    }
  }
}