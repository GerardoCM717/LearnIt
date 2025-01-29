package com.mmgl.pruebas;

import android.content.ClipData;
import android.content.ClipDescription;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.DragEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WordsActivity extends AppCompatActivity {
    private LinearLayout wordContainer;
    private LinearLayout letterContainer;
    private TextView scoreTextView;
    private TextView levelTextView;
    private TextView timerTextView;
    private TextView livesTextView;
    private int currentLevel = 0;
    private int score = 0;
    private int lives = 3;
    private StringBuilder currentWord = new StringBuilder();
    private CountDownTimer timer;
    private static final long TIMER_DURATION = 60000; // 60 segundos

    private final String[] words = {
            "APPLE",
            "HOUSE",
            "BEACH",
            "BIRTHDAY",
            "SNAKE"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_words);

        wordContainer = findViewById(R.id.wordContainer);
        letterContainer = findViewById(R.id.letterContainer);
        scoreTextView = findViewById(R.id.scoreTextView);
        levelTextView = findViewById(R.id.levelTextView);
        timerTextView = findViewById(R.id.timerTextView);
        livesTextView = findViewById(R.id.livesTextView);

        setupNewLevel();
    }

    private void startTimer() {
        if (timer != null) {
            timer.cancel();
        }

        timer = new CountDownTimer(TIMER_DURATION, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerTextView.setText(String.format("Time: %d s", millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                loseLife();
            }
        }.start();
    }

    private void loseLife() {
        lives--;
        updateLivesDisplay();

        if (lives <= 0) {
            showGameOverDialog();
        } else {
            Toast.makeText(this, "You lost a life! " + lives + " remaining", Toast.LENGTH_SHORT).show();
            setupNewLevel();
        }
    }

    private void updateLivesDisplay() {
        livesTextView.setText("Lives: " + lives);
    }

    private void showGameOverDialog() {
        if (timer != null) {
            timer.cancel();
        }

        new AlertDialog.Builder(this)
                .setTitle("Game Over")
                .setMessage("Final Score: " + score)
                .setPositiveButton("Try Again", (dialog, which) -> {
                    lives = 3;
                    score = 0;
                    currentLevel = 0;
                    setupNewLevel();
                })
                .setNegativeButton("Exit", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    private void showVictoryDialog() {
        if (timer != null) {
            timer.cancel();
        }

        new AlertDialog.Builder(this)
                .setTitle("Congratulations!")
                .setMessage("You completed all levels!\nFinal Score: " + score)
                .setPositiveButton("Play Again", (dialog, which) -> {
                    lives = 3;
                    score = 0;
                    currentLevel = 0;
                    setupNewLevel();
                })
                .setNegativeButton("Exit", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    private void setupNewLevel() {
        if (currentLevel >= words.length) {
            showVictoryDialog();
            return;
        }

        levelTextView.setText("Level " + (currentLevel + 1));
        scoreTextView.setText("Score: " + score);
        updateLivesDisplay();

        wordContainer.removeAllViews();
        letterContainer.removeAllViews();
        currentWord.setLength(0);

        setupWordSpaces();
        setupDraggableLetters();
        setupDragAndDrop();
        startTimer();
    }

    private void setupWordSpaces() {
        String targetWord = words[currentLevel];
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(8, 8, 8, 8);

        for (int i = 0; i < targetWord.length(); i++) {
            TextView spaceView = new TextView(this);
            spaceView.setText("_");
            spaceView.setTextSize(30);
            spaceView.setPadding(16, 16, 16, 16);
            spaceView.setTextColor(Color.GRAY);
            spaceView.setBackgroundResource(R.drawable.word_space_background);
            spaceView.setLayoutParams(params);
            wordContainer.addView(spaceView);
        }
    }

    private void setupDraggableLetters() {
        List<String> letters = new ArrayList<>();
        for (char c : words[currentLevel].toCharArray()) {
            letters.add(String.valueOf(c));
        }

        String extras = "XYZWQT";
        for (int i = 0; i < 3; i++) {
            letters.add(String.valueOf(extras.charAt(i)));
        }
        Collections.shuffle(letters);

        // Colores más vibrantes y visibles
        int[] letterColors = new int[]{
                Color.rgb(255, 87, 34),    // Naranja profundo
                Color.rgb(156, 39, 176),   // Morado
                Color.rgb(3, 169, 244),    // Azul brillante
                Color.rgb(139, 195, 74),   // Verde lima
                Color.rgb(233, 30, 99),    // Rosa
                Color.rgb(0, 150, 136)     // Verde azulado
        };

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(8, 8, 8, 8);

        int colorIndex = 0;
        for (String letter : letters) {
            TextView letterView = new TextView(this);
            letterView.setText(letter);
            letterView.setTextSize(24);
            letterView.setPadding(16, 16, 16, 16);
            letterView.setBackgroundColor(letterColors[colorIndex % letterColors.length]);
            letterView.setTextColor(Color.WHITE);  // Texto en blanco
            letterView.setLayoutParams(params);
            letterView.setElevation(4f);

            // Agregar borde negro para mayor contraste
            GradientDrawable shape = new GradientDrawable();
            shape.setColor(letterColors[colorIndex % letterColors.length]);
            shape.setCornerRadius(8);
            shape.setStroke(2, Color.BLACK);
            letterView.setBackground(shape);

            letterView.setOnLongClickListener(v -> {
                ClipData.Item item = new ClipData.Item(letter);
                ClipData dragData = new ClipData(
                        letter,
                        new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN},
                        item
                );
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                v.startDragAndDrop(dragData, shadowBuilder, letterView, 0);
                return true;
            });

            letterContainer.addView(letterView);
            colorIndex++;
        }
    }

    private void setupDragAndDrop() {
        View.OnDragListener dragListener = (v, event) -> {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    return event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN);

                case DragEvent.ACTION_DROP:
                    TextView droppedLetter = (TextView) event.getLocalState();
                    String letter = droppedLetter.getText().toString();

                    if (currentWord.length() < words[currentLevel].length()) {
                        TextView space = (TextView) wordContainer.getChildAt(currentWord.length());
                        space.setText(letter);
                        space.setTextColor(Color.BLACK);
                        droppedLetter.setVisibility(View.INVISIBLE);
                        currentWord.append(letter);

                        if (currentWord.length() == words[currentLevel].length()) {
                            checkWord();
                        }
                    }
                    return true;

                case DragEvent.ACTION_DRAG_ENDED:
                    return true;

                default:
                    return true;
            }
        };

        wordContainer.setOnDragListener(dragListener);
    }

    private void checkWord() {
        if (currentWord.toString().equals(words[currentLevel])) {
            // Resaltar la palabra correcta
            for (int i = 0; i < wordContainer.getChildCount(); i++) {
                TextView letterView = (TextView) wordContainer.getChildAt(i);
                letterView.setBackgroundColor(Color.GREEN);
                letterView.setTextColor(Color.WHITE);
            }

            score += 100;
            Toast.makeText(this, "Correct! +100 points", Toast.LENGTH_SHORT).show();
            currentLevel++;

            wordContainer.postDelayed(this::setupNewLevel, 1000);
        } else {
            loseLife();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }
}