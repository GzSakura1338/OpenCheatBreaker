package com.cheatbreaker.client.event;

import org.java_websocket.handshake.ServerHandshake;

public class ServerHandshakeEvent extends Event {
    public ServerHandshake serverHandshake;

    public ServerHandshakeEvent(ServerHandshake serverHandshake) {
        this.serverHandshake = serverHandshake;
    }

    public void setServerHandshake(ServerHandshake serverHandshake) {
        this.serverHandshake = serverHandshake;
    }

    public ServerHandshake getServerHandshake() {
        return this.serverHandshake;
    }
}
