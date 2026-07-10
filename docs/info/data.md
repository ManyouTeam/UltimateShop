# 📊Data

## Data Type

UltimateShop has 2 data types, they are: Player Data and Server Data.

Player data saves player self data, like each product buy times and sell times, cooldown times, and other things.

Server data saved server total product buy times, sell times and so on. Random placeholder's data will also saved in server data.

## Save&#x20;

For player data, we will auto save player data into database when player leave the server.

For server data, we will auto save server data when you are trying to stop the server normally. (Not directly close the console window, you need use /stop command in server)

Data not saved will be lost after server crash, so enable auto save feature if you don't want this.

## Auto Save

You can use auto save feature so that plugin can store plugin data periodically to avoid data loss due to server crashes. It is not recommended to store at a high frequency, as this can cause server lag. You can find the following content in `config.yml` to set this feature:

```yaml
auto-save:
  enabled: true
  hide-message: false
  period-tick: 6000 # In ticks, 20 ticks = 1 second.
```

## Database

You can find the following content in `config.yml` to set this feature:

```yaml
database:
  enabled: false
  jdbc-url: "jdbc:mysql://localhost:3306/ultimateshop?useSSL=false&autoReconnect=true"
  jdbc-class: "com.mysql.cj.jdbc.Driver"
  properties:
    user: root
    password: 123456
```

| Database                    | JDBC URL Example                              | JDBC Class               |
| --------------------------- | --------------------------------------------- | ------------------------ |
| MySQL                       | jdbc:mysql://localhost:3306/ultimateshop      | com.mysql.cj.jdbc.Driver |
| MariaDB (Added in 4.1.4)    | jdbc:mariadb://localhost:3306/ultimateshop    | org.mariadb.jdbc.Driver  |
| PostgreSQL (Added in 4.1.4) | jdbc:postgresql://localhost:5432/ultimateshop | org.postgresql.Driver    |
| SQLLite (Added in 4.1.4)    | jdbc:sqlite:plugins/UltimateShop/data.db      | org.sqlite.JDBC          |
| h2 (Added in 4.1.4)         | jdbc:h2:file:./plugins/UltimateShop/data      | org.h2.Driver            |
