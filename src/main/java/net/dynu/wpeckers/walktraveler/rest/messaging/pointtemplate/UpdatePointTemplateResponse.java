package net.dynu.wpeckers.walktraveler.rest.messaging.pointtemplate;

import lombok.Data;
import net.dynu.wpeckers.walktraveler.rest.messaging.ResponseBase;

@Data
public class UpdatePointTemplateResponse extends ResponseBase {
    private PointTemplateModel pointTemplateModel;
}
