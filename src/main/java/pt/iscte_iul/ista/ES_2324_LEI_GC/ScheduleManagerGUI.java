package pt.iscte_iul.ista.ES_2324_LEI_GC;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Calendar;

public class ScheduleManagerGUI extends JFrame {
    private static final String CSV_DELIMITER = ",";
    private static final String[] COLUMN_HEADERS = {
            "Curso", "Unidade Curricular", "Turno", "Turma", "Inscritos no turno",
            "Dia da semana", "Hora início da aula", "Hora fim da aula", "Data da aula",
            "Características da sala pedida para a aula", "Sala atribuída à aula", "Semana", "Semana-Semestre"
    };
    private JTable table;
    private JTextField searchField;
    private JComboBox<String> columnComboBox;
    private JCheckBox keepColumnCheckBox;
    
    public ScheduleManagerGUI() {
        setTitle("Schedule Manager");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create table with default table model
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        for (String header : COLUMN_HEADERS) {
            model.addColumn(header);
        }
        table = new JTable(model);
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(table);
        // Create load button
        JButton loadButton = new JButton("Load Schedule");
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadSchedule();
            }
        });

        // Create search field
        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(200, 25));
        searchField.setToolTipText("Search...");
        searchField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filterTable(searchField.getText());
            }
        });

        // Create column combo box
        columnComboBox = new JComboBox<>(COLUMN_HEADERS);
        columnComboBox.setPreferredSize(new Dimension(200, 25));
        columnComboBox.setToolTipText("Select column to hide");

        // Create hide column button
        JButton hideColumnButton = new JButton("Hide Column");
        hideColumnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedColumn = (String) columnComboBox.getSelectedItem();
                if (selectedColumn != null && !selectedColumn.isEmpty()) {
                    hideColumn(selectedColumn);
                }
            }
        });

        // Create keep column checkbox
        keepColumnCheckBox = new JCheckBox("Keep Column Visible");
        keepColumnCheckBox.setSelected(false);
        // Add components to content pane
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        JPanel panel = new JPanel();
        panel.add(loadButton);
        panel.add(searchField);
        panel.add(columnComboBox);
        panel.add(hideColumnButton);
        panel.add(keepColumnCheckBox);
        getContentPane().add(panel, BorderLayout.SOUTH);
    }

    private void filterTable(String searchText) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
    }

    private void hideColumn(String columnName) {
        TableColumn column = table.getColumn(columnName);
        if (column != null) {
            if (!keepColumnCheckBox.isSelected()) {
                table.removeColumn(column);
            }
        }
    }

    private void loadSchedule() {
    	 
            try (BufferedReader reader = new BufferedReader(new FileReader("horario.csv"))) {
            	DefaultTableModel model = (DefaultTableModel) table.getModel();
                model.setRowCount(0); // Clear existing rows
                String line;
                boolean isFirst = true;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(";");
                    int week = 0;
                    int weekSemestre=0;
                    if (parts.length>8 && !isFirst) {
                    	week = getWeek(parts[8]);
                    	weekSemestre = countWeeksBetween("13/09/2022", parts[8]);
                    }
                    
                    String [] partsUpdated = Arrays.copyOf(parts,13);
                    partsUpdated[11]= String.valueOf(week);
                    partsUpdated[12] = String.valueOf(weekSemestre);
                    model.addRow(partsUpdated);
                    isFirst = false;
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to load schedule from CSV file.", "Error", JOptionPane.ERROR_MESSAGE);
            }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ScheduleManagerGUI frame = new ScheduleManagerGUI();
                frame.setVisible(true);
            }
        });
    }
    
    public static int getWeek(String strdate) {
    	 try {
             SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
             java.util.Date date = sdf.parse(strdate);
             Calendar calendar = Calendar.getInstance();
             calendar.setTime(date);
             int weekNumber = calendar.get(Calendar.WEEK_OF_YEAR);
             return weekNumber;
         } catch (ParseException e) {
             e.printStackTrace();
             return -1; 
         }
    }
    
    
    //Considerando que a primeira semana é a de 13-09
    public static int countWeeksBetween(String startDateStr, String endDateStr) {
DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        LocalDate startDate = LocalDate.parse(startDateStr, formatter);
        
        LocalDate endDate = LocalDate.parse(endDateStr, formatter);
        
        long weeks = ChronoUnit.WEEKS.between(startDate, endDate);
        return (int) weeks;
    }}


