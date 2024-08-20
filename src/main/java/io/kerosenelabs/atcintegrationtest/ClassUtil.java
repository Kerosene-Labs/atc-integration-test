package io.kerosenelabs.atcintegrationtest;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClassUtil {
    public static List<Class<?>> getClasses(String packageName) throws ClassNotFoundException {
        String path = packageName.replace('.', '/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resource = classLoader.getResource(path);
        File directory = new File(resource.getFile());

        List<Class<?>> classes = new ArrayList<>();
        if (directory.exists()) {
            String[] files = directory.list();
            for (String file : files) {
                if (file.endsWith(".class")) {
                    String className = packageName + '.' + file.substring(0, file.length() - 6);
                    classes.add(Class.forName(className));
                }
            }
        }
        return classes;
    }

    /**
     * Filters classes that implement the interfaces
     * @param classes The list of classes to filter
     * @return The list of classes that implement all the interfaces
     */
    public static <T> List<Class<T>> filterClassesByImplementation(List<Class<?>> classes, Class<T> iface) {
        return classes.stream()
                .filter(iface::isAssignableFrom)
                .map(clazz -> (Class<T>) clazz)
                .toList();
    }
}
