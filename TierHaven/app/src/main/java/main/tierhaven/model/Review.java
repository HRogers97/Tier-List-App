package main.tierhaven.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

public class Review implements Parcelable {

    private class Links {
        public Self self;
        public Links() {
            self = new Self();
        }
        public class Self {
            public String href;
        }
    }

    private class Embedded {
        public ArrayList<Review> reviews;
        public Embedded() {
            reviews = new ArrayList();
        }
    }

    private String uuid;
    private String itemUuid;
    private String tierUuid;
    private String name;
    private String origin;
    private String newPosition;
    private String reason;

    private Embedded _embedded;
    private Links _links;

    public Review() {
        this.uuid = "-1";
        _embedded = new Embedded();
        _links = new Links();
    }

    public Review(String uuid, String itemUuid, String tierUuid, String origin) {
        this.uuid = uuid;
        this.itemUuid = itemUuid;
        this.tierUuid = tierUuid;
        this.origin = origin;
    }

    public Review(String uuid, String name, String origin, String newPosition, String reason) {
        this.uuid = uuid;
        this.name = name;
        this.origin = origin;
        this.newPosition = newPosition;
        this.reason = reason;
    }

    protected Review(Parcel in) {
        uuid = in.readString();
        name = in.readString();
        itemUuid = in.readString();
        tierUuid = in.readString();
        origin = in.readString();
        newPosition = in.readString();
        reason = in.readString();
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getItemUuid() {
        return itemUuid;
    }

    public void setItemUuid(String itemUuid) {
        this.itemUuid = itemUuid;
    }


    public String getTierUuid() {
        return tierUuid;
    }

    public void setTierUuid(String tierUuid) {
        this.tierUuid = tierUuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getNewPosition() {
        return newPosition;
    }

    public void setNewPosition(String newPosition) {
        this.newPosition = newPosition;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(getUuid());
        parcel.writeString(getName());
        parcel.writeString(getItemUuid());
        parcel.writeString(getTierUuid());
        parcel.writeString(getOrigin());
        parcel.writeString(getNewPosition());
        parcel.writeString(getReason());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String toJson(){
        Gson gson = new Gson();

        return gson.toJson(this);
    }

    public static Review parseJson(String json){
        Gson gson = new Gson();

        return gson.fromJson(json, Review.class);
    }

    public static Review[] parseJsonArray(String json){
        Gson gson = new GsonBuilder().create();
        Review convertedJson = gson.fromJson(json, Review.class);

        int size = convertedJson._embedded.reviews.size();
        Review[] reviewArray = new Review[size];

        for(int i = 0; i < size; i++) {
            Review currReview = convertedJson._embedded.reviews.get(i);

            String[] splits = currReview._links.self.href.split("/");
            currReview.setUuid((splits[4]));

            reviewArray[i] = currReview;
        }

        return reviewArray;
    }
}
