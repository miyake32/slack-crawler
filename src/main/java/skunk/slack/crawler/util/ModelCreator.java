package skunk.slack.crawler.util;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ModelCreator {
	private static final Gson gson = new Gson();
	private static final Set<Class<?>> NATIVE_TYPES = new HashSet<>(
			Arrays.asList(Integer.class, Boolean.class, Byte.class, Short.class, Long.class, BigDecimal.class,
					Float.class, Time.class, Date.class, Timestamp.class, String.class, UUID.class));
	private static Set<String> EXCLUDED_FIELD_NAME = new HashSet<>(Arrays.asList("this$0", "ANNOTATION", "ENUM",
			"SYNTHETIC", "cachedConstructor", "newInstanceCallerCache", "allPermDomain", "useCaches", "reflectionData",
			"classRedefinedCount", "genericInfo", "serialVersionUID", "serialPersistentFields", "reflectionFactory",
			"initted", "enumConstants", "enumConstantDirectory", "annotationData", "annotationType", "classValueMap"));
	private Map<Object, Map<String, Object>> processedObjectMap = new HashMap<>();

	private ModelCreator() {
	}

	public static String createJsonModelFromCollection(Collection<?> arrayOfObj) {
		return gson.toJson(createModelFromCollection(arrayOfObj));
	}
	
	public static List<Map<String, Object>> createModelFromCollection(Collection<?> arrayOfObj) {
		return arrayOfObj.stream().map(ModelCreator::createModel).collect(Collectors.toList());
	}
	
	public static String createJsonModel(Object obj) {
		return gson.toJson(createModel(obj));
	}
	
	
	public static Map<String, Object> createModel(Object obj) {
		return new ModelCreator().getModel(obj);
	}

	private Map<String, Object> getModel(Object obj) {
		Map<String, Object> map = new HashMap<>();
		if (processedObjectMap.containsKey(obj)) {
			return processedObjectMap.get(obj);
		}
		processedObjectMap.put(obj, map);
		getFieldsOf(obj.getClass()).stream().forEach(field -> mapField(field, obj, map, true));
		return map;
	}

	private Map<String, Object> getModelWithoutCircularReference(Object obj) {
		Map<String, Object> map = new HashMap<>();
		if (processedObjectMap.containsKey(obj)) {
			return null;
		}
		processedObjectMap.put(obj, map);
		getFieldsOf(obj.getClass()).stream().forEach(field -> mapField(field, obj, map, false));
		return map;
	}

	@SuppressWarnings("unchecked")
	private void mapField(Field field, Object obj, Map<String, Object> map, boolean circularReference) {
		Object value;
		try {
			field.setAccessible(true);
			value = field.get(obj);
		} catch (IllegalArgumentException | IllegalAccessException e1) {
			log.error("failed to get value of {}.{}", obj.getClass().getName(), field.getName(), e1);
			return;
		}
		if (Objects.isNull(value)) {
			map.put(field.getName(), null);
		} else if (isNativeTypeField(field)) {
			map.put(field.getName(), value);
		} else if (isCollectionField(field)) {
			map.put(field.getName(), getValueFromCollectionFiled((Collection<Object>) value));
		} else if (field.getType().isEnum()) {
			map.put(field.getName(), value.toString());
		} else {
			if (circularReference) {
				map.put(field.getName(), getModel(value));
			} else {
				Map<String, Object> childMap = getModelWithoutCircularReference(obj);
				if (Objects.nonNull(childMap)) {
					map.put(field.getName(), childMap);
				}
			}
		}
	}

	private List<Object> getValueFromCollectionFiled(Collection<Object> collection) {
		List<Object> list = new ArrayList<>();
		for (Object obj : collection) {
			if (NATIVE_TYPES.contains(obj.getClass())) {
				list.add(obj);
			} else {
				list.add(getModel(obj));
			}
		}
		return list;
	}

	private static boolean isNativeTypeField(Field field) {
		Class<?> type = field.getType();
		return NATIVE_TYPES.contains(type);
	}

	private static boolean isCollectionField(Field field) {
		Class<?> type = field.getType();
		return Collection.class.isAssignableFrom(type);
	}

	private static List<Field> getFieldsOf(Class<?> clazz) {
		List<Field> fields = Arrays.asList((clazz.getDeclaredFields())).stream()
				.filter(f -> !EXCLUDED_FIELD_NAME.contains(f.getName())).collect(Collectors.toList());
		Class<?> superClass = clazz.getSuperclass();
		if (Objects.isNull(superClass) || superClass.equals(Object.class) || superClass.equals(Enum.class)) {
			return fields;
		}
		fields.addAll(getFieldsOf(superClass));
		return fields;
	}
}
