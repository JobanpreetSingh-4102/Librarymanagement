package main.java.library.panels;

import main.java.library.models.Book;
import main.java.library.models.BookRequest;
import main.java.library.models.LibraryInfo;
import main.java.library.models.Member;
import main.java.library.models.Transaction;
import main.java.library.utils.BackgroundPanel;
import main.java.library.utils.DataManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class StudentPanel extends JPanel {
    private DataManager dataManager;
    private JTabbedPane tabbedPane;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final Color PRIMARY_COLOR = new Color(0x2E, 0x5C, 0x8A);
    private static final Color SECONDARY_COLOR = new Color(0x5A, 0x9B, 0xD5);
    private static final Color AVAILABLE_COLOR = new Color(0x4C, 0xAF, 0x50);
    private static final Color WARNING_COLOR = new Color(0xFF, 0x98, 0x00);
    private static final Color DANGER_COLOR = new Color(0xE5, 0x39, 0x35);
    private static final Color BACKGROUND_COLOR = new Color(0xF0, 0xF0, 0xF0);

    public StudentPanel() {
        dataManager = DataManager.getInstance();
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFocusable(false);
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 13));

        tabbedPane.addTab("Search Books", createSearchBooksPanel());
        tabbedPane.addTab("My Books", createMyBooksPanel());
        tabbedPane.addTab("Borrowing History", createBorrowingHistoryPanel());
        tabbedPane.addTab("Request Book", createRequestBookPanel());
        tabbedPane.addTab("Library Info", createLibraryInfoPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createSearchBooksPanel() {
        JPanel panel = new BackgroundPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setOpaque(false);

        JComboBox<String> fieldCombo = new JComboBox<>(new String[]{"All", "Title", "Author", "ISBN", "Section", "Genre"});
        JTextField searchField = new JTextField(25);
        JButton searchBtn = createButton("Search", PRIMARY_COLOR);
        JButton clearBtn = createButton("Clear", SECONDARY_COLOR);

        searchPanel.add(new JLabel("Search by:"));
        searchPanel.add(fieldCombo);
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(clearBtn);

        String[] columns = {"ID", "Title", "Author", "Genre", "Section", "Shelf", "Available", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = createStyledTable(tableModel);

        table.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    String status = (String) value;
                    c.setForeground(status.startsWith("Available") ? AVAILABLE_COLOR : WARNING_COLOR);
                }
                return c;
            }
        });

        Runnable refreshTable = () -> {
            tableModel.setRowCount(0);
            String query = searchField.getText().trim();
            String field = (String) fieldCombo.getSelectedItem();
            List<Book> books;
            if (query.isEmpty()) {
                books = dataManager.getBooks();
            } else {
                books = dataManager.searchBooksByField(query, field);
            }
            for (Book book : books) {
                tableModel.addRow(new Object[]{
                        book.getId(), book.getTitle(), book.getAuthor(),
                        book.getGenre(), book.getSection(), book.getShelf(),
                        book.getAvailableQuantity() + "/" + book.getQuantity(),
                        book.getStatus()
                });
            }
        };

        searchBtn.addActionListener(e -> refreshTable.run());
        clearBtn.addActionListener(e -> { searchField.setText(""); fieldCombo.setSelectedIndex(0); refreshTable.run(); });
        refreshTable.run();

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createMyBooksPanel() {
        JPanel panel = new BackgroundPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        Member currentUser = dataManager.getCurrentUser();
        int borrowed = dataManager.getMemberBorrowedCount(currentUser);
        int remaining = dataManager.getRemainingBorrowLimit(currentUser);
        double totalFines = dataManager.getMemberTotalFines(currentUser);

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        infoPanel.setOpaque(false);
        infoPanel.add(createInfoLabel("Currently Borrowed: " + borrowed, PRIMARY_COLOR));
        infoPanel.add(createInfoLabel("Can Borrow More: " + remaining, AVAILABLE_COLOR));
        infoPanel.add(createInfoLabel("Max Limit: " + dataManager.getLibraryInfo().getMaxBooksPerStudent(), SECONDARY_COLOR));
        Color finesColor = totalFines > 0 ? DANGER_COLOR : AVAILABLE_COLOR;
        infoPanel.add(createInfoLabel(String.format("Outstanding Fines: $%.2f", totalFines), finesColor));

        String[] columns = {"Book Title", "Author", "Borrow Date", "Due Date", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = createStyledTable(tableModel);

        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    String status = (String) value;
                    if (status.startsWith("Overdue")) c.setForeground(WARNING_COLOR);
                    else if ("Active".equals(status)) c.setForeground(SECONDARY_COLOR);
                    else c.setForeground(AVAILABLE_COLOR);
                }
                setHorizontalAlignment(CENTER);
                return c;
            }
        });

        List<Transaction> myTransactions = dataManager.getMemberActiveTransactions(currentUser);
        for (Transaction t : myTransactions) {
            tableModel.addRow(new Object[]{
                    t.getBook().getTitle(), t.getBook().getAuthor(),
                    t.getBorrowDate().format(dateFormatter),
                    t.getDueDate().format(dateFormatter),
                    t.getStatus()
            });
        }

        panel.add(infoPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createBorrowingHistoryPanel() {
        JPanel panel = new BackgroundPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columns = {"Book Title", "Author", "Borrow Date", "Due Date", "Return Date", "Status", "Fine"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = createStyledTable(tableModel);

        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    String status = (String) value;
                    if (status.startsWith("Overdue")) c.setForeground(WARNING_COLOR);
                    else if ("Returned".equals(status)) c.setForeground(AVAILABLE_COLOR);
                    else if ("Active".equals(status)) c.setForeground(SECONDARY_COLOR);
                    else c.setForeground(Color.BLACK);
                }
                setHorizontalAlignment(CENTER);
                return c;
            }
        });

        table.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    String fineStr = (String) value;
                    if (!"-".equals(fineStr)) c.setForeground(DANGER_COLOR);
                    else c.setForeground(Color.BLACK);
                }
                setHorizontalAlignment(CENTER);
                return c;
            }
        });

        Member currentUser = dataManager.getCurrentUser();
        List<Transaction> allTransactions = dataManager.getMemberAllTransactions(currentUser);
        for (Transaction t : allTransactions) {
            double fine = t.calculateFine();
            tableModel.addRow(new Object[]{
                    t.getBook().getTitle(), t.getBook().getAuthor(),
                    t.getBorrowDate().format(dateFormatter),
                    t.getDueDate().format(dateFormatter),
                    t.isReturned() ? t.getReturnDate().format(dateFormatter) : "-",
                    t.getStatus(),
                    fine > 0 ? String.format("$%.2f", fine) : "-"
            });
        }

        JLabel headerLabel = new JLabel("Complete Borrowing History");
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);

        panel.add(headerLabel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createRequestBookPanel() {
        JPanel panel = new BackgroundPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel topSection = new JPanel(new BorderLayout(10, 10));
        topSection.setOpaque(false);

        JLabel headerLabel = new JLabel("Request a New Book for the Library");
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        topSection.add(headerLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JTextField titleField = new JTextField(25);
        JTextField authorField = new JTextField(25);
        JTextArea reasonArea = new JTextArea(3, 25);
        reasonArea.setLineWrap(true);
        reasonArea.setWrapStyleWord(true);

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Book Title:"), gbc);
        gbc.gridx = 1;
        formPanel.add(titleField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Author Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(authorField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(new JLabel("Reason (optional):"), gbc);
        gbc.gridx = 1;
        formPanel.add(new JScrollPane(reasonArea), gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton submitBtn = createButton("Submit Request", AVAILABLE_COLOR);
        formPanel.add(submitBtn, gbc);

        topSection.add(formPanel, BorderLayout.CENTER);

        JPanel bottomSection = new JPanel(new BorderLayout(10, 5));
        bottomSection.setOpaque(false);

        JLabel myRequestsLabel = new JLabel("My Submitted Requests");
        myRequestsLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        myRequestsLabel.setForeground(PRIMARY_COLOR);
        myRequestsLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        bottomSection.add(myRequestsLabel, BorderLayout.NORTH);

        String[] columns = {"ID", "Book Title", "Author", "Reason", "Date", "Status"};
        DefaultTableModel requestsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable requestsTable = createStyledTable(requestsTableModel);

        requestsTable.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    String status = (String) value;
                    if ("Approved".equals(status)) c.setForeground(AVAILABLE_COLOR);
                    else if ("Rejected".equals(status)) c.setForeground(DANGER_COLOR);
                    else c.setForeground(WARNING_COLOR);
                }
                setHorizontalAlignment(CENTER);
                return c;
            }
        });

        Runnable refreshRequests = () -> {
            requestsTableModel.setRowCount(0);
            List<BookRequest> myRequests = dataManager.getMemberBookRequests(dataManager.getCurrentUser());
            for (BookRequest r : myRequests) {
                requestsTableModel.addRow(new Object[]{
                        r.getId(), r.getBookTitle(), r.getAuthorName(),
                        r.getReason().isEmpty() ? "-" : r.getReason(),
                        r.getRequestDate().format(dateFormatter), r.getStatus()
                });
            }
        };

        refreshRequests.run();

        bottomSection.add(new JScrollPane(requestsTable), BorderLayout.CENTER);

        submitBtn.addActionListener(e -> {
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            String reason = reasonArea.getText().trim();

            if (title.isEmpty() || author.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Please enter book title and author",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            BookRequest request = new BookRequest(0, dataManager.getCurrentUser(), title, author, reason);
            dataManager.addBookRequest(request);
            titleField.setText("");
            authorField.setText("");
            reasonArea.setText("");
            refreshRequests.run();
            JOptionPane.showMessageDialog(panel, "Book request submitted successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        });

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topSection, bottomSection);
        splitPane.setResizeWeight(0.45);
        splitPane.setDividerSize(5);
        splitPane.setOpaque(false);
        splitPane.setBorder(null);

        panel.add(splitPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createLibraryInfoPanel() {
        JPanel panel = new BackgroundPanel(new BorderLayout(20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        LibraryInfo info = dataManager.getLibraryInfo();

        JPanel contentPanel = new JPanel(new GridLayout(2, 1, 20, 20));
        contentPanel.setOpaque(false);

        JPanel contactPanel = createInfoCard("Contact Information",
                "Name: " + info.getName() + "\n\n" +
                        "Address: " + info.getAddress() + "\n\n" +
                        "Phone: " + info.getPhone() + "\n\n" +
                        "Email: " + info.getEmail());

        JPanel hoursPanel = createInfoCard("Opening Hours",
                "Monday - Friday: " + info.getMondayToFriday() + "\n\n" +
                        "Saturday: " + info.getSaturday() + "\n\n" +
                        "Sunday: " + info.getSunday() + "\n\n" +
                        "Maximum books per student: " + info.getMaxBooksPerStudent());

        contentPanel.add(contactPanel);
        contentPanel.add(hoursPanel);

        JLabel headerLabel = new JLabel("Library Information");
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);

        panel.add(headerLabel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createInfoCard(String title, String content) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xDD, 0xDD, 0xDD)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        titleLabel.setForeground(PRIMARY_COLOR);

        JTextArea contentArea = new JTextArea(content);
        contentArea.setEditable(false);
        contentArea.setOpaque(false);
        contentArea.setFont(new Font("SansSerif", Font.PLAIN, 13));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(contentArea, BorderLayout.CENTER);
        return card;
    }

    private JLabel createInfoLabel(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        label.setForeground(color);
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        return label;
    }

    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(28);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setPreferredSize(new Dimension(table.getTableHeader().getPreferredSize().width, 32));
        table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = new JLabel(value.toString());
                label.setOpaque(true);
                label.setBackground(PRIMARY_COLOR);
                label.setForeground(Color.WHITE);
                label.setFont(new Font("SansSerif", Font.BOLD, 13));
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, new Color(0x25, 0x4A, 0x70)));
                return label;
            }
        });
        table.setGridColor(new Color(0xDD, 0xDD, 0xDD));
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        return table;
    }
}
