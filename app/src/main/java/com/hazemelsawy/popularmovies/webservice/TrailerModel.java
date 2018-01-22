package com.hazemelsawy.popularmovies.webservice;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class TrailerModel {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("quicktime")
    @Expose
    private ArrayList<Object> quicktime = null;
    @SerializedName("youtube")
    @Expose
    private ArrayList<Youtube> youtube = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ArrayList<Object> getQuicktime() {
        return quicktime;
    }

    public void setQuicktime(ArrayList<Object> quicktime) {
        this.quicktime = quicktime;
    }

    public ArrayList<Youtube> getYoutube() {
        return youtube;
    }

    public void setYoutube(ArrayList<Youtube> youtube) {
        this.youtube = youtube;
    }

    public class Youtube {

        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("size")
        @Expose
        private String size;
        @SerializedName("source")
        @Expose
        private String source;
        @SerializedName("type")
        @Expose
        private String type;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

    }

}
