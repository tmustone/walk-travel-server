package net.dynu.wpeckers.walktraveler.rest.messaging.user;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.dynu.wpeckers.walktraveler.rest.messaging.ResponseBase;
import net.dynu.wpeckers.walktraveler.rest.messaging.point.PointModel;

import java.util.List;

@Data
@NoArgsConstructor
public class UpdatePositionResponse extends ResponseBase {
    private String longitude;
    private String latitude;
    private List<PointModel> collectedPoints;
}
