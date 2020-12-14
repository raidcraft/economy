package de.raidcraft.economy;

import io.ebean.annotation.DbEnumValue;

public enum TransactionReason {

    COMMAND,
    PLUGIN,
    ROLLBACK,
    WITHDRAW,
    DEPOSIT,
    SET_MONEY,
    OTHER;

    @DbEnumValue
    public String getValue() {

        return name();
    }
}
