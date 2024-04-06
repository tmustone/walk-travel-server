package net.dynu.wpeckers.walktraveler.database.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
public class BaseObject {

    protected Date createdDate;
    protected Date modifiedDate;

    @PrePersist
    protected void prePersist() {
        if (this.createdDate == null) createdDate = new Date();
        if (this.modifiedDate == null) modifiedDate = new Date();
    }

    @PreUpdate
    protected void preUpdate() {
        this.modifiedDate = new Date();
    }

    @PrePersist
    protected void onCreate() {
        createdDate = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        modifiedDate = new Date();
    }

}
