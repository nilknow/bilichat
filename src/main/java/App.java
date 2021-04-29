import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);
    private static final App app = new App();
    private static final JFrame f = new JFrame();

    private static final int windowWidth = 300;
    private static final int windowHeight = 600;

    private JTextArea textArea_messagePanel = new JTextArea();
    private JTextField textField_inputPanel = new JTextField();

    private JPanel messagePanel() {
        JPanel panel = new JPanel(f.getWidth(), f.getHeight() - 200);

        textArea_messagePanel.setEditable(false);
        textArea_messagePanel.setText("no message now\n");
        textArea_messagePanel.setPreferredSize(panel.getPreferredSize());

        panel.add(textArea_messagePanel);
        return panel;
    }

    private JPanel inputPanel() {
        JPanel panel = new JPanel();

        textField_inputPanel.setHint("input your message here");

        JButton buttonSend = new JButton("send");
        buttonSend.addActionListener(actionEvent -> {
            String input = textField_inputPanel.getText();
            if (input == null || input.isEmpty() || input.trim().isEmpty()) {
                return;
            }
            textArea_messagePanel.append(input.trim()+"\n");
            f.setVisible(true);
            logger.debug("send button clicked");
        });

        panel.add(textField_inputPanel);
        panel.add(buttonSend);
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        return panel;
    }

    private static void showFrame() {
        f.setResizable(false);
        f.setSize(windowWidth, windowHeight);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        f.setLocation(screenSize.width - windowWidth - 200, (screenSize.height - windowHeight) / 2);

        JPanel messagePanel = app.messagePanel();
        JPanel inputPanel = app.inputPanel();
        f.add(messagePanel);
        f.add(inputPanel);
        f.setLayout(new BoxLayout(f.getContentPane(), BoxLayout.Y_AXIS));
        f.pack();
        f.setVisible(true);
    }

    public static void main(String[] args) throws InterruptedException, InvocationTargetException {
        SwingUtilities.invokeAndWait(()->{
            showFrame();
//            setEventListener();
        });
    }
}
