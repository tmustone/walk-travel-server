package net.dynu.wpeckers.walktraveler.rest.messaging.pointtemplate;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.dynu.wpeckers.walktraveler.rest.messaging.ResponseBase;

@Data
public class ReadPointTemplateResponse extends ResponseBase {
    private PointTemplateModel pointTemplate;
}
