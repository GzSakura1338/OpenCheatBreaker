package shadersmod.uniform;

import java.util.HashMap;
import java.util.Map;
import net.optifine.SmoothFloat;

public class Smoother {
    public static Map<Integer, SmoothFloat> mapSmoothValues = new HashMap<>();

    public static float getSmoothValue(int n, float f, float f2, float f3) {
        Map mapSmoothValues2 = mapSmoothValues;
        synchronized (mapSmoothValues) {
            Integer integer = n;
            SmoothFloat smoothFloat = mapSmoothValues.get(integer);
            if (smoothFloat == null) {
                smoothFloat = new SmoothFloat(f, f2, f3);
                mapSmoothValues.put(integer, smoothFloat);
            }

            return smoothFloat.getSmoothValue(f);
        }
    }

    public static void reset() {
        Map mapSmoothValues2 = mapSmoothValues;
        synchronized (mapSmoothValues) {
            mapSmoothValues.clear();
        }
    }
}
