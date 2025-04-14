package net.dynu.wpeckers.walktraveler.rest.messaging.user;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class LoginUserRequest {
    private String sessionId;
    private String fastLoginSecret;
}
