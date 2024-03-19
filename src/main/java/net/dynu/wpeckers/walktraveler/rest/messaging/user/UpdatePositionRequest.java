package net.dynu.wpeckers.walktraveler.rest.messaging.user;

import lombok.Data;

@Data
public class UpdatePositionRequest {
    private String longitude;
    private String latitude;
}
