package net.dynu.wpeckers.walktraveler.rest.messaging.point;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollectPointRequest {
    private Long pointId;
    private String email;
    private String userLongitude;
    private String userLatitude;

}
