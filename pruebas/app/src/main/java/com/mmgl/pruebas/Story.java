package com.mmgl.pruebas;

public class Story {
    private String story;
    private String[] possibleResponses;
    private int correctResponseIndex;

    public Story(String story, String[] possibleResponses, int correctResponseIndex) {
        this.story = story;
        this.possibleResponses = possibleResponses;
        this.correctResponseIndex = correctResponseIndex;
    }

    public String getStory() {
        return story;
    }

    public String[] getPossibleResponses() {
        return possibleResponses;
    }

    public int getCorrectResponseIndex() {
        return correctResponseIndex;
    }
}
