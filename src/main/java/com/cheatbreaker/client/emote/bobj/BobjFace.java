package com.cheatbreaker.client.emote.bobj;

public class BobjFace {
    public BobjFacePoint[] points = new BobjFacePoint[3];

    public BobjFace(String str, String str2, String str3) {
        this.points[0] = this.parsePoint(str);
        this.points[1] = this.parsePoint(str2);
        this.points[2] = this.parsePoint(str3);
    }

    public BobjFace offsetIndices(int n, int n2, int n3) {
        BobjFace bobjFace = new BobjFace();

        for (int i = 0; i < bobjFace.points.length; i++) {
            BobjFacePoint bobjFacePoint = this.points[i];
            bobjFace.points[i] = new BobjFacePoint(bobjFacePoint.vertexIndex + n, bobjFacePoint.texCoordIndex + n3, bobjFacePoint.normalIndex + n2);
        }

        return bobjFace;
    }

    public BobjFacePoint parsePoint(String str) {
        BobjFacePoint bobjFacePoint = new BobjFacePoint();
        String[] split = str.split("/");
        int length = split.length;
        bobjFacePoint.vertexIndex = Integer.parseInt(split[0]) - 1;
        if (length > 1) {
            String str2 = split[1];
            if (!str2.isEmpty()) {
                bobjFacePoint.texCoordIndex = Integer.parseInt(str2) - 1;
            }

            if (length > 2) {
                bobjFacePoint.normalIndex = Integer.parseInt(split[2]) - 1;
            }
        }

        return bobjFacePoint;
    }

    public BobjFace() {
    }
}
