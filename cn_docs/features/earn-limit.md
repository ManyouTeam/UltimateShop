# 🌈收益上限

## EconomyLimit

EconomyLimit 是基于 Vault 经济 API 的_收益限制插件_。它通过注入受支持的经济提供器来跟踪玩家收入，并按配置规则限制收益。

当玩家在某条有效规则的重置周期内达到上限时，多出的货币不会丢失，而会转入插件管理的虚拟银行。玩家可以稍后取款，但取款仍计入收益；如果取款会超过当前收益上限，则会被拒绝。

在[这里](https://www.spigotmc.org/resources/economylimit-limit-your-player-earnings-by-anyway-daily-weekly-monthly-1-20-1-21-11.133458/)获取插件。

<figure><img src="../.gitbook/assets/image (15).png" alt=""><figcaption></figcaption></figure>

## 安装

1. 安装 `Vault`。
2. 安装任意兼容 `Vault` 的经济插件。
3. 将 `EconomyLimit` 放入插件目录。
4. 启动一次服务器，生成配置文件。
5. 修改数据库、语言和规则设置。
6. 重启服务器，或执行 `/economylimit reload`。
7. 使用 `/economylimit debug` 确认注入正常。

## 命令

#### 玩家命令

* `/economylimit`：查看银行余额和规则进度。
* `/economylimit status`：查看银行余额和规则进度。
* `/economylimit withdraw <amount>`：从虚拟银行取款。

#### 管理员命令

* `/economylimit status <player>`：查看其他玩家的银行余额与进度。
* `/economylimit reload`：重载配置和语言文件。
* `/economylimit debug`：查看 Vault 注入状态、桥接调用命中情况和错误。

### 权限

* `economylimit.withdraw`
* `economylimit.admin.status`
* `economylimit.admin.reload`
* `economylimit.admin.debug`

## 配置

所有限制规则都保存在 `config.yml` 中，例如：

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

#### 条件类型

* `ANY`
* `PERMISSION`
* `WORLD`
* `PLAYER`
* `OP`

## 占位符

#### 银行余额

```yaml
%economylimit_bank_balance%
%economylimit_bank%
```

返回玩家当前虚拟银行余额。

#### 规则名称

```yaml
%economylimit_rule_<ruleId>_name%
```

示例：

```yaml
%economylimit_rule_daily_name%
%economylimit_rule_weekly_name%
```

返回规则显示名称。

#### 已获得收益

```yaml
%economylimit_rule_<ruleId>_earned%
%economylimit_rule_<ruleId>_progress%
%economylimit_rule_<ruleId>_current%
```

示例：

```yaml
%economylimit_rule_daily_earned%
```

返回玩家在该规则当前周期内已获得的收益。

#### 规则上限

```yaml
%economylimit_rule_<ruleId>_limit%
```

示例：

```
%economylimit_rule_daily_limit%
```

返回该规则对玩家生效的当前收益上限。如果此玩家不受该规则限制，则返回语言文件中的 `status.unlimited`。

#### 剩余可获得收益

```yaml
%economylimit_rule_<ruleId>_remaining%
```

示例：

```
%economylimit_rule_daily_remaining%
```

返回玩家在该规则当前周期内还能获得多少收益。

#### 下次重置时间

```yaml
%economylimit_rule_<ruleId>_next_reset%
%economylimit_rule_<ruleId>_reset%
```

示例：

```yaml
%economylimit_rule_daily_next_reset%
%economylimit_rule_weekly_reset%
```

返回该规则的下次重置时间。如果规则不重置，则返回语言文件中的 `status.never`。
