package com.mmgl.pruebas;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PhraseGuessActivity extends AppCompatActivity {
    private TextView gameIntroText, scoreText, timerText;
    private LinearLayout spanishContainer, englishContainer;
    private ProgressView progressView;

    private List<Phrase> phrasesList;
    private int score = 0;
    private CountDownTimer gameTimer;

    private Button selectedSpanishButton = null;
    private Button selectedEnglishButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phrase_guess);

        initializeViews();
        initializePhrases();
        startGame();
    }

    private void initializeViews() {
        gameIntroText = findViewById(R.id.gameIntroText);
        scoreText = findViewById(R.id.scoreText);
        timerText = findViewById(R.id.timerText);
        spanishContainer = findViewById(R.id.spanishContainer);
        englishContainer = findViewById(R.id.englishContainer);
        progressView = findViewById(R.id.progressView);
    }

    private void initializePhrases() {
        phrasesList = new ArrayList<>();
        phrasesList.add(new Phrase("aeropuerto", "airport"));
        phrasesList.add(new Phrase("pasaporte", "passport"));
        phrasesList.add(new Phrase("hotel", "hotel"));
        phrasesList.add(new Phrase("restaurante", "restaurant"));
        phrasesList.add(new Phrase("equipaje", "luggage"));
        phrasesList.add(new Phrase("taxi", "taxi"));
        phrasesList.add(new Phrase("tren", "train"));
        phrasesList.add(new Phrase("boleto", "ticket"));
        phrasesList.add(new Phrase("mapa", "map"));
        phrasesList.add(new Phrase("playa", "beach"));
    }

    private void startGame() {
        score = 0;
        scoreText.setText("Score: 0");
        progressView.setProgress(0);
        progressView.setEnabled(true);
        startGameTimer();
        loadQuestion();
    }

    private void loadQuestion() {
        spanishContainer.removeAllViews();
        englishContainer.removeAllViews();

        Collections.shuffle(phrasesList);

        List<Button> spanishButtons = new ArrayList<>();
        List<Button> englishButtons = new ArrayList<>();

        for (Phrase phrase : phrasesList) {
            spanishButtons.add(createButton(phrase.getSpanish(), true));
            englishButtons.add(createButton(phrase.getEnglish(), false));
        }

        Collections.shuffle(spanishButtons);
        Collections.shuffle(englishButtons);

        for (Button spanishButton : spanishButtons) {
            spanishContainer.addView(spanishButton);
        }
        for (Button englishButton : englishButtons) {
            englishContainer.addView(englishButton);
        }
    }

    private Button createButton(String text, boolean isSpanish) {
        Button button = new Button(this);
        button.setText(text);
        button.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        button.setOnClickListener(v -> handleButtonClick((Button) v, isSpanish));
        return button;
    }

    private void handleButtonClick(Button button, boolean isSpanish) {
        if (isSpanish) {
            if (selectedSpanishButton != null) {
                selectedSpanishButton.setBackgroundColor(0);
            }
            selectedSpanishButton = button;
        } else {
            if (selectedEnglishButton != null) {
                selectedEnglishButton.setBackgroundColor(0);
            }
            selectedEnglishButton = button;
        }

        button.setBackgroundColor(0xFFCCCCCC);

        if (selectedSpanishButton != null && selectedEnglishButton != null) {
            checkMatch();
        }
    }

    private void checkMatch() {
        boolean isCorrect = phrasesList.stream()
                .anyMatch(p -> p.getSpanish().equals(selectedSpanishButton.getText()) &&
                        p.getEnglish().equals(selectedEnglishButton.getText()));

        if (isCorrect) {
            selectedSpanishButton.setVisibility(View.INVISIBLE);
            selectedEnglishButton.setVisibility(View.INVISIBLE);
            score += 10;
            scoreText.setText("Score: " + score);
            progressView.incrementProgress(10);

            if (checkIfGameCompleted()) {
                endGame("Complete all the words! Great job!");
            }
        } else {
            selectedSpanishButton.setBackgroundColor(0xFFFFCCCC);
            selectedEnglishButton.setBackgroundColor(0xFFFFCCCC);
        }

        resetSelections();
    }

    private boolean checkIfGameCompleted() {
        for (int i = 0; i < spanishContainer.getChildCount(); i++) {
            if (spanishContainer.getChildAt(i).getVisibility() == View.VISIBLE) {
                return false;
            }
        }
        return true;
    }

    private void resetSelections() {
        selectedSpanishButton = null;
        selectedEnglishButton = null;
    }

    private void endGame(String message) {
        if (gameTimer != null) gameTimer.cancel();
        progressView.setEnabled(false);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("¡Game Over!")
                .setMessage(message + "\nFinal Score: " + score)
                .setPositiveButton("Tyr Again", (dialog, which) -> startGame())
                .setNegativeButton("Exit", (dialog, which) -> finish())
                .show();
    }

    private void startGameTimer() {
        gameTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerText.setText("Time: " + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                endGame("Time's up! Try again.");
            }
        }.start();
    }
}
