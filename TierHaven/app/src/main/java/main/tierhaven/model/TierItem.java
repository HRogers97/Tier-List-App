package main.tierhaven.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;

public class TierItem implements ServerObject {

    public _embedded _embedded;
    public _links _links;

    public class _embedded{
        public TierItem[] tierItems;
    }

    public class _links{
        public self self;
        public profile profile;
        public tierItems tierItems;

        public class self{
            public String href;
        }
        public class tierItems{
            public String href;
            public boolean templated;
        }
        public class profile{
            public String href;
        }
    }

    private static class BitmapToBase64TypeAdapter implements JsonSerializer<Bitmap>, JsonDeserializer<Bitmap> {
        public Bitmap deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
        {
            byte[] bytes = Base64.decode(json.getAsString(), Base64.NO_WRAP);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }
        public JsonElement serialize(Bitmap src, Type typeOfSrc, JsonSerializationContext context)
        {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            src.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            return new JsonPrimitive(Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP));
        }
    }

    private String uuid;
    private String rowUuid;
    //name of tier item
    private String name;
    //description of tier item
    private String description;
    //image of tier items
    private Bitmap image;

    //placement of tier in tier item
    private int placement;

    public TierItem (){}

    public TierItem(String name, String description, Bitmap image, int placement) {
        this.name = name;
        this.description = description;
        this.image = image;
        this.placement = placement;
    }

    public String getUuid() {
        return uuid;
    }

    public TierItem setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public String getRowUuid() {
        return rowUuid;
    }

    public void setRowUuid(String rowUuid) {
        this.rowUuid = rowUuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        if (image != null){
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.WEBP, 25, stream);
        byte[] byteArray = stream.toByteArray();
        Bitmap compressedBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        this.image = compressedBitmap;
    }
    else
    {
        this.image=null;
    }
    }

    public int getPlacement(){
        return this.placement;
    }

    public void setPlacement(int placement) {
        this.placement = placement;
    }

    public String toJson(){
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeHierarchyAdapter(Bitmap.class, new BitmapToBase64TypeAdapter());
        Gson gson = builder.create();

        return gson.toJson(this);
    }

    public static TierItem parseJson(String json){
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeHierarchyAdapter(Bitmap.class, new BitmapToBase64TypeAdapter());
        Gson gson = builder.create();
        TierItem tierItem= gson.fromJson(json,TierItem.class);

        String url = tierItem._links.self.href;

        String[] values = url.split("/");

        return tierItem.setUuid(values[(values.length-1)]);
    }

    public static TierItem[] parseJsonArray(String json){
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeHierarchyAdapter(Bitmap.class, new BitmapToBase64TypeAdapter());
        Gson gson = builder.create();
        TierItem tierItem= gson.fromJson(json, TierItem.class);

        for(TierItem t: tierItem._embedded.tierItems){
            String url = t._links.self.href;
            String[] values = url.split("/");
            t = t.setUuid(values[(values.length-1)]);
        }
        return tierItem._embedded.tierItems;
    }

    public String getObjectModel(){
        return "/tieritem/";
    }
}
