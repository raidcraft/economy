-- apply changes
alter table rc_eco_transactions drop constraint if exists ck_rc_eco_transactions_reason;
alter table rc_eco_transactions alter column reason type varchar(9) using reason::varchar(9);
alter table rc_eco_transactions add constraint ck_rc_eco_transactions_reason check ( reason in ('COMMAND','PLUGIN','ROLLBACK','WITHDRAW','DEPOSIT','SET_MONEY','OTHER'));
