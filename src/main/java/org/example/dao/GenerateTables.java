package org.example.dao;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

public class GenerateTables {
    public void generate() {
        try {
            String url = Objects
                    .requireNonNull(Thread
                            .currentThread()
                            .getContextClassLoader()
                            .getResource("init.sql"))
                    .getPath();
            url = url.substring(1);
            String sql = new String(Files.readAllBytes(Paths.get(url)));
            Connection connection = ConnectionPool.getInstance().getConnection();
            Statement stmt = connection.createStatement();
            stmt.execute(sql);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }
}
