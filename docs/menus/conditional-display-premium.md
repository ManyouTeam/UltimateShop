# 🔀 Conditional Display - Premium

Display conditions decide whether a product/button can appear. They are separate from click/trade conditions: hiding an entry is a presentation decision, while buying and selling still run their own validation.

<a id="configure-display-conditions"></a>

## Quick start: display conditions

Use `display-conditions` at button/product root with [Condition Format](../format/condition-format.md):

```yaml
buttons:
  A:
    display-item:
      material: DIAMOND
      name: '&bVIP offers'
    display-conditions:
      1:
        type: permission
        permission: shop.vip
  B:
    display-item:
      material: PAPER
      name: '&7Regular offers'
```

Enable `dynamic-layout: true` for layouts that must be evaluated per player. Without dynamic evaluation, a cached static layout cannot reliably choose player-dependent alternatives. `ui.visible-in` is checked separately and does not require dynamic layout.

## Ordered alternatives

Use one backtick-delimited token containing candidate IDs separated by `||`:

```yaml
dynamic-layout: true
layout:
  - '000000000'
  - '000`A||B`00000'
```

This is two nine-slot rows. The token occupies one slot. Candidates are checked from left to right; the first existing entry that is visible in the presentation and meets display conditions is used. `B` has no condition here and acts as the fallback. If no candidate qualifies, the slot is empty. An unconditional candidate placed first prevents later alternatives from being selected.

The same form works for shop product IDs and sub-buttons. IDs are case-sensitive; whitespace around alternatives is trimmed. Use valid one-slot tokens rather than writing `A||B` without backticks, which would be read as separate characters.

## Related configuration

| Setting | Purpose |
| --- | --- |
| `display-conditions` | Whether a whole button/product appears |
| `dynamic-layout` | Resolve layout placeholders/conditions per player |
| `ui.visible-in` | Restrict entries to Inventory/Form/Dialog/Ore/ArcMenu |
| `conditions`, `fail-actions` | Ordinary button click requirements and failure response |
| Product buy/sell conditions | Authorize the actual trade |
| Conditional display-item variants | Change a display item's appearance using the configured `conditions.display-item-key`; see [Display Item Format](../format/display-item-format.md) |
| Add-lore `@` conditions | Show/hide individual generated lore lines; see [Add Lore](display-item-add-lore.md) |

Form and native Dialog reuse selected entries from the root layout. Ore/ArcMenu also need graphical placements; an allowed entry without a placement may still be absent from a graphical view. Give alternatives suitable placements and keep them consistent when they replace one another. Do not use display conditions as a substitute for trade permissions or purchase conditions.

## Related documentation

[Layout and Buttons](layout-and-buttons.md) · [Graphical Layout](ui-layout.md) · [Condition Format](../format/condition-format.md)
