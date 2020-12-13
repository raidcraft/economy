-- apply changes
create table rc_eco_bank_accounts (
  id                            uuid not null,
  name                          varchar(255),
  type                          varchar(255),
  balance                       double not null,
  owner_id                      uuid,
  version                       bigint not null,
  when_created                  timestamp not null,
  when_modified                 timestamp not null,
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

create table rc_eco_player_accounts (
  id                            uuid not null,
  name                          varchar(255),
  type                          varchar(255),
  balance                       double not null,
  version                       bigint not null,
  when_created                  timestamp not null,
  when_modified                 timestamp not null,
  constraint pk_rc_eco_player_accounts primary key (id)
);

create index ix_rc_eco_bank_accounts_name on rc_eco_bank_accounts (name);
create index ix_rc_eco_player_accounts_name on rc_eco_player_accounts (name);
create index ix_rc_eco_bank_accounts_owner_id on rc_eco_bank_accounts (owner_id);
alter table rc_eco_bank_accounts add constraint fk_rc_eco_bank_accounts_owner_id foreign key (owner_id) references rc_eco_players (id) on delete restrict on update restrict;

create index ix_rc_eco_bank_accounts_eco_players_rc_eco_bank_accounts on rc_eco_bank_accounts_eco_players (rc_eco_bank_accounts_id);
alter table rc_eco_bank_accounts_eco_players add constraint fk_rc_eco_bank_accounts_eco_players_rc_eco_bank_accounts foreign key (rc_eco_bank_accounts_id) references rc_eco_bank_accounts (id) on delete restrict on update restrict;

create index ix_rc_eco_bank_accounts_eco_players_rc_eco_players on rc_eco_bank_accounts_eco_players (rc_eco_players_id);
alter table rc_eco_bank_accounts_eco_players add constraint fk_rc_eco_bank_accounts_eco_players_rc_eco_players foreign key (rc_eco_players_id) references rc_eco_players (id) on delete restrict on update restrict;

alter table rc_eco_players add constraint fk_rc_eco_players_account_id foreign key (account_id) references rc_eco_player_accounts (id) on delete restrict on update restrict;

create index ix_rc_eco_players_eco_bank_accounts_rc_eco_players on rc_eco_players_eco_bank_accounts (rc_eco_players_id);
alter table rc_eco_players_eco_bank_accounts add constraint fk_rc_eco_players_eco_bank_accounts_rc_eco_players foreign key (rc_eco_players_id) references rc_eco_players (id) on delete restrict on update restrict;

create index ix_rc_eco_players_eco_bank_accounts_rc_eco_bank_accounts on rc_eco_players_eco_bank_accounts (rc_eco_bank_accounts_id);
alter table rc_eco_players_eco_bank_accounts add constraint fk_rc_eco_players_eco_bank_accounts_rc_eco_bank_accounts foreign key (rc_eco_bank_accounts_id) references rc_eco_bank_accounts (id) on delete restrict on update restrict;

