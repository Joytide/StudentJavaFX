package fr.esilv.javaproject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DBManager extends SQL {

    public DBManager(String address, int port, String user, String pass, String database) {
        super(address, port, user, pass, database);
    }

    public void initTables() {
        //query("SET sql_mode = 'NO_BACKSLASH_ESCAPES'");
        update("CREATE TABLE IF NOT EXISTS students \n" +
                "( \n" +
                "id INT PRIMARY KEY NOT NULL AUTO_INCREMENT, \n" +
                "name VARCHAR(128) NOT NULL, \n" +
                "gender VARCHAR(16), \n" +
                "birthDate DATE, \n" +
                "photo VARCHAR(128), \n" +
                "mark TEXT, \n" +
                "comments TEXT" +
                ")");
        //Debugging
        // addStudent(new Student(0,"TestName","Male",LocalDate.parse("2000-01-01"),null,"18","testCom"));
    }

    public void updateStudent(Student student) {
        update("UPDATE students SET name = '" +
                student.getName()+"', gender = '"+
                student.getGender()+"', birthDate = DATE('"+
                student.getBirthDate().toString()+"'), photo = '"+
                student.getPhoto()+"',  mark =  '"+
                student.getMark()+"', comments = '"+
                student.getComments()+"' "+
                "WHERE id="+student.getId()
        );
    }

    public void addStudent(Student student) {
        System.err.println("####@@ "+student);
        update("INSERT INTO students VALUES (NULL, '" +
                        student.getName()+"', '"+
                        student.getGender()+"', DATE('"+
                        student.getBirthDate().toString()+"'), '"+
                        student.getPhoto()+"', '"+
                        student.getMark()+"', '"+
                        student.getComments()+"') "
                );
    }

    public void deleteStudent(Student student) {
        deleteStudent(student.getId());
    }

    public void deleteStudent(int id) {
        update("DELETE FROM students WHERE id = ?",
                id);
    }

    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();

        ResultSet result = query("SELECT * FROM students");
        int id;
        String name;
        String gender;
        LocalDate birthDate;
        String photo;
        String mark;
        String comments;

        try {
            while(result.next()) {
                id = result.getInt("id");
                name = result.getString("name");
                gender = result.getString("gender");
                birthDate = result.getDate("birthDate").toLocalDate();
                photo = result.getString("photo");
                mark = result.getString("mark");
                comments = result.getString("comments");

                students.add(new Student(id, name, gender, birthDate, photo, mark, comments));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return students;
    }


}
