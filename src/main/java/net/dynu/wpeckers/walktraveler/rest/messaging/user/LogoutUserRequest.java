package net.dynu.wpeckers.walktraveler.rest.messaging.user;


import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class LogoutUserRequest {
    private String logout;
}
