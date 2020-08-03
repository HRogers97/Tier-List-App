package ca.qc.johnabbott.cs616.server.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "comment")
public class Comment {

    @Id
    @GeneratedValue(generator = UuidGenerator.generatorName)
    @GenericGenerator(name = UuidGenerator.generatorName, strategy = "ca.qc.johnabbott.cs616.server.model.UuidGenerator")
    @Column(name = "uuid")
    private String uuid;

    //@Column(name = "userUuid")
    //private String userUuid;

    @Column(name = "tieruuid")
    private String tieruuid;

    @Column(name = "body")
    private String body;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    //public String getUserID() {
    //    return userUuid;
    //}

    //public void setUserID(String userID) {
    //    this.userUuid = userID;
    //}

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
}
