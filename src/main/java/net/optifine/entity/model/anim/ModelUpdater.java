package net.optifine.entity.model.anim;

public class ModelUpdater {
   public ModelVariableUpdater[] modelVariableUpdaters;

   public ModelUpdater(ModelVariableUpdater[] modelVariableUpdaters) {
      this.modelVariableUpdaters = modelVariableUpdaters;
   }

   public void update() {
      for (int i = 0; i < this.modelVariableUpdaters.length; i++) {
         ModelVariableUpdater modelVariableUpdater = this.modelVariableUpdaters[i];
         modelVariableUpdater.update();
      }
   }

   public boolean initialize(IModelResolver modelResolver) {
      for (int i = 0; i < this.modelVariableUpdaters.length; i++) {
         ModelVariableUpdater modelVariableUpdater = this.modelVariableUpdaters[i];
         if (!modelVariableUpdater.initialize(modelResolver)) {
            return false;
         }
      }

      return true;
   }
}
