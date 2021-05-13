package backend.util;

import backend.FileAddress;
import backend.LoginApi;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Address;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Objects;

/**
 * the only http client of the application, don't use any other httpclient
 */
@Slf4j
public class HttpClient {
    private static volatile OkHttpClient client = null;
    private static final Object clientLock = new Object();
    //if client was set (especially the cookie)
    private static boolean set = false;

    private static final String USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36";

    public static OkHttpClient getClient() {
        if (client == null) {
            synchronized (clientLock) {
                if (client == null) {
//                    Proxy proxy = new Proxy(Proxy.Type.HTTP,new InetSocketAddress("localhost",8080));
                    client = new OkHttpClient.Builder()
//                            .proxy(proxy)
                            .build();
                }
            }
        }
        return client;
    }

    public static Response get(String url){
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        try (Response resp = getClient().newCall(request).execute()) {
            return resp;
        } catch (IOException e) {
            return null;
        }
    }

    public static String getRespBody(String url){
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        try (Response resp = getClient().newCall(request).execute()) {
            return Objects.requireNonNull(resp.body()).string();
        } catch (IOException e) {
            return null;
        }
    }

    public static String getRespBodyWithCookie(String url){
        if (!set) {
            File file = new File(FileAddress.COOKIE_PATH);
            if (file.exists() && !file.isDirectory()) {
                try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file));){
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine())!=null) {
                        sb.append(line);
                    }
                    OkHttpClient cookieClient = new OkHttpClient.Builder()
                            .addInterceptor(chain -> {
                                Request original = chain.request();
                                Request cookieRequest = original.newBuilder()
                                        .addHeader("Cookie", sb.toString())
                                        .addHeader("user-agent", USER_AGENT)
                                        .build();
                                return chain.proceed(cookieRequest);
                            }).build();
                    setClient(cookieClient);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                LoginApi.login();
            }
        }
        Response resp = get(url);
        if (resp == null) {
            return null;
        }

        try {
            return Objects.requireNonNull(resp.body()).string();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void setClient(OkHttpClient newClient){
        client = newClient;
        set = true;
    }
}
