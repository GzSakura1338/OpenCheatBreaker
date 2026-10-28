package shadersmod.client;

public class MultiTexID {
    public int base;
    public int norm;
    public int spec;

    public MultiTexID(int n, int n2, int n3) {
        this.base = n;
        this.norm = n2;
        this.spec = n3;
    }
}
