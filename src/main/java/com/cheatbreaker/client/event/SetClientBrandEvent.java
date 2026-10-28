package com.cheatbreaker.client.event;

public class SetClientBrandEvent extends CancellableEvent {
    public String brand;

    public SetClientBrandEvent(String str) {
        this.brand = str;
    }

    public String getBrand() {
        return this.brand;
    }
}
