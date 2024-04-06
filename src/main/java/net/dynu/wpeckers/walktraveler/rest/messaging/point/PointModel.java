package net.dynu.wpeckers.walktraveler.rest.messaging.point;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.dynu.wpeckers.walktraveler.database.model.PointStatus;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PointModel {

    private Long pointId;
    private String title;
    private String description;
    private Date createdDate;
    private Date terminationDate;
    private PointStatus pointStatus;
    private String longitude;
    private String latitude;
    private String userEmail;
    private int weight;
    private String colorCode;
}
