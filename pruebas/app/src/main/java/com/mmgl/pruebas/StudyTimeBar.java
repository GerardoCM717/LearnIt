package com.mmgl.pruebas;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class StudyTimeBar extends LinearLayout {

    private ProgressBar progressBar;
    private TextView timeText;
    private SeekBar seekBar;
    private int totalStudyTime;  // Tiempo total de estudio en minutos
    private int currentStudyTime;  // Tiempo actual de estudio en minutos

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "StudyTimePrefs";

    private Handler handler;
    private Runnable timerRunnable;

    public StudyTimeBar(Context context) {
        super(context);
        init(context);
    }

    public StudyTimeBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        // Inflar el layout de la vista personalizada
        inflate(context, R.layout.study_time_bar, this);

        // Inicializar vistas
        progressBar = findViewById(R.id.progressBar);
        timeText = findViewById(R.id.timeText);
        seekBar = findViewById(R.id.seekBar);

        // Inicializar SharedPreferences
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Obtener el tiempo guardado en SharedPreferences
        currentStudyTime = sharedPreferences.getInt("currentStudyTime", 0);

        // Establecer el tiempo máximo que el usuario puede seleccionar (por ejemplo, 120 minutos)
        totalStudyTime = 120;  // Puedes personalizar este valor

        seekBar.setMax(totalStudyTime);
        seekBar.setProgress(currentStudyTime);

        updateProgress();

        // Establecer el listener para el SeekBar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    // Guardar el tiempo de estudio seleccionado en SharedPreferences
                    currentStudyTime = progress;
                    sharedPreferences.edit().putInt("currentStudyTime", currentStudyTime).apply();
                    updateProgress();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Iniciar el temporizador que resta minutos cada minuto
        startTimer();
    }

    private void updateProgress() {
        // Actualiza el ProgressBar y el texto de tiempo
        progressBar.setProgress(currentStudyTime);
        timeText.setText(String.format("%d / %d minutos", currentStudyTime, totalStudyTime));
    }

    // Iniciar el temporizador
    private void startTimer() {
        handler = new Handler(Looper.getMainLooper());

        timerRunnable = new Runnable() {
            @Override
            public void run() {
                // Decrementar el tiempo de estudio cada minuto
                if (currentStudyTime > 0) {
                    currentStudyTime--;
                    sharedPreferences.edit().putInt("currentStudyTime", currentStudyTime).apply();
                    updateProgress();
                }
                // Repetir el timer cada minuto
                handler.postDelayed(this, 60000);
            }
        };

        // Iniciar el timer (al abrir la pantalla o la vista)
        handler.post(timerRunnable);
    }

    @Override
    protected void onDetachedFromWindow() {
        // Detener el temporizador cuando la vista se destruye o se navega fuera
        super.onDetachedFromWindow();
        if (handler != null) {
            handler.removeCallbacks(timerRunnable);
        }
    }
}
