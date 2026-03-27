module sk.ukf.gui {
    requires javafx.controls;
    requires javafx.fxml;


    opens sk.ukf.gui to javafx.fxml;
    exports sk.ukf.gui;
}