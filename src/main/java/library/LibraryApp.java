package main.java.library;

import main.java.library.dialogs.RoleSelectionDialog;
import main.java.library.panels.StudentPanel;
import main.java.library.panels.StaffPanel;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class LibraryApp extends JFrame {
    private String userRole;
    private JPanel contentPanel;
    private static final Color PRIMARY_COLOR = new Color(0x2E, 0x5C, 0x8A);
    private static final Color BACKGROUND_COLOR = new Color(0xF0, 0xF0, 0xF0);

    public LibraryApp(String role) {
        this.userRole = role;

        setTitle("Library Management System - " + role + " Portal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 750);
        setMinimumSize(new Dimension(900, 650));
        setLocationRelativeTo(null);

        URL iconURL = getClass().getResource("/main/java//library/resources/logo.png");
        if (iconURL != null) {
            setIconImage(new ImageIcon(iconURL).getImage());
        }

        getContentPane().setBackground(BACKGROUND_COLOR);

        createMainContent();

        setVisible(true);
    }

    private void createMainContent() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);

        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        if ("Student".equals(userRole)) {
            contentPanel = new StudentPanel();
        } else {
            contentPanel = new StaffPanel();
        }

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        JPanel statusBar = createStatusBar();
        mainPanel.add(statusBar, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Library Management System");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        String subtitle = "Student".equals(userRole)
                ? "Welcome, Student! Browse books, check your loans, and more."
                : "Staff Portal - Manage books, members, and library operations";

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(0xCC, 0xDD, 0xEE));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);

        JLabel roleLabel = new JLabel(userRole + " Portal");
        roleLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        roleLabel.setForeground(new Color(0xCC, 0xDD, 0xEE));

        JButton switchBtn = new JButton("Switch Role");
        switchBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        switchBtn.setBackground(new Color(0xFF, 0xFF, 0xFF));
        switchBtn.setForeground(PRIMARY_COLOR);
        switchBtn.setFocusPainted(false);
        switchBtn.setBorderPainted(false);
        switchBtn.setOpaque(true);
        switchBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        switchBtn.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));
        switchBtn.addActionListener(e -> switchRole());

        rightPanel.add(roleLabel);
        rightPanel.add(switchBtn);

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(rightPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBackground(new Color(0xDD, 0xDD, 0xDD));
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JLabel statusLabel = new JLabel("Ready | Library Management System v2.0 | " + userRole + " Portal");
        statusLabel.setForeground(new Color(0x33, 0x33, 0x33));

        statusBar.add(statusLabel);

        return statusBar;
    }

    private void switchRole() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Switch to a different role? This will close the current session.",
                "Switch Role", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            SwingUtilities.invokeLater(() -> {
                String newRole = RoleSelectionDialog.showDialog();
                if (newRole != null) {
                    new LibraryApp(newRole);
                } else {
                    System.exit(0);
                }
            });
        }
    }

    private void showAboutDialog() {
        String aboutText =
                "Library Management System\n" +
                        "Version 2.0\n\n" +
                        "A comprehensive application for:\n" +
                        "- Students: Search books, borrow, request books\n" +
                        "- Staff: Manage books, members, transactions\n\n" +
                        "Built with Java Swing";

        JOptionPane.showMessageDialog(this, aboutText, "About", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            String role = RoleSelectionDialog.showDialog();
            if (role != null) {
                new LibraryApp(role);
            } else {
                System.exit(0);
            }
        });
    }
}
