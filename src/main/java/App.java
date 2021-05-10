import backend.BiliApi;
import backend.tool.HttpClient;
import dto.RoomInfo;
import frontend.*;
import frontend.JButton;
import frontend.JFrame;
import frontend.JPanel;
import frontend.JTextField;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class App {
    private static final Logger log = LoggerFactory.getLogger(App.class);
    private static final App app = new App();
    private static final JFrame f = new JFrame();

    private static final int windowWidth = 300;
    private static final int windowHeight = 700;

    private JTextArea textArea_messagePanel = new JTextArea();
    private JTextField textField_inputPanel = new JTextField();
    private JButton buttonSend_inputPanel = new JButton("send");

    private boolean noMessage = true;

    private static final Color TEXT_AREA_BACKGROUND_COLOR_DEFAULT = Color.BLACK;
    private static final Color TEXT_AREA_FOREGROUND_COLOR_DEFAULT = Color.WHITE;


    public static void main(String[] args) throws InterruptedException, InvocationTargetException {
        SwingUtilities.invokeAndWait(() -> {
            BiliApi.login();

            RoomInfo roomInfo = BiliApi.roomInfo(BiliApi.roomId);
            if (roomInfo != null&&roomInfo.getLiveStatus()!=null&&roomInfo.getLiveStatus()==0) {
                boolean isStreamStart = BiliApi.startStream();
                if (!isStreamStart) {
                    log.error("stream can't start");
                }
            }

            mainFrame();
            BiliApi.buildWebsocket();
        });
    }

    /**
     * the chat window
     */
    private static void mainFrame() {
        f.setResizable(false);
        f.setAlwaysOnTop( true );
        f.setUndecorated(true);
        f.setPreferredSize(new Dimension(windowWidth, windowHeight));
        f.pack();
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        f.setLocation(screenSize.width - windowWidth - 200, (screenSize.height - windowHeight) / 2);

        JPanel messagePanel = app.messagePanel();
        JPanel inputPanel = app.inputPanel();
        f.add(messagePanel);
        f.add(inputPanel);
        f.setLayout(new BoxLayout(f.getContentPane(), BoxLayout.Y_AXIS));
        f.setVisible(true);
    }

    private JPanel messagePanel() {
        JPanel panel = new JPanel(f.getPreferredSize().width, f.getPreferredSize().height - 100);

        textArea_messagePanel.setEditable(false);
        textArea_messagePanel.setLineWrap(true);

        textArea_messagePanel.setText("enter room...\n");

        textArea_messagePanel.setBackground(TEXT_AREA_BACKGROUND_COLOR_DEFAULT);
        textArea_messagePanel.setForeground(TEXT_AREA_FOREGROUND_COLOR_DEFAULT);
        JScrollPane jsp = new JScrollPane(textArea_messagePanel);
        jsp.setPreferredSize(panel.getPreferredSize());

        panel.add(jsp);
        AppContext context = AppContext.instance();
        context.add("textArea", app.textArea_messagePanel);
        return panel;
    }


    private JPanel inputPanel() {
        JPanel panel = new JPanel();

        String hint = "input your message here";
        textField_inputPanel.setHint(hint);
        textField_inputPanel.setForeground(Color.GRAY);
        textField_inputPanel.setTransferHandler(null);//disable copy,paste
        AtomicBoolean hasInput = new AtomicBoolean(false);
        textField_inputPanel.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (textField_inputPanel.getText() == null || "".equals(textField_inputPanel.getText())) {
                    hasInput.set(false);
                    textField_inputPanel.setForeground(Color.GRAY);
                    buttonSend_inputPanel.setEnabled(false);
                }else{
                    hasInput.set(true);
                    textField_inputPanel.setForeground(Color.BLACK);
                    buttonSend_inputPanel.setEnabled(true);
                }
            }
        });
        textField_inputPanel.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (!hasInput.get()) {
                    textField_inputPanel.setText("");
                    buttonSend_inputPanel.setEnabled(false);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                log.debug("lost");
                if (!hasInput.get()) {
                    textField_inputPanel.setText(hint);
                    buttonSend_inputPanel.setEnabled(false);
                }
            }
        });
        textField_inputPanel.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    buttonSend_inputPanel.doClick();
                    textArea_messagePanel.setText("");
                    hasInput.set(false);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        buttonSend_inputPanel.addActionListener(actionEvent -> {
            if (!hasInput.get()) {
                return;
            }
            if (noMessage) {
                noMessage = false;
                textArea_messagePanel.setText("");
            }

            String input = textField_inputPanel.getText();
            if (input == null || input.isEmpty() || input.trim().isEmpty()) {
                return;
            }
            if (input.trim().length() > 20) {
                textArea_messagePanel.append("您当前输入内容长度大于b站限制");
                return;
            }

            BiliApi.sendMessage(input.trim());

            hasInput.set(false);
            //textField will lose focus first, so we need to set it manually
            textField_inputPanel.setText(hint);
            textField_inputPanel.setForeground(Color.GRAY);
        });
        buttonSend_inputPanel.setEnabled(false);


        panel.add(textField_inputPanel);
        panel.add(buttonSend_inputPanel);
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        return panel;
    }

}
