package pt.iscte_iul.ista.ES_2324_LEI_GC;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ScheduleManager {
    private static final String CSV_DELIMITER = ",";
    private static final String[] COLUMN_HEADERS = {
            "Curso", "Unidade Curricular", "Turno", "Turma", "Inscritos no turno",
            "Dia da semana", "Hora início da aula", "Hora fim da aula", "Data da aula",
            "Características da sala pedida para a aula", "Sala atribuída à aula"
    };

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String csvFile = "horario.csv";
        String htmlFile = "horar.html";

        List<ScheduleEntry> schedule = loadScheduleFromCSV(csvFile);

        if (schedule != null) {
            generateHTMLSchedule(schedule, htmlFile);
            System.out.println("HTML schedule generated successfully.");
        } else {
            System.out.println("Failed to load the schedule from CSV.");
        }
        scanner.close();
    }

    public static List<ScheduleEntry> loadScheduleFromCSV(String csvFile) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(csvFile));
            List<ScheduleEntry> schedule = new ArrayList<>();

            for (String line : lines) {
               String[] parts = line.split(";");
               ScheduleEntry entry = new ScheduleEntry(parts);
               schedule.add(entry);
            }

            return schedule;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void generateHTMLSchedule(List<ScheduleEntry> schedule, String htmlFile) {
        try (FileWriter writer = new FileWriter(htmlFile)) {
            writer.write("<!DOCTYPE html>\n");
            writer.write("<html>\n<head>\n<title>Schedule</title>\n");
            writer.write("<style>\n");
            writer.write("table { border-collapse: collapse; width: 100%; }\n");
            writer.write("th, td { border: 1px solid black; padding: 8px; text-align: left; }\n");
            writer.write("th { background-color: #f2f2f2; }\n");
            writer.write("</style>\n");
            writer.write("</head>\n<body>\n");
            writer.write("<table>\n");

            // Write column headers
            writer.write("<tr>\n");
            for (String header : COLUMN_HEADERS) {
                writer.write("<th>" + header + "</th>\n");
            }
            writer.write("</tr>\n");
            // Write schedule data
            for (ScheduleEntry entry : schedule) {
                writer.write("<tr>\n");
                String[] data = entry.getData();
                for (String datum : data) {
                    writer.write("<td>" + datum + "</td>\n");
                }
                writer.write("</tr>\n");
            }
            writer.write("</table>\n");
            writer.write("</body>\n</html>");
            System.out.println("HTML schedule generated successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ScheduleEntry {
    private String[] data;

    public ScheduleEntry(String[] data) {
        this.data = data;
    }

    public String[] getData() {
        return data;
    }
}