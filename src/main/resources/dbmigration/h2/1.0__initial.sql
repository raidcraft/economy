-- apply changes
create table rc_eco_accounts (
  id                            uuid not null,
  balance                       double not null,
  version                       bigint not null,
  when_created                  timestamp not null,
  when_modified                 timestamp not null,
  constraint pk_rc_eco_accounts primary key (id)
);

create table rc_eco_bank_accounts (
  id                            uuid not null,
  name                          varchar(255),
  account_id                    uuid not null,
  owner_id                      uuid not null,
  version                       bigint not null,
  when_created                  timestamp not null,
  when_modified                 timestamp not null,
  constraint uq_rc_eco_bank_accounts_account_id unique (account_id),
  constraint pk_rc_eco_bank_accounts primary key (id)
);

create table rc_eco_bank_accounts_eco_players (
  rc_eco_bank_accounts_id       uuid not null,
  rc_eco_players_id             uuid not null,
  constraint pk_rc_eco_bank_accounts_eco_players primary key (rc_eco_bank_accounts_id,rc_eco_players_id)
);

create table rc_eco_players (
  id                            uuid not null,
  name                          varchar(255),
  account_id                    uuid not null,
  version                       bigint not null,
  when_created                  timestamp not null,
  when_modified                 timestamp not null,
  constraint uq_rc_eco_players_account_id unique (account_id),
  constraint pk_rc_eco_players primary key (id)
);

create table rc_eco_players_eco_bank_accounts (
  rc_eco_players_id             uuid not null,
  rc_eco_bank_accounts_id       uuid not null,
  constraint pk_rc_eco_players_eco_bank_accounts primary key (rc_eco_players_id,rc_eco_bank_accounts_id)
);

create table rc_eco_transactions (
  id                            uuid not null,
  source_id                     uuid not null,
  source_balance                double not null,
  target_id                     uuid not null,
  target_balance                double not null,
  amount                        double not null,
  status                        varchar(17),
  reason                        varchar(11),
  details                       varchar(255),
  data                          clob,
  version                       bigint not null,
  when_created                  timestamp not null,
  when_modified                 timestamp not null,
  constraint ck_rc_eco_transactions_status check ( status in ('SUCCESS','NOT_ENOUGH_MONEY','FAILURE','PENDING','EMPTY_TRANSACTION','INVALID','UNKNOWN')),
  constraint ck_rc_eco_transactions_reason check ( reason in ('COMMAND','PLUGIN','ROLLBACK','WITHDRAW','DEPOSIT','SET_BALANCE','OTHER')),
  constraint pk_rc_eco_transactions primary key (id)
);

alter table rc_eco_bank_accounts add constraint fk_rc_eco_bank_accounts_account_id foreign key (account_id) references rc_eco_accounts (id) on delete restrict on update restrict;

create index ix_rc_eco_bank_accounts_owner_id on rc_eco_bank_accounts (owner_id);
alter table rc_eco_bank_accounts add constraint fk_rc_eco_bank_accounts_owner_id foreign key (owner_id) references rc_eco_players (id) on delete restrict on update restrict;

create index ix_rc_eco_bank_accounts_eco_players_rc_eco_bank_accounts on rc_eco_bank_accounts_eco_players (rc_eco_bank_accounts_id);
alter table rc_eco_bank_accounts_eco_players add constraint fk_rc_eco_bank_accounts_eco_players_rc_eco_bank_accounts foreign key (rc_eco_bank_accounts_id) references rc_eco_bank_accounts (id) on delete restrict on update restrict;

create index ix_rc_eco_bank_accounts_eco_players_rc_eco_players on rc_eco_bank_accounts_eco_players (rc_eco_players_id);
alter table rc_eco_bank_accounts_eco_players add constraint fk_rc_eco_bank_accounts_eco_players_rc_eco_players foreign key (rc_eco_players_id) references rc_eco_players (id) on delete restrict on update restrict;

alter table rc_eco_players add constraint fk_rc_eco_players_account_id foreign key (account_id) references rc_eco_accounts (id) on delete restrict on update restrict;

create index ix_rc_eco_players_eco_bank_accounts_rc_eco_players on rc_eco_players_eco_bank_accounts (rc_eco_players_id);
alter table rc_eco_players_eco_bank_accounts add constraint fk_rc_eco_players_eco_bank_accounts_rc_eco_players foreign key (rc_eco_players_id) references rc_eco_players (id) on delete restrict on update restrict;

create index ix_rc_eco_players_eco_bank_accounts_rc_eco_bank_accounts on rc_eco_players_eco_bank_accounts (rc_eco_bank_accounts_id);
alter table rc_eco_players_eco_bank_accounts add constraint fk_rc_eco_players_eco_bank_accounts_rc_eco_bank_accounts foreign key (rc_eco_bank_accounts_id) references rc_eco_bank_accounts (id) on delete restrict on update restrict;

create index ix_rc_eco_transactions_source_id on rc_eco_transactions (source_id);
alter table rc_eco_transactions add constraint fk_rc_eco_transactions_source_id foreign key (source_id) references rc_eco_accounts (id) on delete restrict on update restrict;

create index ix_rc_eco_transactions_target_id on rc_eco_transactions (target_id);
alter table rc_eco_transactions add constraint fk_rc_eco_transactions_target_id foreign key (target_id) references rc_eco_accounts (id) on delete restrict on update restrict;

