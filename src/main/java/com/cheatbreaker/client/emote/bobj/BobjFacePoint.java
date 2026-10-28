package com.cheatbreaker.client.emote.bobj;

public class BobjFacePoint {
    public int vertexIndex = -1;
    public static final int UNSET_INDEX = -1;
    public int texCoordIndex = -1;
    public int normalIndex = -1;

    public BobjFacePoint(int n, int n2, int n3) {
        this.vertexIndex = n;
        this.texCoordIndex = n2;
        this.normalIndex = n3;
    }

    public BobjFacePoint() {
    }
}
