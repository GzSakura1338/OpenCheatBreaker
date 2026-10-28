package net.optifine;

public class NaturalProperties {
    public int rotation = 1;
    public boolean flip = false;

    public NaturalProperties(String str) {
        if (str.equals("4")) {
            this.rotation = 4;
        } else if (str.equals("2")) {
            this.rotation = 2;
        } else if (str.equals("F")) {
            this.flip = true;
        } else if (str.equals("4F")) {
            this.rotation = 4;
            this.flip = true;
        } else if (str.equals("2F")) {
            this.rotation = 2;
            this.flip = true;
        } else {
            Config.warn("NaturalTextures: Unknown type: " + str);
        }
    }

    public boolean isValid() {
        return this.rotation != 2 && this.rotation != 4 ? this.flip : true;
    }
}
