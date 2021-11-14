package fr.esilv.javaproject;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class StudentController implements Initializable {

    private Student picked;
    private DBManager db;
    private ObservableList<Student> students;

    private String currentImagePath;

    @FXML
    private ListView<Student> studentsView;

    @FXML
    private TextField name;

    @FXML
    private ComboBox<String> gender;

    @FXML
    private DatePicker birthDate;

    @FXML
    private TextField mark;

    @FXML
    private TextArea comments;

    @FXML
    private ImageView photo;

    @FXML
    private Button deleteBtn;

    @FXML
    private Button saveBtn;

    @FXML
    private Button cancelBtn;

    @FXML
    private Button newBtn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.db = new DBManager("localhost", 3306, "REDACTED", "REDACTED", "project");

        if (this.db.connect()) {
            this.db.initTables();
        } else {
            Platform.exit();
            System.exit(1);
        }

        students = FXCollections.observableList(this.db.getAllStudents());
        studentsView.setItems(students);
        ChangeListener<Object> onChange = new ChangeListener<Object>() {
            @Override
            public void changed(ObservableValue<?> observableValue, Object o, Object t1) {

                if (picked != null) {

                    if (picked.getId() == -1) { // In creation
                        boolean saveDisableStatus = (Objects.equals(name.getText(), ""))
                                || (Objects.equals(name.getText(), null))
                                || (gender.getSelectionModel().getSelectedItem() == null)
                                || (Objects.equals(mark.getText(), ""))
                                || (Objects.equals(mark.getText(), null))
                                || !(mark.getText() != null && mark.getText().matches("(0|[1-9]\\d*)"))
                                || (Objects.equals(comments.getText(), ""))
                                || (Objects.equals(comments.getText(), null))
                                || (birthDate.getValue() == null);

                        saveBtn.setDisable(saveDisableStatus);
                    } else {  // In edit

                        boolean saveDisableStatus = Objects.equals(name.getText(), picked.getName())
                                && (Objects.equals(mark.getText(), picked.getMark()) || !mark.getText().matches("-?(0|[1-9]\\d*)"))
                                && Objects.equals(comments.getText(), picked.getComments())
                                && Objects.equals(gender.getSelectionModel().getSelectedItem(), picked.getGender())
                                && Objects.equals(birthDate.getValue(), picked.getBirthDate())
                                && Objects.equals(currentImagePath, picked.getPhoto());
                        saveBtn.setDisable(saveDisableStatus);
                        System.err.println(currentImagePath);
                        System.err.println(picked.getPhoto());
                    }
                } else { // On button press
                    saveBtn.setDisable(true);
                }
            }
        };

        //Display only name in cell
        studentsView.setCellFactory(new Callback<ListView<Student>, ListCell<Student>>() {
            @Override
            public ListCell<Student> call(ListView<Student> studentListView) {
                ListCell<Student> cell = new ListCell<Student>() {
                    @Override
                    protected void updateItem(Student item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setText(item.getName());
                        } else {
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        });

        name.textProperty().addListener(onChange);
        gender.valueProperty().addListener(onChange);
        birthDate.valueProperty().addListener(onChange);
        mark.textProperty().addListener(onChange);
        comments.textProperty().addListener(onChange);
        photo.imageProperty().addListener(onChange);
    }

    private void displayStudent() {
        
        if (picked != null) {
            name.setText(picked.getName());
            gender.getSelectionModel().select(picked.getGender());
            birthDate.setValue(picked.getBirthDate());
            mark.setText(picked.getMark());
            comments.setText(picked.getComments());
            currentImagePath = picked.getPhoto();

            System.err.println("€€€"+currentImagePath);
            System.err.println(picked.getPhoto());

            displayImage();
        } else {
            name.setText("");
            gender.getSelectionModel().select("");
            birthDate.setValue(null);
            mark.setText("");
            comments.setText("");
            currentImagePath = null;
            displayImage();
        }
    }

    private void displayImage() {
        try {
            FileInputStream file = new FileInputStream(currentImagePath);
            photo.setImage(new Image(file));
        } catch (Exception e) {
            photo.setImage(new Image(Main.class.getResource("default.png").toString()));
        }
    }

    @FXML
    public void onChooseStudent(MouseEvent arg0) {
        Student s = studentsView.getSelectionModel().getSelectedItem();
        if (s == null)
            return;
        picked = s;
        displayStudent();
        disableDisplays(false, false, false, false, false);
    }

    public void disableDisplays(boolean disableParams, boolean disableNew, boolean disableDelete, boolean disableCancel, boolean disableList) {
        name.setDisable(disableParams);
        gender.setDisable(disableParams);
        birthDate.setDisable(disableParams);
        mark.setDisable(disableParams);
        comments.setDisable(disableParams);
        photo.setDisable(disableParams);

        newBtn.setDisable(disableNew);
        deleteBtn.setDisable(disableDelete);
        cancelBtn.setDisable(disableCancel);

        studentsView.setDisable(disableList);
    }


    public void onSave(ActionEvent actionEvent) {
        picked.setName(name.getText());
        picked.setGender(gender.getSelectionModel().getSelectedItem());
        picked.setBirthDate(birthDate.getValue());
        picked.setMark(mark.getText());
        picked.setPhoto(currentImagePath);
        picked.setComments(comments.getText());

        if (picked.getId() != -1) { // Updating
            db.updateStudent(picked);
            students = FXCollections.observableList(this.db.getAllStudents());
            studentsView.setItems(students);
        } else { // New
            db.addStudent(picked);
            students = FXCollections.observableList(this.db.getAllStudents());
            studentsView.setItems(students);
        }
        picked = null;
        displayStudent();
        disableDisplays(false, false, true, true, false);
    }

    public void onDelete(ActionEvent actionEvent) {
        db.deleteStudent(picked);
        students.remove(picked);
        picked = null;
        displayStudent();
        disableDisplays(false, false, true, true, false);
    }

    public void onCancel(ActionEvent actionEvent) {

        picked = null;
        disableDisplays(true, false, false, true, false);
        displayStudent();

    }

    public void onNew(ActionEvent actionEvent) {
        picked = new Student();
        displayStudent();
        disableDisplays(false, true, true, false, true);
    }

    public void onPhotoEnter(MouseEvent mouseEvent) {
        photo.setOpacity(0.5D);
    }

    public void onPhotoClick(MouseEvent mouseEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg"));
        Stage stage = new Stage();
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            currentImagePath = selectedFile.getAbsolutePath();
            displayImage();
        }
    }

    public void onPhotoExit(MouseEvent mouseEvent) {
        photo.setOpacity(1.0D);
    }
}
