package de.hasait.sprinkler.util;

public class ValueWithExplanation<T> {

    private final T value;
    private final String explanation;

    public ValueWithExplanation(T value, String explanation) {
        this.value = value;
        this.explanation = explanation;
    }

    public T getValue() {
        return value;
    }

    public String getExplanation() {
        return explanation;
    }

}
