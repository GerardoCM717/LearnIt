package com.mmgl.pruebas;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.text.InputFilter;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class CrosswordGameActivity extends AppCompatActivity {
    private EditText[][] cellGrid;
    private final int GRID_SIZE = 10;
    private int currentScore = 0;
    private int attempts = 0;
    private TextView scoreText;
    private TextView timerText;
    private long startTime;
    private Handler timerHandler = new Handler();
    private boolean gameCompleted = false;

    private static final int MAX_SCORE = 100;
    private static final int TIME_BONUS_MAX = 50;
    private static final int ATTEMPT_PENALTY = 10;
    private static final int INCORRECT_CELL_PENALTY = 5;
    private int lastScore = 0;

    private String[] words = {
            "RIVER",      // Horizontal
            "DESERT",     // Horizontal
            "HILL",       // Horizontal
            "SUN",        // Horizontal
            "LAGOON",     // Horizontal
            "MOUNTAIN",   // Vertical
            "VALLEY",     // Vertical
            "POND"        // Vertical
    };

    private Object[][] wordPositions = {
            {0, 0, true},   // RIVER (Horizontal, Fila 0, Columna 0)
            {4, 1, true},   // DESERT (Horizontal, Fila 4, Columna 1)
            {2, 0, true},   // HILL (Horizontal, Fila 2, Columna 0)
            {2, 5, true},   // SUN (Horizontal, Fila 2, Columna 5)
            {7, 1, true},   // LAGOON (Horizontal, Fila 7, Columna 1)
            {0, 6, false},  // MOUNTAIN (Vertical, Fila 0, Columna 6)
            {0, 2, false},  // VALLEY (Vertical, Fila 0, Columna 2)
            {6, 4, false}   // POND (Vertical, Fila 6, Columna 4)
    };

    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            timerText.setText(String.format("%02d:%02d", minutes, seconds));
            if (!gameCompleted) {
                timerHandler.postDelayed(this, 500);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crossword_game);

        scoreText = findViewById(R.id.scoreText);
        timerText = findViewById(R.id.timerText);

        initializeGrid();
        setupClues();
        startTimer();
        updateScoreDisplay();
    }

    private void initializeGrid() {
        GridLayout gridLayout = findViewById(R.id.gridLayout);
        gridLayout.setColumnCount(GRID_SIZE);
        gridLayout.setRowCount(GRID_SIZE);
        cellGrid = new EditText[GRID_SIZE][GRID_SIZE];

        // Calculate cell size based on screen width
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int cellSize = (screenWidth - 32) / GRID_SIZE; // 32dp of total padding

        // Create the grid cells
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                EditText cell = new EditText(this);
                cell.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1)}); // Limit to 1 character
                cell.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
                cell.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                cell.setTextColor(Color.WHITE);
                cell.setBackgroundResource(R.drawable.cell_background);
                cell.setInputType(InputType.TYPE_CLASS_TEXT);
                cell.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

                // Configure the layout for the cell
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = cellSize;
                params.height = cellSize;
                params.rowSpec = GridLayout.spec(i);
                params.columnSpec = GridLayout.spec(j);
                params.setMargins(2, 2, 2, 2);

                cell.setLayoutParams(params);
                cell.setPadding(0, 0, 0, 0);

                // If this cell should be locked (not part of any word)
                if (!isCellPartOfWord(i, j)) {
                    cell.setEnabled(false);
                    cell.setBackgroundColor(getResources().getColor(R.color.cellBackgroundDisabled));
                }

                cellGrid[i][j] = cell;
                gridLayout.addView(cell);
            }
        }
    }

    private boolean isCellPartOfWord(int row, int col) {
        for (int i = 0; i < words.length; i++) {
            int startRow = (int) wordPositions[i][0];
            int startCol = (int) wordPositions[i][1];
            boolean isHorizontal = (boolean) wordPositions[i][2];
            String word = words[i];

            if (isHorizontal) {
                if (row == startRow && col >= startCol && col < startCol + word.length()) {
                    return true;
                }
            } else {
                if (col == startCol && row >= startRow && row < startRow + word.length()) {
                    return true;
                }
            }
        }
        return false;
    }

    private void setupClues() {
        TextView cluesView = findViewById(R.id.cluesText);
        StringBuilder clues = new StringBuilder("Clues:\n\n");

        clues.append("Across:\n");
        clues.append("1. A flowing body of water (5 letters)\n");  // RIVER
        clues.append("2. A large, sandy, and dry area (6 letters)\n");  // DESERT
        clues.append("3. A small raised area of land (4 letters)\n");  // HILL
        clues.append("4. A bright star that gives us light and heat (3 letters)\n");  // SUN
        clues.append("5. A small, calm body of water (6 letters)\n");  // LAGOON

        clues.append("\nDown:\n");
        clues.append("1. A high elevation on Earth's surface (8 letters)\n");  // MOUNTAIN
        clues.append("2. A low area between hills or mountains (6 letters)\n");  // VALLEY
        clues.append("3. A small body of still water (4 letters)\n");  // POND

        cluesView.setText(clues.toString());
    }

    private void startTimer() {
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);
    }

    private void stopTimer() {
        timerHandler.removeCallbacks(timerRunnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        timerHandler.removeCallbacks(timerRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!gameCompleted) {
            timerHandler.postDelayed(timerRunnable, 0);
        }
    }

    private void updateScoreDisplay() {
        scoreText.setText("Score: " + currentScore);
    }

    private void updateScore(int correctWords, int incorrectCells) {
        // Calcula el score base según las palabras correctas
        float percentageComplete = (float) correctWords / words.length;
        int baseScore = (int) (MAX_SCORE * percentageComplete);

        // Calcula el bonus por tiempo
        int timeBonus = calculateTimeBonus();

        // Calcula las penalizaciones
        int attemptsPenalty = (attempts - 1) * ATTEMPT_PENALTY;
        int incorrectPenalty = incorrectCells * INCORRECT_CELL_PENALTY;

        // Calcula el score final
        int newScore = baseScore + timeBonus - attemptsPenalty - incorrectPenalty;

        // Asegúrate de que el score nunca sea menor que el anterior ni menor que 0
        currentScore = Math.max(Math.max(newScore, lastScore), 0);
        lastScore = currentScore;

        // Actualiza el display
        updateScoreDisplay();
    }

    private int calculateTimeBonus() {
        long currentTime = System.currentTimeMillis();
        int secondsElapsed = (int) ((currentTime - startTime) / 1000);
        // Bonus por tiempo: máximo TIME_BONUS_MAX puntos, se reduce en 1 punto cada 30 segundos
        return Math.max(0, TIME_BONUS_MAX - (secondsElapsed / 30));
    }

    public void checkAnswers(View view) {
        if (gameCompleted) {
            Toast.makeText(this, "¡Juego ya completado!", Toast.LENGTH_SHORT).show();
            return;
        }

        attempts++;
        boolean allCorrect = true;
        int correctWords = 0;
        int incorrectCells = 0;

        // Resetear colores de fondo
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (cellGrid[i][j].isEnabled()) {
                    cellGrid[i][j].setBackgroundResource(R.drawable.cell_background);
                }
            }
        }

        // Verificar cada palabra
        for (int wordIndex = 0; wordIndex < words.length; wordIndex++) {
            String word = words[wordIndex];
            int startRow = (int) wordPositions[wordIndex][0];
            int startCol = (int) wordPositions[wordIndex][1];
            boolean isHorizontal = (boolean) wordPositions[wordIndex][2];
            boolean wordCorrect = true;

            for (int i = 0; i < word.length(); i++) {
                int row = isHorizontal ? startRow : startRow + i;
                int col = isHorizontal ? startCol + i : startCol;

                String userInput = cellGrid[row][col].getText().toString().toUpperCase();
                if (userInput.isEmpty() || !userInput.equals(String.valueOf(word.charAt(i)))) {
                    wordCorrect = false;
                    allCorrect = false;
                    incorrectCells++;
                    cellGrid[row][col].setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
                }
            }

            if (wordCorrect) {
                correctWords++;
                // Marcar palabra correcta en verde
                for (int i = 0; i < word.length(); i++) {
                    int row = isHorizontal ? startRow : startRow + i;
                    int col = isHorizontal ? startCol + i : startCol;
                    cellGrid[row][col].setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
                }
            }
        }

        // Actualizar puntuación
        updateScore(correctWords, incorrectCells);

        if (allCorrect) {
            gameCompleted = true;
            stopTimer();
            String message = String.format("¡Felicitaciones!\nPalabras correctas: %d/%d\nPuntuación final: %d",
                    words.length, words.length, currentScore);
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        } else {
            String message = String.format("Palabras correctas: %d/%d\nIntentos: %d\n¡Sigue intentando!",
                    correctWords, words.length, attempts);
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }

}
