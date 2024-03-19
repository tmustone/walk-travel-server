package net.dynu.wpeckers.walktraveler.rest.messaging.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.dynu.wpeckers.walktraveler.rest.enums.Status;
import net.dynu.wpeckers.walktraveler.rest.messaging.ResponseBase;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadUsersResponse extends ResponseBase {
    private List<UserModel> users;

    public ReadUsersResponse(Status status, String message, List<UserModel> users) {
        super(status, message);
        this.users = users;
    }
}
