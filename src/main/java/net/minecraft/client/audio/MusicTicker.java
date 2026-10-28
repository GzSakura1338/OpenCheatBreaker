package net.minecraft.client.audio;

import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class MusicTicker implements IUpdatePlayerListBox {
    public Random rand = new Random();
    public int timeUntilNextMusic = 100;
    public ISound currentMusic;
    public Minecraft mc;

    public MusicTicker(Minecraft mc) {
        this.mc = mc;
    }

    @Override
    public void update() {
        MusicTicker.MusicType ambientMusicType = this.mc.getAmbientMusicType();
        if (this.currentMusic != null) {
            if (!ambientMusicType.getMusicTickerLocation().equals(this.currentMusic.getSoundLocation())) {
                this.mc.getSoundHandler().stopSound(this.currentMusic);
                this.timeUntilNextMusic = MathHelper.getRandomIntegerInRange(this.rand, 0, ambientMusicType.getMinDelay() / 2);
            }

            if (!this.mc.getSoundHandler().isSoundPlaying(this.currentMusic)) {
                this.currentMusic = null;
                this.timeUntilNextMusic = Math.min(MathHelper.getRandomIntegerInRange(this.rand, ambientMusicType.getMinDelay(), ambientMusicType.getMaxDelay()), this.timeUntilNextMusic);
            }
        }

        if (this.currentMusic == null && this.timeUntilNextMusic-- <= 0) {
            this.currentMusic = PositionedSoundRecord.createPositionedSoundRecord(ambientMusicType.getMusicTickerLocation());
            this.mc.getSoundHandler().playSound(this.currentMusic);
            this.timeUntilNextMusic = Integer.MAX_VALUE;
        }
    }

    public enum MusicType {
        MENU("MENU", 0, new ResourceLocation("minecraft:music.menu"), 20, 600),
        GAME("GAME", 1, new ResourceLocation("minecraft:music.game"), 12000, 24000),
        CREATIVE("CREATIVE", 2, new ResourceLocation("minecraft:music.game.creative"), 1200, 3600),
        CREDITS("CREDITS", 3, new ResourceLocation("minecraft:music.game.end.credits"), Integer.MAX_VALUE, Integer.MAX_VALUE),
        NETHER("NETHER", 4, new ResourceLocation("minecraft:music.game.nether"), 1200, 3600),
        END_BOSS("END_BOSS", 5, new ResourceLocation("minecraft:music.game.end.dragon"), 0, 0),
        END("END", 6, new ResourceLocation("minecraft:music.game.end"), 6000, 24000);

        public ResourceLocation resourceLocation;
        public int minDelay;
        public int maxDelay;

        public ResourceLocation getMusicTickerLocation() {
            return this.resourceLocation;
        }

        public int getMinDelay() {
            return this.minDelay;
        }

        public int getMaxDelay() {
            return this.maxDelay;
        }

        MusicType(String str, int n, ResourceLocation resourceLocation, int n2, int n3) {
            this.resourceLocation = resourceLocation;
            this.minDelay = n2;
            this.maxDelay = n3;
        }
    }
}
