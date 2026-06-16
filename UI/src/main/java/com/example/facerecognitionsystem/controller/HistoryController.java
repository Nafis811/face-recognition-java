package com.example.facerecognitionsystem.controller;

import com.example.facerecognitionsystem.HelloApplication;
import com.example.facerecognitionsystem.model.AttendanceLog;
import com.example.facerecognitionsystem.repository.SQLiteLogRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class HistoryController {

    @FXML private TableView<HistoryRow> historyTable;
    @FXML private TableColumn<HistoryRow, Integer> colNo;
    @FXML private TableColumn<HistoryRow, String> colName;
    @FXML private TableColumn<HistoryRow, String> colStatus;
    @FXML private TableColumn<HistoryRow, String> colConfidence;
    @FXML private TableColumn<HistoryRow, String> colTime;
    @FXML private Label totalLabel;

    private SQLiteLogRepository logRepository;

    private static final String STYLE_REFRESH = "-fx-background-color: #0984e3; -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 8 20; -fx-border-radius: 6; -fx-background-radius: 6; -fx-cursor: hand;";
    private static final String STYLE_REFRESH_HOVER = "-fx-background-color: #0773c5; -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 8 20; -fx-border-radius: 6; -fx-background-radius: 6; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(9,132,227,0.4), 10, 0, 0, 3); -fx-translate-y: -2;";

    @FXML
    public void initialize() {
        logRepository = new SQLiteLogRepository();
        setupTable();
        loadData();
    }

    private void setupTable() {
        colNo.setCellValueFactory(new PropertyValueFactory<>("no"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colConfidence.setCellValueFactory(new PropertyValueFactory<>("confidence"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("time"));
    }

    private void loadData() {
        List<AttendanceLog> logs = logRepository.findAll();
        ObservableList<HistoryRow> rows = FXCollections.observableArrayList();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss");
        for (int i = 0; i < logs.size(); i++) {
            AttendanceLog log = logs.get(i);
            rows.add(new HistoryRow(
                    i + 1,
                    log.getResult().getPersonName() != null ? log.getResult().getPersonName() : "Unknown",
                    log.getResult().getStatus(),
                    String.format("%.0f%%", log.getResult().getConfidence() * 100),
                    log.getLoggedAt().format(formatter)
            ));
        }

        historyTable.setItems(rows);
        totalLabel.setText("Total: " + logs.size() + " data");
    }

    @FXML
    private void handleRefresh() {
        loadData();
    }

    @FXML
    private void handleBack() {
        HelloApplication.navigateTo("main-view.fxml");
    }

    @FXML
    private void onRefreshHover(MouseEvent e) {
        ((Button) e.getSource()).setStyle(STYLE_REFRESH_HOVER);
    }

    @FXML
    private void onRefreshExit(MouseEvent e) {
        ((Button) e.getSource()).setStyle(STYLE_REFRESH);
    }

    public static class HistoryRow {
        private final Integer no;
        private final String name;
        private final String status;
        private final String confidence;
        private final String time;

        public HistoryRow(Integer no, String name, String status, String confidence, String time) {
            this.no = no;
            this.name = name;
            this.status = status;
            this.confidence = confidence;
            this.time = time;
        }

        public Integer getNo() { return no; }
        public String getName() { return name; }
        public String getStatus() { return status; }
        public String getConfidence() { return confidence; }
        public String getTime() { return time; }
    }
}