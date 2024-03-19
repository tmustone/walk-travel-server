package net.dynu.wpeckers.walktraveler.rest.messaging;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.dynu.wpeckers.walktraveler.rest.enums.Status;

@Data
@NoArgsConstructor
public abstract class ResponseBase {

    private Status status;
    private String message;

    public ResponseBase(Status status, String message) {
        this.status = status;
        this.message = message;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
