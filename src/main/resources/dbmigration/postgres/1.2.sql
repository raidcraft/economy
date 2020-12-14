-- apply changes
alter table rc_eco_transactions drop constraint if exists ck_rc_eco_transactions_status;
alter table rc_eco_transactions alter column status type varchar(17) using status::varchar(17);
alter table rc_eco_transactions add constraint ck_rc_eco_transactions_status check ( status in ('SUCCESS','NOT_ENOUGH_MONEY','FAILURE','PENDING','EMPTY_TRANSACTION','UNKNOWN'));
