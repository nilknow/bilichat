package frontend;

import java.awt.*;

public class JFrame extends javax.swing.JFrame {
    public JFrame() throws HeadlessException {
    }

    public JFrame(String title) throws HeadlessException {
        super(title);
    }

    public void flash() {
        for (int i = 0; i < 3; i++) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
            this.setVisible(false);

            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
            this.setVisible(true);
        }
    }
}
