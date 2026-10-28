package net.minecraft.entity;

public interface IEntityOwnable {
    String getWatchableObjectString();

    Entity getOwner();
}
