package frontend;

import com.google.gson.Gson;
import json.RoomInfo;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

public class BiliApi {
    private static final Logger logger = LoggerFactory.getLogger(BiliApi.class);
    private volatile OkHttpClient client = null;
    private Object clientLock = new Object();

    public OkHttpClient getClient() {
        if (client == null) {
            synchronized (clientLock) {
                if (client == null) {
                    client = new OkHttpClient();
                }
            }
        }
        return client;
    }

    /**
     * login: some api need to login first
     */
    public boolean login() {
        return false;
    }

    /**
     * get live stream room info
     */
    public RoomInfo roomInfo(String roomId) throws IOException {
        Request request = new Request.Builder()
                .url("https://api.live.bilibili.com/room/v1/Room/room_init?id=" + roomId).get().build();
        try (Response response = getClient().newCall(request).execute()) {
            return new Gson().fromJson(Objects.requireNonNull(response.body()).string(), RoomInfo.class);
        }
    }

    /**
     * start live stream
     */
    public boolean startStream(String roomId, String areaId, String platform, String csrfToken) {
        RequestBody requestBody = new FormBody.Builder()
                .add("room_id", roomId)
                .add("area_v2", areaId)
                .add("platform", platform)
                .add("csrf", csrfToken)
                .build();
        Request request = new Request.Builder().url("https://api.live.bilibili.com/room/v1/Room/startLive")
                .post(requestBody).build();
        try (Response response = getClient().newCall(request).execute()) {
            logger.debug(Objects.requireNonNull(response.body()).string());
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * stop live stream
     */
    public void stopStream(String roomId, String csrf) {

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
