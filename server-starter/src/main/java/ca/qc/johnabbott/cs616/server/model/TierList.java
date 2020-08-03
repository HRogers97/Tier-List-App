package ca.qc.johnabbott.cs616.server.model;

/**
 *
 */
import org.hibernate.annotations.GenericGenerator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;

import javax.persistence.*;
import java.util.List;


/**
 * Tier List
 * A Tier List is a collection of elements arranged in a certain order.
 */
@Entity
@Table(name = "tierlist")
public class TierList {

    @Id
    @GeneratedValue(generator = UuidGenerator.generatorName)
    @GenericGenerator(name = UuidGenerator.generatorName, strategy = "ca.qc.johnabbott.cs616.server.model.UuidGenerator")
    @Column(name = "uuid")
    private String uuid;

    //@OneToMany
    //@Column(name = "tierRows")
    //private List<TierRow> tierRows;

    @Column(name = "tiername")
    private String tierName;

    @Column(name = "username")
    private String username;

    @Column(name = "category")
    private String category;

    public String getUuid() {
        return uuid;
    }

    public TierList setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    //public List<TierRow> getTierRows() {
    //    return tierRows;
    //}

    //public TierList setTierRows(List<TierRow> tierRows) {
    //this.tierRows = tierRows;
    //    return this;
    //}

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
}
