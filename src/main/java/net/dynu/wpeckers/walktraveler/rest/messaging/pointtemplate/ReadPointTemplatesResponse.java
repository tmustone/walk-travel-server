package net.dynu.wpeckers.walktraveler.rest.messaging.pointtemplate;

import lombok.Data;
import net.dynu.wpeckers.walktraveler.rest.messaging.ResponseBase;

import java.util.List;

@Data
public class ReadPointTemplatesResponse extends ResponseBase {
    List<PointTemplateModel> pointTemplates;
}
