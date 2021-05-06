package frontend;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Singleton.
 * save all components in it.
 */
public class AppContext {
    private static AppContext instance = null;
    private static final Object instanceLock = new Object();

    private static final Map<String, Component> cache = new HashMap<>();

    private AppContext(){}

    public static AppContext instance(){
        if (instance == null) {
            synchronized (instanceLock){
                if (instance == null) {
                    instance = new AppContext();
                }
            }
        }
        return instance;
    }

    public void add(String id, Component component) {
        cache.put(id, component);
    }

    public <T> T get(String id,Class<T> clazz) {
        return (T)cache.get(id);
    }
}
