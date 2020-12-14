package de.raidcraft.economy;

import io.ebean.annotation.DbEnumValue;

public enum TransactionStatus {

    SUCCESS,
    NOT_ENOUGH_MONEY,
    FAILURE,
    PENDING,
    UNKNOWN;

    @DbEnumValue
    public String getValue() {

        return name();
    }
}
