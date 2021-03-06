# [2.4.0](https://github.com/raidcraft/economy/compare/v2.3.6...v2.4.0) (2021-02-15)


### Features

* add balance change event ([2bf8844](https://github.com/raidcraft/economy/commit/2bf8844dcf70a09fefaea93d5a78a458736a229e))

## [2.3.6](https://github.com/raidcraft/economy/compare/v2.3.5...v2.3.6) (2021-02-10)


### Bug Fixes

* revert loadbefore art-framework ([5201f98](https://github.com/raidcraft/economy/commit/5201f98323fbe65c2cace21ae67bd314379cdbe6))

## [2.3.5](https://github.com/raidcraft/economy/compare/v2.3.4...v2.3.5) (2021-02-10)


### Bug Fixes

* load economy before art-framework ([c92cf3a](https://github.com/raidcraft/economy/commit/c92cf3a0dab47541671220629213518aa728403d))

## [2.3.4](https://github.com/raidcraft/economy/compare/v2.3.3...v2.3.4) (2021-02-10)


### Bug Fixes

* plugin not loading shaded acf ([190ad4c](https://github.com/raidcraft/economy/commit/190ad4c3f85b6da209a8cbc2e9b4df3cd6c440fa))
* update art-framework ([0ab111d](https://github.com/raidcraft/economy/commit/0ab111dbc549d035d461970d8512fa4bc16846c6))

## [2.3.3](https://github.com/raidcraft/economy/compare/v2.3.2...v2.3.3) (2021-02-08)


### Bug Fixes

* economy not starting due to invalid shaded configlib ([3011cab](https://github.com/raidcraft/economy/commit/3011caba7e4d3e25ca69969c32a5960753fb28ea))

## [2.3.2](https://github.com/raidcraft/economy/compare/v2.3.1...v2.3.2) (2021-01-31)


### Bug Fixes

* **build:** shade adventure text lib properly ([30d3122](https://github.com/raidcraft/economy/commit/30d3122258c61afbbe26d90a8e187847e1ce28c5))

## [2.3.1](https://github.com/raidcraft/economy/compare/v2.3.0...v2.3.1) (2021-01-30)


### Bug Fixes

* messages to economy player not sent ([0a70ac0](https://github.com/raidcraft/economy/commit/0a70ac06c7906272ce01b50f0510e1e923964f18))

# [2.3.0](https://github.com/raidcraft/economy/compare/v2.2.0...v2.3.0) (2021-01-17)


### Features

* add transaction info to received payments ([7f10118](https://github.com/raidcraft/economy/commit/7f101181780d352a9fc9f6cfee31db9fcd0c11ca))
* print transactions sent by server in player chat ([a06e504](https://github.com/raidcraft/economy/commit/a06e504277281ae1c2c099011065060dd09ac908))

# [2.2.0](https://github.com/raidcraft/economy/compare/v2.1.2...v2.2.0) (2021-01-17)


### Features

* **msg:** display source and target balance in transaction history ([3002d16](https://github.com/raidcraft/economy/commit/3002d16dfb7ff71c192dd16c5cdcf63cd0963e5c)), closes [#14](https://github.com/raidcraft/economy/issues/14)

## [2.1.2](https://github.com/raidcraft/economy/compare/v2.1.1...v2.1.2) (2021-01-16)


### Bug Fixes

* add softdepend and depend on art-framework and ebean-wrapper ([e3e768c](https://github.com/raidcraft/economy/commit/e3e768c21f823260ac49ae7a097a7f1de2fd1f1f))

## [2.1.1](https://github.com/raidcraft/economy/compare/v2.1.0...v2.1.1) (2021-01-15)


### Bug Fixes

* register art directly ([59e8b96](https://github.com/raidcraft/economy/commit/59e8b96f5e4b85f7aa34197ae8d6ee71dd5ada59))

# [2.1.0](https://github.com/raidcraft/economy/compare/v2.0.2...v2.1.0) (2021-01-14)


### Features

* add art actions and requirements ([0528952](https://github.com/raidcraft/economy/commit/0528952cdaceb6fc44b36dabee7dd2b2399e7763))

## [2.0.2](https://github.com/raidcraft/economy/compare/v2.0.1...v2.0.2) (2021-01-09)


### Bug Fixes

* **cmd:** payed player receives no message about transaction ([3079b74](https://github.com/raidcraft/economy/commit/3079b743e127bdf37a2a6e2c477ce99d2e87d58d)), closes [#8](https://github.com/raidcraft/economy/issues/8)

## [2.0.1](https://github.com/raidcraft/economy/compare/v2.0.0...v2.0.1) (2020-12-17)


### Bug Fixes

* **test:** fallback to empty wrapper implementation if vault is not found ([7eb29cc](https://github.com/raidcraft/economy/commit/7eb29cc10b1453dec610e694c175493e704899ed))

# [2.0.0](https://github.com/raidcraft/economy/compare/v1.5.3...v2.0.0) (2020-12-17)


### Bug Fixes

* **wrapper:** move economy wrapper into its own package to avoid shading problems ([53be886](https://github.com/raidcraft/economy/commit/53be886ad53f12ae68d8da01ee1c8225fba23913))


### BREAKING CHANGES

* **wrapper:** the economy wrapper is now located in de.raidcraft.economy.wrapper. You need to adjust your shading and imports.

## [1.5.3](https://github.com/raidcraft/economy/compare/v1.5.2...v1.5.3) (2020-12-17)


### Bug Fixes

* **wrapper:** get the proper Economy instance from the plugin ([648d8bb](https://github.com/raidcraft/economy/commit/648d8bb71ed86f1a973d6d88e56dc45cbd54fa93))

## [1.5.2](https://github.com/raidcraft/economy/compare/v1.5.1...v1.5.2) (2020-12-16)


### Bug Fixes

* **vault:** include vault api as transparent dependency ([0ab37c4](https://github.com/raidcraft/economy/commit/0ab37c400cb4af6286f4587a2556fea318dbf57e))

## [1.5.1](https://github.com/raidcraft/economy/compare/v1.5.0...v1.5.1) (2020-12-16)


### Bug Fixes

* **build:** use jdk11 for jitpack builds ([3df6dc8](https://github.com/raidcraft/economy/commit/3df6dc8e95d22a76fe8e3276d7f2e6afba50f751))

# [1.5.0](https://github.com/raidcraft/economy/compare/v1.4.0...v1.5.0) (2020-12-16)


### Features

* add wrapper class for RCEconomy and Vault ([c9559b5](https://github.com/raidcraft/economy/commit/c9559b561faa81314cf88dcb57edc29306107077))

# [1.4.0](https://github.com/raidcraft/economy/compare/v1.3.0...v1.4.0) (2020-12-15)


### Features

* **cmd:** add /money top command ([3a9c9e4](https://github.com/raidcraft/economy/commit/3a9c9e4356d256d5e7de5696d652bcd4bc036fb6))
* shorten server transactions and display details ([7de8b5e](https://github.com/raidcraft/economy/commit/7de8b5e1fe87f3ab57e70bdccc52c3a08779697f))

# [1.3.0](https://github.com/raidcraft/economy/compare/v1.2.1...v1.3.0) (2020-12-15)


### Features

* **transaction:** store original and new balance ([493e5c8](https://github.com/raidcraft/economy/commit/493e5c87b7244eaab79d72dbb5d8e3b23b43f16d))

## [1.2.1](https://github.com/raidcraft/economy/compare/v1.2.0...v1.2.1) (2020-12-15)


### Bug Fixes

* **db:** properly create server account ([eecf3d3](https://github.com/raidcraft/economy/commit/eecf3d3a428fac4a859057bd9bf0300cb4721108))

# [1.2.0](https://github.com/raidcraft/economy/compare/v1.1.0...v1.2.0) (2020-12-14)


### Features

* add paginated transaction history ([0e267e6](https://github.com/raidcraft/economy/commit/0e267e68391ab54ededf1586394ef984508f5e1f))

# [1.1.0](https://github.com/raidcraft/economy/compare/v1.0.0...v1.1.0) (2020-12-14)


### Features

* **cmd:** add /money add and remove commands ([ac76bb5](https://github.com/raidcraft/economy/commit/ac76bb57cf5ee30e923e64d5d0af2457f67b46f9))

# 1.0.0 (2020-12-14)


### Features

* add bank account support ([968c1d4](https://github.com/raidcraft/economy/commit/968c1d46770c35cf611810b3079defdc15b22fdc))
* add transaction history including rollback option ([31b976e](https://github.com/raidcraft/economy/commit/31b976e8d3e41f2f9a4bd2737acc47f4c3f988d6))
* add transfer and set commands ([4f9af81](https://github.com/raidcraft/economy/commit/4f9af813c9011dcc5192792759f14a29b519c1f0))
