package com.mmgl.pruebas;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private SharedPreferences sharedPreferences;

    // UI Components
    private TextView tvUsername, tvUserLevel, tvCurrentLanguage, tvCurrentDifficulty;
    private Switch switchNotifications;
    private Button btnLogout;
    private LinearLayout layoutLanguageSetting, layoutDifficultySetting;
    private LinearLayout layoutStudyTimeBar;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        // Inicializar el layout que contiene el StudyTimeBar
        layoutStudyTimeBar = findViewById(R.id.layoutStudyTimeBar);

        // Crear una instancia de StudyTimeBar
        StudyTimeBar studyTimeBar = new StudyTimeBar(this);

        // Agregar StudyTimeBar al layout
        layoutStudyTimeBar.addView(studyTimeBar);

        // Inicializar base de datos y shared preferences
        db = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("LearnItPrefs", MODE_PRIVATE);

        // Inicializar vistas
        initializeViews();

        // Cargar datos de usuario
        loadUserData();

        // Configurar listeners
        setupListeners();


    }

    private void initializeViews() {
        tvUsername = findViewById(R.id.tvUsername);
        tvUserLevel = findViewById(R.id.tvUserLevel);
        tvCurrentLanguage = findViewById(R.id.tvCurrentLanguage);
        tvCurrentDifficulty = findViewById(R.id.tvCurrentDifficulty);
        switchNotifications = findViewById(R.id.switchNotifications);
        btnLogout = findViewById(R.id.btnLogout);
        layoutLanguageSetting = findViewById(R.id.layoutLanguageSetting);
        layoutDifficultySetting = findViewById(R.id.layoutDifficultySetting);
    }

    private void loadUserData() {
        // Intentar obtener username de SharedPreferences
        String currentUsername = sharedPreferences.getString("CURRENT_USERNAME", null);
        Log.d("SettingsActivity", "Username from SharedPreferences: " + currentUsername);

        // Si no hay username en SharedPreferences, intentar de la base de datos
        if (currentUsername == null || currentUsername.isEmpty()) {
            currentUsername = db.getUsernameFromCurrentSession();
            Log.d("SettingsActivity", "Username from Database: " + currentUsername);
        }

        // Establecer username
        if (currentUsername != null && !currentUsername.isEmpty()) {
            tvUsername.setText(currentUsername);

            // Guardar en SharedPreferences para futuras consultas
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("CURRENT_USERNAME", currentUsername);
            editor.apply();
        } else {
            tvUsername.setText("Usuario Desconocido");
            Log.e("SettingsActivity", "No se pudo recuperar el nombre de usuario");
        }

        // Cargar configuraciones guardadas
        tvCurrentLanguage.setText(getCurrentLanguageName());
        tvCurrentDifficulty.setText(getCurrentDifficulty());

        // Configuración de notificaciones
        boolean notificationsEnabled = sharedPreferences.getBoolean("NOTIFICATIONS_ENABLED", true);
        switchNotifications.setChecked(notificationsEnabled);
    }

    private void setupListeners() {
        // Cambiar idioma
        layoutLanguageSetting.setOnClickListener(v -> showLanguageDialog());

        // Cambiar dificultad
        layoutDifficultySetting.setOnClickListener(v -> showDifficultyDialog());

        // Configuración de notificaciones
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("NOTIFICATIONS_ENABLED", isChecked);
            editor.apply();
            Toast.makeText(this, isChecked ? "Notificaciones activadas" : "Notificaciones desactivadas", Toast.LENGTH_SHORT).show();
        });

        // Botón de cerrar sesión
        btnLogout.setOnClickListener(v -> {
            // Limpiar la sesión actual
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("CURRENT_USERNAME");
            editor.apply();

            // Redirigir a la pantalla de login
            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void showLanguageDialog() {
        // Implementar diálogo de selección de idioma
        String[] languages = {"Español", "Inglés"};
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Seleccionar Idioma")
                .setItems(languages, (dialog, which) -> {
                    String selectedLanguage = languages[which];
                    tvCurrentLanguage.setText(selectedLanguage);

                    // Guardar preferencia de idioma
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("APP_LANGUAGE", selectedLanguage);
                    editor.apply();

                    // Opcional: Cambiar idioma de la app
                    setAppLanguage(selectedLanguage);
                });
        builder.create().show();
    }

    private void showDifficultyDialog() {
        String[] difficulties = {"Principiante", "Intermedio", "Avanzado"};
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Seleccionar Nivel de Dificultad")
                .setItems(difficulties, (dialog, which) -> {
                    String selectedDifficulty = difficulties[which];
                    tvCurrentDifficulty.setText(selectedDifficulty);

                    // Guardar preferencia de dificultad
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("APP_DIFFICULTY", selectedDifficulty);
                    editor.apply();
                });
        builder.create().show();
    }

    private void setAppLanguage(String language) {
        Locale locale = language.equals("Inglés") ? Locale.ENGLISH : new Locale("es");
        Locale.setDefault(locale);
        // Nota: Para implementación completa, necesitarías configurar la configuración de recursos
    }

    private String getCurrentLanguageName() {
        return sharedPreferences.getString("APP_LANGUAGE", "Español");
    }

    private String getCurrentDifficulty() {
        return sharedPreferences.getString("APP_DIFFICULTY", "Intermedio");
    }
}