# 📦Inventory UI

Inventory UI displays standard chest-style slots on both Java and Bedrock, reading root `size`, `layout`, and `buttons` (graphical `ui.layout` and `ui.placement` do not affect Inventory slots).

## Select Inventory UI

For a **single menu**, disable other presentations at the root:

```yaml
dialog:
  enabled: false
bedrock:
  enabled: false
arcmenu:
  enabled: false
```

For **all menus**, force Inventory globally in `config.yml`:

```yaml
menu:
  force-ui:
    enabled: true
    type: inventory
    style: ''
```

Global `force-ui` overrides per-menu switches and restores normal selection when disabled. Inventory is also the standard fallback whenever a requested presentation is unavailable.

## Configure Rows and Entries

Save as `menus/common_menus/example.yml`, reload, and run `/shop menu example`:

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

Each row has 9 slots (`size` must be `9`, `18`, `27`, `36`, `45`, or `54`). Undefined IDs stay empty, `'0'` can be defined as a filler button, and multi-character IDs use backticks; see [Layout & Buttons](layout-and-buttons.md).

<a id="clicks-visibility-and-refresh"></a>

## Clicks, Visibility, and Refresh

Common buttons run `actions` after click `conditions` pass, while shop products use configured product click actions. Use `menu-visibility.inventory: false` to hide decorative entries only in Inventory, or use Premium `dynamic-layout` and `display-conditions` for per-player slots.

Global refresh and title-update options in `config.yml`:

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

* `circle-update` refreshes every second; `click-update` refreshes after clicks (enable periodic refresh only when placeholders change over time).
* `title-update` is a Premium feature requiring **PacketEvents**.
* Inventory click and reopen cooldowns use `menu.cooldown.click` and `menu.cooldown.reopen` in ticks (`-1` disables).

## Screens That Always Use Inventory

[Item Sell](item-sell-menus.md) accepts physical items and waits for confirmation, while [Sell All](sell-all-menu.md) sells deposited items on close—both always use Inventory regardless of global UI settings. [Buy More](buy-more-menus.md) uses its own quantity controls and item previews.

[UI Modes](ui-overview.md) · [Display Item Add Lore](display-item-add-lore.md)
