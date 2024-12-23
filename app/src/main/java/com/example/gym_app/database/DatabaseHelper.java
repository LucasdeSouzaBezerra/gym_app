package com.example.gym_app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "gymlab.db";
    private static final int DATABASE_VERSION = 2;

    // Tabelas
    private static final String TABLE_PROFISSIONAIS = "profissionais";
    private static final String TABLE_CLIENTES = "clientes";
    private static final String TABLE_CONSULTAS = "consultas";

    // Colunas comuns
    private static final String COLUMN_USER_ID = "id";
    private static final String COLUMN_USER_NAME = "name";
    private static final String COLUMN_USER_EMAIL = "email";
    private static final String COLUMN_USER_PASSWORD = "password";

    // Colunas de consultas
    private static final String COLUMN_CONSULTA_ID = "consulta_id";
    private static final String COLUMN_CONSULTA_DATA = "data";
    private static final String COLUMN_CONSULTA_STATUS = "status";

    // Scripts de criação das tabelas
    private static final String TABLE_CREATE_PROFISSIONAIS =
            "CREATE TABLE IF NOT EXISTS " + TABLE_PROFISSIONAIS + " (" +
                    COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_USER_NAME + " TEXT NOT NULL, " +
                    COLUMN_USER_EMAIL + " TEXT NOT NULL UNIQUE, " +
                    COLUMN_USER_PASSWORD + " TEXT NOT NULL);";

    private static final String TABLE_CREATE_CLIENTES =
            "CREATE TABLE IF NOT EXISTS " + TABLE_CLIENTES + " (" +
                    COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_USER_NAME + " TEXT NOT NULL, " +
                    COLUMN_USER_EMAIL + " TEXT NOT NULL UNIQUE, " +
                    COLUMN_USER_PASSWORD + " TEXT NOT NULL);";

    private static final String TABLE_CREATE_CONSULTAS =
            "CREATE TABLE IF NOT EXISTS " + TABLE_CONSULTAS + " (" +
                    COLUMN_CONSULTA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_CONSULTA_DATA + " TEXT NOT NULL, " +
                    COLUMN_CONSULTA_STATUS + " TEXT NOT NULL);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE_PROFISSIONAIS);
        db.execSQL(TABLE_CREATE_CLIENTES);
        db.execSQL(TABLE_CREATE_CONSULTAS);
        Log.d("DatabaseHelper", "Banco de dados criado com sucesso.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL(TABLE_CREATE_CONSULTAS);  // Adiciona a tabela de consultas
            Log.d("DatabaseHelper", "Tabela consultas adicionada durante atualização.");
        }
    }

    // Adicionar profissional
    public boolean addProfissional(String name, String email, String password) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_USER_NAME, name);
            values.put(COLUMN_USER_EMAIL, email);
            values.put(COLUMN_USER_PASSWORD, password);
            return db.insert(TABLE_PROFISSIONAIS, null, values) != -1;
        }
    }

    // Verificar login de profissional
    public boolean checkProfissional(String email, String password) {
        String query = "SELECT * FROM " + TABLE_PROFISSIONAIS + " WHERE " +
                COLUMN_USER_EMAIL + " = ? AND " + COLUMN_USER_PASSWORD + " = ?";
        try (SQLiteDatabase db = this.getReadableDatabase();
             Cursor cursor = db.rawQuery(query, new String[]{email, password})) {
            return cursor.moveToFirst();
        }
    }

    // Consultar status de consulta
    public String getConsultaStatus(String data) {
        String status = "livre";  // Valor default
        String query = "SELECT " + COLUMN_CONSULTA_STATUS + " FROM " + TABLE_CONSULTAS +
                " WHERE " + COLUMN_CONSULTA_DATA + " = ?";
        try (SQLiteDatabase db = this.getReadableDatabase();
             Cursor cursor = db.rawQuery(query, new String[]{data})) {
            if (cursor.moveToFirst()) {
                status = cursor.getString(0);  // Obtém o status da consulta
            }
        }
        return status;
    }

    // Adicionar consulta
    public boolean addConsulta(String data, String status) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_CONSULTA_DATA, data);
            values.put(COLUMN_CONSULTA_STATUS, status);
            return db.insert(TABLE_CONSULTAS, null, values) != -1;
        }
    }

    // Atualizar status da consulta
    public boolean updateConsulta(String data, String status) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_CONSULTA_STATUS, status);
            int rowsUpdated = db.update(TABLE_CONSULTAS, values, COLUMN_CONSULTA_DATA + " = ?", new String[]{data});
            if (rowsUpdated == 0) {
                // Se não existe, insere a nova consulta
                values.put(COLUMN_CONSULTA_DATA, data);
                db.insert(TABLE_CONSULTAS, null, values);
            }
            return true;
        }
    }

    // Verificar se o e-mail existe para o profissional
    public boolean checkEmailExistsProfissional(String email) {
        String query = "SELECT * FROM " + TABLE_PROFISSIONAIS + " WHERE " +
                COLUMN_USER_EMAIL + " = ?";
        try (SQLiteDatabase db = this.getReadableDatabase();
             Cursor cursor = db.rawQuery(query, new String[]{email})) {
            return cursor.moveToFirst();
        }
    }

    // Verificar se o e-mail existe para o cliente
    public boolean checkEmailExistsCliente(String email) {
        String query = "SELECT * FROM " + TABLE_CLIENTES + " WHERE " +
                COLUMN_USER_EMAIL + " = ?";
        try (SQLiteDatabase db = this.getReadableDatabase();
             Cursor cursor = db.rawQuery(query, new String[]{email})) {
            return cursor.moveToFirst();
        }
    }

    // Adicionar cliente
    public boolean addCliente(String name, String email, String password) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_USER_NAME, name);
            values.put(COLUMN_USER_EMAIL, email);
            values.put(COLUMN_USER_PASSWORD, password);
            return db.insert(TABLE_CLIENTES, null, values) != -1;
        }
    }

    // Verificar login de cliente
    public boolean checkCliente(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_USER_ID};
        String selection = COLUMN_USER_EMAIL + " = ? AND " + COLUMN_USER_PASSWORD + " = ?";
        String[] selectionArgs = {email, password};
        try (Cursor cursor = db.query(TABLE_CLIENTES, columns, selection, selectionArgs, null, null, null)) {
            return cursor.moveToFirst();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
