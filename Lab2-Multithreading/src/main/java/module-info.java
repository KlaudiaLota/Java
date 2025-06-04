module org.example.lab2multithreading {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.swing;

    opens org.example.lab2multithreading to javafx.fxml;
    exports org.example.lab2multithreading;
}