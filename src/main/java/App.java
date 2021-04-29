import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

public class App {
    private static final App app = new App();

    private static final Logger logger= LoggerFactory.getLogger(App.class);
    private static final int windowWidth = 300;
    private static final int windowHeight = 600;
    private static final int windowLocationX = 2000;
    private static final int windowLocationY = 1200;

    private JTextArea textArea_messagePanel = new JTextArea();
    private JTextField textField_inputPanel = new JTextField();

    private JPanel messagePanel(){
        JPanel panel = new JPanel();
        textArea_messagePanel.setText("no message now\n");
        panel.add(textArea_messagePanel);
        return panel;
    }

    private JPanel inputPanel(){
        JPanel panel = new JPanel();

        textField_inputPanel.setText("input your message here");

        JButton buttonSend = new JButton("send");
        buttonSend.addActionListener(actionEvent -> {
            textArea_messagePanel.append("send button clicked\n");
            logger.debug("send button clicked");
        });

        panel.add(textField_inputPanel);
        panel.add(buttonSend);
        return panel;
    }

    private static void showFrame(){
        JFrame f = new JFrame();
        f.setSize(windowWidth, windowHeight);
        f.setLocation(windowLocationX,windowLocationY);

        JPanel messagePanel = app.messagePanel();
        JPanel inputPanel = app.inputPanel();
        f.add(messagePanel);
        f.add(inputPanel);
        f.setLayout(new BoxLayout(f.getContentPane(), BoxLayout.Y_AXIS));
        f.setVisible(true);
    }

    public static void main(String[] args) {
        showFrame();

        //init components
//        JTextField textField=new JTextField();
//
//        JButton buttonClear = new JButton("clear");
////        buttonClear.setBounds(0, 300);
//        buttonClear.addActionListener(actionEvent -> {
//            textArea.setText("");
//            logger.debug("clear button clicked");
//        });
//        JButton buttonSend = new JButton("send");
////        buttonSend.setBounds(300, 300);
//        buttonSend.addActionListener(actionEvent -> {
//            textArea.append("send button clicked\n");
//            logger.debug("send button clicked");
//        });
//
//        //set layout
//        f.add(textArea, BorderLayout.CENTER);
//        f.add(buttonClear,BorderLayout.SOUTH,0);
//        f.add(buttonSend, BorderLayout.SOUTH, 1);

        //show window

    }
}
