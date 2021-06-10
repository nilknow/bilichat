package frontend;

import java.awt.event.*;

public class JTextField extends javax.swing.JTextField {
    /**
     * if hint is not empty and there are no input in this JTextField,
     * the hint will be shown in the JTextField, And the color of the hint is gray
     * hint can't be committed as text in JTextField.
     */
    private String hint = "";
    private FocusListener focusListener = null;
    private KeyListener keyListener = null;
    private boolean hintOnText = true;

    public void setHint(String hint){
        this.hint=hint;
        setText(hint);
    }

    @Override
    public void setText(String t) {
        super.setText(t);
        //get related components
        //change related components' status
    }
}
