package ca.qc.johnabbott.cs616.server.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "tierrow")
public class TierRow {

    @Id
    @GeneratedValue(generator = UuidGenerator.generatorName)
    @GenericGenerator(name = UuidGenerator.generatorName, strategy = "ca.qc.johnabbott.cs616.server.model.UuidGenerator")
    @Column(name = "uuid")
    private String uuid;

    @Column(name = "tieruuid")
    private String tierUuid;

    @Column(name = "title")
    private String title;

    //@OneToMany
    //@Column(name = "tierItems")
    //private List<TierItem> tierItems;

    @Column(name = "color")
    private int color;

    @Column(name = "placement")
    private int placement;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    //public List<TierItem> getTierItems() {
    //    return tierItems;
    //}

    //public void setTierItems(List<TierItem> tierItems) {
    //    this.tierItems = tierItems;
    //}
  
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
}
