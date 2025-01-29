package com.mmgl.pruebas;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "LearnItDatabase";
    private static final int DATABASE_VERSION = 1;

    // Tabla de usuarios
    private static final String TABLE_USUARIOS = "usuarios";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";

    // Script de creación de tabla
    private static final String CREATE_TABLE_USUARIOS =
            "CREATE TABLE " + TABLE_USUARIOS + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_USERNAME + " TEXT UNIQUE, "
                    + COLUMN_PASSWORD + " TEXT)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USUARIOS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIOS);
        onCreate(db);
    }

    // Método para insertar usuario
    public boolean insertUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USERNAME, username);
        contentValues.put(COLUMN_PASSWORD, password);

        try {
            long result = db.insertOrThrow(TABLE_USUARIOS, null, contentValues);
            return result != -1;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error inserting user", e);
            return false;
        } finally {
            db.close();
        }
    }

    // Método para verificar usuario
    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_ID};
        String selection = COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {username, password};

        try {
            Cursor cursor = db.query(TABLE_USUARIOS, columns, selection, selectionArgs, null, null, null);
            int cursorCount = cursor.getCount();
            cursor.close();
            return cursorCount > 0;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error checking user", e);
            return false;
        } finally {
            db.close();
        }
    }

    // Método para obtener username
    public String getUsernameFromCurrentSession() {
        SQLiteDatabase db = this.getReadableDatabase();
        String username = null;

        try {
            Cursor cursor = db.query(TABLE_USUARIOS,
                    new String[]{COLUMN_USERNAME},
                    null, null, null, null, null, "1");

            if (cursor != null && cursor.moveToFirst()) {
                username = cursor.getString(cursor.getColumnIndex(COLUMN_USERNAME));
                cursor.close();
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error recuperando username", e);
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }

        return username;
    }
}
