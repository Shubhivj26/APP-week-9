import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class GymApp extends JFrame {

    private static final String URL = "jdbc:mysql://localhost:3306/app?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "Vj#2662006";

    private JTextField idField, nameField, typeField, feesField;
    private JButton addButton, loadButton, updateButton, deleteButton;
    private JTable table;
    private DefaultTableModel model;

    public GymApp() {
        setTitle("Gym Membership Management");
        setSize(800, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10,10));

        JPanel inputPanel = new JPanel(new GridLayout(2, 4, 5, 5));
        idField = new JTextField();
        nameField = new JTextField();
        typeField = new JTextField();
        feesField = new JTextField();
        inputPanel.add(new JLabel("Member ID"));
        inputPanel.add(new JLabel("Name"));
        inputPanel.add(new JLabel("Membership Type"));
        inputPanel.add(new JLabel("Fees"));
        inputPanel.add(idField);
        inputPanel.add(nameField);
        inputPanel.add(typeField);
        inputPanel.add(feesField);

        JPanel buttonPanel = new JPanel();
        addButton = new JButton("Add Member");
        loadButton = new JButton("Load Members");
        updateButton = new JButton("Update Member");
        deleteButton = new JButton("Delete Member");
        buttonPanel.add(addButton);
        buttonPanel.add(loadButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        String[] columns = {"Member ID", "Name", "Type", "Fees"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        add(inputPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);

        addButton.addActionListener(e -> addMember());
        loadButton.addActionListener(e -> loadMembers());
        updateButton.addActionListener(e -> updateMember());
        deleteButton.addActionListener(e -> deleteMember());

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int selectedRow = table.getSelectedRow();
                if(selectedRow >= 0){
                    idField.setText(table.getValueAt(selectedRow, 0).toString());
                    nameField.setText(table.getValueAt(selectedRow, 1).toString());
                    typeField.setText(table.getValueAt(selectedRow, 2).toString());
                    feesField.setText(table.getValueAt(selectedRow, 3).toString());
                }
            }
        });
    }

    private void addMember() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            String query = "INSERT INTO GymMembers (member_id, name, membership_type, fees) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, Integer.parseInt(idField.getText()));
            pstmt.setString(2, nameField.getText());
            pstmt.setString(3, typeField.getText());
            pstmt.setDouble(4, Double.parseDouble(feesField.getText()));
            pstmt.executeUpdate();
            conn.close();
            JOptionPane.showMessageDialog(this, "Member added successfully!");
            clearFields();
            loadMembers();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void loadMembers() {
        model.setRowCount(0);
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM GymMembers");
            while (rs.next()) {
                Object[] row = {
                        rs.getInt("member_id"),
                        rs.getString("name"),
                        rs.getString("membership_type"),
                        rs.getDouble("fees")
                };
                model.addRow(row);
            }
            conn.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading members: " + ex.getMessage());
        }
    }

    private void updateMember() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            String query = "UPDATE GymMembers SET name=?, membership_type=?, fees=? WHERE member_id=?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, nameField.getText());
            pstmt.setString(2, typeField.getText());
            pstmt.setDouble(3, Double.parseDouble(feesField.getText()));
            pstmt.setInt(4, Integer.parseInt(idField.getText()));
            int rows = pstmt.executeUpdate();
            conn.close();
            if(rows > 0) {
                JOptionPane.showMessageDialog(this, "Member updated successfully!");
                clearFields();
                loadMembers();
            } else {
                JOptionPane.showMessageDialog(this, "No member found with this ID!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void deleteMember() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            String query = "DELETE FROM GymMembers WHERE member_id=?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, Integer.parseInt(idField.getText()));
            int rows = pstmt.executeUpdate();
            conn.close();
            if(rows > 0) {
                JOptionPane.showMessageDialog(this, "Member deleted successfully!");
                clearFields();
                loadMembers();
            } else {
                JOptionPane.showMessageDialog(this, "No member found with this ID!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        typeField.setText("");
        feesField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GymApp app = new GymApp();
            app.setVisible(true);
        });
    }
}
