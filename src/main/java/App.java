import frontend.JButton;
import frontend.JFrame;
import frontend.JPanel;
import frontend.JTextField;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
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
import java.util.concurrent.atomic.AtomicBoolean;

public class App {
    private static final Logger log = LoggerFactory.getLogger(App.class);
    private static final App app = new App();
    private static final JFrame f = new JFrame();

    private static final int windowWidth = 300;
    private static final int windowHeight = 600;

    private static final String loginUrl = "https://passport.bilibili.com/login";

    private JTextArea textArea_messagePanel = new JTextArea();

    private JTextField textField_inputPanel = new JTextField();
    private JButton buttonSend_inputPanel = new JButton("send");

    private boolean noMessage = true;

    private static final Color TEXT_AREA_BACKGROUND_COLOR_DEFAULT = Color.BLACK;
    private static final Color TEXT_AREA_FOREGROUND_COLOR_DEFAULT = Color.WHITE;


    public static void main(String[] args) throws InterruptedException, InvocationTargetException {
        SwingUtilities.invokeAndWait(() -> {
            WebDriver browser = new ChromeDriver();
            browser.get(loginUrl);
            WebElement element = browser.findElement(By.id("login-username"));
            element.sendKeys("maxwangein@gmail.com");
            browser.findElement(By.id("login-passwd")).click();
//            loginFrame();
//            mainFrame();
//            setEventListener();
        });
    }

    private static void loginFrame(){

        JFrame f = new JFrame("java browser");

        JEditorPane jep = new JEditorPane();
        jep.setEditable(true);
        try {
            jep.setPage(loginUrl);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        JScrollPane jsp = new JScrollPane(jep);

        f.setContentPane(jsp);
        f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        f.setSize(1024, 860);
        f.setVisible(true);
    }

    private static void mainFrame() {
        f.setResizable(false);
        f.setAlwaysOnTop( true );
        f.setSize(windowWidth, windowHeight);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        f.setLocation(screenSize.width - windowWidth - 200, (screenSize.height - windowHeight) / 2);

        JPanel messagePanel = app.messagePanel();
        JPanel inputPanel = app.inputPanel();
        f.add(messagePanel);
        f.add(inputPanel);
        f.setLayout(new BoxLayout(f.getContentPane(), BoxLayout.Y_AXIS));
        f.setUndecorated(true);
        f.setVisible(true);
    }

    private JPanel messagePanel() {
        JPanel panel = new JPanel(f.getWidth(), f.getHeight() - 100);

        textArea_messagePanel.setEditable(false);
        textArea_messagePanel.setText("no message now...\n");
        textArea_messagePanel.setPreferredSize(panel.getPreferredSize());
        textArea_messagePanel.setBackground(TEXT_AREA_BACKGROUND_COLOR_DEFAULT);
        textArea_messagePanel.setForeground(TEXT_AREA_FOREGROUND_COLOR_DEFAULT);

        panel.add(textArea_messagePanel);
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
                log.debug("keyrelease");
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
                log.debug("focused");
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

        buttonSend_inputPanel = new JButton("send");
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
            textArea_messagePanel.append(input.trim() + "\n");

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
