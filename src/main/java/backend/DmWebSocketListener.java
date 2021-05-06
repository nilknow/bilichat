package backend;

import backend.tool.Zlib;
import frontend.AppContext;
import frontend.JTextField;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

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
        super.onFailure(webSocket, t, response);
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
        super.onMessage(webSocket, text);
    }


    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteString bytes) {
        byte[] byteArray = bytes.toByteArray();
        if (isDm(byteArray)) {
            AppContext context = AppContext.instance();
            JTextArea textArea = context.get("textArea", JTextArea.class);
            String msg = Zlib.inflate(byteArray);
            textArea.append(msg);
        }
        super.onMessage(webSocket, bytes);
    }

    @Override
    public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
        //todo deal with the first request
        logger.info("websocket connection built");
        super.onOpen(webSocket, response);
    }

    //todo analyse websocket message
    private boolean isDm(byte[] bytes) {
        return true;
    }
}
