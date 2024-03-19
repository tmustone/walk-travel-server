package net.dynu.wpeckers.walktraveler.rest.messaging.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.dynu.wpeckers.walktraveler.rest.enums.Status;
import net.dynu.wpeckers.walktraveler.rest.messaging.ResponseBase;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadUserResponse extends ResponseBase {
    private UserModel user;

    public ReadUserResponse(Status status, String message, UserModel user) {
        super(status, message);
        this.user = user;
    }
}
