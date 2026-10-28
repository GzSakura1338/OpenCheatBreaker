package com.cheatbreaker.client.util;

import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import java.net.Proxy;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

public class AltLoginThread extends Thread {
    public boolean flag = false;
    public Minecraft mc = Minecraft.getMinecraft();
    public String username;
    public String password;
    public boolean flag2;

    public Minecraft getMc() {
        return this.mc;
    }

    public Session authenticate(String str, String str2) {
        YggdrasilAuthenticationService yggdrasilAuthenticationService = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
        YggdrasilUserAuthentication createUserAuthentication = (YggdrasilUserAuthentication)yggdrasilAuthenticationService.createUserAuthentication(Agent.MINECRAFT);
        createUserAuthentication.setUsername(str);
        createUserAuthentication.setPassword(str2);

        try {
            createUserAuthentication.logIn();
            this.flag2 = true;
            return new Session(createUserAuthentication.getSelectedProfile().getName(), createUserAuthentication.getSelectedProfile().getId().toString(), createUserAuthentication.getAuthenticatedToken(), "mojang");
        } catch (AuthenticationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isFlag() {
        return this.flag;
    }

    public void setMc(Minecraft mc) {
        this.mc = mc;
    }

    public AltLoginThread() {
        this.flag2 = false;
    }

    public AltLoginThread(String str, String str2) {
        super("Alt Login Thread");
        this.username = str;
        this.password = str2;
        this.flag2 = false;
    }

    public boolean isFlag2() {
        return this.flag2;
    }

    public void setUsername(String str) {
        this.username = str;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getPassword() {
        return this.password;
    }

    @Override
    public void run() {
        if (this.password != null && !this.password.equals("")) {
            Session authenticate = this.authenticate(this.username, this.password);
            if (authenticate == null) {
                this.flag2 = false;
            } else {
                this.mc.setSession(authenticate);
                this.flag2 = true;
            }
        } else {
            this.mc.setSession(new Session(this.username, "", "", "mojang"));
            this.flag2 = true;
        }
    }

    public String getUsername() {
        return this.username;
    }

    public AltLoginThread(String str) {
        super("Alt Login Thread");
        this.username = str.split(":")[0];
        this.password = str.split(":")[1];
        this.flag2 = false;
    }

    public void setPassword(String str) {
        this.password = str;
    }

    public void setFlag2(boolean flag) {
        this.flag2 = flag;
    }
}
