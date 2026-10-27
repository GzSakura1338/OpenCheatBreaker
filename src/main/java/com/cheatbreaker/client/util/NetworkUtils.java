package com.cheatbreaker.client.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;

public class NetworkUtils {
   public static ArrayList<InetAddress> localAddresses;
   public static ArrayList<InetAddress> netmasks;

   public static boolean isLanAddress(InetAddress inetAddress) {
      try {
         for (int i = 0; i < localAddresses.size(); i++) {
            if (isSameSubnet(localAddresses.get(i), inetAddress, netmasks.get(i))) {
               return true;
            }
         }
      } catch (Exception e) {
      }

      return false;
   }

   public static void loadInterfaces() throws java.net.SocketException {
      localAddresses = new ArrayList<>();
      netmasks = new ArrayList<>();
      Enumeration networkInterfaces = NetworkInterface.getNetworkInterfaces();

      while (networkInterfaces.hasMoreElements()) {
         NetworkInterface nextElement = (NetworkInterface)networkInterfaces.nextElement();
         if (nextElement != null && !nextElement.isLoopback() && nextElement.isUp()) {
            for (InterfaceAddress interfaceAddress : nextElement.getInterfaceAddresses()) {
               if (interfaceAddress != null) {
                  InetAddress address = interfaceAddress.getAddress();
                  if (address instanceof Inet4Address) {
                     InetAddress prefixToNetmask2 = prefixToNetmask(interfaceAddress.getNetworkPrefixLength());
                     if (prefixToNetmask2 != null) {
                        localAddresses.add(address);
                        netmasks.add(prefixToNetmask2);
                     }
                  }
               }
            }
         }
      }
   }

   public static boolean isSameSubnet(InetAddress inetAddress, InetAddress inetAddress2, InetAddress inetAddress3) {
      byte[] address = inetAddress.getAddress();
      byte[] address2 = inetAddress2.getAddress();
      byte[] address3 = inetAddress3.getAddress();

      for (int i = 0; i < address.length; i++) {
         if ((address[i] & address3[i]) != (address2[i] & address3[i])) {
            return false;
         }
      }

      return true;
   }

   public static InetAddress prefixToNetmask(int n) {
      try {
         int n2 = Integer.MIN_VALUE;

         for (int i = n - 1; i > 0; i--) {
            n2 >>= 1;
         }

         String str = (n2 >> 24 & 0xFF) + "." + (n2 >> 16 & 0xFF) + "." + (n2 >> 8 & 0xFF) + "." + (n2 & 0xFF);
         return InetAddress.getByName(str);
      } catch (Exception e) {
         e.printStackTrace();
         return null;
      }
   }
}
