package com.cheatbreaker.client.radio;

import java.util.List;

public class RadioManager {
   public List<Station> stationList = RadioApi.loadStations();
   public RadioPlayerThread radioPlayerThread = new RadioPlayerThread();
   public Station station;

   public RadioManager() {
      this.radioPlayerThread.start();
      RadioStartThread radioStartThread = new RadioStartThread();
      radioStartThread.start();
      if (this.stationList.size() > 0) {
         this.station = this.stationList.get(0);
         this.radioPlayerThread.handleStation(this.station);
      }
   }

   public RadioPlayerThread getRadioPlayerThread() {
      return this.radioPlayerThread;
   }

   public List<Station> getStationList() {
      return this.stationList;
   }

   public void handleStation(Station station) {
      station.play();
      this.station = station;
   }

   public Station getStation() {
      return this.station;
   }
}
