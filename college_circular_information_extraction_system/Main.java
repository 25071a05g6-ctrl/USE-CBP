import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Main.java
 * Smart College Circular Information Extraction System
 * Complete All-In-One Single File Implementation:
 * - Swing GUI (Input, Fields, Table)
 * - Rule-Based Regex Extraction
 * - MySQL JDBC Database Connection & CRUD Operations
 */
public class Main extends JFrame {

    // Database Connection Configuration (Update password if needed)
    private static final String DB_URL = "jdbc:mysql://localhost:3306/college_circular_db?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";

    // GUI Components
    private JTextArea txtCircular;
    private JTextField txtDept, txtEvent, txtDate, txtDeadline, txtVenue, txtTarget;
    private JButton btnExtract, btnSave, btnView, btnClear;
    private JTable tableCirculars;
    private DefaultTableModel tableModel;

    public Main() {
        setTitle("Smart College Circular Information Extraction System");
        setSize(950, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initGUI();
    }

    /**
     * Initializes and arranges all Swing GUI components.
     */
    private void initGUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 1. Top Panel: Circular Input Area
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.setBorder(BorderFactory.createTitledBorder("1. Enter / Paste College Circular Text"));

        txtCircular = new JTextArea(5, 70);
        txtCircular.setLineWrap(true);
        txtCircular.setWrapStyleWord(true);
        txtCircular.setFont(new Font("SansSerif", Font.PLAIN, 13));
        JScrollPane scrollInput = new JScrollPane(txtCircular);

        JPanel btnExtractPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnExtract = new JButton("Extract Information");
        btnExtract.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnExtractPanel.add(btnExtract);

        topPanel.add(scrollInput, BorderLayout.CENTER);
        topPanel.add(btnExtractPanel, BorderLayout.SOUTH);

        // 2. Center Panel: Extracted Fields (Editable) and Action Buttons
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.setBorder(BorderFactory.createTitledBorder("2. Extracted Information (Editable for Correction)"));

        JPanel fieldsPanel = new JPanel(new GridLayout(3, 4, 10, 8));
        txtDept = new JTextField();
        txtEvent = new JTextField();
        txtDate = new JTextField();
        txtDeadline = new JTextField();
        txtVenue = new JTextField();
        txtTarget = new JTextField();

        fieldsPanel.add(new JLabel("Department:"));
        fieldsPanel.add(txtDept);
        fieldsPanel.add(new JLabel("Event Name:"));
        fieldsPanel.add(txtEvent);

        fieldsPanel.add(new JLabel("Event Date:"));
        fieldsPanel.add(txtDate);
        fieldsPanel.add(new JLabel("Registration Deadline:"));
        fieldsPanel.add(txtDeadline);

        fieldsPanel.add(new JLabel("Venue:"));
        fieldsPanel.add(txtVenue);
        fieldsPanel.add(new JLabel("Target Students:"));
        fieldsPanel.add(txtTarget);

        JPanel actionBtnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        btnSave = new JButton("Save Circular");
        btnView = new JButton("View Saved Circulars");
        btnClear = new JButton("Clear All");

        btnSave.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnView.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnClear.setFont(new Font("SansSerif", Font.PLAIN, 12));

        actionBtnPanel.add(btnSave);
        actionBtnPanel.add(btnView);
        actionBtnPanel.add(btnClear);

        centerPanel.add(fieldsPanel, BorderLayout.CENTER);
        centerPanel.add(actionBtnPanel, BorderLayout.SOUTH);

        // 3. Bottom Panel: Saved Records Table
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.setBorder(BorderFactory.createTitledBorder("3. Saved Circular Records (From MySQL)"));

        String[] columns = {"ID", "Department", "Event Name", "Date", "Deadline", "Venue", "Target Students"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Read-only table
            }
        };
        tableCirculars = new JTable(tableModel);
        tableCirculars.setFillsViewportHeight(true);
        tableCirculars.setRowHeight(22);
        JScrollPane scrollTable = new JScrollPane(tableCirculars);
        scrollTable.setPreferredSize(new Dimension(900, 200));

        bottomPanel.add(scrollTable, BorderLayout.CENTER);

        // Assemble Panels
        JPanel upperCombined = new JPanel(new BorderLayout(5, 5));
        upperCombined.add(topPanel, BorderLayout.NORTH);
        upperCombined.add(centerPanel, BorderLayout.SOUTH);

        mainPanel.add(upperCombined, BorderLayout.NORTH);
        mainPanel.add(bottomPanel, BorderLayout.CENTER);

        add(mainPanel);

        // Button Event Handlers
        btnExtract.addActionListener(e -> extractInformation());
        btnSave.addActionListener(e -> saveCircular());
        btnView.addActionListener(e -> loadSavedCirculars());
        btnClear.addActionListener(e -> clearAllFields());
    }

    /**
     * Rule-based extraction logic using Regular Expressions.
     */
    private void extractInformation() {
        String text = txtCircular.getText().trim();
        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter or paste circular text first!",
                    "Input Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 1. Department
        String dept = matchPattern(text, new String[]{
                "(?i)(?:Department|Dept)\\s*[:\\-]\\s*([A-Za-z0-9 &]+)",
                "(?i)(?:Department|Dept\\.?)\\s+of\\s+([A-Za-z0-9 &]+?)(?=\\s+(?:is|will|organizing|announces|conducts|\\.|\\n|,|$))"
        });

        // 2. Event Name
        String event = matchPattern(text, new String[]{
                "(?i)(?:Event\\s*Name|Event)\\s*[:\\-]\\s*([^\\n\\r,.]+)",
                "(?i)(?:organizing|conducting|hosting|announcing|presents)\\s+([A-Za-z0-9\\s_-]+?)(?=\\s+(?:on|at|for|from|in|\\.|\\n|$))"
        });

        // 3. Event Date
        String date = matchPattern(text, new String[]{
                "(?i)(?:Event\\s*Date|Date\\s*of\\s*Event|Date)\\s*[:\\-]\\s*([0-9]{1,2}[-/][0-9]{1,2}[-/][0-9]{2,4}|[^\\n\\r,.]+)",
                "(?i)\\bon\\s+([0-9]{1,2}[-/][0-9]{1,2}[-/][0-9]{2,4})"
        });

        // 4. Registration Deadline
        String deadline = matchPattern(text, new String[]{
                "(?i)(?:Registration\\s*Deadline|Deadline|Last\\s*Date(?:\\s*for\\s*registration)?)\\s*[:\\-]\\s*([0-9]{1,2}[-/][0-9]{1,2}[-/][0-9]{2,4}|[^\\n\\r.]+)"
        });

        // 5. Venue
        String venue = matchPattern(text, new String[]{
                "(?i)(?:Venue|Location|Place)\\s*[:\\-]\\s*([^\\n\\r.]+)",
                "(?i)\\bat\\s+([A-Za-z0-9\\s]+?(?:Hall|Auditorium|Lab|Room|Ground|Campus|Block|Center|Centre))(?=\\.|\\n|,|$)",
                "(?i)\\bat\\s+([A-Za-z0-9\\s]+?)(?=\\.|\\n|,|Registration|Eligible|$)"
        });

        // 6. Target Students
        String target = matchPattern(text, new String[]{
                "(?i)(?:Eligible\\s*students?|Target\\s*students?|Eligibility|Open\\s*to|Participants?)\\s*[:\\-]\\s*([^\\n\\r.]+)",
                "(?i)(?:for|open\\s+to)\\s+([A-Za-z0-9\\s,]+?(?:students?|year))(?=\\.|\\n|$)"
        });

        // Set extracted values to editable text fields
        txtDept.setText(dept);
        txtEvent.setText(event);
        txtDate.setText(date);
        txtDeadline.setText(deadline);
        txtVenue.setText(venue);
        txtTarget.setText(target);

        JOptionPane.showMessageDialog(this,
                "Information extracted successfully!\nPlease review and correct any fields before saving.",
                "Extraction Complete", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Helper to match text against an array of regex patterns.
     */
    private String matchPattern(String text, String[] patterns) {
        for (String patternStr : patterns) {
            Pattern pattern = Pattern.compile(patternStr);
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                return matcher.group(1).trim();
            }
        }
        return "Not found";
    }

    /**
     * Database Connection Method.
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL Driver not found: " + e.getMessage());
        }
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    /**
     * Saves extracted/corrected circular information into MySQL.
     */
    private void saveCircular() {
        String dept = txtDept.getText().trim();
        String event = txtEvent.getText().trim();
        String date = txtDate.getText().trim();
        String deadline = txtDeadline.getText().trim();
        String venue = txtVenue.getText().trim();
        String target = txtTarget.getText().trim();
        String text = txtCircular.getText().trim();

        if (text.isEmpty() && event.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Cannot save empty circular! Please extract or fill in the details.",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = "INSERT INTO circulars (department, event_name, event_date, deadline, venue, target_students, circular_text) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, dept);
            pstmt.setString(2, event);
            pstmt.setString(3, date);
            pstmt.setString(4, deadline);
            pstmt.setString(5, venue);
            pstmt.setString(6, target);
            pstmt.setString(7, text);

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this,
                        "Circular details saved successfully into MySQL database!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadSavedCirculars(); // Refresh table view automatically
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database Error: Could not connect or save record!\n"
                    + "Details: " + ex.getMessage(),
                    "MySQL Connection/Execution Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Retrieves saved circulars from MySQL and populates the JTable.
     */
    private void loadSavedCirculars() {
        String sql = "SELECT circular_id, department, event_name, event_date, deadline, venue, target_students "
                   + "FROM circulars ORDER BY circular_id DESC";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            tableModel.setRowCount(0);

            boolean hasRecords = false;
            while (rs.next()) {
                hasRecords = true;
                Object[] row = {
                        rs.getInt("circular_id"),
                        rs.getString("department"),
                        rs.getString("event_name"),
                        rs.getString("event_date"),
                        rs.getString("deadline"),
                        rs.getString("venue"),
                        rs.getString("target_students")
                };
                tableModel.addRow(row);
            }

            if (!hasRecords) {
                JOptionPane.showMessageDialog(this,
                        "No saved circular records found in the database.",
                        "Information", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database Error: Could not retrieve records from MySQL!\n"
                    + "Details: " + ex.getMessage(),
                    "MySQL Connection/Execution Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Clears all input fields and resets the form.
     */
    private void clearAllFields() {
        txtCircular.setText("");
        txtDept.setText("");
        txtEvent.setText("");
        txtDate.setText("");
        txtDeadline.setText("");
        txtVenue.setText("");
        txtTarget.setText("");
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}
