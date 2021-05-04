package backend.tool;

import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.Proxy;

public class HttpClient {
    private static final Logger logger = LoggerFactory.getLogger(HttpClient.class);
    private static volatile OkHttpClient client = null;
    private static final Object clientLock = new Object();

    public static OkHttpClient getClient() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (client == null) {
            synchronized (clientLock) {
                if (client == null) {
                    Proxy proxy = new Proxy(Proxy.Type.HTTP,
                            new InetSocketAddress("localhost", 23333));
                    client = new OkHttpClient.Builder().proxy(proxy).build();
                }
            }
        }
        return client;
    }
}
