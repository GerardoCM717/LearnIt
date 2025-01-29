package com.mmgl.pruebas;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MenuActivity extends AppCompatActivity {

    // Declaración de las CardViews
    private CardView cardLecciones, cardJuegos, cardProgreso, cardConfiguracion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activiy_menu);

        // Inicializar las CardViews
        cardLecciones = findViewById(R.id.cardLecciones);
        cardJuegos = findViewById(R.id.cardJuegos);
        cardProgreso = findViewById(R.id.cardHockey);
        cardConfiguracion = findViewById(R.id.cardConfiguracion);

        // Configurar eventos de clic para cada CardView
        cardLecciones.setOnClickListener(v -> openActivity(LessonsActivity.class));
        cardJuegos.setOnClickListener(v -> openActivity(GamesActivity.class));
        cardProgreso.setOnClickListener(v -> openActivity(HockeyActivity.class));
        cardConfiguracion.setOnClickListener(v -> openActivity(SettingsActivity.class));

        // Habilitar el botón de "Atrás" en la barra de acciones
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    // Método genérico para abrir actividades
    private void openActivity(Class<?> activityClass) {
        Intent intent = new Intent(MenuActivity.this, activityClass);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Regresa a la actividad principal al presionar "Atrás" en la barra
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        // Evita que el usuario vuelva a la pantalla anterior si `MenuActivity` es el menú principal
        finishAffinity();
    }
}
