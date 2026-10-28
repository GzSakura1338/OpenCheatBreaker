package com.cheatbreaker.client.emote.bobj;

import java.util.ArrayList;
import java.util.List;

public class BobjVertex {
    public List<BobjWeight> weights = new ArrayList<>();
    public float x;
    public float y;
    public float z;

    public void normalizeWeights() {
        this.weights.removeIf(bobjWeight -> bobjWeight.weight < 0.05);
        if (this.weights.size() > 0) {
            float f = 0.0F;

            for (BobjWeight bobjWeight : this.weights) {
                f += bobjWeight.weight;
            }

            if (f < 1.0F) {
                BobjWeight bobjWeight = this.weights.get(this.weights.size() - 1);
                bobjWeight.weight += 1.0F - f;
            }
        }
    }

    public BobjVertex(float f, float f2, float f3) {
        this.x = f;
        this.y = f2;
        this.z = f3;
    }
}
