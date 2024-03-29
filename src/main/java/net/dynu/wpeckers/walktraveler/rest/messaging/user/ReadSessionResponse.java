package net.dynu.wpeckers.walktraveler.rest.messaging.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ReadSessionResponse {
    private String sessionId;
}
