package net.dynu.wpeckers.walktraveler.rest.messaging.user;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.dynu.wpeckers.walktraveler.rest.messaging.ResponseBase;

@Data
@NoArgsConstructor
public class LoginUserResponse extends ResponseBase {
    private UserModel user;
}
