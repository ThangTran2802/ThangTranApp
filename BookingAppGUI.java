import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;

public class BookingAppGUI extends JFrame {
    private Database db;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    // UI Components for Customer
    private JTextField txtPickup, txtDropoff, txtDistance;
    private JTable historyTable;
    private DefaultTableModel tableModel;
    private JLabel lblWelcome;

    // UI Components for Driver
    private JButton btnCompleteRide;
    private Driver currentDriver;
    private User currentUser;

    // === PREMIUM COLOR PALETTE ===
    private Color bgDark = new Color(15, 23, 42);        // Slate 900
    private Color bgCard = new Color(30, 41, 59);         // Slate 800
    private Color bgCardHover = new Color(51, 65, 85);    // Slate 700
    private Color accentBlue = new Color(56, 189, 248);   // Sky 400
    private Color accentGreen = new Color(52, 211, 153);  // Emerald 400
    private Color accentRed = new Color(251, 113, 133);   // Rose 400
    private Color accentYellow = new Color(251, 191, 36); // Amber 400
    private Color textPrimary = new Color(241, 245, 249); // Slate 100
    private Color textSecondary = new Color(148, 163, 184);// Slate 400
    private Color borderColor = new Color(51, 65, 85);    // Slate 700
    private Color inputBg = new Color(15, 23, 42);        // Slate 900

    private Font titleFont = new Font("Segoe UI", Font.BOLD, 32);
    private Font headingFont = new Font("Segoe UI", Font.BOLD, 20);
    private Font regularFont = new Font("Segoe UI", Font.PLAIN, 15);
    private Font smallFont = new Font("Segoe UI", Font.PLAIN, 13);
    private Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);
    private Font monoFont = new Font("Consolas", Font.BOLD, 14);

    public BookingAppGUI() {
        db = new Database();
        setTitle("ThangTranApp - Smart Booking System");
        setSize(900, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 600));
        getContentPane().setBackground(bgDark);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setOpaque(false);

        initLoginScreen();
        initCustomerDashboard();
        initDriverDashboard();

        add(mainPanel);
        setVisible(true);
    }

    // === GRADIENT PANEL ===
    static class GradientPanel extends JPanel {
        private Color startColor, endColor;
        private boolean vertical;

        GradientPanel(Color start, Color end, boolean vertical) {
            this.startColor = start;
            this.endColor = end;
            this.vertical = vertical;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint gp = vertical
                ? new GradientPaint(0, 0, startColor, 0, getHeight(), endColor)
                : new GradientPaint(0, 0, startColor, getWidth(), 0, endColor);
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // === ROUNDED CARD PANEL ===
    static class RoundedPanel extends JPanel {
        private int radius;
        private Color bgColor;

        RoundedPanel(int radius, Color bgColor) {
            this.radius = radius;
            this.bgColor = bgColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // === STYLED BUTTON WITH HOVER ===
    private JButton createStyledButton(String text, Color bgColor, Color hoverColor) {
        JButton btn = new JButton(text) {
            private boolean hovered = false;
            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { hovered = true; repaint(); }
                    public void mouseExited(MouseEvent e) { hovered = false; repaint(); }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hovered ? hoverColor : bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(buttonFont);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(200, 48));
        return btn;
    }

    // === STYLED TEXT FIELD ===
    private JTextField createStyledField(String placeholder) {
        JTextField field = new JTextField() {
            String ph = placeholder;
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    g2.setColor(new Color(100, 116, 139));
                    g2.setFont(regularFont);
                    Insets ins = getInsets();
                    g2.drawString(ph, ins.left + 4, getHeight() / 2 + 5);
                    g2.dispose();
                }
            }
        };
        field.setFont(regularFont);
        field.setBackground(inputBg);
        field.setForeground(textPrimary);
        field.setCaretColor(accentBlue);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, 1, true),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));
        field.setPreferredSize(new Dimension(250, 44));
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(accentBlue, 2, true),
                    BorderFactory.createEmptyBorder(9, 13, 9, 13)
                ));
                field.repaint();
            }
            public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(borderColor, 1, true),
                    BorderFactory.createEmptyBorder(10, 14, 10, 14)
                ));
                field.repaint();
            }
        });
        return field;
    }

    // =============================================
    //          LOGIN SCREEN
    // =============================================
    private void initLoginScreen() {
        GradientPanel loginPanel = new GradientPanel(
            new Color(15, 23, 42), new Color(30, 58, 95), true
        );
        loginPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 20, 8, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Card container
        RoundedPanel card = new RoundedPanel(24, bgCard);
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 10, 6, 10);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridwidth = GridBagConstraints.REMAINDER;

        // Title
        JLabel lblTitle = new JLabel("ThangTranApp", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 40));
        lblTitle.setForeground(accentBlue);
        gc.gridy = 0;
        gc.insets = new Insets(4, 10, 0, 10);
        card.add(lblTitle, gc);

        // Subtitle
        JLabel lblSub = new JLabel("Smart Ride Booking System", SwingConstants.CENTER);
        lblSub.setFont(smallFont);
        lblSub.setForeground(textSecondary);
        gc.gridy = 2;
        gc.insets = new Insets(0, 10, 20, 10);
        card.add(lblSub, gc);

        // Separator
        JSeparator sep = new JSeparator();
        sep.setForeground(borderColor);
        gc.gridy = 3;
        gc.insets = new Insets(0, 10, 20, 10);
        card.add(sep, gc);

        // Customer button
        JButton btnCustomer = createStyledButton("LOGIN AS CUSTOMER", accentBlue, accentBlue.brighter());
        gc.gridy = 4;
        gc.insets = new Insets(6, 10, 6, 10);
        card.add(btnCustomer, gc);

        // Driver button
        JButton btnDriver = createStyledButton("LOGIN AS DRIVER", accentGreen, accentGreen.brighter());
        gc.gridy = 5;
        card.add(btnDriver, gc);

        // Register Driver button
        JButton btnRegister = createStyledButton("REGISTER NEW DRIVER", accentYellow, accentYellow.darker());
        gc.gridy = 6;
        gc.insets = new Insets(6, 10, 6, 10);
        card.add(btnRegister, gc);

        // Author
        JLabel lblAuthor = new JLabel("By Tran Quyet Thang", SwingConstants.CENTER);
        lblAuthor.setFont(smallFont);
        lblAuthor.setForeground(new Color(100, 116, 139));
        gc.gridy = 7;
        gc.insets = new Insets(20, 10, 0, 10);
        card.add(lblAuthor, gc);

        loginPanel.add(card);

        // === ACTION LISTENERS ===
        btnCustomer.addActionListener(e -> {
            String phone = JOptionPane.showInputDialog(this,
                "Enter your Phone Number to continue:", "Customer Login",
                JOptionPane.QUESTION_MESSAGE);
            if (phone == null || phone.trim().isEmpty()) return;
            currentUser = new User("Guest (" + phone + ")", phone, "", "");
            // Polymorphism: getRole() returns "Customer", getInfo() returns customer info
            System.out.println("Login: " + currentUser.getRole() + " - " + currentUser.getInfo());
            lblWelcome.setText(currentUser.getRole() + " — Book a Ride");
            cardLayout.show(mainPanel, "Customer");
            refreshHistory();
        });

        btnDriver.addActionListener(e -> {
            String user = JOptionPane.showInputDialog(this,
                "Enter Username:", "Driver Login", JOptionPane.QUESTION_MESSAGE);
            if (user == null) return;
            JPasswordField passField = new JPasswordField();
            int passResult = JOptionPane.showConfirmDialog(this,
                new Object[]{"Enter Password:", passField}, "Driver Login",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (passResult != JOptionPane.OK_OPTION) return;
            String pass = new String(passField.getPassword());
            currentDriver = db.findDriver(user, pass);
            if (currentDriver != null) {
                // Polymorphism: getRole() returns "Driver", getInfo() returns driver info
                System.out.println("Login: " + currentDriver.getRole() + " - " + currentDriver.getInfo());
                refreshDriverDashboard();
                cardLayout.show(mainPanel, "Driver");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnRegister.addActionListener(e -> {
            showDriverRegistrationDialog();
        });

        mainPanel.add(loginPanel, "Login");
    }

    // =============================================
    //          CUSTOMER DASHBOARD
    // =============================================
    private void initCustomerDashboard() {
        GradientPanel panel = new GradientPanel(bgDark, new Color(15, 23, 42), true);
        panel.setLayout(new BorderLayout(0, 16));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // === TOP HEADER ===
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        lblWelcome = new JLabel("Customer — Book a Ride");
        lblWelcome.setFont(headingFont);
        lblWelcome.setForeground(textPrimary);
        headerPanel.add(lblWelcome, BorderLayout.WEST);

        JButton btnLogout = createStyledButton("Logout", accentRed, accentRed.darker());
        btnLogout.setPreferredSize(new Dimension(100, 38));
        headerPanel.add(btnLogout, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.NORTH);

        // === CENTER: FORM + TABLE ===
        JPanel centerPanel = new JPanel(new BorderLayout(0, 16));
        centerPanel.setOpaque(false);

        // Form Card
        RoundedPanel formCard = new RoundedPanel(16, bgCard);
        formCard.setLayout(new GridBagLayout());
        formCard.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 8, 6, 8);
        gc.fill = GridBagConstraints.HORIZONTAL;

        // Row 1: Pickup
        JLabel lp = new JLabel("Pickup");
        lp.setFont(smallFont);
        lp.setForeground(textSecondary);
        gc.gridx = 0; gc.gridy = 0; gc.weightx = 0;
        formCard.add(lp, gc);
        txtPickup = createStyledField("e.g. 123 Le Loi, Ha Noi");
        gc.gridx = 1; gc.weightx = 1;
        formCard.add(txtPickup, gc);

        // Row 2: Dropoff
        JLabel ld = new JLabel("Dropoff");
        ld.setFont(smallFont);
        ld.setForeground(textSecondary);
        gc.gridx = 0; gc.gridy = 1; gc.weightx = 0;
        formCard.add(ld, gc);
        txtDropoff = createStyledField("e.g. 45 Tran Hung Dao, HN");
        gc.gridx = 1; gc.weightx = 1;
        formCard.add(txtDropoff, gc);

        // Row 3: Distance + Book button
        JLabel ldi = new JLabel("Distance");
        ldi.setFont(smallFont);
        ldi.setForeground(textSecondary);
        gc.gridx = 0; gc.gridy = 2; gc.weightx = 0;
        formCard.add(ldi, gc);

        JPanel distRow = new JPanel(new BorderLayout(12, 0));
        distRow.setOpaque(false);
        txtDistance = createStyledField("km");
        txtDistance.setPreferredSize(new Dimension(120, 44));
        distRow.add(txtDistance, BorderLayout.CENTER);

        JButton btnBook = createStyledButton("BOOK NOW", accentBlue, accentBlue.brighter());
        btnBook.setPreferredSize(new Dimension(160, 44));
        distRow.add(btnBook, BorderLayout.EAST);

        gc.gridx = 1; gc.weightx = 1;
        formCard.add(distRow, gc);

        centerPanel.add(formCard, BorderLayout.NORTH);

        // === TABLE ===
        String[] columns = {"#", "Phone", "Driver", "Plate", "From", "To", "Fare (VND)", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        historyTable = new JTable(tableModel);
        historyTable.setFont(regularFont);
        historyTable.setRowHeight(40);
        historyTable.setBackground(bgCard);
        historyTable.setForeground(textPrimary);
        historyTable.setSelectionBackground(bgCardHover);
        historyTable.setSelectionForeground(textPrimary);
        historyTable.setGridColor(borderColor);
        historyTable.setShowHorizontalLines(true);
        historyTable.setShowVerticalLines(false);
        historyTable.setIntercellSpacing(new Dimension(0, 1));

        // Table Header
        JTableHeader header = historyTable.getTableHeader();
        header.setFont(buttonFont);
        header.setBackground(new Color(30, 41, 59));
        header.setForeground(accentBlue);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, accentBlue));
        header.setPreferredSize(new Dimension(0, 44));

        // Status column renderer
        historyTable.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                boolean selected, boolean focused, int row, int col) {
                JLabel lbl = new JLabel(value != null ? value.toString() : "", SwingConstants.CENTER);
                lbl.setOpaque(true);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
                String status = value != null ? value.toString() : "";
                if ("Completed".equals(status)) {
                    lbl.setForeground(accentGreen);
                    lbl.setBackground(new Color(6, 78, 59));
                } else if ("In Progress".equals(status)) {
                    lbl.setForeground(accentYellow);
                    lbl.setBackground(new Color(120, 53, 15));
                } else {
                    lbl.setForeground(accentRed);
                    lbl.setBackground(new Color(127, 29, 29));
                }
                lbl.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                return lbl;
            }
        });

        // Fare column renderer (right-aligned, formatted)
        historyTable.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                boolean selected, boolean focused, int row, int col) {
                super.getTableCellRendererComponent(table, value, selected, focused, row, col);
                setHorizontalAlignment(SwingConstants.RIGHT);
                setFont(monoFont);
                setBackground(selected ? bgCardHover : bgCard);
                setForeground(accentGreen);
                if (value instanceof Number) {
                    setText(String.format("%,.0f", ((Number) value).doubleValue()));
                }
                return this;
            }
        });

        // Default cell renderer for other columns
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                boolean selected, boolean focused, int row, int col) {
                super.getTableCellRendererComponent(table, value, selected, focused, row, col);
                setBackground(selected ? bgCardHover : bgCard);
                setForeground(textPrimary);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return this;
            }
        };
        for (int i = 0; i < 6; i++) {
            historyTable.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        // Plate column renderer (monospace, yellow accent)
        historyTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                boolean selected, boolean focused, int row, int col) {
                super.getTableCellRendererComponent(table, value, selected, focused, row, col);
                setFont(monoFont);
                setBackground(selected ? bgCardHover : bgCard);
                setForeground(accentYellow);
                setHorizontalAlignment(SwingConstants.CENTER);
                setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
                return this;
            }
        });

        // Column widths
        historyTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        historyTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        historyTable.getColumnModel().getColumn(2).setPreferredWidth(90);
        historyTable.getColumnModel().getColumn(3).setPreferredWidth(85);
        historyTable.getColumnModel().getColumn(6).setPreferredWidth(100);
        historyTable.getColumnModel().getColumn(7).setPreferredWidth(90);

        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(borderColor, 1, true));
        scrollPane.getViewport().setBackground(bgCard);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        panel.add(centerPanel, BorderLayout.CENTER);

        // === BOTTOM BUTTONS ===
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        bottomPanel.setOpaque(false);

        JButton btnRefresh = createStyledButton("\u21BB  Refresh", accentGreen, accentGreen.darker());
        btnRefresh.setPreferredSize(new Dimension(130, 40));
        bottomPanel.add(btnRefresh);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        // === ACTIONS ===
        btnBook.addActionListener(e -> {
            try {
                String pick = txtPickup.getText();
                String drop = txtDropoff.getText();
                float dist = Float.parseFloat(txtDistance.getText());
                Driver driver = db.findAvailableDriver();
                if (driver != null) {
                    double fare = 10000 + (dist * 8000);
                    db.addRideWithPhone(currentUser.getName(), currentUser.getPhone(), driver.getName(), pick, drop, dist,
                            (float) fare, "In Progress");
                    db.updateDriverAvailability(driver.getUsername(), false);
                    JOptionPane.showMessageDialog(this,
                        "Ride Booked!\nRole: " + currentUser.getRole() +
                        "\nDriver: " + driver.getName() + " (" + driver.getRole() + ")" +
                        "\nPlate: " + driver.getVehicle() +
                        "\nFare: " + String.format("%,.0f", fare) + " VND",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshHistory();
                    txtPickup.setText(""); txtDropoff.setText(""); txtDistance.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "No available drivers at the moment!", "Sorry", JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid distance!", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnRefresh.addActionListener(e -> refreshHistory());
        btnLogout.addActionListener(e -> cardLayout.show(mainPanel, "Login"));

        mainPanel.add(panel, "Customer");
    }

    // =============================================
    //          DRIVER DASHBOARD
    // =============================================

    // Driver dashboard UI references (for dynamic updates)
    private JLabel lblDriverName, lblDriverPlate, lblStatusBadge;
    private JLabel lblStatTrips, lblStatEarnings, lblStatPlate;
    private JPanel rideDetailsContainer;
    private JLabel lblRideCustomer, lblRidePhone, lblRideFrom, lblRideTo, lblRideFare, lblRideDist;
    private JPanel noRidePanel, activeRidePanel;

    private void initDriverDashboard() {
        GradientPanel panel = new GradientPanel(bgDark, new Color(15, 32, 58), true);
        panel.setLayout(new BorderLayout(0, 0));

        // =============== TOP HEADER BAR ===============
        RoundedPanel headerBar = new RoundedPanel(0, new Color(22, 33, 55));
        headerBar.setLayout(new BorderLayout());
        headerBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(56, 189, 248, 40)),
            BorderFactory.createEmptyBorder(16, 28, 16, 28)
        ));

        // Left: Avatar circle + name + plate
        JPanel headerLeft = new JPanel();
        headerLeft.setOpaque(false);
        headerLeft.setLayout(new BoxLayout(headerLeft, BoxLayout.X_AXIS));

        // Avatar circle
        JPanel avatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, accentGreen, getWidth(), getHeight(), accentBlue);
                g2.setPaint(gp);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 22));
                FontMetrics fm = g2.getFontMetrics();
                String init = "D";
                g2.drawString(init, (getWidth() - fm.stringWidth(init)) / 2, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        avatar.setOpaque(false);
        avatar.setPreferredSize(new Dimension(50, 50));
        avatar.setMaximumSize(new Dimension(50, 50));
        avatar.setMinimumSize(new Dimension(50, 50));
        headerLeft.add(avatar);
        headerLeft.add(Box.createHorizontalStrut(16));

        // Name + plate column
        JPanel nameCol = new JPanel();
        nameCol.setOpaque(false);
        nameCol.setLayout(new BoxLayout(nameCol, BoxLayout.Y_AXIS));

        lblDriverName = new JLabel("Driver");
        lblDriverName.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblDriverName.setForeground(textPrimary);
        lblDriverName.setAlignmentX(Component.LEFT_ALIGNMENT);
        nameCol.add(lblDriverName);

        lblDriverPlate = new JLabel("--");
        lblDriverPlate.setFont(new Font("Consolas", Font.PLAIN, 13));
        lblDriverPlate.setForeground(textSecondary);
        lblDriverPlate.setAlignmentX(Component.LEFT_ALIGNMENT);
        nameCol.add(lblDriverPlate);

        headerLeft.add(nameCol);
        headerBar.add(headerLeft, BorderLayout.WEST);

        // Right: Status badge + Logout
        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 6));
        headerRight.setOpaque(false);

        lblStatusBadge = new JLabel("  ONLINE  ") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(6, 78, 59));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lblStatusBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblStatusBadge.setForeground(accentGreen);
        lblStatusBadge.setOpaque(false);
        lblStatusBadge.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        headerRight.add(lblStatusBadge);

        JButton btnLogout = createStyledButton("Logout", accentRed, accentRed.darker());
        btnLogout.setPreferredSize(new Dimension(100, 38));
        headerRight.add(btnLogout);
        headerBar.add(headerRight, BorderLayout.EAST);

        panel.add(headerBar, BorderLayout.NORTH);

        // =============== CENTER CONTENT ===============
        JPanel centerWrap = new JPanel(new BorderLayout(0, 20));
        centerWrap.setOpaque(false);
        centerWrap.setBorder(BorderFactory.createEmptyBorder(24, 28, 20, 28));

        // --- STATS CARDS ROW ---
        JPanel statsRow = new JPanel(new GridLayout(1, 3, 16, 0));
        statsRow.setOpaque(false);
        statsRow.setPreferredSize(new Dimension(0, 110));

        // Stat Card 1: Completed Trips
        statsRow.add(createStatCard("Completed Trips", "0", accentBlue, new Color(14, 47, 84), "trips"));
        // Stat Card 2: Total Earnings
        statsRow.add(createStatCard("Total Earnings", "0 VND", accentGreen, new Color(6, 58, 45), "earnings"));
        // Stat Card 3: Vehicle
        statsRow.add(createStatCard("Vehicle Plate", "--", accentYellow, new Color(80, 53, 15), "plate"));

        centerWrap.add(statsRow, BorderLayout.NORTH);

        // --- CURRENT RIDE DETAILS CARD ---
        rideDetailsContainer = new JPanel(new CardLayout());
        rideDetailsContainer.setOpaque(false);

        // No ride panel
        noRidePanel = new JPanel(new GridBagLayout());
        noRidePanel.setOpaque(false);
        RoundedPanel noRideCard = new RoundedPanel(20, bgCard);
        noRideCard.setLayout(new BoxLayout(noRideCard, BoxLayout.Y_AXIS));
        noRideCard.setBorder(BorderFactory.createEmptyBorder(50, 40, 50, 40));

        JLabel lblNoRideIcon = new JLabel("\u2615");
        lblNoRideIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        lblNoRideIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        noRideCard.add(lblNoRideIcon);
        noRideCard.add(Box.createVerticalStrut(12));

        JLabel lblNoRideText = new JLabel("No Active Ride");
        lblNoRideText.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblNoRideText.setForeground(textPrimary);
        lblNoRideText.setAlignmentX(Component.CENTER_ALIGNMENT);
        noRideCard.add(lblNoRideText);

        JLabel lblNoRideSub = new JLabel("You're available. Waiting for the next booking...");
        lblNoRideSub.setFont(smallFont);
        lblNoRideSub.setForeground(textSecondary);
        lblNoRideSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        noRideCard.add(Box.createVerticalStrut(6));
        noRideCard.add(lblNoRideSub);

        noRidePanel.add(noRideCard);
        rideDetailsContainer.add(noRidePanel, "no_ride");

        // Active ride panel
        activeRidePanel = new JPanel(new BorderLayout());
        activeRidePanel.setOpaque(false);

        RoundedPanel rideCard = new RoundedPanel(20, bgCard);
        rideCard.setLayout(new BorderLayout(0, 0));
        rideCard.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // Ride card header
        JPanel rideHeader = new JPanel(new BorderLayout());
        rideHeader.setOpaque(false);
        rideHeader.setBorder(BorderFactory.createEmptyBorder(20, 28, 12, 28));

        JLabel lblCurrentRide = new JLabel("Current Ride");
        lblCurrentRide.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblCurrentRide.setForeground(textPrimary);
        rideHeader.add(lblCurrentRide, BorderLayout.WEST);

        JLabel lblLive = new JLabel("  LIVE  ") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(127, 29, 29));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lblLive.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblLive.setForeground(accentRed);
        lblLive.setOpaque(false);
        lblLive.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        rideHeader.add(lblLive, BorderLayout.EAST);

        rideCard.add(rideHeader, BorderLayout.NORTH);

        // Ride detail rows
        JPanel rideBody = new JPanel(new GridLayout(3, 2, 20, 14));
        rideBody.setOpaque(false);
        rideBody.setBorder(BorderFactory.createEmptyBorder(8, 28, 24, 28));

        lblRideCustomer = new JLabel("--");
        rideBody.add(createDetailRow("Customer", lblRideCustomer));
        lblRidePhone = new JLabel("--");
        rideBody.add(createDetailRow("Phone", lblRidePhone));
        lblRideFrom = new JLabel("--");
        rideBody.add(createDetailRow("Pickup", lblRideFrom));
        lblRideTo = new JLabel("--");
        rideBody.add(createDetailRow("Dropoff", lblRideTo));
        lblRideDist = new JLabel("--");
        rideBody.add(createDetailRow("Distance", lblRideDist));
        lblRideFare = new JLabel("--");
        rideBody.add(createDetailRow("Fare", lblRideFare));

        rideCard.add(rideBody, BorderLayout.CENTER);
        activeRidePanel.add(rideCard, BorderLayout.CENTER);
        rideDetailsContainer.add(activeRidePanel, "active_ride");

        centerWrap.add(rideDetailsContainer, BorderLayout.CENTER);
        panel.add(centerWrap, BorderLayout.CENTER);

        // =============== BOTTOM ACTIONS BAR ===============
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        bottomBar.setOpaque(false);
        bottomBar.setBorder(BorderFactory.createEmptyBorder(0, 28, 24, 28));

        btnCompleteRide = createStyledButton("COMPLETE RIDE", accentBlue, accentBlue.brighter());
        btnCompleteRide.setPreferredSize(new Dimension(220, 48));
        btnCompleteRide.setFont(new Font("Segoe UI", Font.BOLD, 15));
        bottomBar.add(btnCompleteRide);

        JButton btnRefreshDriver = createStyledButton("Refresh", accentGreen, accentGreen.darker());
        btnRefreshDriver.setPreferredSize(new Dimension(130, 48));
        bottomBar.add(btnRefreshDriver);

        panel.add(bottomBar, BorderLayout.SOUTH);

        // === ACTIONS ===
        btnCompleteRide.addActionListener(e -> {
            if (currentDriver != null) {
                Ride current = db.getCurrentRideForDriver(currentDriver.getName());
                if (current == null) {
                    JOptionPane.showMessageDialog(this, "No active ride to complete!", "Info",
                        JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                db.completeCurrentRide(currentDriver.getName());
                db.updateDriverAvailability(currentDriver.getUsername(), true);
                JOptionPane.showMessageDialog(this,
                    "Ride Completed!\nFare: " + String.format("%,.0f", current.getFare()) + " VND\nYou are now Available.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshDriverDashboard();
            }
        });

        btnRefreshDriver.addActionListener(e -> refreshDriverDashboard());
        btnLogout.addActionListener(e -> cardLayout.show(mainPanel, "Login"));

        mainPanel.add(panel, "Driver");
    }

    // Helper: Create a stat card for the driver dashboard
    private JPanel createStatCard(String label, String value, Color accent, Color bgTint, String type) {
        RoundedPanel card = new RoundedPanel(16, bgCard);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 3, 0, 0, accent),
            BorderFactory.createEmptyBorder(18, 20, 18, 20)
        ));

        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblLabel.setForeground(textSecondary);
        lblLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(lblLabel);
        card.add(Box.createVerticalStrut(8));

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblValue.setForeground(accent);
        lblValue.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(lblValue);

        // Save reference for dynamic updates
        if ("trips".equals(type)) lblStatTrips = lblValue;
        else if ("earnings".equals(type)) lblStatEarnings = lblValue;
        else if ("plate".equals(type)) lblStatPlate = lblValue;

        return card;
    }

    // Helper: Create a detail row (label + value) for ride info
    private JPanel createDetailRow(String label, JLabel valueLabel) {
        JPanel row = new JPanel(new BorderLayout(0, 4));
        row.setOpaque(false);

        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblLabel.setForeground(textSecondary);
        row.add(lblLabel, BorderLayout.NORTH);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        valueLabel.setForeground(textPrimary);
        row.add(valueLabel, BorderLayout.CENTER);

        return row;
    }

    // Refresh all driver dashboard data
    private void refreshDriverDashboard() {
        if (currentDriver == null) return;

        // Update header
        lblDriverName.setText(currentDriver.getName() + " (" + currentDriver.getRole() + ")");
        lblDriverPlate.setText(currentDriver.getVehicle());

        // Update stats
        int trips = db.getCompletedRideCount(currentDriver.getName());
        double earnings = db.getTotalEarnings(currentDriver.getName());
        lblStatTrips.setText(String.valueOf(trips));
        lblStatEarnings.setText(String.format("%,.0f VND", earnings));
        lblStatPlate.setText(currentDriver.getVehicle());

        // Update current ride
        Ride currentRide = db.getCurrentRideForDriver(currentDriver.getName());
        CardLayout cl = (CardLayout) rideDetailsContainer.getLayout();
        if (currentRide != null) {
            lblRideCustomer.setText(currentRide.getUserId());
            lblRidePhone.setText(currentRide.getPhone());
            lblRideFrom.setText(currentRide.getPickupLocation());
            lblRideTo.setText(currentRide.getDropoffLocation());
            lblRideDist.setText(String.format("%.1f km", currentRide.getDistance()));
            lblRideFare.setText(String.format("%,.0f VND", currentRide.getFare()));
            cl.show(rideDetailsContainer, "active_ride");
            lblStatusBadge.setText("  ON TRIP  ");
            lblStatusBadge.setForeground(accentYellow);
        } else {
            cl.show(rideDetailsContainer, "no_ride");
            lblStatusBadge.setText("  ONLINE  ");
            lblStatusBadge.setForeground(accentGreen);
        }
    }

    private void refreshHistory() {
        tableModel.setRowCount(0);
        ArrayList<Ride> rides = (currentUser != null)
            ? db.getRidesByPhone(currentUser.getPhone())
            : db.getRides();
        int idx = 1;
        for (Ride r : rides) {
            tableModel.addRow(new Object[]{
                idx++, r.getPhone(), r.getDriverId(), r.getLicensePlate(),
                r.getPickupLocation(), r.getDropoffLocation(), r.getFare(), r.getStatus()
            });
        }
    }

    // === DRIVER REGISTRATION DIALOG ===
    private void showDriverRegistrationDialog() {
        JDialog dialog = new JDialog(this, "Register New Driver", true);
        dialog.setSize(420, 440);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(bgDark);

        JPanel formPanel = new RoundedPanel(20, bgCard);
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5, 8, 5, 8);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridwidth = GridBagConstraints.REMAINDER;

        // Title
        JLabel lblTitle = new JLabel("New Driver Registration", SwingConstants.CENTER);
        lblTitle.setFont(headingFont);
        lblTitle.setForeground(accentYellow);
        gc.gridy = 0;
        gc.insets = new Insets(0, 8, 16, 8);
        formPanel.add(lblTitle, gc);

        gc.insets = new Insets(4, 8, 4, 8);

        // Fields
        JTextField txtName = createStyledField("Full Name");
        gc.gridy = 1; formPanel.add(txtName, gc);

        JTextField txtPhone = createStyledField("Phone Number");
        gc.gridy = 2; formPanel.add(txtPhone, gc);

        JTextField txtUsername = createStyledField("Username");
        gc.gridy = 3; formPanel.add(txtUsername, gc);

        JPasswordField txtPassword = new JPasswordField();
        txtPassword.setFont(regularFont);
        txtPassword.setBackground(inputBg);
        txtPassword.setForeground(textPrimary);
        txtPassword.setCaretColor(accentBlue);
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, 1, true),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));
        txtPassword.setPreferredSize(new Dimension(250, 44));
        txtPassword.setEchoChar('\u25CF');
        gc.gridy = 4; formPanel.add(txtPassword, gc);

        JTextField txtPlate = createStyledField("License Plate (e.g. 29A-12345)");
        gc.gridy = 5; formPanel.add(txtPlate, gc);

        // Register button
        JButton btnSubmit = createStyledButton("REGISTER", accentGreen, accentGreen.brighter());
        btnSubmit.setPreferredSize(new Dimension(200, 48));
        gc.gridy = 6;
        gc.insets = new Insets(16, 8, 4, 8);
        formPanel.add(btnSubmit, gc);

        // Cancel button
        JButton btnCancel = createStyledButton("CANCEL", accentRed, accentRed.darker());
        btnCancel.setPreferredSize(new Dimension(200, 42));
        gc.gridy = 7;
        gc.insets = new Insets(4, 8, 4, 8);
        formPanel.add(btnCancel, gc);

        btnSubmit.addActionListener(ev -> {
            String name = txtName.getText().trim();
            String phone = txtPhone.getText().trim();
            String user = txtUsername.getText().trim();
            String pass = new String(txtPassword.getPassword()).trim();
            String plate = txtPlate.getText().trim();

            if (name.isEmpty() || phone.isEmpty() || user.isEmpty() || pass.isEmpty() || plate.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Driver newDriver = new Driver(name, phone, user, pass, plate);
            db.addDriver(newDriver);
            JOptionPane.showMessageDialog(dialog,
                "Driver registered successfully!\n\nName: " + name +
                "\nUsername: " + user + "\nPlate: " + plate,
                "Success", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
        });

        btnCancel.addActionListener(ev -> dialog.dispose());

        dialog.setContentPane(formPanel);
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        // Use system anti-aliasing
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(() -> new BookingAppGUI());
    }
}
