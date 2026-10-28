package net.minecraft.world;

public enum EnumDifficulty {
    PEACEFUL(0, "options.difficulty.peaceful"),
    EASY(1, "options.difficulty.easy"),
    NORMAL(2, "options.difficulty.normal"),
    HARD(3, "options.difficulty.hard");

    public int difficultyId;
    public String difficultyResourceKey;
    public static EnumDifficulty[] difficultyEnums = new EnumDifficulty[values().length];

    EnumDifficulty(int n, String str) {
        this.difficultyId = n;
        this.difficultyResourceKey = str;
    }

    public int getDifficultyId() {
        return this.difficultyId;
    }

    public static EnumDifficulty getDifficultyEnum(int n) {
        return difficultyEnums[n % difficultyEnums.length];
    }

    public String getDifficultyResourceKey() {
        return this.difficultyResourceKey;
    }

    static {
        for (EnumDifficulty enumDifficulty : values()) {
            difficultyEnums[enumDifficulty.difficultyId] = enumDifficulty;
        }
    }
}
