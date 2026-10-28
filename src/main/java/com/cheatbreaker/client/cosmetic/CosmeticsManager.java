package com.cheatbreaker.client.cosmetic;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.client.Minecraft;

public class CosmeticsManager {
    public List<Cosmetic> cosmeticList = new CopyOnWriteArrayList<>();
    public List<Cosmetic> cosmeticList2 = new CopyOnWriteArrayList<>();
    public List<Cosmetic> cosmeticList3 = new CopyOnWriteArrayList<>();

    public Cosmetic findCosmetic(UUID uuid) {
        return this.findCosmetic(this.cosmeticList3, uuid);
    }

    public Cosmetic findCosmetic2(UUID uuid) {
        return this.findCosmetic(this.cosmeticList, uuid);
    }

    public List<Cosmetic> getList() {
        String replace = Minecraft.getMinecraft().getSession().getPlayerID().replace("-", "");
        return Stream.concat(Stream.concat(this.cosmeticList2.stream(), this.cosmeticList.stream()), this.cosmeticList3.stream())
            .filter(cosmetic -> cosmetic.getPlayerId().replace("-", "").equals(replace))
            .collect(Collectors.toList());
    }

    public List<Cosmetic> getCosmeticList() {
        return this.cosmeticList;
    }

    public Cosmetic findCosmetic(List<Cosmetic> list, UUID uuid) {
        return list.stream().filter(cosmetic -> cosmetic.isEquipped() && cosmetic.getPlayerId().equals(uuid.toString())).findFirst().orElse(null);
    }

    public List<Cosmetic> getCosmeticList2() {
        return this.cosmeticList2;
    }

    public Cosmetic findCosmetic3(UUID uuid) {
        return this.findCosmetic(this.cosmeticList2, uuid);
    }

    public List<Cosmetic> getCosmeticList3() {
        return this.cosmeticList3;
    }

    public void removePlayerCosmetics(String str) {
        this.cosmeticList.removeIf(cosmetic -> cosmetic.getPlayerId().equals(str));
        this.cosmeticList2.removeIf(cosmetic -> cosmetic.getPlayerId().equals(str));
        this.cosmeticList3.removeIf(cosmetic -> cosmetic.getPlayerId().equals(str));
    }
}
