# 🔑物品发放方式

在 `config.yml` 中修改以下内容，即可切换发放方式：

```yaml
give-item:
  # Support value: BUKKIT, SMART
  # SMART will cost more server performance but will follow the vanilla max stack to give player item, also support check full.
  give-method: BUKKIT
  # Only support SMART give method.
  check-full: false
```

插件提供两种方式。

## BUKKIT

这是最稳定的发放方式，但一次购买数量超过物品最大堆叠数量时，可能出现问题。

例如，购买 64 个末影珍珠时，插件会错误地将它们全部堆在一个槽位中。

下图的第一个槽位展示了此问题：

<figure><img src="../.gitbook/assets/image (2).png" alt=""><figcaption></figcaption></figure>

## SMART - 早期测试

SMART 是 UltimateShop 自行实现的发放方式，遵循原版最大堆叠数量。同样购买 64 个末影珍珠时，由于末影珍珠的堆叠上限是 16，会被分为 4 组。它还支持检查购买后物品栏是否会满。

此方式会产生额外的性能开销，大型服务器或在线人数较多时请谨慎使用。

<figure><img src="../.gitbook/assets/image (3).png" alt=""><figcaption></figcaption></figure>

不直接使用 NMS 就完整还原原版机制非常困难，因此此方式仍处于测试阶段，可能存在较多问题。
