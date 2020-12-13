-- apply changes
create table rc_eco_bank_accounts (
  id                            varchar(40) not null,
  name                          varchar(255),
  type                          varchar(255),
  balance                       double not null,
  owner_id                      varchar(40),
  version                       integer not null,
  when_created                  timestamp not null,
  when_modified                 timestamp not null,
  constraint pk_rc_eco_bank_accounts primary key (id),
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
  foreign key (account_id) references rc_eco_player_accounts (id) on delete restrict on update restrict
);

create table rc_eco_players_eco_bank_accounts (
  rc_eco_players_id             varchar(40) not null,
  rc_eco_bank_accounts_id       varchar(40) not null,
  constraint pk_rc_eco_players_eco_bank_accounts primary key (rc_eco_players_id,rc_eco_bank_accounts_id),
  foreign key (rc_eco_players_id) references rc_eco_players (id) on delete restrict on update restrict,
  foreign key (rc_eco_bank_accounts_id) references rc_eco_bank_accounts (id) on delete restrict on update restrict
);

create table rc_eco_player_accounts (
  id                            varchar(40) not null,
  name                          varchar(255),
  type                          varchar(255),
  balance                       double not null,
  version                       integer not null,
  when_created                  timestamp not null,
  when_modified                 timestamp not null,
  constraint pk_rc_eco_player_accounts primary key (id)
);

create index ix_rc_eco_bank_accounts_name on rc_eco_bank_accounts (name);
create index ix_rc_eco_player_accounts_name on rc_eco_player_accounts (name);
