package net.dynu.wpeckers.walktraveler.rest.messaging.point;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadPointsRequest {

    private String userLatitude;
    private String userLongitude;

}
