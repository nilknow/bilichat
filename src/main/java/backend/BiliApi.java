package backend;

import backend.tool.HttpClient;
import com.google.gson.Gson;
import json.RoomInfo;
import okhttp3.*;
import org.openqa.selenium.*;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class BiliApi {
    private static final Logger logger = LoggerFactory.getLogger(BiliApi.class);

    private static final String roomId = "9325157";
    private static final String platform = "pc";
    private static String csrfToken = "";
    private static final String loginUrl = "https://passport.bilibili.com/login";
    private static final Set<Cookie> cookieSet = new HashSet<>();
    /**
     * login: some api need to login first
     */
    public static void login() {
        WebDriver driver = new ChromeDriver();
        driver.get(loginUrl);
        WebElement element = driver.findElement(By.id("login-username"));
        element.sendKeys("maxwangein@gmail.com");
        driver.findElement(By.id("login-passwd")).click();

        //wait until logged in
        WebDriverWait wait = new WebDriverWait(driver, 24 * 60 * 60);
        wait.until(ExpectedConditions.urlToBe("https://passport.bilibili.com/account/security#/home"));

        cookieSet.addAll(driver.manage().getCookies());
        String userAgent = (String) ((JavascriptExecutor) driver).executeScript("return navigator.userAgent;");
        for (Cookie cookie : cookieSet) {
            if("bili_jct".equals(cookie.getName())){
                csrfToken = cookie.getValue();
                break;
            }
        }
        driver.close();
        initClient(userAgent);
    }

    /**
     * init http client
     */
    private static void initClient(String userAgent){
        OkHttpClient cookieClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request cookieRequest = original.newBuilder()
                            .addHeader("Cookie", cookieToString())
                            .addHeader("user-agent",userAgent)
                            .build();
                    return chain.proceed(cookieRequest);
                }).build();
        HttpClient.setClient(cookieClient);
    }

    /**
     * get live stream room info
     */
    public RoomInfo roomInfo(String roomId) throws IOException {
        Request request = new Request.Builder()
                .url("https://api.live.bilibili.com/room/v1/Room/room_init?id=" + roomId).get().build();
        try (Response response = HttpClient.getClient().newCall(request).execute()) {
            return new Gson().fromJson(Objects.requireNonNull(response.body()).string(), RoomInfo.class);
        }
    }

    public static boolean startStream(){
        return startStream("9325157", "372", "pc", csrfToken);
    }
    /**
     * start live stream
     */
    public static boolean startStream(String roomId, String areaId, String platform, String csrfToken) {
        RequestBody requestBody = new FormBody.Builder()
                .add("room_id", roomId)
                .add("area_v2", areaId)
                .add("platform", platform)
                .add("csrf", csrfToken)
                .add("csrf_token", csrfToken)
                .build();
        Request request = new Request.Builder().url("https://api.live.bilibili.com/room/v1/Room/startLive")
                .post(requestBody).build();
        try (Response response = HttpClient.getClient().newCall(request).execute()) {
            logger.debug(Objects.requireNonNull(response.body()).string());
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * stop live stream
     */
    public boolean stopStream(String csrf) {
        RequestBody requestBody = new FormBody.Builder()
                .add("room_id", roomId)
                .add("platform", platform)
                .add("csrf", csrf)
                .add("csrf_token", csrf)
                .build();
        Request request = new Request.Builder().url(" https://api.live.bilibili.com/room/v1/Room/stopLive")
                .post(requestBody).build();
        try (Response response = HttpClient.getClient().newCall(request).execute()) {
            logger.debug(Objects.requireNonNull(response.body()).string());
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * cookie to string
     */
    private static String cookieToString() {
        StringBuilder sb = new StringBuilder();
        for (Cookie cookie : BiliApi.cookieSet) {
            sb.append(cookie.getName())
                    .append("=")
                    .append(cookie.getValue())
                    .append("; ");
        }
        sb.delete(sb.length() - 2,sb.length());
        return sb.toString();
    }

    //curl 'http://api.live.bilibili.com/room/v1/Room/startLive' \
    //--data-urlencode 'room_id=9325157L' \
    //--data-urlencode 'area_v2=27' \
    //--data-urlencode 'platform=pc' \
    //--data-urlencode 'csrf=xxx' \
    //-b 'SESSDATA=xxx;bili_jct=xx'

    public static void main(String[] args) throws IOException {
        System.out.println(new BiliApi().roomInfo("9325157"));
//        System.out.println(new BiliApi().startStream("9325157", "372", "pc", "4a92355f9f9431c36bb1066e71d1c578"));
    }
}
