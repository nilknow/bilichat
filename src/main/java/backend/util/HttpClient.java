package backend.util;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.Proxy;

/**
 * the only http client of the application, don't use any other httpclient
 */
@Slf4j
public class HttpClient {
    private static volatile OkHttpClient client = null;
    private static final Object clientLock = new Object();

    public static OkHttpClient getClient() {
        if (client == null) {
            synchronized (clientLock) {
                if (client == null) {
                    client = new OkHttpClient.Builder().build();
                }
            }
        }
        return client;
    }

    public static void setClient(OkHttpClient newClient){
        client = newClient;
    }
}
