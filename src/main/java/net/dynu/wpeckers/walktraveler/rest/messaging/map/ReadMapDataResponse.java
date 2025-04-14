package net.dynu.wpeckers.walktraveler.rest.messaging.map;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.dynu.wpeckers.walktraveler.rest.messaging.ResponseBase;
import net.dynu.wpeckers.walktraveler.rest.messaging.point.PointModel;
import net.dynu.wpeckers.walktraveler.rest.messaging.user.UserModel;

import java.util.List;

@Data
@NoArgsConstructor
public class ReadMapDataResponse extends ResponseBase {
    private List<UserModel> onlineUsers;
    private List<PointModel> points;
}
