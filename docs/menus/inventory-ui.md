# 📦 Inventory UI

Inventory displays chest-style slots on Java and Bedrock. It reads the menu's root `size`, `layout` and buttons. Graphical `ui.layout` and `ui.placement` do not change Inventory positions.

## Select Inventory

For a single menu, disable alternative presentations at menu root:

```yaml
dialog:
  enabled: false
bedrock:
  enabled: false
arcmenu:
  enabled: false
```

For all menus, use `config.yml`:

```yaml
menu:
  force-ui:
    enabled: true
    type: inventory
    style: ''
```

The global force setting takes priority over local switches. Disable it to restore normal selection. Inventory is also the standard fallback when a requested presentation cannot be used.

## Configure rows and entries

Save as `menus/common_menus/example.yml`:

```yaml
title: '&8Example'
size: 18
layout:
  - '000000000'
  - '0000X0000'
buttons:
  X:
    display-item:
      material: BARRIER
      name: '&cClose'
    actions:
      1:
        type: close
```

With Inventory selected, reload and open `/shop menu example`. Each row resolves to nine slots. Supported sizes are `9`, `18`, `27`, `36`, `45` and `54`. Undefined IDs leave empty slots; `0` can be defined as a filler. Multi-character IDs use backticks. See [Layout & Buttons](layout-and-buttons.md).

## Clicks, visibility and refresh

Ordinary buttons run `actions` after their click conditions pass. Shop products use configured product click operations. `menu-visibility.inventory: false` hides an entry only from Inventory; Premium display conditions can additionally select entries per player with dynamic layout.

Global refresh options in `config.yml`:

```yaml
menu:
  menu-update:
    circle-update: false
    click-update: false
  title-update:
    enabled: false
    circle-update: false
    click-update: true
    resend-items-pack: false
```

`circle-update` refreshes every second; `click-update` refreshes after a click. Enable periodic refresh when changing placeholders need it. Title updating is Premium and requires PacketEvents. Inventory click/reopen cooldowns are configured with `menu.cooldown.click` and `menu.cooldown.reopen`, in ticks; bundled values are `-1`.

## Physical inventory screens

[Item Sell](item-sell-menus.md) accepts real items and requires sale confirmation. [Sell All](sell-all-menu.md) sells deposits on close. Both remain Inventory interfaces regardless of global presentation selection. [Buy More](buy-more-menus.md) has its own amount controls and product previews.

[UI Modes](ui-overview.md) · [Display Item Add Lore](display-item-add-lore.md)
