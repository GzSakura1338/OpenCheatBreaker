package net.optifine;

public class VillagerProfession {
    public int profession;
    public int[] careers;

    public VillagerProfession(int n) {
        this(n, (int[])null);
    }

    public VillagerProfession(int n, int n2) {
        this(n, new int[]{n2});
    }

    public VillagerProfession(int n, int[] ints) {
        this.profession = n;
        this.careers = ints;
    }

    public boolean matches(int n, int n2) {
        return this.profession != n ? false : this.careers == null || Config.equalsOne(n2, this.careers);
    }

    public boolean hasCareer(int n) {
        return this.careers == null ? false : Config.equalsOne(n, this.careers);
    }

    public boolean addCareer(int n) {
        if (this.careers == null) {
            this.careers = new int[]{n};
            return true;
        }

        if (this.hasCareer(n)) {
            return false;
        }

        this.careers = Config.addIntToArray(this.careers, n);
        return true;
    }

    public int getProfession() {
        return this.profession;
    }

    public int[] getCareers() {
        return this.careers;
    }

    @Override
    public String toString() {
        return this.careers == null ? "" + this.profession : "" + this.profession + ":" + Config.arrayToString(this.careers);
    }
}
