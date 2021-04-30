import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicBoolean;

public class App {
    private static final Logger log = LoggerFactory.getLogger(App.class);
    private static final App app = new App();
    private static final JFrame f = new JFrame();

    private static final int windowWidth = 300;
    private static final int windowHeight = 600;


    private JTextArea textArea_messagePanel = new JTextArea();

    private JTextField textField_inputPanel = new JTextField();
    private JButton buttonSend_inputPanel = new JButton("send");

    private static final Color TEXT_AREA_BACKGROUND_COLOR_DEFAULT = Color.BLACK;
    private static final Color TEXT_AREA_FOREGROUND_COLOR_DEFAULT = Color.WHITE;

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

    AtomicBoolean hasInput = new AtomicBoolean(false);
    private JPanel inputPanel() {
        JPanel panel = new JPanel();

        String hint = "input your message here";
        textField_inputPanel.setHint(hint);
        DocumentListener documentInsertListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
//                try {
//                    log.debug(e.getDocument().getText(0, e.getDocument().getLength()));
//                } catch (BadLocationException badLocationException) {
//                    log.error(badLocationException.toString());
//                }
                hasInput.set(true);
                buttonSend_inputPanel.setEnabled(true);
                log.debug("document inserted");
            }

            @Override
            public void removeUpdate(DocumentEvent e) {

            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        };
        textField_inputPanel.getDocument().addDocumentListener(documentInsertListener);
        textField_inputPanel.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                log.debug("focusgained");
                //todo use another method to test if use hint
                if (!hasInput.get()) {
                    textField_inputPanel.setText("");
                    log.debug("clear hint");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                log.debug("focuslost");
                if (!hasInput.get()) {
                    try {
                        //ugly implement
                        textField_inputPanel.getDocument().removeDocumentListener(documentInsertListener);
                        textField_inputPanel.getDocument().insertString(0, hint, null);
                        textField_inputPanel.getDocument().addDocumentListener(documentInsertListener);
                        log.debug("set hint to text");
                    } catch (BadLocationException badLocationException) {
                        log.error(badLocationException.toString());
                    }
                }
            }
        });

        buttonSend_inputPanel = new JButton("send");
        buttonSend_inputPanel.addActionListener(actionEvent -> {
            log.debug("clicked");
            if (!hasInput.get()) {
                return;
            }
            String input = textField_inputPanel.getText();
            if (input == null || input.isEmpty() || input.trim().isEmpty()) {
                return;
            }
            textArea_messagePanel.append(input.trim() + "\n");
            log.debug("send button clicked");

            hasInput.set(false);
            textField_inputPanel.setText("");
        });
        buttonSend_inputPanel.setEnabled(false);

        panel.add(textField_inputPanel);
        panel.add(buttonSend_inputPanel);
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
        f.setUndecorated(true);
        f.setVisible(true);
    }

    public static void main(String[] args) throws InterruptedException, InvocationTargetException {
        SwingUtilities.invokeAndWait(() -> {
            showFrame();
//            setEventListener();
        });
    }
}
