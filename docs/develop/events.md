---
hidden: true
---

# Events

Start from 3.6.0, UltimateShop bring 2 custom Bukkit events. They are:

* ItemPreTransactionEvent - will call we will start take price and give player products. Can modify **GiveResult** and **TakeResult**. The transaction maybe failed by player's inventory full.
* ItemFinshTransactionEvent - will call when all transaction steps are finished. Read-only.
