package com.mmgl.pruebas;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class VocabularyActivity extends AppCompatActivity {

    // Orden de categorías exactamente igual al layout XML
    private String[] categories = {
            "Animals",   // categoryLayout1
            "Fruits",    // categoryLayout2
            "Food",      // categoryLayout3
            "Clothes",   // categoryLayout4
            "Transport"  // categoryLayout5
    };

    private ArrayList<String> remainingWords;
    private int totalWords;
    private String currentWord;
    private HashMap<String, String> wordToCategory;

    private int correctAnswers = 0;
    private TextView wordTextView;
    private LinearLayout[] categoryLayouts;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocabulary);

        wordTextView = findViewById(R.id.wordTextView);
        categoryLayouts = new LinearLayout[categories.length];
        progressBar = findViewById(R.id.progressBar);

        // Inicializar el mapeo de palabras y sus categorías
        initializeWordCategories();

        // Inicializar la lista de palabras aleatorias
        initializeWords();

        // Inicializar los layouts de categorías
        for (int i = 0; i < categories.length; i++) {
            int resID = getResources().getIdentifier("categoryLayout" + (i + 1), "id", getPackageName());
            categoryLayouts[i] = findViewById(resID);

            final String category = categories[i];
            categoryLayouts[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkAnswer(category);
                }
            });
        }

        // Cargar la primera palabra
        loadNextWord();
    }

    private void initializeWordCategories() {
        wordToCategory = new HashMap<>();

        // Animals (categoryLayout1)
        addWordsToCategory("Animals", Arrays.asList("Dog", "Cat"));

        // Fruits (categoryLayout2)
        addWordsToCategory("Fruits", Arrays.asList("Apple", "Banana", "Strawberry"));

        // Food (categoryLayout3)
        addWordsToCategory("Food", Arrays.asList("Pizza", "Taco", "Salad", "Soup", "Bread", "Hamburger"));

        // Clothes (categoryLayout4)
        addWordsToCategory("Clothes", Arrays.asList("Shirt", "Pants", "Shoes"));

        // Transport (categoryLayout5)
        addWordsToCategory("Transport", Arrays.asList("Car", "Bicycle", "Airplane"));
    }

    private void addWordsToCategory(String category, List<String> words) {
        for (String word : words) {
            wordToCategory.put(word, category);
        }
    }

    private void initializeWords() {
        remainingWords = new ArrayList<>(wordToCategory.keySet());
        totalWords = remainingWords.size();
        Collections.shuffle(remainingWords);
    }

    private void loadNextWord() {
        if (!remainingWords.isEmpty()) {
            currentWord = remainingWords.remove(0);
            wordTextView.setText(currentWord);
            updateProgressBar();
        } else {
            String message = String.format("¡Felicitaciones! Completaste %d de %d palabras correctamente",
                    correctAnswers, totalWords);
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void checkAnswer(String selectedCategory) {
        String correctCategory = wordToCategory.get(currentWord);

        if (correctCategory != null && correctCategory.equals(selectedCategory)) {
            correctAnswers++;
            Toast.makeText(this, "✅ Correcto ✅", Toast.LENGTH_SHORT).show();
            loadNextWord();
        } else {
            String message = String.format("❗Incorrecto❗");
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    private void updateProgressBar() {
        int progress = (int) (((totalWords - remainingWords.size()) / (float) totalWords) * 100);
        progressBar.setProgress(progress);
    }
}