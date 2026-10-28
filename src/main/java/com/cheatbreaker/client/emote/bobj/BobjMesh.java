package com.cheatbreaker.client.emote.bobj;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BobjMesh {
    public List faces = new ArrayList();
    public BobjArmature armature;
    public String name;
    public String armatureName;

    public BobjMesh copyWithOffsets(int n, int n2, int n3) {
        BobjMesh bobjMesh = new BobjMesh(this.name);
        bobjMesh.armatureName = this.armatureName;
        bobjMesh.armature = this.armature;
        Iterator iterator = this.faces.iterator();

        while (iterator.hasNext()) {
            bobjMesh.faces.add(((BobjFace)iterator.next()).offsetIndices(n, n2, n3));
        }

        return bobjMesh;
    }

    public BobjMesh(String str) {
        this.name = str;
    }
}
