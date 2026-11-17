# 🆚功能对比

| 功能 | 免费版 | 付费版 | 其他常见付费插件 |
| --- | --- | --- | --- |
| **更多命令**：部分命令仅限付费版使用，详见[命令](commands-and-permissions.md)。 | ❌ | ✅ | - |
| **更多命令参数**：付费版支持更多参数，例如用 `*` 表示所有商品，详见[命令](commands-and-permissions.md)。 | ❌ | ✅ | - |
| **Bedrock 菜单**：自动将 Java 菜单转换为 Bedrock 表单 UI，支持自动生成原版物品图标和手动设置自定义图标，详见[Bedrock 菜单](../menus/bedrock-menus-premium.md)。 | ❌ | ✅ | ❌ |
| **对话框菜单**：在 Paper 1.21.9+ 上为 Java 客户端提供 Minecraft 原生对话框 UI。现有菜单文件可使用动作按钮或 `item-action-list` 布局，包括物品图标和可点击标签，无需资源包。详见[对话框菜单](../menus/dialog-menus-premium.md)。 | ❌ | ✅ | |
| **Ore 资源包菜单**：图形化对话框布局支持自定义背景、按钮和商品卡片。卡片在商品名称与描述旁显示物品图标，并提供用于购买和出售的详情按钮。可自定义纹理和布局模板，沿用现有菜单条件与动作。需要 Paper 1.21.9+、Java 客户端及生成的资源包。详见[Ore 资源包布局](../menus/dialog-menus-premium.md#ore-layout)。 | ❌ | ✅ | |
| **多服务器同步**：在同一代理下的多台服务器间同步全服数据，详见[多服务器同步](../features/multi-server-sync-premium.md)。 | ❌ | ✅ | ❓部分插件支持 MySQL 数据库（本插件免费版也支持），我们还支持通过 BungeeCord 插件消息通道同步数据。 |
| **物品名称本地化**：为未设置自定义名称的物品显示本地化名称，详见[物品名称本地化](../features/localized-item-name-premium.md)。 | ❌ | ✅ | ❌ |
| **随机占位符**：支持内置随机占位符，详见[随机占位符](../placeholders/random-placeholder-premium.md)。 | ❌ | ✅ | ❌ |
| **条件占位符**：支持内置条件占位符，详见[条件占位符](../placeholders/conditional-placeholder-premium.md)。 | ❌ | ✅ | ❌ |
| **自定义占位符（测试版）**：支持自定义数值或文本，既可按玩家独立保存，也可全服共享。可用命令设置值或增加数值，再通过占位符展示。详见[自定义占位符](../placeholders/custom-placeholder-premium.md)。 | ❌ | ✅ | |
| **动态价格状态**：比较当前价格与基础价格，并在商品信息描述中显示状态，详见[动态价格状态](../dynamic-prices/dynamic-price-status-premium.md)。 | ❌ | ✅ | ❓部分插件可能支持，但本插件的动态价格还支持多种数学公式与占位符。 |
| **出售倍率**：按条件提供出售加成或税费。`MAX` 选取符合条件的最高倍率，`STACK` 则将符合条件的倍率相乘。详见[出售倍率](../shops/sell-multiplier-premium.md)。 | ❌ | ✅ | |
| **价格修正器（实验性）**：根据实际出售的物品调整出售奖励，支持耐久度、描述或 NBT 中的数值、MythicChanger 匹配规则。为商品设置 `price-modifier: true` 启用。部分修正器类型需要额外插件。详见[价格修正器](../shops/price-modifier.md)。 | ✅ | ✅ | |
| **更多内置占位符**：付费版提供更多占位符，详见[内置占位符](../placeholders/built-in-placeholders.md)。 | ❌ | ✅ | ❌ |
| **更高级的动作**：付费版支持更多高级动作，详见[动作格式](../format/action-format.md)。 | ❌ | ✅ | ❌许多同类插件只支持以命令作为动作。 |
| **更高级的条件**：付费版支持更多高级条件，详见[条件格式](../format/condition-format.md)。 | ❌ | ✅ | ❌许多同类插件只支持权限条件。 |
| **更高级的重置模式**：为商品购买/出售次数提供更多重置方式，详见[购买/出售次数重置](../shops/product-config-buy-sell-times-reset.md)。 | ❌ | ✅ | ❓ |
| **出售棒**：创建出售棒，快速出售容器内的全部物品，详见[出售棒](../features/sell-stick-premium.md)。 | ❌ | ✅ | ❌需要另装其他作者的出售棒插件。 |
| **出售箱（实验性）**：区块已加载且所有者在线时，自动出售箱内物品。可配置使用次数、出售倍率与全息信息，不支持 Folia。详见[出售箱](../features/sell-chest-premium.md)。 | ❌ | ✅ | |
| **更多 ItemFormat™ 配置项**：包括 AdvancedEnchantments 对接、Paper 1.21.6+ 的数据组件，以及 Minecraft 1.21.2+ 的可装备设置。详见[ItemFormat](../format/itemformat-tm/)。 | ❌ | ✅ | ❓许多同类插件只支持少量原版组件，ItemFormat™ 则支持几乎全部原版组件。 |
| **交易日志**：记录交易信息，便于检查价格配置是否按预期运行，详见[交易日志](../features/log-transaction-premium.md)。 | ❌ | ✅ | ✅ |
| **自定义点击事件**：自定义玩家用不同按键点击商品时的行为，详见[自定义点击事件](../features/custom-click-event-premium.md)。 | ❌ | ✅ | ❌ |
| **自定义单项内容类型**：支持 `CUSTOM` 类型，详见[单项内容](../shops/products-config-single-thing/)。 | ❌ | ✅ | ❌ |
| **商品子按钮**：同一商品可在多个槽位或菜单中显示，甚至可在不同位置使用不同展示物品，但它们仍属于同一商品。详见[商品](../shops/products.md)。 | ❌ | ✅ | ❌ |
| **动态布局**：在菜单布局中使用动态数值，详见[通用菜单](../menus/general-menus.md)。 | ❌ | ✅ | |
| **条件显示**：根据条件，在同一槽位显示不同商品或按钮。按顺序检查候选项，条件不满足时可使用后备选项。详见[条件显示](../menus/conditional-display-premium.md)。 | ❌ | ✅ | |
| **自定义命令**：通过自定义命令打开通用菜单或商店菜单，详见[通用菜单](../menus/general-menus.md)。 | ❌ | ✅ | |
| **自动更新 GUI 标题**：每次点击后或按秒定时更新标题，详见[通用菜单](../menus/general-menus.md)。 | ❌ | ✅ | |
| **玩家独立语言与高级消息格式**：支持在消息中发送 Boss 栏、动作栏、声音和标题，详见[这里](../features/advanced-language-managment.md)。 | ❌ | ✅ | |
| **潜影盒出售（早期测试）**：在 `config.yml` 启用后，自动检查并出售潜影盒内的物品，适用于普通出售、出售棒、全部出售 GUI、出售箱等场景。 | ❌ | ✅ | |
| **游戏内编辑器**：使用 `/shop editor` 编辑商店、商品和菜单，详见[游戏内编辑器](../features/in-game-editor-premium.md)。 | ❌ | ✅ | |
| **搜索 GUI**：搜索所需商品，详见[此页面](../menus/search-menus-premium.md)。 | ❌ | ✅ | |
| **收藏菜单**：保存收藏商品并在专用菜单中访问，可在编辑模式下排序或移除收藏。详见[收藏菜单](../menus/favourite-menus-premium.md)。 | ❌ | ✅ | |
| **共享商品数据（早期测试）**：让多个商品共享购买次数、出售次数、限额、冷却重置时间等数据，详见[此页面](../shops/shared-product-data-premium.md)。 | ❌ | ✅ | |
| **更多功能……**：还有许多细节差异未在此列出，未来也会继续增加付费版专属功能。 | ❌ | ✅ | |
