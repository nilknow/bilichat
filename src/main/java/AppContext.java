import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Singleton.
 * save all components in it.
 */
public class AppContext {
    private AppContext instance = null;
    private final Object instanceLock = new Object();

    private static final Map<String, Component> cache = new HashMap<>();

    private AppContext(){}

    public AppContext instance(){
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

    public void get(String id) {
        cache.get(id);
    }
}
