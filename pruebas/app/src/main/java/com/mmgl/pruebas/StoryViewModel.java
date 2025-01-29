package com.mmgl.pruebas;

import java.util.ArrayList;
import java.util.List;

public class StoryViewModel {
    private List<Story> stories;
    private int lives;

    public StoryViewModel() {
        stories = new ArrayList<>();
        lives = 3; // Inicializamos con 3 vidas
        initializeStories();
    }

    private void initializeStories() {
        stories.add(new Story(
                "John was walking in the park when he saw a dog stuck in a bush. " +
                        "He decided to help the dog and took it to a shelter. " +
                        "The dog was adopted by a family the next day.",
                new String[]{
                        "John ignored the dog.",
                        "John helped the dog and took it to a shelter.",
                        "John took the dog home.",
                        "John called the police."
                },
                1
        ));

        stories.add(new Story(
                "Emma loves baking. She wakes up early every morning to prepare bread and cakes for her small bakery. " +
                        "Her customers love her chocolate cake the most.",
                new String[]{
                        "Emma loves gardening.",
                        "Emma works in a supermarket.",
                        "Emma bakes bread and cakes every morning.",
                        "Emma dislikes baking."
                },
                2
        ));

        stories.add(new Story(
                "A new library opened in the city center. It has thousands of books, and a special corner for children. " +
                        "Many people visit it every weekend to read and study.",
                new String[]{
                        "The library has a special corner for children.",
                        "The library is located in a school.",
                        "Nobody visits the library.",
                        "The library only has old books."
                },
                0
        ));

        // Nuevas historias
        stories.add(new Story(
                "Sophia loves playing soccer. One day, while playing with her friends, she accidentally kicked the ball into the neighbor's garden. " +
                        "She apologized and promised to be more careful.",
                new String[]{
                        "Sophia loves playing soccer.",
                        "Sophia broke the neighbor's window.",
                        "Sophia ignored her friends.",
                        "Sophia doesn't like sports."
                },
                0
        ));

        stories.add(new Story(
                "Lucas was traveling to the mountains. He forgot to bring his jacket, and it started raining. " +
                        "Luckily, he found shelter in a small cabin and waited for the rain to stop.",
                new String[]{
                        "Lucas forgot his jacket and found shelter.",
                        "Lucas got lost in the mountains.",
                        "Lucas arrived at the top of the mountain.",
                        "Lucas went home early."
                },
                0
        ));
    }

    public List<Story> getStories() {
        return stories;
    }

    public int getLives() {
        return lives;
    }

    public void decreaseLife() {
        lives--;
    }

    public void resetLives() {
        lives = 3; // Reseteamos las vidas
    }
}
