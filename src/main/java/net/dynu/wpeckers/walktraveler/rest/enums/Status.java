package net.dynu.wpeckers.walktraveler.rest.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Status {

    OK(0),
    NOTFOUND(1),
    INVALIDUSER(2),
    ALREADYEXISTS(3),
    ERROR(4);

    private int internalValue;

    Status(int value) {
        this.internalValue = value;
    }
    @JsonCreator
    public static Status getStatusByInteger(final int value) {
        return value < Status.values().length ? Status.values()[value] : Status.ERROR;
    }

    @JsonValue
    public Integer getIntegerValue() {
       return this.internalValue;
    }

}

