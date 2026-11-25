# 🔀Conditional Display - Premium

Use `display-conditions` to control whether a button or product appears, and enable `dynamic-layout: true` when slot selection depends on the player (remember that `display-conditions` only controls visibility—click and trade conditions still validate actions separately).

## Choose Between Two Entries

This complete Common menu shows a VIP button to players with `shop.vip` and a regular button to everyone else:

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

Save in `menus/common_menus/`, create the target shops, and reload. `` `A||B` `` occupies one slot: `A` is chosen if visible and its `display-conditions` pass, otherwise `B` is checked (if the first candidate has no conditions, later fallbacks are never reached; if none qualify, the slot stays empty).

Global `menu.force-ui` still applies, and Ore/ArcMenu entries still need appropriate `ui.placement`.

## Dynamic Layout Rules

* Separate fallback IDs with `||` inside a single backtick token.
* Candidate IDs are case-sensitive and trimmed of surrounding spaces.
* Candidates must exist and be visible in the active presentation (`menu-visibility`).
* Shop layouts follow the same rules for product IDs and common buttons.
* With `dynamic-layout: true`, PlaceholderAPI placeholders inside layout tokens also resolve per player.

{% hint style="info" %}
Static layouts (`dynamic-layout: false`) are cached and cannot reliably pick per-player fallbacks, whereas `menu-visibility` evaluates independently without needing `dynamic-layout`.
{% endhint %}

## Choose the Right Condition Scope

| Setting | What It Controls |
| --- | --- |
| Root menu `conditions` | Opening the entire menu |
| Button `conditions` / `fail-actions` | Click eligibility and failure response |
| Button / product `display-conditions` | Whether the entry is shown |
| Product `buy-conditions` / `sell-conditions` | Actual trade eligibility |
| `menu-visibility` | Which presentations may show the entry |
| Conditional display-item variants | Which item appearance is used |
| Add-lore condition symbols | Which auto-generated lore lines appear |

Hiding a button does not replace permission or trade checks—always enforce conditions on the action or product as well. See [Condition Format](../format/condition-format.md), [Display Item Format](../format/display-item-format.md), and [Display Item Add Lore](display-item-add-lore.md).

## Graphical Alternatives

In Ore and ArcMenu, **every fallback candidate must have its own compatible `ui.placement`**—otherwise whichever one gets picked will not appear in the graphical UI.

Also note that any entry with an explicit `ui.placement` may be collected for graphical display even if it is omitted from root `layout`, so make sure its `display-conditions` and `menu-visibility` clearly state when it should appear.

[Layout & Buttons](layout-and-buttons.md) · [Graphical UI Layout](../menus-getting-started/ui-layout.md)
