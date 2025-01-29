package com.mmgl.pruebas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class GamesActivity extends AppCompatActivity {

    private CardView wordGameCard, quizGameCard, memoryGameCard, crosswordGameCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Referencias a los CardViews de cada juego
        wordGameCard = findViewById(R.id.card_word_game);
        quizGameCard = findViewById(R.id.card_quiz_game);
        memoryGameCard = findViewById(R.id.card_memory_game);
        crosswordGameCard = findViewById(R.id.card_crossword_game);

        // Configuración de intents para cada CardView
        wordGameCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Llamar a WordsActivity cuando se hace clic en el CardView de Word Game
                startActivity(new Intent(GamesActivity.this, WordsActivity.class));
            }
        });

        quizGameCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 startActivity(new Intent(GamesActivity.this, PronunciationGameActivity.class));
            }
        });

        memoryGameCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GamesActivity.this, MemoryGameActivity.class));
            }
        });

        crosswordGameCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GamesActivity.this, CrosswordGameActivity.class));
            }
        });
    }
}
