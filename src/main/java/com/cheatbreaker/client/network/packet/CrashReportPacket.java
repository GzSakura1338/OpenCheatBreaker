package com.cheatbreaker.client.network.packet;

import com.cheatbreaker.client.network.AbstractPacket;
import com.cheatbreaker.client.network.PacketBuffer;
import com.cheatbreaker.client.network.PacketHandler;
import com.cheatbreaker.client.network.ServerPacketHandler;

public class CrashReportPacket extends AbstractPacket {
   public String reportId;
   public String clientBuild;
   public String serverAddress;
   public String clientVersion;
   public String gitCommit;
   public String branch;
   public String os;
   public String cpu;
   public String gpu;
   public String memory;
   public String javaVersion;
   public String jvm;
   public String jvmArguments;
   public String crashReport;

   public String getJvm() {
      return this.jvm;
   }

   public String getGitCommit() {
      return this.gitCommit;
   }

   public String getServerAddress() {
      return this.serverAddress;
   }

   public CrashReportPacket(
      String str,
      String str2,
      String str3,
      String str4,
      String str5,
      String str6,
      String str7,
      String str8,
      String str9,
      String str10,
      String str11,
      String str12,
      String str13,
      String str14
   ) {
      this.reportId = str;
      this.clientBuild = str2;
      this.serverAddress = str3;
      this.clientVersion = str4;
      this.gitCommit = str5;
      this.branch = str6;
      this.os = str7;
      this.cpu = str8;
      this.gpu = str9;
      this.memory = str10;
      this.javaVersion = str11;
      this.jvm = str12;
      this.jvmArguments = str13;
      this.crashReport = str14;
   }

   public String getMemory() {
      return this.memory;
   }

   public CrashReportPacket() {
   }

   public String getOs() {
      return this.os;
   }

   public String getClientBuild() {
      return this.clientBuild;
   }

   public String getCpu() {
      return this.cpu;
   }

   @Override
   public void handle(PacketHandler packetHandler) {
      ((ServerPacketHandler)packetHandler).handleCrashReport(this);
   }

   public String getJavaVersion() {
      return this.javaVersion;
   }

   public String getReportId() {
      return this.reportId;
   }

   public String getGpu() {
      return this.gpu;
   }

   public String getJvmArguments() {
      return this.jvmArguments;
   }

   public String getCrashReport() {
      return this.crashReport;
   }

   public String getClientVersion() {
      return this.clientVersion;
   }

   @Override
   public void read(PacketBuffer packetBuffer) {
      this.reportId = packetBuffer.readString();
      this.clientBuild = packetBuffer.readString();
      this.serverAddress = packetBuffer.readString();
      this.clientVersion = packetBuffer.readString();
      this.gitCommit = packetBuffer.readString();
      this.branch = packetBuffer.readString();
      this.os = packetBuffer.readString();
      this.cpu = packetBuffer.readString();
      this.gpu = packetBuffer.readString();
      this.memory = packetBuffer.readString();
      this.javaVersion = packetBuffer.readString();
      this.jvm = packetBuffer.readString();
      this.jvmArguments = packetBuffer.readString();
      this.crashReport = packetBuffer.readString();
   }

   public String getBranch() {
      return this.branch;
   }

   @Override
   public void write(PacketBuffer packetBuffer) {
      packetBuffer.writeString(this.reportId);
      packetBuffer.writeString(this.clientBuild);
      packetBuffer.writeString(this.serverAddress);
      packetBuffer.writeString(this.clientVersion);
      packetBuffer.writeString(this.gitCommit);
      packetBuffer.writeString(this.branch);
      packetBuffer.writeString(this.os);
      packetBuffer.writeString(this.cpu);
      packetBuffer.writeString(this.gpu);
      packetBuffer.writeString(this.memory);
      packetBuffer.writeString(this.javaVersion);
      packetBuffer.writeString(this.jvm);
      packetBuffer.writeString(this.jvmArguments);
      packetBuffer.writeString(this.crashReport);
   }
}
