import frontend.JFrame;
import frontend.JPanel;
import org.junit.jupiter.api.Test;

import java.awt.*;

class AppTest {
    @Test
    public void transparentWindow() throws InterruptedException {
        JFrame jFrame = new JFrame();
        jFrame.setSize(new Dimension(300,300));
        JPanel jPanel = new JPanel();
        jPanel.setPreferredSize(new Dimension(300, 300));
        jFrame.setContentPane(jPanel);
        jFrame.setVisible(true);
        while (true) {
            Thread.sleep(100);
            jFrame.setVisible(false);
            Thread.sleep(100);
            jFrame.setVisible(true);
            ;
        }
    }
}