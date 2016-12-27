package com.example.alex.restaurantx.backend.database;

import com.example.alex.restaurantx.backend.callbacks.IResultCallback;
import com.example.alex.restaurantx.backend.database.annotations.*;
import com.sun.istack.internal.Nullable;

import javax.validation.constraints.NotNull;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper {

    private static DatabaseHelper sHelper;
    private final static String sConnectionStringTemplate = "jdbc:sqlite:%s";
    public final static String sUniversalQuantificator = "*";
    private final Connection mConnection;

    private DatabaseHelper(String connectionString) throws SQLException {
        mConnection = DriverManager.getConnection(connectionString);
    }

    public static synchronized DatabaseHelper getInstance(String filepath) throws SQLException {
        if (sHelper == null) {
            sHelper = new DatabaseHelper(String.format(sConnectionStringTemplate, filepath));
        }
        return sHelper;
    }

    @SuppressWarnings("unused")
    @Nullable
    public static String getTableName(final AnnotatedElement model) {
        final Table table = model.getAnnotation(Table.class);
        if (table != null) {
            return table.value();
        } else {
            return null;
        }
    }

    @SuppressWarnings("unused")
    @Nullable
    public static String getTableCreateQuery(final Class<?> model) {
        final String SQL_TABLE_CREATE_TEMPLATE = "CREATE TABLE IF NOT EXISTS %s (%s);";
        final String SQL_TABLE_CREATE_FIELD_TEMPLATE = "%s %s%s";
        final Table table = model.getAnnotation(Table.class);
        if (table != null) {
            try {
                final String name = table.value();
                final StringBuilder builder = new StringBuilder();
                final Field[] fields = model.getFields();
                for (int i = 0; i < fields.length; i++) {
                    final Annotation[] annotations = fields[i].getAnnotations();
                    String type = null;
                    String additionalKeys = "";
                    for (Annotation annotation : annotations) {
                        if (annotation instanceof dbInteger) {
                            type = ((dbInteger) annotation).value();
                        } else if (annotation instanceof dbText) {
                            type = ((dbText) annotation).value();
                        } else if (annotation instanceof dbReal) {
                            type = ((dbReal) annotation).value();
                        } else if (annotation instanceof dbBlob) {
                            type = ((dbBlob) annotation).value();
                        } else if (annotation instanceof dbPrimaryKey) {
                            additionalKeys = " " + ((dbPrimaryKey) annotation).value();
                        }
                    }
                    if (type != null) {
                        final String value = (String) fields[i].get(null);
                        builder.append(String.format(Locale.US, SQL_TABLE_CREATE_FIELD_TEMPLATE, value, type, additionalKeys));
                        if (i < fields.length - 1) {
                            builder.append(",");
                        }
                    }
                }
                return String.format(Locale.US, SQL_TABLE_CREATE_TEMPLATE, name, builder.toString());
            } catch (final Exception e) {
                return null;
            }
        } else {
            return null;
        }
    }

    @SuppressWarnings("unused")
    public void executeTransaction(final ISQLCallback callback) {
        Statement statement = null;
        try {
            statement = mConnection.createStatement();
            callback.onSuccess(statement);
        } catch (SQLException ex1) {
            ex1.printStackTrace();
            try {
                mConnection.rollback();
            } catch (SQLException ex2) {
                ex2.printStackTrace();
                if (callback != null) {
                    callback.onError(ex2);
                }
            }
            if (callback != null) {
                callback.onError(ex1);
                ex1.printStackTrace();
            }
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    if (callback != null) {
                        callback.onError(ex);
                    }
                }
            }
        }
    }

    @SuppressWarnings("unused")
    public synchronized void executeAsyncTransaction(final ISQLCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                executeTransaction(callback);
            }
        }).run();
    }

    @SuppressWarnings("unused")
    public synchronized void createTablesAsync(Class<?>[] models, @Nullable IResultCallback<Integer> callback) {
        for (final Class<?> model : models) {
            executeAsyncTransaction(new ISQLCallback() {
                @Override
                public void onSuccess(Statement statement) throws SQLException {
                    final String sqlQuery = getTableCreateQuery(model);
                    int result = statement.executeUpdate(sqlQuery);
                    if (callback != null) {
                        callback.onSuccess(result);
                    }
                }

                @Override
                public void onError(SQLException ex) {
                    if (callback != null) {
                        callback.onError(ex);
                    }
                }
            });
        }
    }

    @SuppressWarnings("unused")
    public synchronized void createTables(Class<?>[] models, @Nullable IResultCallback<Integer> callback) {
        for (final Class<?> model : models) {
            executeTransaction(new ISQLCallback() {
                @Override
                public void onSuccess(Statement statement) throws SQLException {
                    final String sqlQuery = getTableCreateQuery(model);
                    int result = statement.executeUpdate(sqlQuery);
                    if (callback != null) {
                        callback.onSuccess(result);
                    }
                }

                @Override
                public void onError(SQLException ex) {
                    if (callback != null) {
                        callback.onError(ex);
                    }
                }
            });
        }
    }

    @SuppressWarnings("unused")
    public synchronized void queryAsync(@NotNull final IResultCallback<ResultSet> callback, final Class<?> model, final String whatClause, @Nullable final String whereClause) {
        executeAsyncTransaction(new ISQLCallback() {
            @Override
            public void onSuccess(Statement statement) throws SQLException {
                String sqlQueryTemplate = "SELECT %s FROM %s";
                final String whereTemplate = " WHERE %s";
                if (whereClause != null) {
                    sqlQueryTemplate += whereTemplate;
                }
                sqlQueryTemplate += ";";
                String sqlQuery = String.format(sqlQueryTemplate, whatClause, getTableName(model), whereClause);
                ResultSet resultSet = statement.executeQuery(sqlQuery);
                callback.onSuccess(resultSet);
            }

            @Override
            public void onError(SQLException ex) {
                callback.onError(ex);
            }
        });
    }

    @SuppressWarnings("unused")
    public synchronized void query(@NotNull final IResultCallback<ResultSet> callback, final Class<?> model, final String whatClause, @Nullable final String whereClause) {
        executeTransaction(new ISQLCallback() {
            @Override
            public void onSuccess(Statement statement) throws SQLException {
                String sqlQueryTemplate = "SELECT %s FROM %s";
                final String whereTemplate = " WHERE %s";
                if (whereClause != null) {
                    sqlQueryTemplate += whereTemplate;
                }
                sqlQueryTemplate += ";";
                String sqlQuery = String.format(sqlQueryTemplate, whatClause, getTableName(model), whereClause);
                ResultSet resultSet = statement.executeQuery(sqlQuery);
                callback.onSuccess(resultSet);
            }

            @Override
            public void onError(SQLException ex) {
                callback.onError(ex);
            }
        });
    }

    @SuppressWarnings("unused")
    private String getInsertQuery(final Class<?> model, final HashMap<String, String> values) {
        String sqlInsertTemplate = "INSERT INTO %s (%s) VALUES (%s);";
        StringBuilder tableColumns = new StringBuilder();
        StringBuilder insertValues = new StringBuilder();
        for (String key : values.keySet()) {
            tableColumns.append(key);
            tableColumns.append(", ");
            insertValues.append(values.get(key));
            insertValues.append(", ");
        }
        String columnsString = tableColumns.substring(0, tableColumns.length() - 2);
        String valuesString = insertValues.substring(0, insertValues.length() - 2);
        return String.format(sqlInsertTemplate, getTableName(model), columnsString, valuesString);
    }

    @SuppressWarnings("unused")
    public void insertAsync(final Class<?> model, final HashMap<String, String> values, @Nullable final IResultCallback<Integer> callback) {
        executeAsyncTransaction(new ISQLCallback() {
            @Override
            public void onSuccess(Statement statement) throws SQLException {
                String sql = getInsertQuery(model, values);
                int result = statement.executeUpdate(sql);
                if (callback != null) {
                    callback.onSuccess(result);
                }
            }

            @Override
            public void onError(SQLException ex) {
                if (callback != null) {
                    callback.onError(ex);
                }
            }
        });
    }

    @SuppressWarnings("unused")
    public void insert(final Class<?> model, final HashMap<String, String> values, @Nullable final IResultCallback<Integer> callback) {
        executeTransaction(new ISQLCallback() {
            @Override
            public void onSuccess(Statement statement) throws SQLException {
                String sql = getInsertQuery(model, values);
                int result = statement.executeUpdate(sql);
                if (callback != null) {
                    callback.onSuccess(result);
                }
            }

            @Override
            public void onError(SQLException ex) {
                if (callback != null) {
                    callback.onError(ex);
                }
            }
        });
    }

    @SuppressWarnings("unused")
    public synchronized void bulkInsertAsync(final Class<?> model, final List<HashMap<String, String>> valuesList, @Nullable final IResultCallback<Integer> callback) {
        executeAsyncTransaction(new ISQLCallback() {
            @Override
            public void onSuccess(Statement statement) throws SQLException {
                StringBuilder insertQueriesBuilder = new StringBuilder();
                for (HashMap<String, String> values : valuesList) {
                    insertQueriesBuilder.append(getInsertQuery(model, values));
                }
                int result = statement.executeUpdate(insertQueriesBuilder.toString());
                if (callback != null) {
                    callback.onSuccess(result);
                }
            }

            @Override
            public void onError(SQLException ex) {
                if (callback != null) {
                    callback.onError(ex);
                }
            }
        });
    }

    @SuppressWarnings("unused")
    public synchronized void bulkInsert(final Class<?> model, final List<HashMap<String, String>> valuesList, @Nullable final IResultCallback<Integer> callback) {
        executeTransaction(new ISQLCallback() {
            @Override
            public void onSuccess(Statement statement) throws SQLException {
                StringBuilder insertQueriesBuilder = new StringBuilder();
                for (HashMap<String, String> values : valuesList) {
                    insertQueriesBuilder.append(getInsertQuery(model, values));
                }
                int result = statement.executeUpdate(insertQueriesBuilder.toString());
                if (callback != null) {
                    callback.onSuccess(result);
                }
            }

            @Override
            public void onError(SQLException ex) {
                if (callback != null) {
                    callback.onError(ex);
                }
            }
        });
    }

    @SuppressWarnings("unused")
    public synchronized void deleteAsync(final Class<?> model, final String whereClause, @Nullable final IResultCallback<Integer> callback) {
        executeAsyncTransaction(new ISQLCallback() {
            @Override
            public void onSuccess(Statement statement) throws SQLException {
                String deleteTemplate = "DELETE FROM %s WHERE %s;";
                String sql = String.format(deleteTemplate, getTableName(model), whereClause);
                int result = statement.executeUpdate(sql);
                if (callback != null) {
                    callback.onSuccess(result);
                }
            }

            @Override
            public void onError(SQLException ex) {
                if (callback != null) {
                    callback.onError(ex);
                }
            }
        });
    }

    @SuppressWarnings("unused")
    public synchronized void delete(final Class<?> model, final String whereClause, @Nullable final IResultCallback<Integer> callback) {
        executeTransaction(new ISQLCallback() {
            @Override
            public void onSuccess(Statement statement) throws SQLException {
                String deleteTemplate = "DELETE FROM %s WHERE %s;";
                String sql = String.format(deleteTemplate, getTableName(model), whereClause);
                int result = statement.executeUpdate(sql);
                if (callback != null) {
                    callback.onSuccess(result);
                }
            }

            @Override
            public void onError(SQLException ex) {
                if (callback != null) {
                    callback.onError(ex);
                }
            }
        });
    }

    @SuppressWarnings("unused")
    public static String getSqlString(final String string) {
        return '"' + string + '"';
    }

    @SuppressWarnings("unused")
    public static String getUsualString(final String sqlString) {
        return sqlString.substring(1, sqlString.length() - 1);
    }

    @SuppressWarnings("unused")
    public void updateAsync(final Class<?> model, final HashMap<String, String> setClause, final String whereClause, @Nullable final IResultCallback<Integer> callback) {
        executeAsyncTransaction(new ISQLCallback() {
            @Override
            public void onSuccess(Statement statement) throws SQLException {
                String sqlUpdateTemplate = "UPDATE %s SET %s WHERE %s;";
                String tableName = getTableName(model);
                StringBuilder setValues = new StringBuilder();
                for (String columnName : setClause.keySet()) {
                    setValues.append(String.format("%s = %s, ", columnName, setClause.get(columnName)));
                }
                String valuesString = setValues.substring(0, setValues.length() - 2);
                int result = statement.executeUpdate(String.format(sqlUpdateTemplate, tableName, valuesString, whereClause));
                if (callback != null) {
                    callback.onSuccess(result);
                }
            }

            @Override
            public void onError(SQLException ex) {
                if (callback != null) {
                    callback.onError(ex);
                }
            }
        });
    }
}
