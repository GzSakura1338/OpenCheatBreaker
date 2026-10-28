package net.minecraft.client.audio;

import com.google.common.collect.Lists;
import java.util.List;

public class SoundList {
    public List sounds = Lists.newArrayList();
    public boolean replaceExisting;
    public SoundCategory category;

    public List getSoundList() {
        return this.sounds;
    }

    public boolean canReplaceExisting() {
        return this.replaceExisting;
    }

    public void setReplaceExisting(boolean flag) {
        this.replaceExisting = flag;
    }

    public SoundCategory getSoundCategory() {
        return this.category;
    }

    public void setSoundCategory(SoundCategory soundCategory) {
        this.category = soundCategory;
    }

    public static class SoundEntry {
        public String text;
        public float floatValue = 1.0F;
        public float floatValue2 = 1.0F;
        public int intValue = 1;
        public SoundList.SoundEntry.Type type = SoundList.SoundEntry.Type.FILE;
        public boolean flag = false;

        public String getSoundEntryName() {
            return this.text;
        }

        public void setSoundEntryName(String str) {
            this.text = str;
        }

        public float getSoundEntryVolume() {
            return this.floatValue;
        }

        public void setSoundEntryVolume(float f) {
            this.floatValue = f;
        }

        public float getSoundEntryPitch() {
            return this.floatValue2;
        }

        public void setSoundEntryPitch(float f) {
            this.floatValue2 = f;
        }

        public int getSoundEntryWeight() {
            return this.intValue;
        }

        public void setSoundEntryWeight(int n) {
            this.intValue = n;
        }

        public SoundList.SoundEntry.Type getSoundEntryType() {
            return this.type;
        }

        public void setSoundEntryType(SoundList.SoundEntry.Type type) {
            this.type = type;
        }

        public boolean isStreaming() {
            return this.flag;
        }

        public void setStreaming(boolean flag) {
            this.flag = flag;
        }

        public enum Type {
            FILE("FILE", 0, "file"),
            SOUND_EVENT("SOUND_EVENT", 1, "event");

            public String text;
            public static SoundList.SoundEntry.Type[] typeArray = new SoundList.SoundEntry.Type[]{FILE, SOUND_EVENT};

            public static SoundList.SoundEntry.Type getType(String str) {
                for (SoundList.SoundEntry.Type type : values()) {
                    if (type.text.equals(str)) {
                        return type;
                    }
                }

                return null;
            }

            Type(String str, int n, String str2) {
                this.text = str2;
            }
        }
    }
}
