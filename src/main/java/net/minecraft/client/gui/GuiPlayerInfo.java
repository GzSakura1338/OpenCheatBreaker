package net.minecraft.client.gui;

public class GuiPlayerInfo {
    public String name;
    public String nameinLowerCase;
    public int responseTime;

    public GuiPlayerInfo(String str) {
        this.name = str;
        this.nameinLowerCase = str.toLowerCase();
    }
}
