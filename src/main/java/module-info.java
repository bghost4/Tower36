module com.example.tower36 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.tower36 to javafx.fxml;
    exports com.example.tower36;
}