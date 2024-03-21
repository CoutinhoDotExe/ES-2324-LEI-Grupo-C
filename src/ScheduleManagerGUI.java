package pt.iscte_iul.ista.ES_2324_LEI_GC;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ScheduleManagerGUI extends Application {
    private static final String CSV_DELIMITER = ",";
    private static final String[] COLUMN_HEADERS = {
            "Curso", "Unidade Curricular", "Turno", "Turma", "Inscritos no turno",
            "Dia da semana", "Hora início da aula", "Hora fim da aula", "Data da aula",
            "Características da sala pedida para a aula", "Sala atribuída à aula"
    };

    private TableView<String[]> tableView;
    private ObservableList<String[]> data;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Schedule Manager");

        BorderPane borderPane = new BorderPane();
        tableView = new TableView<>();
        data = FXCollections.observableArrayList();
        tableView.setItems(data);

        createTableColumns();

        Button loadButton = new Button("Load Schedule");
        loadButton.setOnAction(e -> loadSchedule());

        VBox vbox = new VBox();
        vbox.getChildren().addAll(loadButton, tableView);
        borderPane.setCenter(vbox);

        Scene scene = new Scene(borderPane, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void createTableColumns() {
        for (int i = 0; i < COLUMN_HEADERS.length; i++) {
            final int columnIndex = i;
            TableColumn<String[], String> column = new TableColumn<>(COLUMN_HEADERS[i]);
            column.setCellValueFactory(param -> {
                String[] rowData = param.getValue();
                if (rowData != null && rowData.length > columnIndex) {
                    return new javafx.beans.property.SimpleStringProperty(rowData[columnIndex]);
                } else {
                    return new javafx.beans.property.SimpleStringProperty("");
                }
            });
            tableView.getColumns().add(column);
        }
    }

    private void loadSchedule() {
        try {
            List<String> lines = Files.readAllLines(Paths.get("horario-exemplo.csv"));
            data.clear();

            for (String line : lines) {
                String[] parts = line.split(CSV_DELIMITER);
                if (parts.length == COLUMN_HEADERS.length) {
                    data.add(parts);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to load schedule from CSV file.");
            alert.showAndWait();
        }
    }
}
