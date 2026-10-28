package com.cheatbreaker.client.emote;

import com.cheatbreaker.client.emote.bobj.BobjArmature;
import com.cheatbreaker.client.emote.bobj.PlayerMeshRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class RockPaperScissorsEmoteDefinition extends EmoteDefinition {
    public String choice = "";

    @Override
    public EmoteDefinition createInstance() {
        int nextInt = this.random.nextInt(30);
        String str = "";
        if (nextInt <= 10) {
            str = "rock";
        } else if (nextInt <= 20) {
            str = "paper";
        } else if (nextInt <= 30) {
            str = "scissors";
        }

        return this.createVariant(str);
    }

    @Override
    public EmoteDefinition createVariant(String str) {
        return new RockPaperScissorsEmoteDefinition(this.name, this.durationTicks, this.looping, str);
    }

    public RockPaperScissorsEmoteDefinition(String str, int n, boolean flag) {
        super(str, n, flag);
    }

    @Override
    public void onTick(EntityLivingBase entityLivingBase, BobjArmature bobjArmature, PlayerMeshRenderer playerMeshRenderer, int n, float f) {
        playerMeshRenderer.heldItemScale = n <= 25 || n >= 55 ? 0.0F : (n < 30 ? (n - 25 + f) / 5.0F : (n >= 50 ? 1.0F - (n - 50 + f) / 5.0F : 1.0F));
    }

    @Override
    public void onStop(PlayerMeshRenderer playerMeshRenderer) {
        playerMeshRenderer.itemStack = null;
        playerMeshRenderer.heldItemScale = 0.0F;
    }

    @Override
    public void onStart(PlayerMeshRenderer playerMeshRenderer) {
        if (this.choice.equals("rock")) {
            playerMeshRenderer.itemStack = new ItemStack(Blocks.stone, 1);
        } else if (this.choice.equals("paper")) {
            playerMeshRenderer.itemStack = new ItemStack(Items.paper, 1);
        } else if (this.choice.equals("scissors")) {
            playerMeshRenderer.itemStack = new ItemStack(Items.shears, 1);
        }

        playerMeshRenderer.heldItemScale = 0.0F;
    }

    @Override
    public String getDisplayName() {
        return this.name + (this.choice.isEmpty() ? "" : ":" + this.choice);
    }

    public RockPaperScissorsEmoteDefinition(String str, int n, boolean flag, String str2) {
        super(str, n, flag);
        this.choice = str2;
    }
}
