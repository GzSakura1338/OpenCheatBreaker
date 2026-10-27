package net.minecraft.entity.ai.attributes;

import io.netty.util.internal.ThreadLocalRandom;
import java.util.UUID;
import net.minecraft.util.MathHelper;
import org.apache.commons.lang3.Validate;

public class AttributeModifier {
   public boolean isSaved = true;
   public UUID id;
   public String name;
   public double amount;
   public int operation;

   public AttributeModifier(String nameIn, double amountIn, int operationIn) {
      this(MathHelper.getRandomUuid(ThreadLocalRandom.current()), nameIn, amountIn, operationIn);
   }

   public AttributeModifier(UUID idIn, String nameIn, double amountIn, int operationIn) {
      this.id = idIn;
      this.name = nameIn;
      this.amount = amountIn;
      this.operation = operationIn;
      Validate.notEmpty(nameIn, "Modifier name cannot be empty", new Object[0]);
      Validate.inclusiveBetween(0L, 2L, operationIn, "Invalid operation");
   }

   public UUID getID() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public int getOperation() {
      return this.operation;
   }

   public double getAmount() {
      return this.amount;
   }

   public boolean isSaved() {
      return this.isSaved;
   }

   public AttributeModifier setSaved(boolean saved) {
      this.isSaved = saved;
      return this;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }

      if (obj != null && this.getClass() == obj.getClass()) {
         AttributeModifier attributeModifier = (AttributeModifier)obj;
         if (this.id != null) {
            if (!this.id.equals(attributeModifier.id)) {
               return false;
            }
         } else if (attributeModifier.id != null) {
            return false;
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.id != null ? this.id.hashCode() : 0;
   }

   @Override
   public String toString() {
      return "AttributeModifier{amount="
         + this.amount
         + ", operation="
         + this.operation
         + ", name='"
         + this.name
         + '\''
         + ", id="
         + this.id
         + ", serialize="
         + this.isSaved
         + '}';
   }
}
