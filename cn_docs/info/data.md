# 📊数据存储

## 数据类型

UltimateShop 的数据分为两类：玩家数据与服务器数据。

玩家数据记录每位玩家自己的信息，例如各商品的购买次数、出售次数、冷却时间等。

服务器数据记录全服商品购买总次数、出售总次数等信息。随机占位符的数据也保存在服务器数据中。

## 保存

玩家离开服务器时，插件会自动将其数据保存到数据库。

服务器正常关闭时，插件会自动保存服务器数据。请使用 `/stop` 命令关闭服务器，不要直接关闭控制台窗口。

服务器崩溃会导致尚未保存的数据丢失。若要减少这类损失，请启用自动保存。

## 自动保存

自动保存会定期写入插件数据，以避免服务器崩溃造成数据丢失。不建议设置过高的保存频率，否则可能造成卡顿。在 `config.yml` 中找到以下配置即可设置：

```yaml
auto-save:
  enabled: true
  hide-message: false
  period-tick: 6000 # In ticks, 20 ticks = 1 second.
```

## 数据库

在 `config.yml` 中找到以下配置进行设置：

```yaml
database:
  enabled: false
  jdbc-url: "jdbc:mysql://localhost:3306/ultimateshop?useSSL=false&autoReconnect=true"
  jdbc-class: "com.mysql.cj.jdbc.Driver"
  properties:
    user: root
    password: 123456
```

| 数据库 | JDBC URL 示例 | JDBC 驱动类 |
| --- | --- | --- |
| MySQL | jdbc:mysql://localhost:3306/ultimateshop | com.mysql.cj.jdbc.Driver |
| MariaDB（4.1.4 新增） | jdbc:mariadb://localhost:3306/ultimateshop | org.mariadb.jdbc.Driver |
| PostgreSQL（4.1.4 新增） | jdbc:postgresql://localhost:5432/ultimateshop | org.postgresql.Driver |
| SQLite（4.1.4 新增） | jdbc:sqlite:plugins/UltimateShop/data.db | org.sqlite.JDBC |
| H2（4.1.4 新增） | jdbc:h2:file:./plugins/UltimateShop/data | org.h2.Driver |
