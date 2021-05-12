package backend;

import backend.util.HttpClient;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import dto.RoomInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import okhttp3.Response;

import javax.annotation.Nullable;
import java.util.Objects;

@Slf4j
public class UserApi {
    private static String getUserInfoUrl = "https://api.live.bilibili.com/xlive/web-ucenter/user/get_user_info";
    private static String getLiveInfoUrl = "https://api.live.bilibili.com/xlive/web-ucenter/user/live_info";

    @Nullable
    public static Long getUserId(){
        Request request = new Request.Builder()
                .url(getUserInfoUrl).get().build();
        try (Response response = HttpClient.getClient().newCall(request).execute()) {
            log.info(Objects.requireNonNull(response.body()).string());
            GetUserInfoResp respJson = new Gson().fromJson(Objects.requireNonNull(response.body()).string(), GetUserInfoResp.class);
            return respJson.data.uid;
        } catch (Exception e) {
            return null;
        }
    }

    @Nullable
    public static Long getLiveRoomId(){
        Request request = new Request.Builder()
                .url(getUserInfoUrl).get().build();
        try (Response response = HttpClient.getClient().newCall(request).execute()) {
            log.info(Objects.requireNonNull(response.body()).string());
            GetLiveInfoResp respJson = new Gson().fromJson(Objects.requireNonNull(response.body()).string(), GetLiveInfoResp.class);
            return respJson.data.roomId;
        } catch (Exception e) {
            return null;
        }
    }
    @EqualsAndHashCode(callSuper = true)
    @Data
    private class GetUserInfoResp extends StandardResp{
        private Integer code;
        private GetUserInfoRespData data;//todo
        private String message;
        private Integer ttl;
    }
    @Data
    private class GetUserInfoRespData{
        private Long uid;
        private String name;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    private class GetLiveInfoResp extends StandardResp{
        private GetLiveInfoRespData data;
    }

    @Data
    private class GetLiveInfoRespData {
        @SerializedName("room_id")
        private Long roomId;
    }

}
