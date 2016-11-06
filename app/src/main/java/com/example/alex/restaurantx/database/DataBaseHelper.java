package com.example.alex.restaurantx.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.alex.restaurantx.callbacks.IResultCallback;
import com.example.alex.restaurantx.database.annotations.Table;
import com.example.alex.restaurantx.database.annotations.dbBlob;
import com.example.alex.restaurantx.database.annotations.dbInteger;
import com.example.alex.restaurantx.database.annotations.dbPrimaryKey;
import com.example.alex.restaurantx.database.annotations.dbReal;
import com.example.alex.restaurantx.database.annotations.dbText;
import com.example.alex.restaurantx.database.models.TablesList;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static DatabaseHelper sHelper;
    private static final String mDatabaseName = "xrestdb";
    private static final String SQL_TABLE_CREATE_TEMPLATE = "CREATE TABLE IF NOT EXISTS %s (%s);";
    private static final String SQL_TABLE_CREATE_FIELD_TEMPLATE = "%s %s %s";
    public static final int CURRENT_VERSION = 1;

    private DatabaseHelper(Context pContext, int pVersion) {
        super(pContext, mDatabaseName, null, pVersion);
    }

    public static synchronized DatabaseHelper getInstance(Context pContext, int pVersion) {
        if (sHelper == null) {
            sHelper = new DatabaseHelper(pContext, pVersion);
        }
        return sHelper;
    }

    @Nullable
    public static String getTableName(final AnnotatedElement pModel) {
        final Table table = pModel.getAnnotation(Table.class);
        if (table != null) {
            return table.value();
        } else {
            return null;
        }
    }

    @Nullable
    public static String getTableCreateQuery(final Class<?> pModel) {
        final Table table = pModel.getAnnotation(Table.class);
        if (table != null) {
            try {
                final String name = table.value();
                final StringBuilder builder = new StringBuilder();
                final Field[] fields = pModel.getFields();
                for (int i = 0; i < fields.length; i++) {
                    final Field field = fields[i];
                    final Annotation[] annotations = field.getAnnotations();
                    String type = null;
                    String additionalKeys = "";
                    for (final Annotation annotation : annotations) {
                        if (annotation instanceof dbInteger) {
                            type = ((dbInteger) annotation).value();
                        } else if (annotation instanceof dbText) {
                            type = ((dbText) annotation).value();
                        } else if (annotation instanceof dbReal) {
                            type = ((dbReal) annotation).value();
                        } else if (annotation instanceof dbBlob) {
                            type = ((dbBlob) annotation).value();
                        } else if (annotation instanceof dbPrimaryKey) {
                            additionalKeys = ((dbPrimaryKey) annotation).value();
                        }
                    }
                    if (type == null) {
                        return null;
                    }
                    final String value = (String) field.get(null);
                    builder.append(String.format(Locale.US, SQL_TABLE_CREATE_FIELD_TEMPLATE, value, type, additionalKeys));
                    if (i < fields.length - 1) {
                        builder.append(",");
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

    @Override
    public void onCreate(final SQLiteDatabase pDatabase) {
        for (final Class<?> clazz : TablesList.MODELS) {
            final String sql = getTableCreateQuery(clazz);
            if (sql != null) {
                pDatabase.execSQL(sql);
            }
        }
    }

    @Override
    public void onUpgrade(final SQLiteDatabase pDatabase, final int pOldVersion, final int pNewVersion) {

    }

    public synchronized void query(@NonNull final IResultCallback<Cursor> pCallback, final String pSqlQuery, final AnnotatedElement pModel, final String pSqlCondition, final String... pArgs) {
        new AsyncTask<Void, Void, Cursor>() {
            @Override
            protected Cursor doInBackground(Void... params) {
                final SQLiteDatabase database = getReadableDatabase();
                String sql = "SELECT " + pSqlQuery + " FROM " + getTableName(pModel) + " " + pSqlCondition;
                return database.rawQuery(sql, pArgs);
            }

            @Override
            protected void onPostExecute(Cursor pCursor) {
                pCallback.onSuccess(pCursor);
            }
        }.execute();
    }

    public synchronized void insert(final Class<?> pModel, final ContentValues pValues, @Nullable final IResultCallback<Long> pCallback) {
        new AsyncTask<Void, Void, Long>(){
            @Override
            protected Long doInBackground(Void... params) {
                final String name = getTableName(pModel);
                if (name != null) {
                    final SQLiteDatabase database = getWritableDatabase();
                    long id;
                    try {
                        database.beginTransaction();
                        id = database.insert(name, null, pValues);
                        database.setTransactionSuccessful();
                    } finally {
                        database.endTransaction();
                    }
                    return id;
                } else {
                    RuntimeException exception = new RuntimeException("No such table exists");
                    if(pCallback != null) {
                        pCallback.onError(exception);
                    } else {
                        throw exception;
                    }
                    return -1L;
                }
            }

            @Override
            protected void onPostExecute(Long pLong) {
                if(pCallback != null) {
                    pCallback.onSuccess(pLong);
                }
            }
        }.execute();
    }

    public synchronized void bulkInsert(final Class<?> pModel, final List<ContentValues> pValuesList, @Nullable final IResultCallback<Integer> pCallback) {
        new AsyncTask<Void, Void, Integer>(){
            @Override
            protected Integer doInBackground(Void... params) {
                final String name = getTableName(pModel);
                if (name != null) {
                    final SQLiteDatabase database = getWritableDatabase();
                    int count = 0;
                    try {
                        database.beginTransaction();
                        for (final ContentValues value : pValuesList) {
                            database.insert(name, null, value);
                            count++;
                        }
                        database.setTransactionSuccessful();
                    } finally {
                        database.endTransaction();
                    }
                    return count;
                } else {
                    RuntimeException exception = new RuntimeException("No such table exists");
                    if(pCallback != null){
                        pCallback.onError(exception);
                    } else {
                        throw exception;
                    }
                    return -1;
                }
            }

            @Override
            protected void onPostExecute(Integer pInteger) {
                if(pCallback != null){
                    pCallback.onSuccess(pInteger);
                }
            }
        }.execute();
    }

    public synchronized void delete(final Class<?> pModel, @Nullable final IResultCallback<Integer> pCallback, final String pSql, final String... pArgs) {
        new AsyncTask<Void, Void, Integer>(){
            @Override
            protected Integer doInBackground(Void... params) {
                final String name = getTableName(pModel);
                if (name != null) {
                    final SQLiteDatabase database = getWritableDatabase();
                    int count = 0;
                    try {
                        database.beginTransaction();
                        count = database.delete(name, pSql, pArgs);
                        database.setTransactionSuccessful();
                    } finally {
                        database.endTransaction();
                    }
                    return count;
                } else {
                    RuntimeException exception = new RuntimeException("No such table exists");
                    if(pCallback != null){
                        pCallback.onError(exception);
                    } else {
                        throw exception;
                    }
                    return -1;
                }
            }

            @Override
            protected void onPostExecute(Integer pInteger) {
                if(pCallback != null){
                    pCallback.onSuccess(pInteger);
                }
            }
        }.execute();
    }
}
