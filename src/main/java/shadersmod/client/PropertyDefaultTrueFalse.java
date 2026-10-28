package shadersmod.client;

import net.optifine.Lang;

public class PropertyDefaultTrueFalse extends Property {
    public static String[] PROPERTY_VALUES = new String[]{"default", "true", "false"};
    public static String[] USER_VALUES = new String[]{"Default", "ON", "OFF"};

    public PropertyDefaultTrueFalse(String str, String str2, int n) {
        super(str, PROPERTY_VALUES, str2, USER_VALUES, n);
    }

    @Override
    public String getUserValue() {
        return this.isDefault() ? Lang.getDefault() : (this.isTrue() ? Lang.getOn() : (this.isFalse() ? Lang.getOff() : super.getUserValue()));
    }

    public boolean isDefault() {
        return this.getValue() == 0;
    }

    public boolean isTrue() {
        return this.getValue() == 1;
    }

    public boolean isFalse() {
        return this.getValue() == 2;
    }
}
