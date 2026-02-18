package main.java.library.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.imageio.ImageIO;

public class BackgroundPanel extends JPanel {
    private static BufferedImage backgroundImage;
    private static final Color OVERLAY = new Color(240, 240, 240, 210);

    static {
        try {
            URL bgURL = BackgroundPanel.class.getResource("/main/java/library/resources/background.png");
            if (bgURL != null) {
                backgroundImage = ImageIO.read(bgURL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public BackgroundPanel(LayoutManager layout) {
        super(layout);
        setOpaque(false);
    }

    public BackgroundPanel() {
        super();
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
            g2d.setColor(OVERLAY);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        } else {
            g2d.setColor(new Color(0xF0, 0xF0, 0xF0));
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
        g2d.dispose();
    }
}
