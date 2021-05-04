package backend.tool;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class PrettyGson {
    private static final Gson instance = new GsonBuilder().setPrettyPrinting().create();
    public static Gson get() {
        return instance;
    }
}
