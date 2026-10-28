package com.cheatbreaker.client.radio;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.TimeZone;
import javax.xml.parsers.DocumentBuilderFactory;
import net.minecraft.util.ResourceLocation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Station {
    public int duration;
    public String coverURL = "";
    public String name;
    public String logoURL;
    public String genre;
    public String title;
    public boolean favorite;
    public String artist;
    public String album;
    public String currentSongURL;
    public String streamURL;
    public ResourceLocation previousResource;
    public LocalDateTime startTime;
    public boolean playing;
    public ResourceLocation currentResource;

    public void loadPlaylist() {
        try {
            Document parse = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(this.getCurrentSongURL());
            NodeList elementsByTagName = parse.getElementsByTagName("playlist");

            for (int i = 0; i < elementsByTagName.getLength(); i++) {
                Node item = elementsByTagName.item(i);
                Element element = (Element)item;
                this.setTitle(this.getTagText(element, "title"));
                this.setArtist(this.getTagText(element, "artist"));
                this.setAlbum(this.getTagText(element, "album"));
                this.setCoverURL(this.getTagText(element, "cover"));
                this.setDuration(Integer.parseInt(this.getTagText(element, "duration")));
                String tagText = this.getTagText(element, "programStartTS");
                String str = "dd MMM yy hh:mm:ss";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(str);
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                if (this.currentResource != null && !("client/songs/" + this.getTitle()).equals(this.currentResource.getResourcePath())) {
                    this.previousResource = this.currentResource;
                    this.currentResource = null;
                }

                try {
                    Date parse2 = simpleDateFormat.parse(tagText);
                    this.setStartTime(LocalDateTime.ofInstant(parse2.toInstant(), ZoneId.systemDefault()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (this.playing) {
            this.playing = false;
            this.play();
        }
    }

    public void setPreviousResource(ResourceLocation resourceLocation) {
        this.previousResource = resourceLocation;
    }

    public void setStartTime(LocalDateTime localDateTime) {
        this.startTime = localDateTime;
    }

    public String getCurrentSongURL() {
        return this.currentSongURL;
    }

    public String getGenre() {
        return this.genre;
    }

    public String getStreamURL() {
        return this.streamURL;
    }

    public void setArtist(String str) {
        this.artist = str;
    }

    public void setCoverURL(String str) {
        this.coverURL = str;
    }

    public void play() {
        RadioApi.play(this.streamURL);
    }

    public String getName() {
        return this.name;
    }

    public void setCurrentResource(ResourceLocation resourceLocation) {
        this.currentResource = resourceLocation;
    }

    public void setDuration(int n) {
        this.duration = n;
    }

    public boolean isFavorite() {
        return this.favorite;
    }

    public LocalDateTime getStartTime() {
        return this.startTime;
    }

    public ResourceLocation getPreviousResource() {
        return this.previousResource;
    }

    public String getTitle() {
        return this.title;
    }

    public Station(String str, String str2, String str3, String str4, String str5) {
        this.name = str;
        this.logoURL = str2;
        this.genre = str3;
        this.currentSongURL = str4;
        this.streamURL = str5;
    }

    public String getTagText(Element element, String str) {
        try {
            NodeList elementsByTagName = element.getElementsByTagName(str);
            Element item = (Element)elementsByTagName.item(0);
            NodeList childNodes = item.getChildNodes();
            return childNodes.item(0).getNodeValue().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public int getDuration() {
        return this.duration;
    }

    public String getAlbum() {
        return this.album;
    }

    public void setPlaying(boolean flag) {
        this.playing = flag;
    }

    @Override
    public String toString() {
        return "Station(name="
            + this.getName()
            + ", logoURL="
            + this.getLogoURL()
            + ", genre="
            + this.getGenre()
            + ", currentSongURL="
            + this.getCurrentSongURL()
            + ", streamURL="
            + this.getStreamURL()
            + ", playing="
            + this.isPlaying()
            + ", currentResource="
            + this.getCurrentResource()
            + ", previousResource="
            + this.getPreviousResource()
            + ", title="
            + this.getTitle()
            + ", artist="
            + this.getArtist()
            + ", album="
            + this.getAlbum()
            + ", coverURL="
            + this.getCoverURL()
            + ", favorite="
            + this.isFavorite()
            + ", duration="
            + this.getDuration()
            + ", startTime="
            + this.getStartTime()
            + ")";
    }

    public String getLogoURL() {
        return this.logoURL;
    }

    public void setAlbum(String str) {
        this.album = str;
    }

    public String getCoverURL() {
        return this.coverURL;
    }

    public String getArtist() {
        return this.artist;
    }

    public boolean isPlaying() {
        return this.playing;
    }

    public ResourceLocation getCurrentResource() {
        return this.currentResource;
    }

    public void setFavorite(boolean flag) {
        this.favorite = flag;
    }

    public void setTitle(String str) {
        this.title = str;
    }
}
