import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.Objects;

public class Main {
    private static final Logger logger= LoggerFactory.getLogger(Main.class);
    private static final int windowWidth = 600;
    private static final int windowHeight = 400;

    public static void main(String[] args) {
        JFrame f = new JFrame();

        JTextField textField = new JTextField();
        textField.setBounds(0, 0, 200, 200);

        JButton buttonClear = new JButton("clear");
        buttonClear.setBounds(0, 300);
        buttonClear.addActionListener(actionEvent -> {
            logger.debug("clear button clicked");
        });
        JButton buttonSend = new JButton("send");
        buttonSend.setBounds(300, 300);
        buttonSend.addActionListener(actionEvent -> {
            logger.debug("send button clicked");
            textField.setText("send button clicked");
        });

        f.add(buttonClear);
        f.add(buttonSend);

        f.setSize(windowWidth, windowHeight);
        f.setLayout(null);
        f.setVisible(true);
    }
}
