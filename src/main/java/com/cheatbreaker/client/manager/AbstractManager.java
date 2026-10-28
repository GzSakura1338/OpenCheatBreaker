package com.cheatbreaker.client.manager;

import com.cheatbreaker.client.CheatBreaker;
import net.minecraft.client.Minecraft;

public abstract class AbstractManager {
    public CheatBreaker cheatBreaker = CheatBreaker.getInstance();
    public Minecraft mc = this.cheatBreaker.getMc();

    public abstract void initialize() throws java.io.IOException;
}
