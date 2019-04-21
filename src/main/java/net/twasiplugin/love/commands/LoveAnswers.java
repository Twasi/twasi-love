package net.twasiplugin.love.commands;

public enum LoveAnswers {

    CLOUD("love.answers.cloud"),
    PERCENT("love.answers.percent"),
    LEVEL("love.answers.level"),
    MATH("love.answers.math"),

    COMPLICATED("love.answers.nope.complicated", 0, 0),
    NOT_SPARKLING("love.answers.nope.not_sparkling", 0, 0),

    OVER9000("love.answers.match.over9000", 100, 100),
    PERFECT_MATCH("love.answers.match.perfect", 100, 100);

    private String key;
    private final int minLove;
    private final int maxLove;

    private LoveAnswers(String key, int min, int max) {
        this.key = key;
        this.minLove = min;
        this.maxLove = max;
    }

    private LoveAnswers(String key) {
        this(key, 0, 100);
    }

    public int getMinLove() {
        return minLove;
    }

    public int getMaxLove() {
        return maxLove;
    }

    public String getKey() {
        return key;
    }
    }
