module fr.esilv.javaproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires mysql.connector.java;
    requires java.sql;


    opens fr.esilv.javaproject to javafx.fxml;
    exports fr.esilv.javaproject;
}