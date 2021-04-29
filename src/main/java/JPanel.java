import java.awt.*;

public class JPanel extends javax.swing.JPanel {
    public JPanel(int preferredWidth, int preferredHeight) {
        super();
        this.setPreferredSize(new Dimension(preferredWidth, preferredHeight));
    }

    public JPanel() {
        super();
    }
}
