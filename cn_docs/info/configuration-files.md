# 🛠️配置文件

插件会生成以下配置文件和目录，其中部分内容仅在首次使用对应功能后生成。

* `datas`：插件数据文件目录。<mark style="color:red;">仅在不使用数据库时生成，不要修改其中的任何内容</mark>。
* `items`：已保存物品的文件目录。&#x20;
* `languages`：语言文件目录。在 `config.yml` 中通过 `config-files.language` 选择语言文件，并在语言文件中自定义游戏内消息。关于玩家个人语言以及动作栏、标题、Boss 栏、声音的发送方式，请查看[此页面](../features/advanced-language-managment.md)。
* `menus/common_menus`：通用菜单配置目录。&#x20;
* `menus/shop_menu_templates`：商店菜单模板目录。
* `ui`：共享图形样式目录，包含 `classic.yml`、`bedrock.yml` 和自定义预设。通过 `menu.ui.style` 或菜单的 `ui.style` 选择，详见[菜单配置与继承](../menus/menu-configuration.md)。
* `menus/buy_more_menus`：批量购买菜单目录。
* `menus/favourite_menus`：收藏菜单目录。
* `menus/search_menus`：搜索菜单目录。
* `menus/item_sell_menus`：物品出售菜单目录。

{% hint style="info" %}
请注意：`/shop` 命令打开的是菜单，而不是商店本身。要设置商品在商店菜单中的槽位，请修改菜单配置文件。
{% endhint %}

* `shops`：商店配置目录。&#x20;
* `placeholders/random_placeholders`：随机占位符配置目录。
* `placeholders/custom_placeholders`：自定义占位符配置目录。
* `placeholders/conditional_placeholders`：条件占位符配置目录。
* `sell_sticks`：出售棒配置目录。
* `config.yml`：插件的全局常用设置。
* `generated-item-format.yml`：执行 `/shop generateitemformat` 后，插件会将手持物品解析为 **ItemFormat**，并把解析结果写入此文件。
* `minecraft-resources/<version>/`：自动菜单图标和物品名称本地化使用的 Minecraft 数据、纹理与语言缓存，由插件自动准备。
* `XX.txt`：[交易日志](../features/log-transaction-premium.md)功能生成的记录文件，文件名由 `config.yml` 中的 `log-transaction.file` 决定。

插件启动时，会将旧版根目录中的占位符目录和专用菜单目录自动迁移到 `placeholders/` 与 `menus/`。若新旧位置存在同名文件，则使用新位置的文件，保留旧文件，并在控制台提示手动合并。`menus/` 中原有的通用菜单文件和自定义子目录会移动到 `menus/common_menus/`。

Paper 组件的 ItemFormat 示例与保存/重建检查方法，见[组件格式](../format/itemformat-tm/component-format.md)和[测试 Paper 组件](../format/itemformat-tm/component-testing.md)。

## config.yml 内容

建议直接查看 GitHub 上的文件，因为 Wiki 中的 `config.yml` 可能不是**最新版本**。点击[这里](https://github.com/PQguanfang/UltimateShop/blob/master/plugin/src/main/resources/config.yml)在 **GitHub** 上查看。

```yaml
# UltimateShop by @PQguanfang
#
# Read the Wiki: ultimateshop.superiormc.cn

# Some options require restart the server to make effect.

debug: false

config-files:
  language: en_US
  # Premium version only.
  per-player-language: true
  force-parse-mini-message: true

# Support value: COMPONENT or LEGACY
# COMPONENT mode requires Paper 1.21.5+ and UltimateShop Premium.
debuild-item-method: 'LEGACY'

# Support value: DEFAULT or ITEMBRIDGE
hook-item-method: 'DEFAULT'

# By default, we will auto force display fail message to make sure player know whether they have successfully clicked the product button.
# If set to false, we will not force display fail message and you can also use hide-message option in shop configs to hide the fail messages.
force-display-fail-message: false

cache:
  # If you are facing multi servers sync issue, try greater this value.
  load-delay: 7
  reset-task:
    # Reset deadlines are rounded forward into this many seconds so nearby entries run together.
    # Set to 0 to disable deadline merging. Maximum value: 3600.
    merge-seconds: 1
    # Maximum number of due reset entries processed in one server tick. Maximum value: 10000.
    max-tasks-per-tick: 100

sell:
  # Support Value: Bukkit or ItemFormat.
  # For each product, you can add match-item section to make custom sell match method, for more info, please view Wiki.
  sell-method: Bukkit
  # Only support ItemFormat sell method.
  item-format:
    require-same-key: false
    ignore-key:
      - 'lore'
      - 'damage'
      - 'enchants'
      - 'tool.damage-per-block'
      - 'nbt.string.CustomNBTKey'
  auto-hide-sell-all-message: true
  sell-all:
    ignore-items:
      # Format: Shop ID;;Item ID
      - 'hideshop;;A'
  # PREMIUM version only.
  sell-stick:
    # The min value here is 5, if you set value less than 5, we will auto set the cooldown to 5 tick.
    # In ticks.
    cooldown: 5
    # Support value: LEFT, RIGHT and LEFT;;RIGHT
    # Do not change this value unless you are know what you are doing!
    click-type: RIGHT
  # PREMIUM version only.
  sell-chest:
    enabled: true
    debug: false
    period-ticks: 600
    batch-count: 5
    send-sell-message: true
    price-empty: '{lang}'
    hologram:
      enabled: true
      # Support value: DecentHolograms, CMI, FancyHolograms
      plugin: 'DecentHolograms'
  max-amount: -1
  # Premium version only.
  shulker-box-sell: true
  # Premium version only
  multiplier:
    enabled: false
    display-original-price: true
    black-dynamic-price: true
    black-shops:
      - 'example'
    # Support value: MAX, STACK
    # MAX mode: will use the maximum value as the result
    # STACK mode: As long as the player meets the conditions, it will be stacked and multiplied.
    mode: STACK
    value:
      default: 1
      rich: 0.9
      vip: 1.1
    value-conditions:
      # Tax
      rich:
        1:
          type: placeholder
          placeholder: '%vault_eco_balance%'
          rule: '>='
          value: 50000
      # Bonus for VIP
      vip:
        1:
          type: permission
          permission: 'group.vip'
  # Item-aware modifiers are a separate global sell feature.
  # Modifiers are applied in configuration order and merged into the final multiplier.
  price-modifier:
    item-sell-menu:
      # Products with `price-modifier: true` open this item sell menu when a sell action is chosen.
      enabled: true
      menu: item-sell
    durability:
      type: durability
      enabled: true
      # 1 means losing 1% durability deducts 1% from the price.
      deduction-coefficient: 1
      minimum-multiplier: 0.1
      # You can use minimum-price instead of, or together with, minimum-multiplier.
      # minimum-price: 1
    lore:
      type: lore
      enabled: false
      # SET uses the captured number as the price; MULTIPLY multiplies the current price by it.
      # Invalid operations are reported and the modifier is ignored.
      operation: SET
      # The default pattern matches lore such as: Item Value: 500
      pattern: 'Item Value[：:]\s*([+-]?(?:\d+(?:\.\d+)?|\.\d+))'
      value-group: 1
      strip-color: true
      case-sensitive: true
      # Values outside this inclusive range, or longer than maximum-number-length, are ignored.
      minimum-value: 0
      maximum-value: 1000000
      maximum-number-length: 64
    nbt:
      type: nbt
      enabled: false
      # Requires the NBTAPI plugin. Nested keys can use dot notation.
      key: item_value
      # AUTO, BYTE, SHORT, INT, LONG, FLOAT, DOUBLE or STRING.
      value-type: AUTO
      # SET uses the NBT value as the price; MULTIPLY multiplies the current price by it.
      # Invalid operations are reported and the modifier is ignored.
      operation: SET
      # Values outside this inclusive range, or longer than maximum-number-length, are ignored.
      minimum-value: 0
      maximum-value: 1000000
      maximum-number-length: 64
    mythic-changer:
      type: match_item
      enabled: false
      # MAX, MIN or STACK. STACK applies matching rules in configuration order.
      mode: STACK
      rules:
        named-item:
          # ADD or MULTIPLY. Invalid operations are reported and this rule is ignored.
          operation: MULTIPLY
          # Supports math expressions and PlaceholderAPI. Invalid expressions are reported and ignored.
          value: 1.2
          match-item:
            has-name: true
        special-lore:
          operation: ADD
          # ADD adds this value to the price multiplier: 1 + 0.1 = 1.1.
          value: 0.1
          match-item:
            contains-lore:
              - 'Special'

give-item:
  # Support value: BUKKIT, SMART
  # SMART will cost more server performance but will follow the vanilla max stack to give player item, also support check full.
  give-method: SMART
  # Only support SMART give method.
  check-full: true

menu:
  force-ui:
    enabled: false
    # inventory, arcmenu, dialog, ore, item-action-list or form.
    # Overrides menu-specific enable switches; unavailable presentations use Inventory.
    type: inventory
    # File name under ui/; empty keeps the menu's selected style.
    style: ''
  dialog:
    enabled: false
    # If enabled, keep the product information dialog open after a buy or sell action.
    not-auto-close: true
    auto-add-sprite:
      enabled: true
      # Hide label sprites when Ore/item-action-list already displays an item icon.
      # Per-button dialog.show-sprite overrides this automatic rule.
      auto-hide: true
      format: "<sprite:\"{namespace}:{atlas}\":{path}>"
    default-button: '{lang:menu.dialog.default-button}'
    details: '{lang:menu.dialog.details}'
    ore:
      show-item-icon:
        enabled: true
        # Hide the overlaid quantity text when the display-item count is 1.
        auto-hide-one: true
      # Rebuilds pack/ on startup and /shop reload only on supported Paper servers,
      # with menu.dialog.enabled enabled and an enabled Ore menu.
      # 0 selects the built-in format for your Minecraft version; override for a different client.
      pack-format: 0
    search:
      input: '{lang:menu.dialog.search.input}'
      buttons:
        search: '{lang:menu.dialog.search.buttons.search}'
    buy-more:
      display-item: true
      title: '{lang:menu.dialog.buy-more.title}'
      input: '{lang:menu.dialog.buy-more.input}'
      buttons:
        confirm: '{lang:menu.dialog.buy-more.buttons.confirm}'
    info:
      display-item: true
      title: '{lang:menu.dialog.info.title}'
      buttons:
        buy: '{lang:menu.dialog.info.buttons.buy}'
        sell: '{lang:menu.dialog.info.buttons.sell}'
        buy-more: '{lang:menu.dialog.info.buttons.buy-more}'
        sell-all: '{lang:menu.dialog.info.buttons.sell-all}'
        back: '{lang:menu.dialog.info.buttons.back}'
    favourite-edit:
      title: '{lang:menu.dialog.favourite-edit.title}'
      content: '{lang:menu.dialog.favourite-edit.content}'
      buttons:
        forward: '{lang:menu.dialog.favourite-edit.buttons.forward}'
        backward: '{lang:menu.dialog.favourite-edit.buttons.backward}'
        remove: '{lang:menu.dialog.favourite-edit.buttons.remove}'
        back: '{lang:menu.dialog.favourite-edit.buttons.back}'
  # Premium version only
  bedrock:
    enabled: true
    search:
      input: '{lang:menu.bedrock.search.input}'
      buttons:
        search: '{lang:menu.bedrock.search.buttons.search}'
    favourite-edit:
      title: '{lang:menu.bedrock.favourite-edit.title}'
      content: '{lang:menu.bedrock.favourite-edit.content}'
      buttons:
        forward: '{lang:menu.bedrock.favourite-edit.buttons.forward}'
        backward: '{lang:menu.bedrock.favourite-edit.buttons.backward}'
        remove: '{lang:menu.bedrock.favourite-edit.buttons.remove}'
        back: '{lang:menu.bedrock.favourite-edit.buttons.back}'
    auto-add-icon:
      enabled: true
      format: "https://raw.githubusercontent.com/InventivetalentDev/minecraft-assets/refs/heads/{version}/assets/minecraft/textures/{path}.png"
    # Support value: FLOODGATE, UUID
    check-method: FLOODGATE
    # Optional action when Bedrock players tap shop products, skipping product info.
    # Only LEFT executes this action. Buy More confirmation controls are unchanged.
    # click-event: select-amount
    # If enabled, we will try to reopen shop menu after player successfully buy or sell products.
    not-auto-close: true
    # Make this option be empty to disable.
    price-extra-line:
      default: '{lang}'
      only-buy: '{lang}'
      only-sell: '{lang}'
    buy-or-sell:
      title: '{lang}'
      buttons:
        amount:
          name: '{lang}'
          tip: '{lang}'
    info:
      title: '{lang}'
      buttons:
        buy: '{lang}'
        sell: '{lang}'
        buy-more: '{lang}'
        sell-all: '{lang}'
        # Remove this option if you don't want this button.
        back: '{lang}'
  arcmenu:
    enabled: false
  ui:
    # File name in plugins/UltimateShop/ui, without .yml.
    style: classic
  anti-dupe-checker: false
  prompt:
    cancel-keyword: '{lang}'
  # Recommend use when you are running big server, it will make player no longer quickly click
  # and reopen shop menu to make sure UltimateShop not lag your server.
  # In ticks. Only supports classic inventory type view.
  cooldown:
    click: -1
    reopen: -1
  ignore-click-outside: false
  # PREMIUM version only, if enabled, can update dynamic value used in GUI title.
  title-update:
    # Require packetevents.
    enabled: false
    # Whether gui title will refresh every buttons every 1 second.
    # This will refresh placeholder that displayed in menu title.
    circle-update: false
    # Whether gui title will refresh every buttons when click any of them.
    # This will refresh placeholder that displayed in menu title.
    click-update: true
    resend-items-pack: false
  menu-update:
    # Whether menu will refresh every buttons every 1 second.
    # This will refresh placeholder that displayed in display item lore.
    # But maybe lead to server lag if you have much online players, and they are all opening shop GUI.
    circle-update: false
    # Whether menu will refresh every buttons when click any of them.
    # This will refresh placeholder that displayed in display item lore.
    # But maybe lead to server lag if you have much online players, and they are all opening shop GUI.
    click-update: false
  sell-all:
    size: 54
    title: '{lang}'
    black-slots: []
  search-gui:
    menu:
      - 'search'
    prompt:
      clear-keyword: '{lang}'
  favourite-gui:
    menu:
      - 'favourite'
  # Premium version only
  # Require Paper 1.21.9+ version.
  buy-more-menu:
    not-open-when-invalid: true
    # Also sets the maximum stack size of display items to 99 on Minecraft 1.20.5+.
    display-item-max-stack: true
    default:
      menu: buy-more
      max-amount: 693
    only-buy:
      menu: buy-more-buy
      max-amount: 693
    only-sell:
      menu: buy-more-sell
      max-amount: 693
  auto-open:
    enabled: true
    menu: main
  # Support value: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/event/inventory/ClickType.html
  # Support use ;; symbol to make multi click type.
  click-event:
    buy: 'SHIFT_LEFT'
    sell: 'RIGHT'
    buy-or-sell: 'LEFT'
    # If you want to disable select-amount feature, set this to NEVER.
    select-amount: 'SHIFT_RIGHT'
    add-favourite: 'SWAP_OFFHAND'
    sell-all: 'DROP'
    # buy-one-stack: 'SWAP_OFFHAND'
  # Custom click actions for shop menu.
  # Premium version only.
  click-event-actions:
    buy-one-stack:
      display-name: 'Buy One Stack'
      buy-only: true
      1:
        type: buy
        shop: '{shop}'
        item: '{item}'
        amount: 64
    sell-one-stack:
      display-name: 'Sell One Stack'
      sell-only: true
      1:
        type: sell
        shop: '{shop}'
        item: '{item}'
        amount: 64
    add-favourite:
      display-name: '{lang:add-favourite-action}'
      1:
        type: conditional
        conditions:
          1:
            type: not
            conditions:
              1:
                type: menu_type
                menu-type: 'favourite'
        actions:
          1:
            type: add_favourite
            menu: favourite
            shop: '{shop}'
            item: '{item}'

secret-shop-items:
  require-display-in-menu: true
  require-meet-menu-open-conditions: true

use-times:
  default-reset-mode: 'NEVER'
  default-reset-time: '00:00:00'
  # This only works for CUSTOM type of reset mode.
  default-reset-time-format: 'yyyy-MM-dd HH:mm:ss'
  default-reset-value: 0
  # Set -1 to disable.
  default-max-value: -1
  # If set to true, product default buy / sell times will be set to reset value set in product configs or default value above.
  set-reset-value-by-default: true
  # If set to true, max value set in product configs or default value set above will only work for total placeholder.
  max-value-for-total-only: true
  auto-reset-mode: true

math:
  enabled: true
  enable-function: true
  # Decimal places used by math results. Applying a multiplier also counts as a math calculation.
  scale: 2
  static-scale: false

number-display:
  format:
    enabled: true
    decimal: "#,##0.00##########"
    integer: "#,##0"
  strip-trailing-zeros:
    enabled: true

# Premium version only.
log-transaction:
  # It will cost extra performance cost.
  enabled: false
  # file | database — database requires database.enabled: true
  storage: file
  # Logs are kept in memory and flushed in batches for both file and database storage.
  cache:
    # How often pending logs are written, in ticks (20 ticks = 1 second).
    flush-period-ticks: 200
    # Flush early once this many pending logs have accumulated.
    max-batch-size: 500
  # Used when storage is file. If set to empty value, we will just print the log into console.
  file: 'log.txt'
  format: '{time} | {player} | {shop} | {buy-or-sell} | {item-name} x{amount} | {price} | Price Multiplier: x{multiplier}'
  time-format: "yyyy-MM-dd HH:mm:ss"

display-item:
  # Require Paper 1.17.1+ version.
  auto-translate-item-name: true
  # Require Paper 1.21.9+ version, only supports Java players.
  auto-use-sprite-item-name: true
  # If set to true, if your product default amount is 10, and you purchase it x64 in one time, we will display 640 as result in amount placeholder.
  calculate-amount: true
  auto-set-first-product: true
  # @+lower case means conditional lore, do not remove them here.
  # Otherwise, the line without this will always display it.
  add-lore:
    - '{lang}'

placeholder:
  auto-settings:
    # If enabled, we will auto add conditional placeholder at all price amount.
    # This can avoid the need to add the discount option in the amount options for each price.
    add-conditional-in-all-price-amount:
      enabled: false
      buy-placeholder: buy
      sell-placeholder: sell
      black-dynamic-price: true
      black-shops:
        - 'example'
    # We will try adds dynamic up or down symbol for all dynamic price placeholder.
    # Please note that in your dynamic price amount option, first number must be the base price.
    # If you made the dynamic price according to the Wiki's instructions, then there is no problem at all.
    add-status-in-dynamic-price-placeholder:
      enabled: true
    # If enabled, we will try change {amount} in price placeholder option to the value you set here.
    change-amount-in-all-price-placeholder:
      enabled: false
      replace-value: '%formatter_number_format_{amount}%'
  # Premium version only
  compare:
    up: '{lang}'
    down: '{lang}'
    same: '{lang}'
  cron:
    format: "{lang}"
  data:
    # If your server never use dynamic value in prices or similar things, you can set this option to false.
    # This will improve little plugin performance.
    can-used-in-amount: true
  refresh:
    format: "{lang}"
    never: "{lang}"
  # Premium version only
  next:
    with-day-format: "{lang}"
    without-day-format: "{lang}"
    never: "{lang}"
  price:
    split-symbol-any: '{lang}'
    split-symbol-all: '{lang}'
    replace-new-line-symbol: '{lang}'
    unknown: "{lang}"
    unknown-price-type: "{lang}"
    empty: "{lang}"
    filter-for-one-line: ''
    item-auto-use-sprite: true
  click:
    # If enabled, {buy-click} and {sell-stick} will display different value according to product status.
    # But, it will maybe make server lag if you are running big server and have many products in your shop.
    enabled: false
    buy: '{lang}'
    sell: '{lang}'
    buy-with-no-sell: '{lang}'
    sell-with-no-buy: '{lang}'
    buy-max-limit-player: '{lang}'
    buy-max-limit-server: '{lang}'
    sell-max-limit-player: '{lang}'
    sell-max-limit-server: '{lang}'
    buy-price-not-enough: '{lang}'
    sell-price-not-enough: '{lang}'
    error: '{lang}'
    buy-condition-not-meet: '{lang}'
    sell-condition-not-meet: '{lang}'
  # Premium version only.
  sell-stick:
    infinite: "{lang}"

database:
  enabled: false
  jdbc-url: "jdbc:mysql://localhost:3306/ultimateshop?useSSL=false&autoReconnect=true"
  properties:
    user: root
    password: 123456

# Premium version only.
bungeecord-sync:
  enabled: false

prices:
  example:
    economy-plugin: Vault
    amount: 200
    placeholder: '{amount} Coins'
  mmoitems-example:
    hook-plugin: MMOItems
    hook-item: AXE;;TEST_AXE
    amount: 1
    placeholder: '{amount} Mythic Axe'

conditions:
  products-key: 'products-conditions'
  buy-prices-key: 'buy-prices-conditions'
  sell-prices-key: 'sell-prices-conditions'
  display-item-key: 'display-item-conditions'

time-offset:
  enabled: false
  offset-hours: 0
  offset-minutes: 0
  offset-seconds: 0

auto-save:
  enabled: true
  hide-message: false
  period-tick: 6000

bypass-plugin-check:
  - 'PluginNameHere'
```
