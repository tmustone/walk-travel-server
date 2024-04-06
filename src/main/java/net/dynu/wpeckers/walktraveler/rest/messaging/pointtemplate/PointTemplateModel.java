package net.dynu.wpeckers.walktraveler.rest.messaging.pointtemplate;

import lombok.Data;

@Data
public class PointTemplateModel {
    private Long pointTemplateId;
    private String title;
    private String description;
    private int weight;
    private String colorCode;
}
