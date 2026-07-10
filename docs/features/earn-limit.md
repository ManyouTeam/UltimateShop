# 🌈Earn Limit

## EconomyLimit&#x20;

EconomyLimit is _an earning limit plugin_ built around the Vault economy API.\
It injects into supported economy providers, tracks player income, and applies configurable earning caps based on your rules.\
When a player reaches the cap of any active rule during its reset period, the extra money is not lost.\
Instead, it is redirected into a virtual bank managed by the plugin.\
Players can withdraw money from the virtual bank later, but withdrawals still count as earned money.\
If a withdrawal would exceed the current earning limit, it will be denied.

Get it [here](https://www.spigotmc.org/resources/economylimit-limit-your-player-earnings-by-anyway-daily-weekly-monthly-1-20-1-21-11.133458/).

<figure><img src="../.gitbook/assets/image (17).png" alt=""><figcaption></figcaption></figure>

## Install

1. Install `Vault`
2. Install any `Vault`-compatible economy plugin
3. Put `EconomyLimit` into your plugins folder
4. Start the server once to generate files
5. Edit database, language, and rule settings
6. Restart the server or run `/economylimit reload`
7. Use `/economylimit debug` to confirm injection works

## Commands

#### Player Commands



* `/economylimit` View your bank balance and rule progress
* `/economylimit status` View your bank balance and rule progress
* `/economylimit withdraw <amount>` Withdraw money from the virtual bank

#### Admin Commands

* `/economylimit status <player>` View another player's bank balance and progress
* `/economylimit reload` Reload config and language files
* `/economylimit debug` View Vault injection status, bridge hits, and errors

### Permissions

* `economylimit.withdraw`
* `economylimit.admin.status`
* `economylimit.admin.reload`
* `economylimit.admin.debug`

## Config

All limit rule config saved in `config.yml` file, example:

```yaml
rules:
  daily:
    display-name: "{lang:rules.daily.name}"
    reset:
      mode: DAILY
      time: "00:00"
    limits:
      - limit: 50000
      - condition:
          type: PERMISSION
          value: economylimit.rule.daily.vip
        limit: 100000
      - condition:
          type: PERMISSION
          value: economylimit.rule.daily.bypass
        limit: -1
```

#### Condition Types

* `ANY`
* `PERMISSION`
* `WORLD`
* `PLAYER`
* `OP`

## Placeholders

#### Bank Balance

```yaml
%economylimit_bank_balance%
%economylimit_bank%
```

Returns the player's current virtual bank balance.

#### Rule Name

```yaml
%economylimit_rule_<ruleId>_name%
```

Examples:

```yaml
%economylimit_rule_daily_name%
%economylimit_rule_weekly_name%
```

Returns the rule display name.

#### Earned Amount

```yaml
%economylimit_rule_<ruleId>_earned%
%economylimit_rule_<ruleId>_progress%
%economylimit_rule_<ruleId>_current%
```

Example:

```yaml
%economylimit_rule_daily_earned%
```

Returns how much the player has earned in the current cycle of that rule.

#### Rule Limit

```yaml
%economylimit_rule_<ruleId>_limit%
```

Example:

```
%economylimit_rule_daily_limit%
```

Returns the player's current earning cap for that rule.\
If that rule is unlimited for the player, it returns `status.unlimited` from the language file.

#### Remaining Earnings

```yaml
%economylimit_rule_<ruleId>_remaining%
```

Example:

```
%economylimit_rule_daily_remaining%
```

Returns how much the player can still earn in the current cycle of that rule.

#### Next Reset Time

```yaml
%economylimit_rule_<ruleId>_next_reset%
%economylimit_rule_<ruleId>_reset%
```

Examples:

```yaml
%economylimit_rule_daily_next_reset%
%economylimit_rule_weekly_reset%
```

Returns the next reset time of that rule.\
If the rule does not reset, it returns `status.never` from the language file.
