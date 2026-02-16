package main.java.library;


import main.java.library.dialogs.RoleSelectionDialog;
import main.java.library.panels.StudentPanel;
import main.java.library.panels.StaffPanel;

import javax.swing.*;
import java.awt.*;

public class LibraryApp extends JFrame {
    private String userRole;
    private JPanel contentPanel;

    private static final Color PRIMARY_COLOR = new Color(0x2E, 0x5C, 0x8A);
    private static final Color BACKGROUND_COLOR = new Color(0xF0, 0xF0, 0xF0);

    public LibraryApp(String role) {
        this.userRole = role;

        setTitle("Library Management System - " + role + " Portal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        getContentPane().setBackground(BACKGROUND_COLOR);

        createMenuBar();
        createMainContent();

        pack();
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        setVisible(true);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(PRIMARY_COLOR);

        JMenu fileMenu = new JMenu("File");
        fileMenu.setForeground(Color.WHITE);

        JMenuItem switchRoleItem = new JMenuItem("Switch Role");
        switchRoleItem.addActionListener(e -> switchRole());

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));

        fileMenu.add(switchRoleItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        JMenu helpMenu = new JMenu("Help");
        helpMenu.setForeground(Color.WHITE);

        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAboutDialog());

        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
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

        headerPanel.setPreferredSize(new Dimension(100, 90));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Library Management System");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        String subtitle = "Student".equals(userRole)
                ? "Welcome, Student! Browse books, check your loans, and more."
                : "Staff Portal - Manage books, members, and library operations";

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitleLabel.setForeground(new Color(0xCC, 0xDD, 0xEE));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);

        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);

        JLabel roleLabel = new JLabel("Logged in as: " + userRole);
        roleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        roleLabel.setForeground(Color.WHITE);
        roleLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 1),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)));

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(roleLabel, BorderLayout.EAST);

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
                        "- Students: Search books, borrow, download e-books\n" +
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
