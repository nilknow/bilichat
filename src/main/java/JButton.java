public class JButton extends javax.swing.JButton {
    private static final int defaultWidth = 90;
    private static final int defaultHeight = 70;

    public JButton() {
        this.setSize(defaultWidth,defaultHeight);
    }

    public JButton(String s) {
        super(s);
        this.setSize(defaultWidth,defaultHeight);
    }

    public void setBounds(int x, int y) {
        super.setBounds(x, y, defaultWidth, defaultHeight);
    }
}
