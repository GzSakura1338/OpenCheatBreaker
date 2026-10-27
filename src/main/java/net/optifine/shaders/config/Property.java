package net.optifine.shaders.config;

import java.util.Properties;
import net.optifine.Config;
import org.apache.commons.lang3.ArrayUtils;

public class Property {
   public int defaultValue = 0;
   public String propertyName = null;
   public String[] propertyValues = null;
   public String userName = null;
   public String[] userValues = null;
   public int value = 0;

   public Property(String str, String[] strs, String str2, String[] strs2, int n) {
      this.propertyName = str;
      this.propertyValues = strs;
      this.userName = str2;
      this.userValues = strs2;
      this.defaultValue = n;
      if (strs.length != strs2.length) {
         throw new IllegalArgumentException("Property and user values have different lengths: " + strs.length + " != " + strs2.length);
      }

      if (n >= 0 && n < strs.length) {
         this.value = n;
      } else {
         throw new IllegalArgumentException("Invalid default value: " + n);
      }
   }

   public boolean setPropertyValue(String str) {
      if (str == null) {
         this.value = this.defaultValue;
         return false;
      }

      this.value = ArrayUtils.indexOf(this.propertyValues, str);
      if (this.value >= 0 && this.value < this.propertyValues.length) {
         return true;
      }

      this.value = this.defaultValue;
      return false;
   }

   public void nextValue(boolean flag) {
      byte b = 0;
      int n = this.propertyValues.length - 1;
      this.value = Config.limit(this.value, b, n);
      if (flag) {
         this.value++;
         if (this.value > n) {
            this.value = b;
         }
      } else {
         this.value--;
         if (this.value < b) {
            this.value = n;
         }
      }
   }

   public void setValue(int n) {
      this.value = n;
      if (this.value < 0 || this.value >= this.propertyValues.length) {
         this.value = this.defaultValue;
      }
   }

   public int getValue() {
      return this.value;
   }

   public String getUserValue() {
      return this.userValues[this.value];
   }

   public String getPropertyValue() {
      return this.propertyValues[this.value];
   }

   public String getUserName() {
      return this.userName;
   }

   public String getPropertyName() {
      return this.propertyName;
   }

   public void resetValue() {
      this.value = this.defaultValue;
   }

   public boolean loadFrom(Properties properties) {
      this.resetValue();
      if (properties == null) {
         return false;
      }

      String property = properties.getProperty(this.propertyName);
      return property == null ? false : this.setPropertyValue(property);
   }

   public void saveTo(Properties properties) {
      if (properties != null) {
         properties.setProperty(this.getPropertyName(), this.getPropertyValue());
      }
   }

   @Override
   public String toString() {
      return "" + this.propertyName + "=" + this.getPropertyValue() + " [" + Config.arrayToString(this.propertyValues) + "], value: " + this.value;
   }
}
