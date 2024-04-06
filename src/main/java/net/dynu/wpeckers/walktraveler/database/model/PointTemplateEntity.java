package net.dynu.wpeckers.walktraveler.database.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "point_templates")
public class PointTemplateEntity extends BaseObject {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long pointTemplateId;
    private String title;
    private String description;
    private int weight;
    private String colorCode;
}
