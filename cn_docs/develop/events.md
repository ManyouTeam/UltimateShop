---
hidden: true
---

# 活动

从3.6.0开始，UltimateShop带来2个自定义Bukkit事件。他们是：

* ItemPreTransactionEvent - 将致电我们将开始报价并给予玩家商品。可以修改**GiveResult**和**TakeResult**。交易可能因玩家的物品栏已满而失败。
* ItemFinshTransactionEvent - 当所有交易步骤完成时将调用。 只读。
