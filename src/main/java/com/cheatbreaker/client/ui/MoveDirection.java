package com.cheatbreaker.client.ui;

public enum MoveDirection {
    MOVE_UP,
    MOVE_DOWN,
    MOVE_BACK;

    public static MoveDirection fromName(String str) {
        return Enum.valueOf(MoveDirection.class, str);
    }
}
