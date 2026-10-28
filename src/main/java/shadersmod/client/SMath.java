package shadersmod.client;

import java.nio.FloatBuffer;
import java.util.Arrays;

public class SMath {
    public static void multiplyMat4xMat4(float[] floats, float[] floats2, float[] floats3) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                floats[4 * i + j] = floats2[4 * i + 0] * floats3[0 + j]
                    + floats2[4 * i + 1] * floats3[4 + j]
                    + floats2[4 * i + 2] * floats3[8 + j]
                    + floats2[4 * i + 3] * floats3[12 + j];
            }
        }
    }

    public static void multiplyMat4xVec4(float[] floats, float[] floats2, float[] floats3) {
        floats[0] = floats2[0] * floats3[0] + floats2[4] * floats3[1] + floats2[8] * floats3[2] + floats2[12] * floats3[3];
        floats[1] = floats2[1] * floats3[0] + floats2[5] * floats3[1] + floats2[9] * floats3[2] + floats2[13] * floats3[3];
        floats[2] = floats2[2] * floats3[0] + floats2[6] * floats3[1] + floats2[10] * floats3[2] + floats2[14] * floats3[3];
        floats[3] = floats2[3] * floats3[0] + floats2[7] * floats3[1] + floats2[11] * floats3[2] + floats2[15] * floats3[3];
    }

    public static void invertMat4(float[] floats, float[] floats2) {
        floats[0] = floats2[5] * floats2[10] * floats2[15]
            - floats2[5] * floats2[11] * floats2[14]
            - floats2[9] * floats2[6] * floats2[15]
            + floats2[9] * floats2[7] * floats2[14]
            + floats2[13] * floats2[6] * floats2[11]
            - floats2[13] * floats2[7] * floats2[10];
        floats[1] = -floats2[1] * floats2[10] * floats2[15]
            + floats2[1] * floats2[11] * floats2[14]
            + floats2[9] * floats2[2] * floats2[15]
            - floats2[9] * floats2[3] * floats2[14]
            - floats2[13] * floats2[2] * floats2[11]
            + floats2[13] * floats2[3] * floats2[10];
        floats[2] = floats2[1] * floats2[6] * floats2[15]
            - floats2[1] * floats2[7] * floats2[14]
            - floats2[5] * floats2[2] * floats2[15]
            + floats2[5] * floats2[3] * floats2[14]
            + floats2[13] * floats2[2] * floats2[7]
            - floats2[13] * floats2[3] * floats2[6];
        floats[3] = -floats2[1] * floats2[6] * floats2[11]
            + floats2[1] * floats2[7] * floats2[10]
            + floats2[5] * floats2[2] * floats2[11]
            - floats2[5] * floats2[3] * floats2[10]
            - floats2[9] * floats2[2] * floats2[7]
            + floats2[9] * floats2[3] * floats2[6];
        floats[4] = -floats2[4] * floats2[10] * floats2[15]
            + floats2[4] * floats2[11] * floats2[14]
            + floats2[8] * floats2[6] * floats2[15]
            - floats2[8] * floats2[7] * floats2[14]
            - floats2[12] * floats2[6] * floats2[11]
            + floats2[12] * floats2[7] * floats2[10];
        floats[5] = floats2[0] * floats2[10] * floats2[15]
            - floats2[0] * floats2[11] * floats2[14]
            - floats2[8] * floats2[2] * floats2[15]
            + floats2[8] * floats2[3] * floats2[14]
            + floats2[12] * floats2[2] * floats2[11]
            - floats2[12] * floats2[3] * floats2[10];
        floats[6] = -floats2[0] * floats2[6] * floats2[15]
            + floats2[0] * floats2[7] * floats2[14]
            + floats2[4] * floats2[2] * floats2[15]
            - floats2[4] * floats2[3] * floats2[14]
            - floats2[12] * floats2[2] * floats2[7]
            + floats2[12] * floats2[3] * floats2[6];
        floats[7] = floats2[0] * floats2[6] * floats2[11]
            - floats2[0] * floats2[7] * floats2[10]
            - floats2[4] * floats2[2] * floats2[11]
            + floats2[4] * floats2[3] * floats2[10]
            + floats2[8] * floats2[2] * floats2[7]
            - floats2[8] * floats2[3] * floats2[6];
        floats[8] = floats2[4] * floats2[9] * floats2[15]
            - floats2[4] * floats2[11] * floats2[13]
            - floats2[8] * floats2[5] * floats2[15]
            + floats2[8] * floats2[7] * floats2[13]
            + floats2[12] * floats2[5] * floats2[11]
            - floats2[12] * floats2[7] * floats2[9];
        floats[9] = -floats2[0] * floats2[9] * floats2[15]
            + floats2[0] * floats2[11] * floats2[13]
            + floats2[8] * floats2[1] * floats2[15]
            - floats2[8] * floats2[3] * floats2[13]
            - floats2[12] * floats2[1] * floats2[11]
            + floats2[12] * floats2[3] * floats2[9];
        floats[10] = floats2[0] * floats2[5] * floats2[15]
            - floats2[0] * floats2[7] * floats2[13]
            - floats2[4] * floats2[1] * floats2[15]
            + floats2[4] * floats2[3] * floats2[13]
            + floats2[12] * floats2[1] * floats2[7]
            - floats2[12] * floats2[3] * floats2[5];
        floats[11] = -floats2[0] * floats2[5] * floats2[11]
            + floats2[0] * floats2[7] * floats2[9]
            + floats2[4] * floats2[1] * floats2[11]
            - floats2[4] * floats2[3] * floats2[9]
            - floats2[8] * floats2[1] * floats2[7]
            + floats2[8] * floats2[3] * floats2[5];
        floats[12] = -floats2[4] * floats2[9] * floats2[14]
            + floats2[4] * floats2[10] * floats2[13]
            + floats2[8] * floats2[5] * floats2[14]
            - floats2[8] * floats2[6] * floats2[13]
            - floats2[12] * floats2[5] * floats2[10]
            + floats2[12] * floats2[6] * floats2[9];
        floats[13] = floats2[0] * floats2[9] * floats2[14]
            - floats2[0] * floats2[10] * floats2[13]
            - floats2[8] * floats2[1] * floats2[14]
            + floats2[8] * floats2[2] * floats2[13]
            + floats2[12] * floats2[1] * floats2[10]
            - floats2[12] * floats2[2] * floats2[9];
        floats[14] = -floats2[0] * floats2[5] * floats2[14]
            + floats2[0] * floats2[6] * floats2[13]
            + floats2[4] * floats2[1] * floats2[14]
            - floats2[4] * floats2[2] * floats2[13]
            - floats2[12] * floats2[1] * floats2[6]
            + floats2[12] * floats2[2] * floats2[5];
        floats[15] = floats2[0] * floats2[5] * floats2[10]
            - floats2[0] * floats2[6] * floats2[9]
            - floats2[4] * floats2[1] * floats2[10]
            + floats2[4] * floats2[2] * floats2[9]
            + floats2[8] * floats2[1] * floats2[6]
            - floats2[8] * floats2[2] * floats2[5];
        float f = floats2[0] * floats[0] + floats2[1] * floats[4] + floats2[2] * floats[8] + floats2[3] * floats[12];
        if (f != 0.0) {
            for (int i = 0; i < 16; i++) {
                floats[i] /= f;
            }
        } else {
            Arrays.fill(floats, 0.0F);
        }
    }

    public static void invertMat4FBFA(FloatBuffer floatBuffer, FloatBuffer floatBuffer2, float[] floats, float[] floats2) {
        floatBuffer2.get(floats2);
        invertMat4(floats, floats2);
        floatBuffer.put(floats);
    }
}
