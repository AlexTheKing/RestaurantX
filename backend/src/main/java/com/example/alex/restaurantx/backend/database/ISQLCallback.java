package com.example.alex.restaurantx.backend.database;

import java.sql.SQLException;
import java.sql.Statement;

interface ISQLCallback {

    void onSuccess(Statement statement) throws SQLException;

    void onError(SQLException ex);
}
