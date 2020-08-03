package ca.qc.johnabbott.cs616.server.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "review")
public class Review {

    @Id
    @GeneratedValue(generator = UuidGenerator.generatorName)
    @GenericGenerator(name = UuidGenerator.generatorName, strategy = "ca.qc.johnabbott.cs616.server.model.UuidGenerator")
    @Column(name = "uuid")
    private String uuid;

    @Column(name = "itemuuid")
    private String itemUuid;

    @Column(name = "tieruuid")
    private String tierUuid;

    @Column(name = "name")
    private String name;

    @Column(name = "origin")
    private String origin;

    @Column(name = "newposition")
    private String newPosition;

    @Column(name = "reason")
    private String reason;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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
}
