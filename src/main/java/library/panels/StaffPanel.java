package main.java.library.panels;

import main.java.library.models.Book;
import main.java.library.models.BookRequest;
import main.java.library.models.LibraryInfo;
import main.java.library.models.Member;
import main.java.library.models.Transaction;
import main.java.library.utils.DataManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class StaffPanel extends JPanel {
    private DataManager dataManager;
    private JTabbedPane tabbedPane;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private DefaultTableModel booksTableModel;
    private DefaultTableModel membersTableModel;
    private DefaultTableModel transactionsTableModel;
    private DefaultTableModel requestsTableModel;

    private static final Color PRIMARY_COLOR = new Color(0x2E, 0x5C, 0x8A);
    private static final Color SECONDARY_COLOR = new Color(0x5A, 0x9B, 0xD5);
    private static final Color AVAILABLE_COLOR = new Color(0x4C, 0xAF, 0x50);
    private static final Color WARNING_COLOR = new Color(0xFF, 0x98, 0x00);
    private static final Color DANGER_COLOR = new Color(0xE5, 0x39, 0x35);
    private static final Color BACKGROUND_COLOR = new Color(0xF0, 0xF0, 0xF0);

    public StaffPanel() {
        dataManager = DataManager.getInstance();
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 13));

        tabbedPane.addTab("Books", createBooksPanel());
        tabbedPane.addTab("Members", createMembersPanel());
        tabbedPane.addTab("Transactions", createTransactionsPanel());
        tabbedPane.addTab("Book Requests", createRequestsPanel());
        tabbedPane.addTab("Reports", createReportsPanel());
        tabbedPane.addTab("Settings", createSettingsPanel());

        tabbedPane.addChangeListener(e -> refreshCurrentTab());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private void refreshCurrentTab() {
        int index = tabbedPane.getSelectedIndex();
        switch (index) {
            case 0: refreshBooksTable(); break;
            case 1: refreshMembersTable(); break;
            case 2: refreshTransactionsTable(); break;
            case 3: refreshRequestsTable(); break;
        }
    }

    private JPanel createBooksPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setOpaque(false);
        JButton addBtn = createButton("Add Book", AVAILABLE_COLOR);
        JButton editBtn = createButton("Edit Book", SECONDARY_COLOR);
        JButton deleteBtn = createButton("Delete Book", DANGER_COLOR);

        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);

        String[] columns = {"ID", "Title", "Author", "ISBN", "Section", "Shelf", "Qty", "Available", "E-Book"};
        booksTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = createStyledTable(booksTableModel);

        addBtn.addActionListener(e -> showAddBookDialog());
        editBtn.addActionListener(e -> showEditBookDialog(table));
        deleteBtn.addActionListener(e -> deleteSelectedBook(table));

        refreshBooksTable();

        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void refreshBooksTable() {
        if (booksTableModel == null) return;
        booksTableModel.setRowCount(0);
        for (Book book : dataManager.getBooks()) {
            booksTableModel.addRow(new Object[]{
                    book.getId(), book.getTitle(), book.getAuthor(), book.getIsbn(),
                    book.getSection(), book.getShelf(), book.getQuantity(),
                    book.getAvailableQuantity(), book.hasEbook() ? "Yes" : "No"
            });
        }
    }

    private void showAddBookDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Book", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        JTextField titleField = new JTextField();
        JTextField authorField = new JTextField();
        JTextField isbnField = new JTextField();
        JTextField sectionField = new JTextField("General");
        JTextField shelfField = new JTextField("A1");
        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        JCheckBox ebookCheck = new JCheckBox("Has E-Book");

        formPanel.add(new JLabel("Title:"));
        formPanel.add(titleField);
        formPanel.add(new JLabel("Author:"));
        formPanel.add(authorField);
        formPanel.add(new JLabel("ISBN:"));
        formPanel.add(isbnField);
        formPanel.add(new JLabel("Section:"));
        formPanel.add(sectionField);
        formPanel.add(new JLabel("Shelf:"));
        formPanel.add(shelfField);
        formPanel.add(new JLabel("Quantity:"));
        formPanel.add(quantitySpinner);
        formPanel.add(new JLabel(""));
        formPanel.add(ebookCheck);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton saveBtn = createButton("Save", AVAILABLE_COLOR);
        JButton cancelBtn = createButton("Cancel", new Color(0x99, 0x99, 0x99));

        saveBtn.addActionListener(e -> {
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            if (title.isEmpty() || author.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Title and Author are required", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int qty = (int) quantitySpinner.getValue();
            Book book = new Book(0, title, author, isbnField.getText().trim(), true,
                    sectionField.getText().trim(), shelfField.getText().trim(), qty, qty,
                    ebookCheck.isSelected(), ebookCheck.isSelected() ? title.toLowerCase().replace(" ", "") + ".pdf" : null);
            dataManager.addBook(book);
            refreshBooksTable();
            dialog.dispose();
        });

        cancelBtn.addActionListener(e -> dialog.dispose());
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showEditBookDialog(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a book to edit", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int bookId = (int) booksTableModel.getValueAt(row, 0);
        Book book = dataManager.getBookById(bookId);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Book", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        JTextField titleField = new JTextField(book.getTitle());
        JTextField authorField = new JTextField(book.getAuthor());
        JTextField isbnField = new JTextField(book.getIsbn());
        JTextField sectionField = new JTextField(book.getSection());
        JTextField shelfField = new JTextField(book.getShelf());
        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(book.getQuantity(), 1, 100, 1));
        JCheckBox ebookCheck = new JCheckBox("Has E-Book", book.hasEbook());

        formPanel.add(new JLabel("Title:"));
        formPanel.add(titleField);
        formPanel.add(new JLabel("Author:"));
        formPanel.add(authorField);
        formPanel.add(new JLabel("ISBN:"));
        formPanel.add(isbnField);
        formPanel.add(new JLabel("Section:"));
        formPanel.add(sectionField);
        formPanel.add(new JLabel("Shelf:"));
        formPanel.add(shelfField);
        formPanel.add(new JLabel("Quantity:"));
        formPanel.add(quantitySpinner);
        formPanel.add(new JLabel(""));
        formPanel.add(ebookCheck);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton saveBtn = createButton("Save", AVAILABLE_COLOR);
        JButton cancelBtn = createButton("Cancel", new Color(0x99, 0x99, 0x99));

        saveBtn.addActionListener(e -> {
            book.setTitle(titleField.getText().trim());
            book.setAuthor(authorField.getText().trim());
            book.setIsbn(isbnField.getText().trim());
            book.setSection(sectionField.getText().trim());
            book.setShelf(shelfField.getText().trim());
            book.setQuantity((int) quantitySpinner.getValue());
            book.setHasEbook(ebookCheck.isSelected());
            dataManager.updateBook(book);
            refreshBooksTable();
            dialog.dispose();
        });

        cancelBtn.addActionListener(e -> dialog.dispose());
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void deleteSelectedBook(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a book to delete", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int bookId = (int) booksTableModel.getValueAt(row, 0);
        String title = (String) booksTableModel.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete \"" + title + "\"?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dataManager.deleteBook(bookId);
            refreshBooksTable();
        }
    }

    private JPanel createMembersPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setOpaque(false);
        JButton addBtn = createButton("Add Member", AVAILABLE_COLOR);
        JButton editBtn = createButton("Edit Member", SECONDARY_COLOR);
        JButton deleteBtn = createButton("Delete Member", DANGER_COLOR);

        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);

        String[] columns = {"ID", "Name", "Email", "Phone", "Member Since", "Books Borrowed"};
        membersTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = createStyledTable(membersTableModel);

        addBtn.addActionListener(e -> showAddMemberDialog());
        editBtn.addActionListener(e -> showEditMemberDialog(table));
        deleteBtn.addActionListener(e -> deleteSelectedMember(table));

        refreshMembersTable();

        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void refreshMembersTable() {
        if (membersTableModel == null) return;
        membersTableModel.setRowCount(0);
        for (Member member : dataManager.getMembers()) {
            membersTableModel.addRow(new Object[]{
                    member.getId(), member.getName(), member.getEmail(), member.getPhone(),
                    member.getMemberSince().format(dateFormatter),
                    dataManager.getMemberBorrowedCount(member)
            });
        }
    }

    private void showAddMemberDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Member", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();

        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Phone:"));
        formPanel.add(phoneField);
        formPanel.add(new JLabel("Member Since:"));
        formPanel.add(new JLabel(LocalDate.now().format(dateFormatter)));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton saveBtn = createButton("Save", AVAILABLE_COLOR);
        JButton cancelBtn = createButton("Cancel", new Color(0x99, 0x99, 0x99));

        saveBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Name is required", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Member member = new Member(0, name, emailField.getText().trim(), phoneField.getText().trim(), LocalDate.now());
            dataManager.addMember(member);
            refreshMembersTable();
            dialog.dispose();
        });

        cancelBtn.addActionListener(e -> dialog.dispose());
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showEditMemberDialog(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a member to edit", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int memberId = (int) membersTableModel.getValueAt(row, 0);
        Member member = dataManager.getMemberById(memberId);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Member", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        JTextField nameField = new JTextField(member.getName());
        JTextField emailField = new JTextField(member.getEmail());
        JTextField phoneField = new JTextField(member.getPhone());

        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Phone:"));
        formPanel.add(phoneField);
        formPanel.add(new JLabel("Member Since:"));
        formPanel.add(new JLabel(member.getMemberSince().format(dateFormatter)));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton saveBtn = createButton("Save", AVAILABLE_COLOR);
        JButton cancelBtn = createButton("Cancel", new Color(0x99, 0x99, 0x99));

        saveBtn.addActionListener(e -> {
            member.setName(nameField.getText().trim());
            member.setEmail(emailField.getText().trim());
            member.setPhone(phoneField.getText().trim());
            dataManager.updateMember(member);
            refreshMembersTable();
            dialog.dispose();
        });

        cancelBtn.addActionListener(e -> dialog.dispose());
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void deleteSelectedMember(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a member to delete", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int memberId = (int) membersTableModel.getValueAt(row, 0);
        String name = (String) membersTableModel.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete member \"" + name + "\"?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dataManager.deleteMember(memberId);
            refreshMembersTable();
        }
    }

    private JPanel createTransactionsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setOpaque(false);
        JButton issueBtn = createButton("Issue Book", AVAILABLE_COLOR);
        JButton returnBtn = createButton("Return Book", SECONDARY_COLOR);

        buttonPanel.add(issueBtn);
        buttonPanel.add(returnBtn);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filterPanel.setOpaque(false);
        JCheckBox showActiveOnly = new JCheckBox("Active Only");
        JCheckBox showOverdueOnly = new JCheckBox("Overdue Only");
        showActiveOnly.setOpaque(false);
        showOverdueOnly.setOpaque(false);
        filterPanel.add(showActiveOnly);
        filterPanel.add(showOverdueOnly);

        topPanel.add(buttonPanel, BorderLayout.WEST);
        topPanel.add(filterPanel, BorderLayout.EAST);

        String[] columns = {"ID", "Book", "Member", "Borrow Date", "Due Date", "Return Date", "Status"};
        transactionsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = createStyledTable(transactionsTableModel);

        table.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    String status = (String) value;
                    switch (status) {
                        case "Active": c.setForeground(SECONDARY_COLOR); break;
                        case "Overdue": c.setForeground(WARNING_COLOR); break;
                        case "Returned": c.setForeground(AVAILABLE_COLOR); break;
                    }
                }
                setHorizontalAlignment(CENTER);
                return c;
            }
        });

        Runnable refreshFiltered = () -> {
            transactionsTableModel.setRowCount(0);
            List<Transaction> list;
            if (showOverdueOnly.isSelected()) list = dataManager.getOverdueTransactions();
            else if (showActiveOnly.isSelected()) list = dataManager.getActiveTransactions();
            else list = dataManager.getTransactions();
            for (Transaction t : list) {
                transactionsTableModel.addRow(new Object[]{
                        t.getId(), t.getBook().getTitle(), t.getMember().getName(),
                        t.getBorrowDate().format(dateFormatter), t.getDueDate().format(dateFormatter),
                        t.getReturnDate() != null ? t.getReturnDate().format(dateFormatter) : "-",
                        t.getStatus()
                });
            }
        };

        showActiveOnly.addActionListener(e -> { if (showActiveOnly.isSelected()) showOverdueOnly.setSelected(false); refreshFiltered.run(); });
        showOverdueOnly.addActionListener(e -> { if (showOverdueOnly.isSelected()) showActiveOnly.setSelected(false); refreshFiltered.run(); });

        issueBtn.addActionListener(e -> showIssueBookDialog(refreshFiltered));
        returnBtn.addActionListener(e -> returnSelectedBook(table, refreshFiltered));

        refreshFiltered.run();

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void refreshTransactionsTable() {
        if (transactionsTableModel == null) return;
        transactionsTableModel.setRowCount(0);
        for (Transaction t : dataManager.getTransactions()) {
            transactionsTableModel.addRow(new Object[]{
                    t.getId(), t.getBook().getTitle(), t.getMember().getName(),
                    t.getBorrowDate().format(dateFormatter), t.getDueDate().format(dateFormatter),
                    t.getReturnDate() != null ? t.getReturnDate().format(dateFormatter) : "-",
                    t.getStatus()
            });
        }
    }

    private void showIssueBookDialog(Runnable onComplete) {
        List<Book> availableBooks = dataManager.getAvailableBooks();
        List<Member> members = dataManager.getMembers();

        if (availableBooks.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No books available", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Issue Book", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(450, 250);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        JComboBox<String> bookCombo = new JComboBox<>();
        for (Book book : availableBooks) bookCombo.addItem(book.getId() + " - " + book.getTitle());

        JComboBox<String> memberCombo = new JComboBox<>();
        for (Member member : members) memberCombo.addItem(member.getId() + " - " + member.getName());

        JSpinner daysSpinner = new JSpinner(new SpinnerNumberModel(14, 1, 60, 1));

        formPanel.add(new JLabel("Book:"));
        formPanel.add(bookCombo);
        formPanel.add(new JLabel("Member:"));
        formPanel.add(memberCombo);
        formPanel.add(new JLabel("Loan Days:"));
        formPanel.add(daysSpinner);
        formPanel.add(new JLabel("Due Date:"));
        JLabel dueLabel = new JLabel(LocalDate.now().plusDays(14).format(dateFormatter));
        daysSpinner.addChangeListener(e -> dueLabel.setText(LocalDate.now().plusDays((int)daysSpinner.getValue()).format(dateFormatter)));
        formPanel.add(dueLabel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton saveBtn = createButton("Issue", AVAILABLE_COLOR);
        JButton cancelBtn = createButton("Cancel", new Color(0x99, 0x99, 0x99));

        saveBtn.addActionListener(e -> {
            int bookId = Integer.parseInt(((String)bookCombo.getSelectedItem()).split(" - ")[0]);
            int memberId = Integer.parseInt(((String)memberCombo.getSelectedItem()).split(" - ")[0]);
            Book book = dataManager.getBookById(bookId);
            Member member = dataManager.getMemberById(memberId);
            int days = (int) daysSpinner.getValue();

            Transaction t = new Transaction(0, book, member, LocalDate.now(), LocalDate.now().plusDays(days));
            dataManager.addTransaction(t);
            onComplete.run();
            dialog.dispose();
            JOptionPane.showMessageDialog(this, "Book issued successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        });

        cancelBtn.addActionListener(e -> dialog.dispose());
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void returnSelectedBook(JTable table, Runnable onComplete) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a transaction", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String status = (String) transactionsTableModel.getValueAt(row, 6);
        if ("Returned".equals(status)) {
            JOptionPane.showMessageDialog(this, "Book already returned", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int transactionId = (int) transactionsTableModel.getValueAt(row, 0);
        Transaction t = dataManager.getTransactions().stream().filter(tr -> tr.getId() == transactionId).findFirst().orElse(null);
        if (t != null) {
            dataManager.returnBook(t);
            onComplete.run();
            JOptionPane.showMessageDialog(this, "Book returned successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private JPanel createRequestsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setOpaque(false);
        JButton approveBtn = createButton("Approve", AVAILABLE_COLOR);
        JButton rejectBtn = createButton("Reject", DANGER_COLOR);

        buttonPanel.add(approveBtn);
        buttonPanel.add(rejectBtn);

        String[] columns = {"ID", "Requested By", "Book Title", "Author", "Date", "Status"};
        requestsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = createStyledTable(requestsTableModel);

        approveBtn.addActionListener(e -> handleRequest(table, true));
        rejectBtn.addActionListener(e -> handleRequest(table, false));

        refreshRequestsTable();

        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void refreshRequestsTable() {
        if (requestsTableModel == null) return;
        requestsTableModel.setRowCount(0);
        for (BookRequest r : dataManager.getBookRequests()) {
            requestsTableModel.addRow(new Object[]{
                    r.getId(), r.getRequestedBy().getName(), r.getBookTitle(), r.getAuthorName(),
                    r.getRequestDate().format(dateFormatter), r.getStatus()
            });
        }
    }

    private void handleRequest(JTable table, boolean approve) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a request", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int requestId = (int) requestsTableModel.getValueAt(row, 0);
        BookRequest request = dataManager.getBookRequests().stream().filter(r -> r.getId() == requestId).findFirst().orElse(null);
        if (request != null && request.isPending()) {
            if (approve) request.approve();
            else request.reject();
            refreshRequestsTable();
            JOptionPane.showMessageDialog(this, "Request " + (approve ? "approved" : "rejected") + "!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 20, 20));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(createStatCard("Total Books", String.valueOf(dataManager.getTotalBooks()), PRIMARY_COLOR));
        panel.add(createStatCard("Total Members", String.valueOf(dataManager.getTotalMembers()), SECONDARY_COLOR));
        panel.add(createStatCard("Active Loans", String.valueOf(dataManager.getActiveLoansCount()), AVAILABLE_COLOR));
        panel.add(createStatCard("Overdue Books", String.valueOf(dataManager.getOverdueCount()), WARNING_COLOR));

        return panel;
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 48));
        valueLabel.setForeground(color);

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        titleLabel.setForeground(new Color(0x66, 0x66, 0x66));

        card.add(valueLabel, BorderLayout.CENTER);
        card.add(titleLabel, BorderLayout.SOUTH);
        return card;
    }

    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        LibraryInfo info = dataManager.getLibraryInfo();

        JPanel formPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        formPanel.setOpaque(false);

        JTextField nameField = new JTextField(info.getName());
        JTextField addressField = new JTextField(info.getAddress());
        JTextField phoneField = new JTextField(info.getPhone());
        JTextField emailField = new JTextField(info.getEmail());
        JTextField mfField = new JTextField(info.getMondayToFriday());
        JTextField satField = new JTextField(info.getSaturday());
        JTextField sunField = new JTextField(info.getSunday());
        JSpinner maxBooksSpinner = new JSpinner(new SpinnerNumberModel(info.getMaxBooksPerStudent(), 1, 20, 1));

        formPanel.add(new JLabel("Library Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Address:"));
        formPanel.add(addressField);
        formPanel.add(new JLabel("Phone:"));
        formPanel.add(phoneField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Mon-Fri Hours:"));
        formPanel.add(mfField);
        formPanel.add(new JLabel("Saturday Hours:"));
        formPanel.add(satField);
        formPanel.add(new JLabel("Sunday Hours:"));
        formPanel.add(sunField);
        formPanel.add(new JLabel("Max Books/Student:"));
        formPanel.add(maxBooksSpinner);

        JButton saveBtn = createButton("Save Settings", AVAILABLE_COLOR);
        saveBtn.addActionListener(e -> {
            info.setName(nameField.getText().trim());
            info.setAddress(addressField.getText().trim());
            info.setPhone(phoneField.getText().trim());
            info.setEmail(emailField.getText().trim());
            info.setMondayToFriday(mfField.getText().trim());
            info.setSaturday(satField.getText().trim());
            info.setSunday(sunField.getText().trim());
            info.setMaxBooksPerStudent((int) maxBooksSpinner.getValue());
            JOptionPane.showMessageDialog(panel, "Settings saved!", "Success", JOptionPane.INFORMATION_MESSAGE);
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setOpaque(false);
        btnPanel.add(saveBtn);

        JLabel header = new JLabel("Library Settings");
        header.setFont(new Font("SansSerif", Font.BOLD, 18));

        panel.add(header, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
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
        table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = new JLabel(value.toString());
                label.setOpaque(true);
                label.setBackground(new Color(0x2E, 0x5C, 0x8A));
                label.setForeground(Color.WHITE);
                label.setFont(new Font("SansSerif", Font.BOLD, 13));
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setBorder(BorderFactory.createLineBorder(new Color(0x25, 0x4A, 0x70)));
                return label;
            }
        });
        table.setGridColor(new Color(0xDD, 0xDD, 0xDD));
        return table;
    }
}
