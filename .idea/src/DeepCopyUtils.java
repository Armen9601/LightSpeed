import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class DeepCopyUtils {

    public static void main(String[] args) throws Exception {

        List<Integer> originalList = new ArrayList<>(Arrays.asList(1, 2, 3));
        List<Integer> copiedList = (List<Integer>) deepCopy(originalList);
        System.out.println("Original list: " + originalList);
        System.out.println("Copied list: " + copiedList);
        copiedList.add(4);
        System.out.println("Original list after modifying copied list: " + originalList);
        System.out.println("Copied list after modification: " + copiedList);
    }

    private static Object deepCopy(Object original) throws Exception {
        if (original == null) {
            return null;
        }

        Class<?> clazz = original.getClass();

        if (clazz.isPrimitive() || clazz == Integer.class || clazz == String.class || clazz == Boolean.class
                || clazz == Character.class || clazz == Byte.class || clazz == Short.class || clazz == Double.class
                || clazz == Long.class || clazz == Float.class) {
            return original;
        }

        if (original instanceof Collection<?> originalCollection) {
            var copiedCollection = originalCollection.getClass().newInstance();
            for (Object element : originalCollection) {
                copiedCollection.add(deepCopy(element));
            }
            return copiedCollection;
        }

        if (original instanceof Map<?, ?> originalMap) {
            Map<Object, Object> copiedMap = (Map<Object, Object>) originalMap.getClass().newInstance();
            for (Map.Entry<?, ?> entry : originalMap.entrySet()) {
                copiedMap.put(deepCopy(entry.getKey()), deepCopy(entry.getValue()));
            }
            return copiedMap;
        }

        if (clazz.isArray()) {
            int length = Array.getLength(original);
            Object copiedArray = Array.newInstance(clazz.getComponentType(), length);
            for (int i = 0; i < length; i++) {
                Array.set(copiedArray, i, deepCopy(Array.get(original, i)));
            }
            return copiedArray;
        }

        Object copiedObject = clazz.newInstance();
        for (Field field : clazz.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers())) {
                field.setAccessible(true);
                field.set(copiedObject, deepCopy(field.get(original)));
            }
        }

        return copiedObject;
    }
}