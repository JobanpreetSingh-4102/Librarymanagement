package main.java.library.dialogs;


import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class RoleSelectionDialog extends JDialog {
    private String selectedRole = null;

    private static final Color PRIMARY_COLOR = new Color(0x2E, 0x5C, 0x8A);
    private static final Color SECONDARY_COLOR = new Color(0x5A, 0x9B, 0xD5);
    private static final Color BACKGROUND_COLOR = new Color(0xF0, 0xF0, 0xF0);

    public RoleSelectionDialog() {
        setTitle("Library Management System");
        setModal(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(450, 350);
        setLocationRelativeTo(null);
        setResizable(false);

        URL iconURL = getClass().getResource("/main/java/library/resources/logo.png");
        if (iconURL != null) {
            setIconImage(new ImageIcon(iconURL).getImage());
        }

        getContentPane().setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout());

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createButtonPanel(), BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));

        JLabel titleLabel = new JLabel("Welcome to Library", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Please select your role to continue", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(0xCC, 0xDD, 0xEE));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 5));
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);

        headerPanel.add(titlePanel, BorderLayout.CENTER);
        return headerPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 30, 0));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JButton studentBtn = createRoleButton ("Student","", PRIMARY_COLOR);
        JButton staffBtn = createRoleButton("Staff", "", PRIMARY_COLOR);

        studentBtn.addActionListener(e -> {
            selectedRole = "Student";
            dispose();
        });

        staffBtn.addActionListener(e -> {
            selectedRole = "Staff";
            dispose();
        });

        buttonPanel.add(studentBtn);
        buttonPanel.add(staffBtn);

        return buttonPanel;
    }

    private JButton createRoleButton(String title, String description, Color bgColor) {
        JButton button = new JButton("<html><center><b style='font-size:16px'>" + title + "</b><br><br><span style='font-size:10px'>" + description.replace("\n", "<br>") + "</span></center></html>");
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(150, 120));
        return button;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setBackground(new Color(0xDD, 0xDD, 0xDD));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel footerLabel = new JLabel("Library Management System v2.0");
        footerLabel.setForeground(new Color(0x66, 0x66, 0x66));
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

