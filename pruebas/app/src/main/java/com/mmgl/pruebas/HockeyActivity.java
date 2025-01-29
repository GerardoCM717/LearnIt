package com.mmgl.pruebas;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.TextView;

public class HockeyActivity extends Activity {
    private GameView gameView;
    private TextView scoreText;
    private TextView timerText;
    private Handler handler;
    private int gameTimeInSeconds = 180; // 3 minutos
    private boolean isGameRunning = false;
    private boolean isGameStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configurar pantalla completa
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        // Establecer el layout
        setContentView(R.layout.acticity_hockey);

        // Inicializar vistas
        initializeViews();

        // Configurar el listener del estado del juego
        gameView.setGameStateListener(new GameView.GameStateListener() {
            @Override
            public void onGameStart() {
                isGameStarted = true;
                startGame();
            }

            @Override
            public void onGameEnd(int winner) {
                showGameOverDialog(winner);
            }
        });

        // Ya no necesitamos llamar a startGame() aquí porque el juego
        // iniciará cuando ambos jugadores estén listos
    }

    private void initializeViews() {
        gameView = findViewById(R.id.gameView);
        scoreText = findViewById(R.id.scoreText);
        timerText = findViewById(R.id.timerText);
        handler = new Handler();

        // Configurar listener para actualización de puntaje
        gameView.setScoreUpdateListener(new GameView.ScoreUpdateListener() {
            @Override
            public void onScoreUpdate(int player1Score, int player2Score) {
                scoreText.setText(String.format("%d - %d", player1Score, player2Score));
            }
        });
    }

    private void startGame() {
        isGameRunning = true;
        gameTimeInSeconds = 180; // 3 minutos
        timerText.setText("03:00");
        updateTimer();
    }

    private void showGameOverDialog(int winner) {
        String message;
        if (winner == 1) {
            message = "¡Jugador 1 ha ganado!";
        } else if (winner == 2) {
            message = "¡Jugador 2 ha ganado!";
        } else {
            message = "¡Empate!";
        }

        new AlertDialog.Builder(this)
                .setTitle("Fin del Juego")
                .setMessage(message)
                .setPositiveButton("Nuevo Juego", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        resetGame();
                    }
                })
                .setNegativeButton("Salir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setCancelable(false)
                .show();
    }

    private void resetGame() {
        isGameStarted = false;
        isGameRunning = false;
        gameTimeInSeconds = 180;
        gameView.resetGame();
        scoreText.setText("0 - 0");
        timerText.setText("03:00");
    }


    private void updateTimer() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isGameRunning && gameTimeInSeconds > 0) {
                    gameTimeInSeconds--;
                    int minutes = gameTimeInSeconds / 60;
                    int seconds = gameTimeInSeconds % 60;
                    timerText.setText(String.format("%02d:%02d", minutes, seconds));
                    handler.postDelayed(this, 1000);
                } else if (gameTimeInSeconds <= 0) {
                    endGame();
                }
            }
        }, 1000);
    }

    private void endGame() {
        isGameRunning = false;
        gameView.endGame();
        // Mostrar diálogo de fin de juego
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isGameStarted) {
            isGameRunning = false;
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isGameStarted && gameTimeInSeconds > 0) {
            isGameRunning = true;
            updateTimer();
        }
    }
}