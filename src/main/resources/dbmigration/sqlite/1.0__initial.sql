-- apply changes
create table rc_eco_accounts (
  id                            varchar(40) not null,
  balance                       double not null,
  version                       integer not null,
  when_created                  timestamp not null,
  when_modified                 timestamp not null,
  constraint pk_rc_eco_accounts primary key (id)
);

create table rc_eco_bank_accounts (
  id                            varchar(40) not null,
  name                          varchar(255),
  account_id                    varchar(40) not null,
  owner_id                      varchar(40) not null,
  version                       integer not null,
  when_created                  timestamp not null,
  when_modified                 timestamp not null,
  constraint uq_rc_eco_bank_accounts_account_id unique (account_id),
  constraint pk_rc_eco_bank_accounts primary key (id),
  foreign key (account_id) references rc_eco_accounts (id) on delete restrict on update restrict,
  foreign key (owner_id) references rc_eco_players (id) on delete restrict on update restrict
);

create table rc_eco_bank_accounts_eco_players (
  rc_eco_bank_accounts_id       varchar(40) not null,
  rc_eco_players_id             varchar(40) not null,
  constraint pk_rc_eco_bank_accounts_eco_players primary key (rc_eco_bank_accounts_id,rc_eco_players_id),
  foreign key (rc_eco_bank_accounts_id) references rc_eco_bank_accounts (id) on delete restrict on update restrict,
  foreign key (rc_eco_players_id) references rc_eco_players (id) on delete restrict on update restrict
);

create table rc_eco_players (
  id                            varchar(40) not null,
  name                          varchar(255),
  account_id                    varchar(40) not null,
  version                       integer not null,
  when_created                  timestamp not null,
  when_modified                 timestamp not null,
  constraint uq_rc_eco_players_account_id unique (account_id),
  constraint pk_rc_eco_players primary key (id),
  foreign key (account_id) references rc_eco_accounts (id) on delete restrict on update restrict
);

create table rc_eco_players_eco_bank_accounts (
  rc_eco_players_id             varchar(40) not null,
  rc_eco_bank_accounts_id       varchar(40) not null,
  constraint pk_rc_eco_players_eco_bank_accounts primary key (rc_eco_players_id,rc_eco_bank_accounts_id),
  foreign key (rc_eco_players_id) references rc_eco_players (id) on delete restrict on update restrict,
  foreign key (rc_eco_bank_accounts_id) references rc_eco_bank_accounts (id) on delete restrict on update restrict
);

create table rc_eco_transactions (
  id                            varchar(40) not null,
  source_id                     varchar(40) not null,
  source_balance                double not null,
  target_id                     varchar(40) not null,
  target_balance                double not null,
  amount                        double not null,
  status                        varchar(17),
  reason                        varchar(11),
  details                       varchar(255),
  data                          clob,
  version                       integer not null,
  when_created                  timestamp not null,
  when_modified                 timestamp not null,
  constraint ck_rc_eco_transactions_status check ( status in ('SUCCESS','NOT_ENOUGH_MONEY','FAILURE','PENDING','EMPTY_TRANSACTION','INVALID','UNKNOWN')),
  constraint ck_rc_eco_transactions_reason check ( reason in ('COMMAND','PLUGIN','ROLLBACK','WITHDRAW','DEPOSIT','SET_BALANCE','OTHER')),
  constraint pk_rc_eco_transactions primary key (id),
  foreign key (source_id) references rc_eco_accounts (id) on delete restrict on update restrict,
  foreign key (target_id) references rc_eco_accounts (id) on delete restrict on update restrict
);

