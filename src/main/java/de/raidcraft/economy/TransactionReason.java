package de.raidcraft.economy;

import io.ebean.annotation.DbEnumValue;

public enum TransactionReason {

    COMMAND,
    PLUGIN,
    ROLLBACK,
    WITHDRAW,
    DEPOSIT,
    OTHER;

    @DbEnumValue
    public String getValue() {

        return name();
    }
}
