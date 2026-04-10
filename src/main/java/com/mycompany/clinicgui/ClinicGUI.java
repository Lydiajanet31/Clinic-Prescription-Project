package com.mycompany.clinicgui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.*;
import java.util.*;

class Patient implements Serializable {
    int id;
    String name, disease, prescription, branch, timeSlot, doctor;
    int age;

    Patient(int id, String name, int age, String disease,
            String prescription, String branch, String timeSlot, String doctor) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.disease = disease;
        this.prescription = prescription;
        this.branch = branch;
        this.timeSlot = timeSlot;
        this.doctor = doctor;
    }

    Object[] toRow() {
        return new Object[]{id, name, age, disease, prescription, branch, timeSlot, doctor};
    }
}

public class ClinicGUI extends JFrame {

    JTextField idField, nameField, ageField, diseaseField, medicineField;
    JComboBox<String> branchBox, timeSlotBox, doctorBox;

    JTable patientTable;
    DefaultTableModel model;

    ArrayList<Patient> patients = new ArrayList<>();
    Set<String> bookedSlots = new HashSet<>();

    JButton dashboardBtn;
    final String FILE_NAME = "patients.dat";

    Color entryBlue = new Color(135, 206, 235);
    Color lightBlue = new Color(173, 216, 230);
    Color lightPurple = new Color(230, 220, 250);
    Color darkPurple = new Color(150, 120, 200);

    public ClinicGUI() {

        setTitle("Smart Clinic System");
        setSize(1200, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Patient Entry", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));

        JPanel top = new JPanel();
        top.setBackground(entryBlue);

        idField = new JTextField(6);
        nameField = new JTextField(8);
        ageField = new JTextField(5);
        diseaseField = new JTextField(8);
        medicineField = new JTextField(15);

        branchBox = new JComboBox<>(new String[]{"Chennai", "Coimbatore"});
        timeSlotBox = new JComboBox<>(new String[]{"9:00 AM","10:00 AM","11:00 AM","12:00 PM"});
        doctorBox = new JComboBox<>(new String[]{"Dr. Kumar", "Dr. Ravi"});

        Font labelFont = new Font("Arial", Font.BOLD, 14);

        top.add(new JLabel("ID")).setFont(labelFont);
        top.add(idField);
        top.add(new JLabel("Name")).setFont(labelFont);
        top.add(nameField);
        top.add(new JLabel("Age")).setFont(labelFont);
        top.add(ageField);
        top.add(new JLabel("Disease")).setFont(labelFont);
        top.add(diseaseField);
        top.add(new JLabel("Medicine")).setFont(labelFont);
        top.add(medicineField);
        top.add(new JLabel("Branch")).setFont(labelFont);
        top.add(branchBox);
        top.add(new JLabel("Time")).setFont(labelFont);
        top.add(timeSlotBox);
        top.add(new JLabel("Doctor")).setFont(labelFont);
        top.add(doctorBox);

        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setBackground(entryBlue);

        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        topContainer.add(title);
        topContainer.add(top);

        add(topContainer, BorderLayout.NORTH);

        model = new DefaultTableModel(
                new String[]{"ID","Name","Age","Disease","Prescription","Branch","Time","Doctor"}, 0);

        patientTable = new JTable(model);
        patientTable.setRowHeight(28);
        patientTable.getColumnModel().getColumn(4).setPreferredWidth(400);

        patientTable.setBackground(lightPurple);
        patientTable.setGridColor(new Color(180,150,220));

        JScrollPane scrollPane = new JScrollPane(patientTable);
        scrollPane.getViewport().setBackground(lightPurple);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        JTableHeader header = patientTable.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 16));
        header.setForeground(Color.WHITE);
        header.setBackground(darkPurple);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < patientTable.getColumnCount(); i++) {
            patientTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JLabel historyLabel = new JLabel("Patient History", JLabel.CENTER);
        historyLabel.setFont(new Font("Arial", Font.BOLD, 24));
        historyLabel.setOpaque(true);
        historyLabel.setBackground(lightPurple);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(lightPurple);
        tablePanel.add(historyLabel, BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        add(tablePanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();

        JButton addBtn = new JButton("Add");
        JButton deleteBtn = new JButton("Delete");
        JButton saveBtn = new JButton("Save");
        dashboardBtn = new JButton("Dashboard (0)");

        JButton[] buttons = {addBtn, deleteBtn, saveBtn, dashboardBtn};

        for (JButton btn : buttons) {
            btn.setBackground(lightBlue);
            btn.setFont(new Font("Arial", Font.BOLD, 14));
            btn.setPreferredSize(new Dimension(130, 45));
            btnPanel.add(btn);
        }

        add(btnPanel, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> addPatient());
        deleteBtn.addActionListener(e -> deletePatient());
        saveBtn.addActionListener(e -> savePatients());
        dashboardBtn.addActionListener(e -> openDashboard());

        loadPatients();
        setVisible(true);
    }

    void addPatient() {
        try {
            int id = Integer.parseInt(idField.getText());
            String name = nameField.getText();
            int age = Integer.parseInt(ageField.getText());
            String disease = diseaseField.getText();
            String medicine = medicineField.getText();
            String branch = branchBox.getSelectedItem().toString();
            String slot = timeSlotBox.getSelectedItem().toString();
            String doctor = doctorBox.getSelectedItem().toString();

            String uniqueSlot = branch + "-" + slot;

            if (bookedSlots.contains(uniqueSlot)) {
                JOptionPane.showMessageDialog(this, "Slot already booked!");
                return;
            }

            bookedSlots.add(uniqueSlot);

            String prescription =
                    "APOLLO HOSPITAL | " + doctor + "\n" +
                    "Patient: " + name + "\n" +
                    "Disease: " + disease + "\n" +
                    "Medicine: " + medicine;

            Patient p = new Patient(id, name, age, disease, prescription, branch, slot, doctor);

            patients.add(p);
            model.addRow(p.toRow());

            updateDashboard();
            clearFields();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Enter valid data!");
        }
    }

    void deletePatient() {
        int row = patientTable.getSelectedRow();
        if (row >= 0) {
            patients.remove(row);
            model.removeRow(row);
            updateDashboard();
        } else {
            JOptionPane.showMessageDialog(this, "Select a row!");
        }
    }

    void updateDashboard() {
        dashboardBtn.setText("Dashboard (" + patients.size() + ")");
    }

    void clearFields() {
        idField.setText("");
        nameField.setText("");
        ageField.setText("");
        diseaseField.setText("");
        medicineField.setText("");
    }

    void savePatients() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            out.writeObject(patients);
            JOptionPane.showMessageDialog(this, "Saved Successfully!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving!");
        }
    }

    void loadPatients() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            patients = (ArrayList<Patient>) in.readObject();
            model.setRowCount(0);

            for (Patient p : patients) {
                model.addRow(p.toRow());
                bookedSlots.add(p.branch + "-" + p.timeSlot);
            }

            updateDashboard();
        } catch (Exception e) {
        }
    }

    void openDashboard() {
        JFrame dash = new JFrame("Dashboard");
        dash.setSize(400, 300);
        dash.setLayout(new GridLayout(4, 1));

        int total = patients.size();

        long chennai = patients.stream().filter(p -> p.branch.equals("Chennai")).count();
        long coimbatore = patients.stream().filter(p -> p.branch.equals("Coimbatore")).count();

        long kumar = patients.stream().filter(p -> p.doctor.equals("Dr. Kumar")).count();
        long ravi = patients.stream().filter(p -> p.doctor.equals("Dr. Ravi")).count();

        dash.add(new JLabel("Total Patients: " + total, JLabel.CENTER));
        dash.add(new JLabel("Chennai: " + chennai + " | Coimbatore: " + coimbatore, JLabel.CENTER));
        dash.add(new JLabel("Dr. Kumar: " + kumar + " | Dr. Ravi: " + ravi, JLabel.CENTER));

        JButton close = new JButton("Close");
        close.addActionListener(e -> dash.dispose());
        dash.add(close);

        dash.setLocationRelativeTo(this);
        dash.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ClinicGUI::new);
    }
}
