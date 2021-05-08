package backend;

import backend.tool.Danmu;
import backend.tool.Zlib;
import com.google.gson.Gson;
import frontend.AppContext;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Timer;
import java.util.TimerTask;

public class DmWebSocketListener extends WebSocketListener {
    private static final Logger logger = LoggerFactory.getLogger(DmWebSocketListener.class);

    @Override
    public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        logger.info("socket connection close");
        super.onClosed(webSocket, code, reason);
    }

    @Override
    public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        logger.info("serve ready to close");
        super.onClosing(webSocket, code, reason);
    }

    @Override
    public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
        logger.error("websocket failed");
        t.printStackTrace();
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
        super.onMessage(webSocket, text);
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteString bytes) {
        byte[] byteArray = bytes.toByteArray();
        if (isPingPong(byteArray)) {
            logger.info("get pong");
            byte[] messageBytes = new byte[byteArray.length - 16];
            System.arraycopy(byteArray, 16, messageBytes, 0, byteArray.length - 16);
            Pong pong = new Gson().fromJson(new String(messageBytes), Pong.class);
            logger.info("popular index value is: "+pong.code);
        } else if (isDm(byteArray)) {
            AppContext context = AppContext.instance();
            JTextArea textArea = context.get("textArea", JTextArea.class);
            String msg = Zlib.inflate(byteArray);
            Danmu danmu = new Gson().fromJson(msg, Danmu.class);
            String danmuStr = danmu.getInfoDetail(1, String.class);
            textArea.append(danmuStr);
        }
    }

    @Override
    public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
        //init json
        String key = BiliApi.webSocketFirstMessageToken;
        if (key == null) {
            logger.error("cannot build web socket connection");
        }
        logger.info("websocket onopen");
        InitJson initJson = new InitJson(BiliApi.uid, Integer.parseInt(BiliApi.roomId), key);
        String json = new Gson().toJson(initJson);
        byte[] jsonBytes = json.getBytes(StandardCharsets.UTF_8);
        int totalLength = jsonBytes.length + 16;
        byte[] lengthBytes = ByteBuffer.allocate(4).putInt(totalLength).array();

        //deal with the first request
        byte[] firstFrame = new byte[totalLength];
        //set length
        System.arraycopy(lengthBytes, 0, firstFrame, 0, 4);
        //set header length
        firstFrame[4] = 0;
        firstFrame[5] = 16;
        //set proto version
        firstFrame[6] = 0;
        firstFrame[7] = 1;
        //set operation type
        firstFrame[8] = 0;
        firstFrame[9] = 0;
        firstFrame[10] = 0;
        firstFrame[11] = 7;
        //I don't know what this value for
        firstFrame[12] = 0;
        firstFrame[13] = 0;
        firstFrame[14] = 0;
        firstFrame[15] = 1;
        //set content
        System.arraycopy(jsonBytes, 0, firstFrame, 16, jsonBytes.length);
        webSocket.send(new ByteString(firstFrame));
        logger.info("websocket connection built");

        //heartbeat
        byte[] heartbeat = new byte[31];
        heartbeat[0]=0;
        heartbeat[1]=0;
        heartbeat[2]=0;
        heartbeat[3]=31;
        heartbeat[4] = 0;
        heartbeat[5] = 16;
        heartbeat[6] = 0;
        heartbeat[7] = 1;
        heartbeat[8] = 0;
        heartbeat[9] = 0;
        heartbeat[10] = 0;
        heartbeat[11] = 2;
        heartbeat[12] = 0;
        heartbeat[13] = 0;
        heartbeat[14] = 0;
        heartbeat[15] = 1;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                webSocket.send(new ByteString(heartbeat));
                logger.info("send ping");
            }
        },0L,30*1000L);
    }

    /**
     * test if a message is danmu
     */
    private boolean isDm(byte[] bytes) {
        return bytes[7] == 2;
    }

    /**
     * test if a message from serve is pingpong
     */
    private boolean isPingPong(byte[] byteArray) {
        return byteArray[7] == 1;
    }

    private class InitJson {
        private String uid;
        private int roomid;
        private String protover = "2";
        private String platform = "web";
        private Integer type = 2;
        private String key;

        public InitJson(String uid, int roomid, String key) {
            this.uid = uid;
            this.roomid = roomid;
            this.key = key;
        }

    }

    private class Pong{
        private Integer code;
    }

    public static void main(String[] args) {
//        byte[] bytes = {16};
//        System.out.printf("%02X%n",bytes[0]);
        String s = "756762355038715f74637247347550304c65544a524d5456385a42735f454e42564151676b32457738464d725a495f5969417144374846464530353941327a69384a3774493747756937344d386e6f75564e444974626c37546830736439616a53385f583066427643764b3279624e69426a4b5164457a4f39654f55675f30684a726772646a32755952652d397a3350533538747a6f4963227d";
        int len = s.length();
        byte[] bytes = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            bytes[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        String result = new String(bytes);
        System.out.println(result);
        byte[] decode = Base64.getDecoder().decode(result);
        System.out.println(new String(decode));
    }
}
