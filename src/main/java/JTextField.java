import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.*;

public class JTextField extends javax.swing.JTextField {
    private String hint = "";
    private FocusListener focusListener = null;
    private KeyListener keyListener = null;
    private boolean hintOnText = true;

    private class FocusListener implements java.awt.event.FocusListener {
        @Override
        public void focusGained(FocusEvent e) {
            setForeground(Color.BLACK);
            if (hintOnText) {
                setText("");
            }
        }

        @Override
        public void focusLost(FocusEvent e) {
            if (hintOnText) {
                setForeground(Color.GRAY);
                setText(hint);
            }
        }
    }

    private class KeyListener implements java.awt.event.KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {
            System.out.println(hintOnText);
            System.out.println("not caught by press");
            boolean textExist = getText() != null && !getText().isEmpty() && !getText().trim().isEmpty();
            if (!hintOnText) {
                hintOnText = textExist;
            } else {
                //todo
                if (textExist) {
                    hintOnText = false;
                    System.out.println("input");
                }else{
                    System.out.println("no input");
                }
            }
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (hintOnText) {
                setText("");
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }
    }

    public void setHint(String hint) {
        if (hint == null) {
            hint = "";
        }
        //not thread safe, but it's not necessary
        if (focusListener == null) {
            focusListener = new FocusListener();
        }
        this.addFocusListener(focusListener);
        if (keyListener == null) {
            keyListener = new KeyListener();
        }
        this.addKeyListener(keyListener);

        this.hint = hint;

        if (getText() == null || getText().isEmpty()) {
            setForeground(Color.gray);
            setText(hint);
        }
    }

    @Override
    public String getText() {
        if (hintOnText) {
            return null;
        }
        return super.getText();
    }
}
