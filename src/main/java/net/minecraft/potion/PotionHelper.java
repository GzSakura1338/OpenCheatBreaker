package net.minecraft.potion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class PotionHelper {
    public static String UNCRAFTABLE = null;
    public static HashMap potionRequirements = new HashMap();
    public static final String ghastTearEffect = "+0-1-2-3&4-4+13";
    public static HashMap potionAmplifiers = new HashMap();
    public static String sugarEffect = "-0+1-2-3&4-4+13";
    public static String magmaCreamEffect = "+0+1-2-3&4-4+13";
    public static String speckledMelonEffect = "+0-1+2-3&4-4+13";
    public static String spiderEyeEffect = "-0-1+2-3&4-4+13";
    public static String fermentedSpiderEyeEffect = "-0+3-4+13";
    public static String blazePowderEffect = "+0-1-2+3&4-4+13";
    public static String goldenCarrotEffect = "-0+1+2-3+13&4-4";
    public static String WATER_BREATHING = "+0-1+2+3+13&4-4";
    public static String glowstoneEffect = "+5-6-7";
    public static String redstoneEffect = "-5+6-7";
    public static String gunpowderEffect = "+14&13-13";
    public static HashMap map = new HashMap();
    public static String[] potionPrefixes = new String[]{
        "potion.prefix.mundane",
        "potion.prefix.uninteresting",
        "potion.prefix.bland",
        "potion.prefix.clear",
        "potion.prefix.milky",
        "potion.prefix.diffuse",
        "potion.prefix.artless",
        "potion.prefix.thin",
        "potion.prefix.awkward",
        "potion.prefix.flat",
        "potion.prefix.bulky",
        "potion.prefix.bungling",
        "potion.prefix.buttered",
        "potion.prefix.smooth",
        "potion.prefix.suave",
        "potion.prefix.debonair",
        "potion.prefix.thick",
        "potion.prefix.elegant",
        "potion.prefix.fancy",
        "potion.prefix.charming",
        "potion.prefix.dashing",
        "potion.prefix.refined",
        "potion.prefix.cordial",
        "potion.prefix.sparkling",
        "potion.prefix.potent",
        "potion.prefix.foul",
        "potion.prefix.odorless",
        "potion.prefix.rank",
        "potion.prefix.harsh",
        "potion.prefix.acrid",
        "potion.prefix.gross",
        "potion.prefix.stinky"
    };

    public static boolean checkFlag(int n, int n2) {
        return (n & 1 << n2) != 0;
    }

    public static int isFlagSet(int n, int n2) {
        return checkFlag(n, n2) ? 1 : 0;
    }

    public static int isFlagUnset(int n, int n2) {
        return checkFlag(n, n2) ? 0 : 1;
    }

    public static int computeInt(int n) {
        return computeInt(n, 5, 4, 3, 2, 1);
    }

    public static int calcPotionLiquidColor(Collection collection) {
        int n = 3694022;
        if (collection != null && !collection.isEmpty()) {
            float f = 0.0F;
            float f2 = 0.0F;
            float f3 = 0.0F;
            float f4 = 0.0F;

            for (Object obj : collection) {
                PotionEffect potionEffect = (PotionEffect)obj;
                int liquidColor = Potion.potionTypes[potionEffect.getPotionID()].getLiquidColor();

                for (int i = 0; i <= potionEffect.getAmplifier(); i++) {
                    f += (liquidColor >> 16 & 0xFF) / 255.0F;
                    f2 += (liquidColor >> 8 & 0xFF) / 255.0F;
                    f3 += (liquidColor >> 0 & 0xFF) / 255.0F;
                    f4++;
                }
            }

            f = f / f4 * 255.0F;
            f2 = f2 / f4 * 255.0F;
            f3 = f3 / f4 * 255.0F;
            return (int)f << 16 | (int)f2 << 8 | (int)f3;
        } else {
            return n;
        }
    }

    public static boolean checkCollection(Collection collection) {
        for (PotionEffect potionEffect : (Iterable<PotionEffect>)collection) {
            if (!potionEffect.getIsAmbient()) {
                return false;
            }
        }

        return true;
    }

    public static int getColor(int n, boolean flag) {
        if (!flag) {
            if (map.containsKey(n)) {
                return (Integer)map.get(n);
            }

            int calcPotionLiquidColor2 = calcPotionLiquidColor(getPotionEffects(n, false));
            map.put(n, calcPotionLiquidColor2);
            return calcPotionLiquidColor2;
        } else {
            return calcPotionLiquidColor(getPotionEffects(n, flag));
        }
    }

    public static String computeInt2(int n) {
        int computeInt2 = computeInt(n);
        return potionPrefixes[computeInt2];
    }

    public static int computeInt(boolean flag, boolean flag2, boolean flag3, int n, int n2, int n3, int n4) {
        int n5 = 0;
        if (flag) {
            n5 = isFlagUnset(n4, n2);
        } else if (n != -1) {
            if (n == 0 && countSetFlags(n4) == n2) {
                n5 = 1;
            } else if (n == 1 && countSetFlags(n4) > n2) {
                n5 = 1;
            } else if (n == 2 && countSetFlags(n4) < n2) {
                n5 = 1;
            }
        } else {
            n5 = isFlagSet(n4, n2);
        }

        if (flag2) {
            n5 *= n3;
        }

        if (flag3) {
            n5 *= -1;
        }

        return n5;
    }

    public static int countSetFlags(int n) {
        int n2;
        for (n2 = 0; n > 0; n2++) {
            n &= n - 1;
        }

        return n2;
    }

    public static int parsePotionEffects(String str, int n, int n2, int n3) {
        if (n < str.length() && n2 >= 0 && n < n2) {
            int indexOf = str.indexOf(124, n);
            if (indexOf >= 0 && indexOf < n2) {
                int parsePotionEffects2 = parsePotionEffects(str, n, indexOf - 1, n3);
                if (parsePotionEffects2 > 0) {
                    return parsePotionEffects2;
                }

                int parsePotionEffects3 = parsePotionEffects(str, indexOf + 1, n2, n3);
                return parsePotionEffects3 > 0 ? parsePotionEffects3 : 0;
            } else {
                int indexOf2 = str.indexOf(38, n);
                if (indexOf2 >= 0 && indexOf2 < n2) {
                    int parsePotionEffects2 = parsePotionEffects(str, n, indexOf2 - 1, n3);
                    if (parsePotionEffects2 <= 0) {
                        return 0;
                    }

                    int parsePotionEffects3 = parsePotionEffects(str, indexOf2 + 1, n2, n3);
                    return parsePotionEffects3 <= 0 ? 0 : (parsePotionEffects2 > parsePotionEffects3 ? parsePotionEffects2 : parsePotionEffects3);
                } else {
                    boolean flag = false;
                    boolean flag2 = false;
                    boolean flag3 = false;
                    boolean flag4 = false;
                    boolean flag5 = false;
                    byte b = -1;
                    int n4 = 0;
                    int n5 = 0;
                    int n6 = 0;

                    for (int i = n; i < n2; i++) {
                        char charAt = str.charAt(i);
                        if (charAt >= '0' && charAt <= '9') {
                            if (flag) {
                                n5 = charAt - '0';
                                flag2 = true;
                            } else {
                                n4 *= 10;
                                n4 += charAt - '0';
                                flag3 = true;
                            }
                        } else if (charAt == '*') {
                            flag = true;
                        } else if (charAt == '!') {
                            if (flag3) {
                                n6 += computeInt(flag4, flag2, flag5, b, n4, n5, n3);
                                flag4 = false;
                                flag5 = false;
                                flag = false;
                                flag2 = false;
                                flag3 = false;
                                n5 = 0;
                                n4 = 0;
                                b = -1;
                            }

                            flag4 = true;
                        } else if (charAt == '-') {
                            if (flag3) {
                                n6 += computeInt(flag4, flag2, flag5, b, n4, n5, n3);
                                flag4 = false;
                                flag5 = false;
                                flag = false;
                                flag2 = false;
                                flag3 = false;
                                n5 = 0;
                                n4 = 0;
                                b = -1;
                            }

                            flag5 = true;
                        } else if (charAt != '=' && charAt != '<' && charAt != '>') {
                            if (charAt == '+' && flag3) {
                                n6 += computeInt(flag4, flag2, flag5, b, n4, n5, n3);
                                flag4 = false;
                                flag5 = false;
                                flag = false;
                                flag2 = false;
                                flag3 = false;
                                n5 = 0;
                                n4 = 0;
                                b = -1;
                            }
                        } else {
                            if (flag3) {
                                n6 += computeInt(flag4, flag2, flag5, b, n4, n5, n3);
                                flag4 = false;
                                flag5 = false;
                                flag = false;
                                flag2 = false;
                                flag3 = false;
                                n5 = 0;
                                n4 = 0;
                                b = -1;
                            }

                            if (charAt == '=') {
                                b = 0;
                            } else if (charAt == '<') {
                                b = 2;
                            } else if (charAt == '>') {
                                b = 1;
                            }
                        }
                    }

                    if (flag3) {
                        n6 += computeInt(flag4, flag2, flag5, b, n4, n5, n3);
                    }

                    return n6;
                }
            }
        } else {
            return 0;
        }
    }

    public static List getPotionEffects(int n, boolean flag) {
        ArrayList list = null;

        for (Potion potion : Potion.potionTypes) {
            if (potion != null && (!potion.isUsable() || flag)) {
                String str = (String)potionRequirements.get(potion.getId());
                if (str != null) {
                    int parsePotionEffects2 = parsePotionEffects(str, 0, str.length(), n);
                    if (parsePotionEffects2 > 0) {
                        int n2 = 0;
                        String str2 = (String)potionAmplifiers.get(potion.getId());
                        if (str2 != null) {
                            n2 = parsePotionEffects(str2, 0, str2.length(), n);
                            if (n2 < 0) {
                                n2 = 0;
                            }
                        }

                        if (potion.isInstant()) {
                            parsePotionEffects2 = 1;
                        } else {
                            parsePotionEffects2 = 1200 * (parsePotionEffects2 * 3 + (parsePotionEffects2 - 1) * 2);
                            parsePotionEffects2 >>= n2;
                            parsePotionEffects2 = (int)Math.round(parsePotionEffects2 * potion.getEffectiveness());
                            if ((n & 16384) != 0) {
                                parsePotionEffects2 = (int)Math.round(parsePotionEffects2 * 0.75 + 0.5);
                            }
                        }

                        if (list == null) {
                            list = new ArrayList();
                        }

                        PotionEffect potionEffect = new PotionEffect(potion.getId(), parsePotionEffects2, n2);
                        if ((n & 16384) != 0) {
                            potionEffect.setSplashPotion(true);
                        }

                        list.add(potionEffect);
                    }
                }
            }
        }

        return list;
    }

    public static int brewBitOperations(int n, int n2, boolean flag, boolean flag2, boolean flag3) {
        if (flag3) {
            if (!checkFlag(n, n2)) {
                return 0;
            }
        } else if (flag) {
            n &= ~(1 << n2);
        } else if (flag2) {
            if ((n & 1 << n2) == 0) {
                n |= 1 << n2;
            } else {
                n &= ~(1 << n2);
            }
        } else {
            n |= 1 << n2;
        }

        return n;
    }

    public static int applyIngredient(int n, String str) {
        byte b = 0;
        int n2 = str.length();
        boolean flag = false;
        boolean flag2 = false;
        boolean flag3 = false;
        boolean flag4 = false;
        int n3 = 0;

        for (int i = b; i < n2; i++) {
            char charAt = str.charAt(i);
            if (charAt >= '0' && charAt <= '9') {
                n3 *= 10;
                n3 += charAt - '0';
                flag = true;
            } else if (charAt == '!') {
                if (flag) {
                    n = brewBitOperations(n, n3, flag3, flag2, flag4);
                    flag4 = false;
                    flag2 = false;
                    flag3 = false;
                    flag = false;
                    n3 = 0;
                }

                flag2 = true;
            } else if (charAt == '-') {
                if (flag) {
                    n = brewBitOperations(n, n3, flag3, flag2, flag4);
                    flag4 = false;
                    flag2 = false;
                    flag3 = false;
                    flag = false;
                    n3 = 0;
                }

                flag3 = true;
            } else if (charAt == '+') {
                if (flag) {
                    n = brewBitOperations(n, n3, flag3, flag2, flag4);
                    flag4 = false;
                    flag2 = false;
                    flag3 = false;
                    flag = false;
                    n3 = 0;
                }
            } else if (charAt == '&') {
                if (flag) {
                    n = brewBitOperations(n, n3, flag3, flag2, flag4);
                    flag4 = false;
                    flag2 = false;
                    flag3 = false;
                    flag = false;
                    n3 = 0;
                }

                flag4 = true;
            }
        }

        if (flag) {
            n = brewBitOperations(n, n3, flag3, flag2, flag4);
        }

        return n & 32767;
    }

    public static int computeInt(int n, int n2, int n3, int n4, int n5, int n6) {
        return (checkFlag(n, n2) ? 16 : 0)
            | (checkFlag(n, n3) ? 8 : 0)
            | (checkFlag(n, n4) ? 4 : 0)
            | (checkFlag(n, n5) ? 2 : 0)
            | (checkFlag(n, n6) ? 1 : 0);
    }

    static {
        potionRequirements.put(Potion.regeneration.getId(), "0 & !1 & !2 & !3 & 0+6");
        potionRequirements.put(Potion.moveSpeed.getId(), "!0 & 1 & !2 & !3 & 1+6");
        potionRequirements.put(Potion.fireResistance.getId(), "0 & 1 & !2 & !3 & 0+6");
        potionRequirements.put(Potion.heal.getId(), "0 & !1 & 2 & !3");
        potionRequirements.put(Potion.poison.getId(), "!0 & !1 & 2 & !3 & 2+6");
        potionRequirements.put(Potion.weakness.getId(), "!0 & !1 & !2 & 3 & 3+6");
        potionRequirements.put(Potion.harm.getId(), "!0 & !1 & 2 & 3");
        potionRequirements.put(Potion.moveSlowdown.getId(), "!0 & 1 & !2 & 3 & 3+6");
        potionRequirements.put(Potion.damageBoost.getId(), "0 & !1 & !2 & 3 & 3+6");
        potionRequirements.put(Potion.nightVision.getId(), "!0 & 1 & 2 & !3 & 2+6");
        potionRequirements.put(Potion.invisibility.getId(), "!0 & 1 & 2 & 3 & 2+6");
        potionRequirements.put(Potion.waterBreathing.getId(), "0 & !1 & 2 & 3 & 2+6");
        potionAmplifiers.put(Potion.moveSpeed.getId(), "5");
        potionAmplifiers.put(Potion.digSpeed.getId(), "5");
        potionAmplifiers.put(Potion.damageBoost.getId(), "5");
        potionAmplifiers.put(Potion.regeneration.getId(), "5");
        potionAmplifiers.put(Potion.harm.getId(), "5");
        potionAmplifiers.put(Potion.heal.getId(), "5");
        potionAmplifiers.put(Potion.resistance.getId(), "5");
        potionAmplifiers.put(Potion.poison.getId(), "5");
    }
}
