package com.mmgl.pruebas;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class BasicPhrasesActivity extends AppCompatActivity {
    private List<BasicPhraseExercise> exercises;
    private int currentExerciseIndex = 0;
    private int score = 0;
    private int lives = 3;
    private TextView questionTextView;
    private Button option1Button, option2Button, option3Button, nextButton;
    private ProgressBar progressBar;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_phrases);

        // Inicializar vistas
        questionTextView = findViewById(R.id.questionTextView);
        option1Button = findViewById(R.id.option1Button);
        option2Button = findViewById(R.id.option2Button);
        option3Button = findViewById(R.id.option3Button);
        nextButton = findViewById(R.id.nextButton);
        progressBar = findViewById(R.id.progressBar);
        sharedPreferences = getSharedPreferences("LearnItPrefs", MODE_PRIVATE);

        // Cargar ejercicios y mostrar el primero
        loadExercises();
        displayCurrentExercise();

        // Configurar listener para avanzar al siguiente ejercicio
        nextButton.setOnClickListener(v -> goToNextExercise());

        // Configurar listeners para las opciones
        option1Button.setOnClickListener(v -> checkAnswer(0));
        option2Button.setOnClickListener(v -> checkAnswer(1));
        option3Button.setOnClickListener(v -> checkAnswer(2));
    }

    private void loadExercises() {
        exercises = new ArrayList<>();
        exercises.add(new BasicPhraseExercise("How do you say 'Hola' in English?",
                new String[]{"Hello", "Goodbye", "Please"}, 0));
        exercises.add(new BasicPhraseExercise("What does 'Thank you' mean in Spanish?",
                new String[]{"Gracias", "Por favor", "Adiós"}, 0));
        exercises.add(new BasicPhraseExercise("Translate 'Good Morning' into Spanish.",
                new String[]{"Buenos Días", "Buenas Tardes", "Buenas Noches"}, 0));
        exercises.add(new BasicPhraseExercise("What is the English word for 'Gracias'?",
                new String[]{"Please", "Sorry", "Thank you"}, 2));
        exercises.add(new BasicPhraseExercise("How do you say 'Perdón' in English?",
                new String[]{"Please", "Sorry", "Excuse me"}, 1));
        exercises.add(new BasicPhraseExercise("Translate 'Good Night' into Spanish.",
                new String[]{"Buenas Tardes", "Buenos Días", "Buenas Noches"}, 2));
        exercises.add(new BasicPhraseExercise("What does 'I need help' mean in Spanish?",
                new String[]{"Necesito ayuda", "Hola", "Gracias"}, 0));
        exercises.add(new BasicPhraseExercise("Translate 'Excuse me' into Spanish.",
                new String[]{"Por favor", "Perdón", "Disculpa"}, 2));
        exercises.add(new BasicPhraseExercise("How do you say 'Adiós' in English?",
                new String[]{"Hello", "Goodbye", "Thank you"}, 1));
        exercises.add(new BasicPhraseExercise("Translate 'I am sorry' into Spanish.",
                new String[]{"Estoy cansado", "Estoy perdido", "Lo siento"}, 2));
    }

    private void displayCurrentExercise() {
        if (currentExerciseIndex < exercises.size()) {
            BasicPhraseExercise currentExercise = exercises.get(currentExerciseIndex);
            questionTextView.setText(currentExercise.getQuestion());
            option1Button.setText(currentExercise.getOptions()[0]);
            option2Button.setText(currentExercise.getOptions()[1]);
            option3Button.setText(currentExercise.getOptions()[2]);
            progressBar.setProgress(currentExerciseIndex * 100 / exercises.size());
            updateLivesDisplay(); // Llamar al método para actualizar la visualización de vidas
        } else {
            showCompletionMessage();
        }
    }

    private void checkAnswer(int selectedOption) {
        BasicPhraseExercise currentExercise = exercises.get(currentExerciseIndex);
        if (currentExercise.getCorrectAnswerIndex() == selectedOption) {
            score++;
            Toast.makeText(this, "✅ Correct ✅", Toast.LENGTH_SHORT).show();
        } else {
            lives--;
            Toast.makeText(this, "❗Incorrect❗", Toast.LENGTH_SHORT).show();
            if (lives <= 0) {
                showGameOverDialog();
                return;
            }
        }
        goToNextExercise();
    }

    private void goToNextExercise() {
        if (currentExerciseIndex < exercises.size()) {
            currentExerciseIndex++;
            displayCurrentExercise();
        }
    }

    private void showGameOverDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Game Over");
        builder.setMessage("You've lost all your lives. Would you like to try again?");
        builder.setPositiveButton("Retry", (dialog, which) -> resetGame());
        builder.setNegativeButton("Exit", (dialog, which) -> finish());
        builder.setCancelable(false);
        builder.show();
    }

    private void resetGame() {
        currentExerciseIndex = 0;
        score = 0;
        lives = 3;
        displayCurrentExercise();
    }

    private void showCompletionMessage() {
        Toast.makeText(this, "Congratulations! You've completed all the exercises.", Toast.LENGTH_LONG).show();
        sharedPreferences.edit().putInt("BasicPhrasesScore", score).apply();
    }

    private void updateLivesDisplay() {
        LinearLayout livesLayout = findViewById(R.id.livesLayout);
        livesLayout.removeAllViews(); // Limpiar cualquier vista existente

        for (int i = 0; i < lives; i++) {
            ImageView heartImageView = new ImageView(this);
            heartImageView.setImageResource(R.drawable.heart_pixel); // Cambia esto por el nombre de tu recurso
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    (int) getResources().getDimension(R.dimen.heart_size), // Tamaño del corazón
                    (int) getResources().getDimension(R.dimen.heart_size));
            params.setMargins(4, 0, 4, 0); // Margen entre los corazones
            heartImageView.setLayoutParams(params);
            livesLayout.addView(heartImageView);
        }
    }
}
