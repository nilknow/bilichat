package backend.util;

import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.Response;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@Deprecated
public class Curl {
    public static String get(String url) {
        return get(url, null);
    }

    public static String get(String url, Map<String, String> headerMap) {
        Builder builder = new Builder()
                .url(url)
                .get();
        if (headerMap != null) {
            headerMap.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4450.0 Safari/537.36");
            headerMap.put("Cookie", "");
            Headers headers = Headers.of(headerMap);
            builder.headers(headers);
        }
        Request request = builder.build();
        try (Response response = HttpClient.getClient().newCall(request).execute()) {
            return Objects.requireNonNull(response.body()).string();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}
