package net.minecraft.entity.ai.attributes;

public abstract class BaseAttribute implements IAttribute {
   public IAttribute iAttribute;
   public String unlocalizedName;
   public double defaultValue;
   public boolean shouldWatch;

   public BaseAttribute(IAttribute attribute, String unlocalizedNameIn, double defaultValueIn) {
      this.iAttribute = attribute;
      this.unlocalizedName = unlocalizedNameIn;
      this.defaultValue = defaultValueIn;
      if (unlocalizedNameIn == null) {
         throw new IllegalArgumentException("Name cannot be null!");
      }
   }

   @Override
   public String getAttributeUnlocalizedName() {
      return this.unlocalizedName;
   }

   @Override
   public double getDefaultValue() {
      return this.defaultValue;
   }

   @Override
   public boolean getShouldWatch() {
      return this.shouldWatch;
   }

   public BaseAttribute setShouldWatch(boolean shouldWatchIn) {
      this.shouldWatch = shouldWatchIn;
      return this;
   }

   @Override
   public IAttribute getParent() {
      return this.iAttribute;
   }

   @Override
   public int hashCode() {
      return this.unlocalizedName.hashCode();
   }

   @Override
   public boolean equals(Object obj) {
      return obj instanceof IAttribute && this.unlocalizedName.equals(((IAttribute)obj).getAttributeUnlocalizedName());
   }
}
