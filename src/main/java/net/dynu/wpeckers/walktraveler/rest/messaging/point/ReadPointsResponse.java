package net.dynu.wpeckers.walktraveler.rest.messaging.point;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.dynu.wpeckers.walktraveler.rest.enums.Status;
import net.dynu.wpeckers.walktraveler.rest.messaging.ResponseBase;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadPointsResponse extends ResponseBase {

    private List<PointModel> points;

    public ReadPointsResponse(Status status, String message, List<PointModel> points) {
        super(status, message);
        this.points = points;
    }
}
