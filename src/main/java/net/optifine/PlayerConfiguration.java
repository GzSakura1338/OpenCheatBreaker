package net.optifine;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;

public class PlayerConfiguration {
    public PlayerItemModel[] playerItemModels = new PlayerItemModel[0];
    public boolean initialized = false;

    public void renderPlayerItems(ModelBiped modelBiped, AbstractClientPlayer abstractClientPlayer, float f, float f2) {
        if (this.initialized) {
            for (int i = 0; i < this.playerItemModels.length; i++) {
                PlayerItemModel playerItemModel = this.playerItemModels[i];
                playerItemModel.render(modelBiped, abstractClientPlayer, f, f2);
            }
        }
    }

    public boolean isInitialized() {
        return this.initialized;
    }

    public void setInitialized(boolean flag) {
        this.initialized = flag;
    }

    public PlayerItemModel[] getPlayerItemModels() {
        return this.playerItemModels;
    }

    public void addPlayerItemModel(PlayerItemModel playerItemModel) {
        this.playerItemModels = (PlayerItemModel[])Config.addObjectToArray(this.playerItemModels, playerItemModel);
    }
}
