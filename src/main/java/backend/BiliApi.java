package backend;

import backend.tool.HttpClient;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import dto.RoomInfo;
import okhttp3.*;
import okhttp3.internal.ws.WebSocketReader;
import org.openqa.selenium.*;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class BiliApi {
    private static final Logger logger = LoggerFactory.getLogger(BiliApi.class);

    public static final String uid = "247897641";
    public static final String roomId = "9325157";
    public static String webSocketFirstMessageToken = null;
    private static final String platform = "pc";
    private static String csrfToken = "";
    private static final String loginUrl = "https://passport.bilibili.com/login";
    private static final String startStreamUrl = "https://api.live.bilibili.com/room/v1/Room/startLive";
    private static final String stopStreamUrl = "https://api.live.bilibili.com/room/v1/Room/stopLive";
    private static final String sendMsgUrl = "https://api.live.bilibili.com/msg/send";
    //api to get socket url
    private static final String getSocketUrlUrl = "https://api.live.bilibili.com/xlive/web-room/v1/index/getDanmuInfo";
    private static final List<DanmuInfoDataHost> danmuWebsocketList = new ArrayList<>();
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
            if ("bili_jct".equals(cookie.getName())) {
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
    private static void initClient(String userAgent) {
        OkHttpClient cookieClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request cookieRequest = original.newBuilder()
                            .addHeader("Cookie", cookieToString())
                            .addHeader("user-agent", userAgent)
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

    public static boolean startStream() {
        return startStream(roomId, "372", platform, csrfToken);
    }

    /**
     * start live stream
     */
    public static boolean startStream(String roomId, String areaId, String platform, String csrfToken) {
        RequestBody requestBody = requestBodyBuilder()
                .add("area_v2", areaId)
                .build();
        Request request = new Request.Builder().url(startStreamUrl)
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
    public static boolean stopStream(String csrf) {
        RequestBody requestBody = requestBodyBuilder().build();
        Request request = new Request.Builder().url(startStreamUrl)
                .post(requestBody).build();
        try (Response response = HttpClient.getClient().newCall(request).execute()) {
            logger.debug(Objects.requireNonNull(response.body()).string());
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * send message to your live stream room
     */
    public static void sendMessage(String msg) {
        String rndStr = String.valueOf(Timestamp.from(Instant.now()).getTime());
        RequestBody requestBody = requestBodyBuilder()
                .add("bubble", "0")
                .add("msg", msg)
                .add("color", "16777215")//default, not necessary
                .add("mode", DmType.NORMAL)
                .add("fontsize", "25")//default, not necessary
                .add("rnd", rndStr.substring(0, rndStr.length() - 3))
                .build();
        Request request = new Request.Builder().url(sendMsgUrl)
                .post(requestBody).build();
        try (Response response = HttpClient.getClient().newCall(request).execute()) {
            logger.debug(Objects.requireNonNull(response.body()).string());
        } catch (IOException e) {
            logger.error(e.toString());
        }
    }

    /**
     * build websocket for 弹幕
     */
    public static void buildWebsocket(){
        OkHttpClient client = new OkHttpClient.Builder()
//                .pingInterval(30, TimeUnit.SECONDS)
                .build();
        setDmWebSocketUrl();
        if (danmuWebsocketList.isEmpty()) {
            logger.error("no danmu websocket url");
            return;
        }
        String url = "wss://" + danmuWebsocketList.get(0).host + "/sub";
        logger.info(url);
        Request request = new Request.Builder()
                .header("User-Agent","Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36")
                .header("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,de;q=0.7,en-US;q=0.6")
                .header("Origin", "https://live.bilibili.com")
                .url(url)
                .build();
        logger.info("start to build websocket connection");
        client.newWebSocket(request, new DmWebSocketListener());
    }

    /**
     * @return
     * @see backend.BiliApi#login() run this method first to init csrfToken
     */
    private static FormBody.Builder requestBodyBuilder() {
        return new FormBody.Builder()
                .add("room_id", roomId)
                .add("roomid", roomId)
                .add("platform", platform)
                .add("csrf", csrfToken)
                .add("csrf_token", csrfToken);
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
        sb.delete(sb.length() - 2, sb.length());
        return sb.toString();
    }

    /**
     * get websocket url to read 弹幕
     */
    @MightEmpty
    public static void setDmWebSocketUrl() {
        Request request = new Request.Builder()
                .url(getSocketUrlUrl + "?id=" + roomId + "&type=0").build();
        try (Response response = HttpClient.getClient().newCall(request).execute()) {
            String responseStr = Objects.requireNonNull(response.body()).string();
            logger.info(responseStr);
            DanmuInfo danmuInfo = new Gson().fromJson(responseStr, DanmuInfo.class);
            if (danmuInfo.code != 0) {
                logger.error("cannot get danmu web socket url");
                logger.error(responseStr);
            }
            webSocketFirstMessageToken = danmuInfo.data.token;
            danmuWebsocketList.clear();
            danmuWebsocketList.addAll(danmuInfo.data.hostList);
        } catch (IOException e) {
            logger.error(e.toString());
            danmuWebsocketList.clear();
        }
    }

    private class DanmuInfo{
        private DanmuInfoData data;

        private Integer code;

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public DanmuInfoData getData() {
            return data;
        }

        public void setData(DanmuInfoData data) {
            this.data = data;
        }
    }
    private class DanmuInfoData{
        @SerializedName("host_list")
        private List<DanmuInfoDataHost> hostList;

        private String token;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public List<DanmuInfoDataHost> getHostList() {
            return hostList;
        }

        public void setHostList(List<DanmuInfoDataHost> hostList) {
            this.hostList = hostList;
        }
    }

    private class DanmuInfoDataHost {
        private String host;
        private Integer port;
        @SerializedName("ws_port")
        private Integer wsPort;
        @SerializedName("wss_port")
        private Integer wssPort;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }

        public Integer getWsPort() {
            return wsPort;
        }

        public void setWsPort(Integer wsPort) {
            this.wsPort = wsPort;
        }

        public Integer getWssPort() {
            return wssPort;
        }

        public void setWssPort(Integer wssPort) {
            this.wssPort = wssPort;
        }
    }

    public static void main(String[] args) throws IOException {
//        System.out.println(new BiliApi().roomInfo("9325157"));
//        System.out.println(new BiliApi().startStream("9325157", "372", "pc", "4a92355f9f9431c36bb1066e71d1c578"));
        setDmWebSocketUrl();
    }
}
