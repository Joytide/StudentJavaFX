package fr.esilv.javaproject;

import java.sql.*;

public abstract class SQL {

    private final String address;
    private final int port;
    private final String user;
    private final String pass;
    private final String database;
    private Connection conn;

    public SQL(String address, int port, String user, String pass, String database) {
        this.address = address;
        this.port = port;
        this.user = user;
        this.pass = pass;
        this.database = database;
    }

    public boolean connect() {
        try {
            if (conn != null && !conn.isClosed()) {
                return true;
            }
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://" + address + ":" + port + "/" + database, user, pass);
            return true;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void disconnect() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }

    public void update(String request, Object... args) {
        if (!connect())
            return;
        try {
            PreparedStatement state = conn.prepareStatement(request);
            for (int i = 1; args != null && i <= args.length; ++i) {
                state.setObject(i, args[i - 1]);
            }
            System.err.println("#########"+state);
            state.executeUpdate();
            state.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(String request) {
        update(request, null);
    }

    public ResultSet query(String request, Object... args) {
        if (!connect())
            return null;

        try {
            PreparedStatement state = conn.prepareStatement(request);
            for (int i = 1; args != null && i <= args.length; ++i) {
                state.setObject(i, args[i - 1]);
            }
            return state.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public final ResultSet query(String request) {
        return query(request, null);
    }
}
