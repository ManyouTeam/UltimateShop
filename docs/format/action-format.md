# 🎬Action Format

The action format will consist of several options.

{% hint style="info" %}
The `actions` in the **Action Format** **example** only represent **Action Format** start from here. Please refer to the page description of the corresponding function for specific option names, such as `buy-actions`.
{% endhint %}

## General Options

#### Apply Times

This action will run only when player have buy/sell spcified times product.&#x20;

Optional, if not set, will execute action every time.

* start-apply: Start which times this action will apply. **Optional. Default to 0.**
* end-apply: Last times the action will apply. **Optional. Default to infinite.**
* apply: Which times this action will apply, format: `[1,2,3,4]`. **Optional. Default use start-apply option value.**

```yaml
    actions:
      1:
        apply: [1,2,3,4,5]
        start-apply: 1
        end-apply: 5
```

#### Sell All Once / Multi Once

When multiple quantity / products are about to be sold, adding this option means that only the first product's action will be executed. Very useful for sounds action, if you didn't add this, all product's sound action will execute.

```yaml
    actions:
      1:
        sell-all-once: true # In sell all
        multi-once: true # In buy more menu
```

#### Open Once

Only work for menu's open-actions option, if enabled, only the menu opened by the player for the first time will trigger this action, which means that if the opened menu was opened through another menu, this action cannot be triggered.

```yaml
    actions:
      1:
        open-once: true
```

#### Click Type

This action only executed when player use this click type to active this action. Won't work for `open-actions`, `close-action` in menu configs and `buy-actions`, `sell-actions` in product configs.

Optional, if not set, this action will execute for each click type.

```yaml
    actions:
      1:
        click-type: LEFT 
```

### Java Only/Bedrock Only <mark style="color:red;">- PREMIUM</mark>

This action only executed if player is java or bedrock players.

Optional, if not set, both java and bedrock players will execute this action.

```yaml
    actions:
      1:
        java-only: true
        bedrock-only: true
```

### Fail Type

Only support `fail-actions` in product configs.

Supported fail type:

* ERROR
* PERMISSION
* PLAYER\_MAX
* SERVER\_MAX
* REQUIRE\_CONDITION\_NOT\_MEET
* NOT\_ENOUGH
* INVENTORY\_FULL
* API\_CANCEL

```yml
    fail-actions:
      1:
        fail-type: 'PLAYER_MAX'
        type: message
        message: 'Hello'
```

## Available Placeholders

* {world}
* {amount}

### Item-Level `buy-actions` and `sell-actions`

When you use `buy-actions` or `sell-actions` on the item itself, `{amount}` means the item-level transaction amount prepared by the buy or sell flow.

In practice:

* if `display-item.calculate-amount` is `false`, `{amount}` is always `1` for the item action layer
* if `display-item.calculate-amount` is `true`, `{amount}` is `transaction quantity * display item amount`

Example:

* display item amount: `1`
* player buys `5`
* item `buy-actions` sees `{amount} = 5`

Another example:

* display item amount: `64`
* player buys `5`
* item `buy-actions` sees `{amount} = 320`

### Single Thing `give-actions` and `take-actions`

When you use `give-actions` or `take-actions` on a single thing, `{amount}` also means the real final amount of that specific single thing.

* {player\_x}
* {player\_y}
* {player\_z}
* {player\_pitch}
* {player\_yaw}
* {player}
* {item} - Product ID
* {item-name} - Product Display Name
* {shop} - Shop ID
* {shop-name} - Shop Display Name
* {shop-menu} - Shop's Menu ID

## Sound

Send sound to player.

```yaml
    actions:
      1:
        type: sound
        sound: 'ui.button.click'
        volume: 1
        pitch: 1
```

## Message

Send a message to the player, support color code.

```yaml
    actions:
      1:
        type: message
        message: 'Hello!'
```

## Title

Send title to the player, support the color code.

```yaml
    actions:
      1:
        type: title
        main-title: 'Good day'
        sub-title: 'Not bad'
        fade-in: 10
        stay: 70
        fade-out: 30
```

## Particle

```yaml
    actions:
      1: 
        type: particle
        particle: HEART
        count: 20
        offset-x: 0.3
        offset-y: 1.0
        offset-z: 0.3
        speed: 0.01
```

## Announcement

Send a message to all online players, support color code.&#x20;

```yaml
    actions:
      1:
        type: announcement
        message: 'Hello!'
```

## Effect

Give players potion effect.

```yaml
    actions:
      1:
        type: effect
        potion: BLINDNESS
        duration: 60
        level: 1
        ambient: true # Optional
        particles: true # Optional
        icon: true # Optional
```

## Teleport

Teleport player to specified location.

```yaml
    actions:
      1:
        type: teleport
        world: LobbyWorld
        x: 100
        y: 30
        z: 300
        pitch: 90 # Optional
        yaw: 0 # Optional
```

## Player Command

Make the player excutes a command.

```yaml
    actions:
      1:
        type: player_command
        command: 'tell Hello!'
```

## Op Command

Make the player excutes a command as OP.

```yaml
    actions:
      1:
        type: op_command
        command: 'tell Hello!'
```

## Console Command

Make the console excutes a command.

```yaml
    actions:
      1:
        type: console_command
        command: 'op {player}'
```

## Spawn vanilla mobs

Spawn vanilla mobs.

```yaml
    actions:
      1:
        type: entity_spawn
        entity: ZOMBIE
        world: LOBBY # Optional
        x: 100.0 # Optional
        y: 2.0 # Optional
        z: -100.0 # Optional
```

## MythicMobs spawn

Require MythicMobs.

```yaml
    actions:
      1:
        type: mythicmobs_spawn
        entity: Super_Skeleton
        level: 1 # Optional
        world: LOBBY # Optional
        x: 100.0 # Optional
        y: 2.0 # Optional
        z: -100.0 # Optional
```

## Open Common Menu

Open specified common menus.

```yaml
    actions:
      1:
        type: open_menu
        menu: main
```

## Open Shop Menu

```yaml
    actions:
      1:
        type: shop_menu
        shop: farming
```

## Open Buy More Menu

```yaml
    actions:
      1:
        type: buy_more_menu
        shop: farming
        item: A
```

## Open Buy More Menu with Custom Buy More Menu settings <mark style="color:red;">- Premium</mark>

```yaml
    actions:
      1:
        type: buy_more_menu
        shop: farming
        item: A
        buy-more-menu:
          menu: buy-more-buy
          max-amount: 128
```

## Open Sell All Menu

```yaml
    actions:
      1:
        type: sell_all_menu
```

## Buy Product

```yaml
    actions:
      1:
        type: buy
        shop: food
        item: A
        amount: 5 # Optional
```

## Sell Product

```yaml
    actions:
      1:
        type: sell
        shop: food
        item: A
        amount: 5 # Optional
        sell-all: true # Optional
```

## Close

Close the inventory.

```yaml
    actions:
      1:
        type: close
```

## Delay <mark style="color:red;">- Premium</mark>

Make the action run after X ticks.

```yaml
    actions:
      1:
        type: delay
        time: 50
        wait-for-player: true
        actions:
          1:
            type: entity_spawn
            entity: ZOMBIE
```

## Chance <mark style="color:red;">- Premium</mark>

Set the chance the action will be excuted, up to 100. 50 means this action has 50% chance to excute.

```yaml
    actions:
      1:
        type: chance
        rate: 50
        actions:
          1:
            type: entity_spawn
            entity: ZOMBIE
```

## Any <mark style="color:red;">- Premium</mark>

Randomly choose specified amount of actions to execute.

```yaml
    actions:
      1:
        type: any
        amount: 2
        actions:
          1:
            type: entity_spawn
            entity: ZOMBIE
          2:
            type: entity_spawn
            entity: SKELETON
          3:
            type: entity_spawn
            entity: WITHER
```

## Conditional <mark style="color:red;">- Premium</mark>

Only players meet the conditions you set here will be able to execute the action.

```yaml
    actions:
      1:
        type: conditional
        conditions:
          1: 
            type: world
            world: lobby
        actions:
          1:
            type: entity_spawn
            entity: ZOMBIE
```

## Connect <mark style="color:red;">- Premium</mark>

Require enable `bungeecord-sync.enabled` option in config.yml and correctly set the BungeeCord settings. For more info, please view [Multi Server Sync](../features/multi-server-sync-premium.md) page.

```yaml
    actions:
      1:
        type: connect
        server: 'lobby'
```

## Update GUI

Update all buttons in opened GUI. Will not update gui title.

```yaml
    actions:
      1:
        type: update_gui
```

## Update GUI Title <mark style="color:red;">- Premium</mark>

Require server enable title update feature, for more info, please view [this page](https://ultimateshop.superiormc.cn/menus/general-menus#title-update-premium).

```yaml
    actions:
      1:
        type: update_title
```

## Add Favourite <mark style="color:red;">- Premium</mark>

Add the product to favourite list.

<pre class="language-yaml"><code class="lang-yaml"><strong>    actions:
</strong>      1:
        type: add_favourite
        menu: favourite # The favourite menu.
        shop: '{shop}'
        item: '{item}'
</code></pre>

## Remove Favourite <mark style="color:red;">- Premium</mark>

Remove this product from favourite list.

```yaml
   actions:
      1:
        type: remove_favourite
        menu: favourite # The favourite menu.
        shop: '{shop}'
        item: '{item}'
```

## Toggle Favourite <mark style="color:red;">- Premium</mark>

Toggle the product favourite status, if it is exist, we will remove, if not exist, we will add.

```yaml
   actions:
      1:
        type: toggle_favourite
        menu: favourite # The favourite menu.
        shop: '{shop}'
        item: '{item}'
```

## Prompt <mark style="color:red;">- Premium</mark>

Asks the player to type something in chat before continuing with follow-up actions.

```yml
    actions:
      1:
        type: prompt
        description: '&eEnter a note'
        reopen-on-submit: true
        reopen-on-cancel: true
        actions:
          1:
            type: message
            message: '&aSaved note: &f{arg}'
        cancel-actions:
          1:
            type: message
            message: '&cYou cancelled the prompt'
```

**`{arg}`**

The full raw input string.

If the player enters:

```
diamond sword 64
```

then:

```
{arg} -> diamond sword 64
```

**`{arg_1}`, `{arg_2}`, `{arg_3}` ...**

Whitespace-split arguments.

If the player enters:

```
diamond sword 64
```

then:

```
{arg_1} -> diamond
{arg_2} -> sword
{arg_3} -> 64
```

## Back&#x20;

Back to the previous menu, if previous menu does not exist, will open the menu exist in menu option.

```yaml
    actions:
      1:
        type: back
        menu: main
```

```yaml
    actions:
      1:
        type: back
        shop: 'flowers'
```
