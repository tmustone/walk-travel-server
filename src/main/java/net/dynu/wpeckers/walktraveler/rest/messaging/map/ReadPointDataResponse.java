package net.dynu.wpeckers.walktraveler.rest.messaging.map;

import lombok.Data;
import net.dynu.wpeckers.walktraveler.rest.messaging.ResponseBase;
import net.dynu.wpeckers.walktraveler.rest.messaging.pointtemplate.PointTemplateModel;

import java.util.List;

@Data
public class ReadPointDataResponse extends ResponseBase {
    private List<PointTemplateModel> pointTemplates;
}
