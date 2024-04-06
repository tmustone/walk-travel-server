package net.dynu.wpeckers.walktraveler.rest.messaging.pointtemplate;

import lombok.Data;
import net.dynu.wpeckers.walktraveler.rest.messaging.ResponseBase;

@Data
public class CreatePointTemplateResponse extends ResponseBase {
    private PointTemplateModel pointTemplateModel;
}
