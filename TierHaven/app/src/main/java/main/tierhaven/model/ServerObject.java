package main.tierhaven.model;

/**
 * Is used by the TierList, TierRow & TierItem models
 */
public interface ServerObject {

    public String toJson();

    public static ServerObject parseJson(String json) {
        return null;
    }

    public static ServerObject[] parseJsonArray(String json) {
        return new ServerObject[0];
    }

    public String getObjectModel();

    public String getUuid();

}
