package net.dynu.wpeckers.walktraveler.rest.messaging.point;

import lombok.Data;
import net.dynu.wpeckers.walktraveler.rest.messaging.ResponseBase;
import net.dynu.wpeckers.walktraveler.rest.messaging.user.UserModel;

@Data
public class UserInfoResponse extends ResponseBase {
    private UserModel user;
}
