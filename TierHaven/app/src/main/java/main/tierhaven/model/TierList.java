package main.tierhaven.model;

/**
 *
 */

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * Tier List
 * A Tier List is a collection of elements arranged in a certain order.
 */
public class TierList implements ServerObject {

    public _embedded _embedded;
    public _links _links;

    public class _embedded{
        public TierList[] tierLists;
    }

    public class _links{
        public self self;
        public profile profile;
        public tierList tierlist;

        public class self{
            public String href;
        }
        public class tierList{
            public String href;
            public boolean templated;
        }
        public class profile{
            public String href;
        }
    }

    private String uuid;
    private transient List<TierRow> tierRows;
    private String tierName;
    private String username;
    private String category;

    public TierList(String uuid, List<TierRow> tierRows, String tierName, String username, String category) {
        this.uuid = uuid;
        this.tierRows = tierRows;
        this.tierName = tierName;
        this.username = username;
        this.category = category;
    }

    public TierList() {
    }

    public String getUuid() {
        return uuid;
    }

    public TierList setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public List<TierRow> getTierRows() {
        return tierRows;
    }

    public TierList setTierRows(List<TierRow> tierRows) {
        this.tierRows = tierRows;
        return this;
    }

    public void addTierRow(TierRow tierRow){
        if(this.tierRows==null)
            this.tierRows=new LinkedList<>();
        this.tierRows.add(tierRow);
    }

    public String getTierName() {
        return tierName;
    }

    public TierList setTierName(String tierName) {
        this.tierName = tierName;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public TierList setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getCategory() {
        return category;
    }

    public TierList setCategory(String category) {
        this.category = category;
        return this;
    }

    public String toJson(){
        Gson gson = new Gson();

        return gson.toJson(this);
    }

    public static TierList parseJson(String json){
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        TierList tierList = gson.fromJson(json,TierList.class);

        String url = tierList._links.self.href;

        String[] values = url.split("/");

        return tierList.setUuid(values[(values.length-1)]);
    }

    public static TierList[] parseJsonArray(String json){
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        TierList tierList = gson.fromJson(json, TierList.class);

        for(TierList t: tierList._embedded.tierLists){
            String url = t._links.self.href;
            String[] values = url.split("/");
            t = t.setUuid(values[(values.length-1)]);
        }
        return tierList._embedded.tierLists;
    }

    public String getObjectModel(){
        return "/tierlist/";
    }
}
