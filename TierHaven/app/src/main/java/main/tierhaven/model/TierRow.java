package main.tierhaven.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TierRow implements ServerObject {

    public _embedded _embedded;
    public _links _links;

    public class _embedded{
        public TierRow[] tierRows;
    }

    public class _links{
        public self self;
        public profile profile;
        public tierRow tierRow;

        public class self{
            public String href;
        }
        public class tierRow{
            public String href;
            public boolean templated;
        }
        public class profile{
            public String href;
        }
    }

    private String uuid;

    private String tierUuid;

    //name of tier
    private String title;
    //list of tier items in tier
    private transient List<TierItem> images;
    //color of tier
    private int color;
    //placement of of row
    private int placement;

    public TierRow(){}

    public TierRow(String title, List<TierItem> images, int color, int placement) {
        this.title = title;
        this.images = images;
        this.color = color;
        this.placement = placement;
    }

    public String getUuid() {
        return uuid;
    }

    public TierRow setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public String getTierUuid() {
        return tierUuid;
    }

    public void setTierUuid(String tierUuid) {
        this.tierUuid = tierUuid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<TierItem> getImages() {
        return images;
    }

    public void setImages(List<TierItem> images) {
        this.images = images;
    }

    public void addTierItem(TierItem item){
        if(this.images==null)
            this.images=new LinkedList<>();
        this.images.add(item);
    }
  
    public int getPlacement() {
        return placement;
    }

    public void setPlacement(int placement) {
        this.placement = placement;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String toJson(){
        Gson gson = new Gson();

        return gson.toJson(this);
    }

    public static TierRow parseJson(String json){
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        TierRow tierRow= gson.fromJson(json,TierRow.class);

        String url = tierRow._links.self.href;

        String[] values = url.split("/");

        return tierRow.setUuid(values[(values.length-1)]);
    }

    public static TierRow[] parseJsonArray(String json){
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        TierRow tierRow = gson.fromJson(json, TierRow.class);

        for(TierRow t: tierRow._embedded.tierRows){
            String url = t._links.self.href;
            String[] values = url.split("/");
            t = t.setUuid(values[(values.length-1)]);
        }
        return tierRow._embedded.tierRows;
    }

    public String getObjectModel(){
        return "/tierrow/";
    }
}
