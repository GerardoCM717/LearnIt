package com.mmgl.pruebas;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ReadingGameActivity extends AppCompatActivity {
    private TextView storyText;
    private TextView instructionsText;
    private TextView scoreText;
    private Button[] responseButtons;
    private StoryViewModel storyViewModel;
    private int currentStoryIndex = 0;
    private int totalScore = 0;
    private int lifeCount = 3; // Número inicial de vidas

    private ImageView life1, life2, life3; // Referencias a los corazones (vidas)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading_game);

        // Inicializar vistas
        storyText = findViewById(R.id.storyText);
        instructionsText = findViewById(R.id.instructionsText);
        scoreText = findViewById(R.id.scoreText);

        // Corazones (vidas)
        life1 = findViewById(R.id.life1);
        life2 = findViewById(R.id.life2);
        life3 = findViewById(R.id.life3);

        // Configurar instrucciones en inglés
        instructionsText.setText("Welcome to Reading Comprehension Practice!\n" +
                "Read the story carefully and answer the questions.\n" +
                "Choose the best answer to improve your reading skills!");

        // Inicializar botones de respuesta
        responseButtons = new Button[4];
        responseButtons[0] = findViewById(R.id.response1);
        responseButtons[1] = findViewById(R.id.response2);
        responseButtons[2] = findViewById(R.id.response3);
        responseButtons[3] = findViewById(R.id.response4);

        // Inicializar ViewModel
        storyViewModel = new StoryViewModel();

        // Cargar primera historia y pregunta
        loadStory();

        // Configurar listeners de botones
        for (int i = 0; i < responseButtons.length; i++) {
            final int responseIndex = i;
            responseButtons[i].setOnClickListener(v -> checkAnswer(responseIndex));
        }
    }

    private void loadStory() {
        if (currentStoryIndex < storyViewModel.getStories().size()) {
            Story currentStory = storyViewModel.getStories().get(currentStoryIndex);

            storyText.setText(currentStory.getStory());
            String[] responses = currentStory.getPossibleResponses();
            for (int i = 0; i < responseButtons.length; i++) {
                responseButtons[i].setText(responses[i]);
            }

            // Actualizar puntuación
            scoreText.setText("Score: " + totalScore);
        }
    }

    private void checkAnswer(int selectedResponseIndex) {
        Story currentStory = storyViewModel.getStories().get(currentStoryIndex);

        if (selectedResponseIndex == currentStory.getCorrectResponseIndex()) {
            totalScore += 10;
        } else {
            totalScore -= 5; // Restar puntos si la respuesta es incorrecta
            reduceLife(); // Reducir una vida si la respuesta es incorrecta
        }

        currentStoryIndex++;

        if (currentStoryIndex < storyViewModel.getStories().size()) {
            loadStory();
        } else {
            showFinalScore();
        }
    }

    private void reduceLife() {
        if (lifeCount > 0) {
            lifeCount--;
            // Ocultar los corazones (vidas) conforme se pierden
            if (lifeCount == 2) {
                life3.setVisibility(View.INVISIBLE); // Eliminar el tercer corazón
            } else if (lifeCount == 1) {
                life2.setVisibility(View.INVISIBLE); // Eliminar el segundo corazón
            } else if (lifeCount == 0) {
                life1.setVisibility(View.INVISIBLE); // Eliminar el primer corazón
            }
        }

        if (lifeCount == 0) {
            showFinalScore();
        }
    }

    private void showFinalScore() {
        // Mostrar el puntaje final cuando se acaban las vidas
        new android.app.AlertDialog.Builder(this)
                .setTitle("Game Finished!")
                .setMessage("Your total score: " + totalScore + " points\n" +
                        "Great job practicing your reading skills!")
                .setPositiveButton("Play Again", (dialog, which) -> {
                    currentStoryIndex = 0;
                    totalScore = 0;
                    lifeCount = 3; // Restaurar vidas
                    life1.setVisibility(View.VISIBLE);
                    life2.setVisibility(View.VISIBLE);
                    life3.setVisibility(View.VISIBLE);
                    loadStory();
                })
                .setNegativeButton("Exit", (dialog, which) -> finish())
                .show();
    }
}
