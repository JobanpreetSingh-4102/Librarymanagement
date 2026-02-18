package main.java.library.dialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.imageio.ImageIO;

public class RoleSelectionDialog extends JDialog {
    private String selectedRole = null;

    private static final Color PRIMARY_COLOR = new Color(0x2E, 0x5C, 0x8A);
    private static final Color SECONDARY_COLOR = new Color(0x5A, 0x9B, 0xD5);
    private static final Color BACKGROUND_COLOR = new Color(0xF0, 0xF0, 0xF0);
    private static final Color CARD_BG = new Color(255, 255, 255, 230);
    private static final Color CARD_HOVER = new Color(0x2E, 0x5C, 0x8A, 25);

    private BufferedImage backgroundImage;

    public RoleSelectionDialog() {
        setTitle("Library Management System");
        setModal(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(650, 520);
        setLocationRelativeTo(null);
        setResizable(false);

        URL iconURL = getClass().getResource("/main/java/library/resources/logo.png");
        if (iconURL != null) {
            setIconImage(new ImageIcon(iconURL).getImage());
        }

        try {
            URL bgURL = getClass().getResource("/main/java/library/resources/bg.png");
            if (bgURL != null) {
                backgroundImage = ImageIO.read(bgURL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
                    g2d.setColor(new Color(0, 0, 0, 120));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    g2d.dispose();
                } else {
                    g.setColor(PRIMARY_COLOR);
                    ((Graphics2D) g).fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        mainPanel.setOpaque(false);
        setContentPane(mainPanel);

        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        mainPanel.add(createButtonPanel(), BorderLayout.CENTER);
        mainPanel.add(createFooterPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(35, 20, 10, 20));

        JPanel titlePanel = new JPanel(new GridLayout(3, 1, 0, 4));
        titlePanel.setOpaque(false);

        URL logoURL = getClass().getResource("/library/resources/logo.png");
        if (logoURL != null) {
            ImageIcon rawIcon = new ImageIcon(logoURL);
            Image scaled = rawIcon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
            JLabel logoLabel = new JLabel(new ImageIcon(scaled), SwingConstants.CENTER);
            logoLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
            headerPanel.add(logoLabel, BorderLayout.NORTH);
        }

        JLabel titleLabel = new JLabel("Library Management System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Select your role to get started", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(0xDD, 0xDD, 0xDD));

        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);

        headerPanel.add(titlePanel, BorderLayout.CENTER);
        return headerPanel;
    }

    private JPanel createButtonPanel() {
        JPanel wrapper = new JPanel(new GridLayout(1, 2, 25, 0));
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(20, 35, 20, 35));

        String[] studentFeatures = {
                "Search Books",
                "View Borrowed Books",
                "Borrowing History",
                "Request New Books",
                "Library Information"
        };

        String[] staffFeatures = {
                "Manage Books & Members",
                "Issue & Return Books",
                "Collect Fines",
                "Approve Book Requests",
                "Reports & Settings"
        };

        JPanel studentCard = createRoleCard(
                "\uD83C\uDF93",
                "Student",
                studentFeatures,
                new Color(0x2E, 0x7D, 0x32)
        );
        studentCard.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                selectedRole = "Student";
                dispose();
            }
        });

        JPanel staffCard = createRoleCard(
                "\uD83D\uDCBC",
                "Staff",
                staffFeatures,
                PRIMARY_COLOR
        );
        staffCard.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                selectedRole = "Staff";
                dispose();
            }
        });

        wrapper.add(studentCard);
        wrapper.add(staffCard);
        return wrapper;
    }

    private JPanel createRoleCard(String icon, String title, String[] features, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(0, 0)) {
            private boolean hovered = false;

            {
                setOpaque(false);
                setCursor(new Cursor(Cursor.HAND_CURSOR));
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) {
                        hovered = true;
                        repaint();
                    }
                    public void mouseExited(MouseEvent e) {
                        hovered = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (hovered) {
                    g2d.setColor(new Color(255, 255, 255, 245));
                } else {
                    g2d.setColor(CARD_BG);
                }
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                g2d.setColor(accentColor);
                g2d.fillRoundRect(0, 0, getWidth(), 6, 20, 20);
                g2d.fillRect(0, 3, getWidth(), 3);

                if (hovered) {
                    g2d.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 15));
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                }

                g2d.dispose();
                super.paintComponent(g);
            }
        };

        card.setBorder(BorderFactory.createEmptyBorder(20, 18, 18, 18));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        topPanel.setOpaque(false);

        JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER);
        iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 36));
        topPanel.add(iconLabel);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(accentColor);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(titleLabel);

        centerPanel.add(Box.createVerticalStrut(10));

        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setForeground(new Color(0xDD, 0xDD, 0xDD));
        centerPanel.add(sep);

        centerPanel.add(Box.createVerticalStrut(8));

        for (String feature : features) {
            JLabel featureLabel = new JLabel("\u2022  " + feature);
            featureLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
            featureLabel.setForeground(new Color(0x55, 0x55, 0x55));
            featureLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            featureLabel.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 0));
            centerPanel.add(featureLabel);
        }

        centerPanel.add(Box.createVerticalStrut(12));

        JLabel clickLabel = new JLabel("Click to enter \u2192", SwingConstants.CENTER);
        clickLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        clickLabel.setForeground(accentColor);
        clickLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(clickLabel);

        card.add(topPanel, BorderLayout.NORTH);
        card.add(centerPanel, BorderLayout.CENTER);

        for (Component c : card.getComponents()) {
            propagateMouseListeners(c, card);
        }

        return card;
    }

    private void propagateMouseListeners(Component child, JPanel card) {
        child.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                for (MouseListener ml : card.getMouseListeners()) {
                    ml.mouseClicked(e);
                }
            }
            public void mouseEntered(MouseEvent e) {
                for (MouseListener ml : card.getMouseListeners()) {
                    ml.mouseEntered(e);
                }
            }
            public void mouseExited(MouseEvent e) {
                Point p = SwingUtilities.convertPoint(child, e.getPoint(), card);
                if (!card.contains(p)) {
                    for (MouseListener ml : card.getMouseListeners()) {
                        ml.mouseExited(e);
                    }
                }
            }
        });
        if (child instanceof Container) {
            for (Component c : ((Container) child).getComponents()) {
                propagateMouseListeners(c, card);
            }
        }
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setOpaque(false);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 15, 10));

        JLabel footerLabel = new JLabel("Library Management System v2.0");
        footerLabel.setForeground(new Color(0xCC, 0xCC, 0xCC));
        footerLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));

        footerPanel.add(footerLabel);
        return footerPanel;
    }

    public String getSelectedRole() {
        return selectedRole;
    }

    public static String showDialog() {
        RoleSelectionDialog dialog = new RoleSelectionDialog();
        dialog.setVisible(true);
        return dialog.getSelectedRole();
    }
}
