package net.dynu.wpeckers.walktraveler.database.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "points")
public class PointEntity extends BaseObject {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long pointId;
    private String title;
    private String description;
    private Date createdDate;
    private Date modifiedDate;
    private Date collectedDate;
    private Date terminationDate;
    private Date deletedDate;
    private PointStatus pointStatus;
    private String longitude;
    private String latitude;
    
    @ManyToOne
    @JoinColumn(name = "user", nullable = true)
    private UserEntity user;
}
