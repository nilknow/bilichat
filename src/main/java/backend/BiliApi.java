package backend;

import backend.util.HttpClient;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import dto.RoomInfo;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
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

@Slf4j
public class BiliApi {
    public static final String uid = "247897641";
    public static final String roomId = "9325157";
    public static String webSocketFirstMessageToken = null;
    private static final String platform = "pc";
    private static final String startStreamUrl = "https://api.live.bilibili.com/room/v1/Room/startLive";
    private static final String stopStreamUrl = "https://api.live.bilibili.com/room/v1/Room/stopLive";
    private static final String sendMsgUrl = "https://api.live.bilibili.com/msg/send";
    //api to get socket url
    private static final String getSocketUrlUrl = "https://api.live.bilibili.com/xlive/web-room/v1/index/getDanmuInfo";
    private static final List<DanmuInfoDataHost> danmuWebsocketList = new ArrayList<>();

    public static void startStreamIfNot() {
        RoomInfo roomInfo = BiliApi.roomInfo(BiliApi.roomId);
        if (roomInfo != null&&roomInfo.getLiveStatus()!=null&&roomInfo.getLiveStatus()==0) {
            boolean isStreamStart = BiliApi.startStream();
            if (!isStreamStart) {
                log.error("stream can't start");
            }
        }
    }

    /**
     * get live stream room info
     */
    public static RoomInfo roomInfo(String roomId) {
        Request request = new Request.Builder()
                .url("https://api.live.bilibili.com/room/v1/Room/room_init?id=" + roomId).get().build();
        try (Response response = HttpClient.getClient().newCall(request).execute()) {
            log.info(Objects.requireNonNull(response.body()).string());
            return new Gson().fromJson(Objects.requireNonNull(response.body()).string(), RoomInfo.class);
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean startStream() {
        return startStream(roomId, "372", platform, LoginApi.csrfToken);
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
            log.debug(Objects.requireNonNull(response.body()).string());
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
            log.debug(Objects.requireNonNull(response.body()).string());
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
            log.debug(Objects.requireNonNull(response.body()).string());
        } catch (IOException e) {
            log.error(e.toString());
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
            log.error("no danmu websocket url");
            return;
        }
        String url = "wss://" + danmuWebsocketList.get(0).host + "/sub";
        log.info(url);
        Request request = new Request.Builder()
                .header("User-Agent","Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36")
                .header("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,de;q=0.7,en-US;q=0.6")
                .header("Origin", "https://live.bilibili.com")
                .url(url)
                .build();
        log.info("start to build websocket connection");
        client.newWebSocket(request, new DmWebSocketListener());
    }

    /**
     * @return
     * @see backend.LoginApi#login() run this method first to init csrfToken
     */
    private static FormBody.Builder requestBodyBuilder() {
        return new FormBody.Builder()
                .add("room_id", roomId)
                .add("roomid", roomId)
                .add("platform", platform)
                .add("csrf", LoginApi.csrfToken)
                .add("csrf_token", LoginApi.csrfToken);
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
            log.info(responseStr);
            DanmuInfo danmuInfo = new Gson().fromJson(responseStr, DanmuInfo.class);
            if (danmuInfo.code != 0) {
                log.error("cannot get danmu web socket url");
                log.error(responseStr);
            }
            webSocketFirstMessageToken = danmuInfo.data.token;
            danmuWebsocketList.clear();
            danmuWebsocketList.addAll(danmuInfo.data.hostList);
        } catch (IOException e) {
            log.error(e.toString());
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
    }
}
