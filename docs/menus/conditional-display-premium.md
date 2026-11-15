# 🔀 Conditional Display — Premium

Use `display-conditions` to decide whether a button or product can appear. Enable `dynamic-layout: true` when layout selection depends on the player. Click and trade conditions continue to validate the action separately.

## Choose between two entries

This complete Common menu displays a VIP button to permitted players and a regular button to others:

```yaml
title: '&8Choose a shop'
size: 9
dynamic-layout: true
layout:
  - '000`A||B`00000'
dialog:
  enabled: false
bedrock:
  enabled: false
arcmenu:
  enabled: false
buttons:
  A:
    display-item:
      material: DIAMOND
      name: '&bVIP Shop'
    display-conditions:
      1:
        type: permission
        permission: shop.vip
    actions:
      1:
        type: shop_menu
        shop: vip
  B:
    display-item:
      material: EMERALD
      name: '&aRegular Shop'
    actions:
      1:
        type: shop_menu
        shop: normal
```

Save in `menus/common_menus/`, create the target shops, then reload. The token `` `A||B` `` occupies one slot. `A` is selected when its visibility and display conditions pass; otherwise `B` is checked. An unconditional first candidate prevents later alternatives from being used. No eligible candidate leaves an empty slot.

Global forced presentation selection still applies to this example. Ore/ArcMenu entries additionally need appropriate graphical placements.

## Dynamic layout rules

- Alternatives use `||` inside one backtick-delimited layout token.
- Candidate IDs are case-sensitive; surrounding spaces are trimmed.
- Each candidate must exist and be allowed in the current presentation.
- Shop layouts can select product IDs and ordinary buttons by the same rules.
- With dynamic layout enabled, PlaceholderAPI can resolve layout tokens per player.

A static layout is cached and cannot reliably select player-dependent alternatives. `menu-visibility` is evaluated separately and does not require dynamic layout.

## Choose the condition scope

| Setting | Controls |
| --- | --- |
| Menu-root `conditions` | Opening the entire menu |
| Button `conditions` / `fail-actions` | Click eligibility and failure response |
| Button/product `display-conditions` | Whether the entry appears |
| Product buy/sell conditions | Actual trade eligibility |
| `menu-visibility` | Which presentations can show the entry |
| Conditional display-item variants | Which item appearance is selected |
| Add-lore condition symbols | Which generated lore lines appear |

Hiding a button does not replace trade permissions or purchase validation. Use [Condition Format](../format/condition-format.md) for condition syntax, [Display Item Format](../format/display-item-format.md) for appearance variants, and [Add Lore](display-item-add-lore.md) for conditional text.

## Graphical alternatives

Ore and ArcMenu still require placements for selected entries. Give replacement buttons compatible `ui.placement` values. Explicitly placed entries can also be collected outside root layout, so their own display conditions and visibility must express when they should appear.

[Layout & Buttons](layout-and-buttons.md) · [Graphical UI Layout](../menus-getting-started/ui-layout.md)
