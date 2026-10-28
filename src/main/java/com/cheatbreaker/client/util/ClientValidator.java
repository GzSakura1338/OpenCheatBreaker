package com.cheatbreaker.client.util;

import com.cheatbreaker.client.CheatBreaker;
import java.util.Objects;
import net.minecraft.client.Minecraft;

public class ClientValidator {
    public ClientValidator() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static boolean isBuildInfoValid() {
        String gitCommit = CheatBreaker.getInstance().getGitCommit();
        String commitId = CheatBreaker.getInstance().getCommitId();
        if (gitCommit.equals("?") && commitId.equals("?")) {
            return true;
        }

        int n = commitId.length();
        return n >= 7 && n <= 9 ? gitCommit.startsWith(commitId) : false;
    }

    public static void drawMessage(float f, float f2) {
        String str = "Unable to connect to the server.";
        String str2 = "Please try again later.";
        if (!isValid()) {
            str = "Unable to validate your client.";
            str2 = "Please ensure everything is genuine.";
        }

        CheatBreaker.getInstance().getFontManager().playRegular16.drawCenteredStringWithShadow(str, f, f2, -806424850);
        CheatBreaker.getInstance().getFontManager().playRegular14.drawCenteredStringWithShadow(str2, f, f2 + 10.0F, -806424850);
    }

    public static boolean isValid() {
        return isBuildInfoValid()
            && CheatBreaker.getInstance().getPlayerDataManager().isValidUuid(CheatBreaker.installationId)
            && !Objects.equals(Minecraft.getMinecraft().getSession().getPlayerID(), CheatBreaker.installationId);
    }
}
