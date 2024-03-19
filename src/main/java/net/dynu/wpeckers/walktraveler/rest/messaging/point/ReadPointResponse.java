package net.dynu.wpeckers.walktraveler.rest.messaging.point;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.dynu.wpeckers.walktraveler.rest.enums.Status;
import net.dynu.wpeckers.walktraveler.rest.messaging.ResponseBase;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReadPointResponse extends ResponseBase {
    private PointModel point;

    public ReadPointResponse(Status status, String message, PointModel point) {
        super(status, message);
        this.point = point;
    }
}
