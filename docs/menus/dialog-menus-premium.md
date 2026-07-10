# 💬Dialog Menus - Premium

{% hint style="info" %}
## Dialog Menu share same menu files with bedrock menus and classic inventory menus. We can auto translate the menu into 3 different types, you do not need any manually change.
{% endhint %}

## Requirements

Dialog menus use Minecraft's native Dialog UI and require Paper 1.21.9 or newer and plugin version 4.7.0 or newer.

Because plugin has a feature called close action, but client close the dialog UI will not send any packet to server, so dialog menus in this plugin do not support closed by ESC, <mark style="color:red;">**you have to provide at least one button to let player close the UI with**</mark> [<mark style="color:red;">**close**</mark>](../format/action-format.md#close) <mark style="color:red;">**action.**</mark>

## Enabling Dialog Menus

Enable the global feature in `config.yml`:

```yml
menu:
  dialog:
    enabled: true # <--- Set it to true
```

Add a Dialog section to each menu that should use it:

```yml
dialog:
  enabled: true # <--- Set it to true.
  content: '<gray>Select a product or action.'
  button-width: 150
  columns: 2
```

After enable, dialog menus will override the classic inventory menu, some menu types does not support dialog menu.

## Extra options for dialog menus

You can add those extra options for menu configs. For example:

<pre class="language-yml"><code class="lang-yml">title: '{shop-name}'
size: 54

# Added line
<strong>dialog:
</strong>  enabled: true
  content: '&#x3C;gray>Select a category.'
  button-width: 180
  columns: 2
</code></pre>

A display item with an empty name does not create a Dialog button.

* enabled: Uses Dialog for this menu. The global option must also be enabled.
* content: Body shown below the title. Supports language placeholders and MiniMessage.
* button-width: Default to 150. Width of each action button.
* columns: Default to 2. Number of button columns.

## Sprite Icons

### Automatic Sprites

By default, buttons in dialog menus do not include any icons, it's boring for view, so we support you auto add sprite compoents at each button to display the item icon before each button.

```yml
menu:
  dialog:
    auto-add-sprite:
      enabled: true # <--- Set this to true
      format: '<sprite:"{namespace}:{atlas}":{path}>'
```

Before you use this feature, you have to download the material -> vanilla text path mapping, find the content in config.yml file:

```yaml
  # Premium version only. Generates a Material -> vanilla texture path mapping from the Minecraft client assets.
  minecraft-item-material-file:
    enabled: false
    generate-new-one: false
    file: 'item-materials.json'
```

* Set both `enabled` and `generate-new-one` option to `true`.
* Stop your server and restart it.
* Plugin will auto download the file.
* After successfully download, you need set `generate-new-one` to `false`. **If your server upgraded game version, you need delete old mapping file and regenerate new one.**

<figure><img src="../.gitbook/assets/image (19).png" alt=""><figcaption></figcaption></figure>

### Explicit Display Item Sprite

Set a complete MiniMessage sprite under `display-item` to override the Material mapping:

```yml
display-item:
  material: SUNFLOWER
  name: '<yellow>Sunflower'
  sprite: '<sprite:"minecraft:blocks":block/sunflower_front>'
```

The `sprite` value must be a complete MiniMessage expression beginning with `<sprite`, not only a texture path.

{% hint style="info" %}
We only support matching various icons through materials. If your item has custom textures or models, we recommend manually setting the dialog sprite, as mentioned earlier.
{% endhint %}

### Sprite in Product Message

You can even use sprite in product message, set `display-item.auto-use-sprite-item-name` option to `true` in `config.yml` file then the product name in message will become a icon! Only supports vanilla items.

<figure><img src="../.gitbook/assets/image (20).png" alt=""><figcaption></figcaption></figure>

```yaml
display-item:
  # Require Paper 1.21.9+ version, only supports Java players.
  auto-use-sprite-item-name: true
```

### Global Text Options

The following options are under `menu.dialog` in `config.yml`:

* `default-button`: fallback close button when no custom actions exist
* `search.*`: search input and button labels
* `buy-more.*`: amount selection labels
* `info.*`: product information button labels
* `favourite-edit.*`: favourite editing labels

These values support `{lang:...}` language references. Product information strings also support relevant placeholders such as `{item-name}` and `{amount}`.

```yaml
  # Premium version only
  # Require Paper 1.21.9+ version.
  dialog:
    default-button: '{lang:menu.dialog.default-button}'
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
```
