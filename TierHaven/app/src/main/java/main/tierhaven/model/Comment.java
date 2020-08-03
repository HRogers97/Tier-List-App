package main.tierhaven.model;

import com.google.gson.Gson;

import java.util.UUID;

public class Comment implements ServerObject {

    private String uuid;
    private String tieruuid;
    private String body;

    // Flag to set the edit and delete buttons in the recycler view
    private transient boolean currSession;

    private _embedded _embedded;
    private _links _links;

    public Comment(String tierID, String body) {
        this.uuid = UUID.randomUUID().toString();
        this.tieruuid = tierID;
        this.body = body;
        this.currSession = false;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTieruuid() {
        return tieruuid;
    }

    public void setTieruuid(String tieruuid) {
        this.tieruuid = tieruuid;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public boolean isCurrSession() {
        return currSession;
    }

    public void setCurrSession(boolean currSession) {
        this.currSession = currSession;
    }

    public String toJson(){
        Gson gson = new Gson();

        return gson.toJson(this);
    }

    public static Comment parseJson(String json){
        Gson gson = new Gson();

        Comment comment = gson.fromJson(json, Comment.class);

        // Get the uuid from the url and set it
        String[] url = (comment._links.self.href).split("/");
        String uuid = url[url.length - 1];
        comment.setUuid(uuid);

        return comment;
    }

    public static Comment[] parseJsonArray(String json){
        Gson gson = new Gson();

        // Get array of comments
        Comment[] comments = gson.fromJson(json, Comment.class)._embedded.comments;

        // Set the uuids for each comment
        for(Comment comment : comments){
            String[] url = (comment._links.self.href).split("/");
            String uuid = url[url.length - 1];

            comment.setUuid(uuid);
        }

        return comments;
    }

    @Override
    public String getObjectModel(){
        return "/comment/";
    }

    // Private classes to parse JSon object
    private class _embedded{
        public Comment[] comments;
    }
    private class _links{
        public href self;
        public href profile;
    }
    private class href{
        public String href;
    }
}
