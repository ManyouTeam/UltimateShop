# 🛠️ Troubleshooting

Work through the symptom below before changing unrelated configuration. All paths are relative to `plugins/UltimateShop/` unless noted.

## Menu cannot be opened

Confirm that the menu file exists in the expected folder and its ID matches `/shop menu <id>`. A Shop Menu normally uses the **shop ID**, not the template filename; Buy More requires an already selected product. Check `conditions` and command permissions. Reload using `/shop reload` after editing files.

## Java Dialog, Ore or ArcMenu opens Inventory instead

1. Check `menu.ui.mode.java` in `config.yml` and any menu-level `ui.mode.java` override.
2. Check whether the installed build is Premium, and whether the server/client fulfills the selected renderer's requirements.
3. Native Dialog/Ore require supported Paper Dialog APIs (Paper 1.21.9+); Ore also needs a generated/loaded resource pack.
4. ArcMenu requires its compatible plugin and client resources.
5. Item Sell/Sell All are physical Inventory interfaces; Buy More has renderer-specific input behavior.
6. Inspect console errors for missing integrations or invalid graphical layout.

See [UI Modes](ui-overview.md).

## A button or product does not appear

| Presentation | What to check |
| --- | --- |
| Any | Root `layout` marker, corresponding button/product ID, `display-conditions`, `ui.visible-in` |
| Inventory | `size` matches the row count; marker occupies the intended slot |
| Form | Nonblank name, `display-item.bedrock.hide`, list/action availability; Inventory fillers are often omitted |
| Native Dialog | Source entry order, `ui.visible-in` including `dialog`, required actions |
| Ore / ArcMenu | `ui.placement` / `ui.products.placement`, area bounds, pagination, overlap and visibility |

`ui.placement.enabled: false` disables a graphical entry, not the corresponding Inventory slot. `display-item.bedrock` is separate from the product root `bedrock.click-event`.

## Global changes have no visible effect

A menu can override global `menu.ui` options under root `ui`; a shop can also override the selected template under `settings.menu-settings`. The bundled menu examples contain explicit values. Compare the actual loaded menu file to the global setting before diagnosing a missing configuration key.

## Graphical positions overlap, clip or disappear

Use [Graphical Layout](ui-layout.md). Check named areas, canvas bounds, grid capacity, placements and all visible entries. Ore requires valid pack templates and multiples-of-nine vertical positioning. An enlarged canvas does not automatically redesign ArcMenu's fixed product detail panel.

## Ore looks wrong after reload

Run `/shop dialogpack` after changing graphical layout, themes or templates; install/distribute the new pack and ensure clients actually use it. Check `plugins/UltimateShop/textures/` for referenced PNGs and `plugins/UltimateShop/pack/` for the generated output. A normal YAML reload alone does not refresh client-side assets.

## Bedrock Form icons are missing

Check that the player is recognized by Floodgate, that Form mode is selected, and that the selected `display-item.bedrock.icon` is a valid `path;;...` or `url;;...` value. Automatic icons use vanilla material mapping, not Java custom model data. The Form may still display a text button when its image cannot be resolved.

## Search, Favourite and quantity behavior differs across UIs

Form, Dialog and ArcMenu use different input/detail screens rather than reproducing Inventory's special slots verbatim. Search results may depend on entered keywords and result capacity; Favourite edit actions depend on editing mode. Quantity input requires the product's Buy More support and an available `select-amount` action. See [Search](search-menus-premium.md), [Favourite](favourite-menus-premium.md) and [Buy More](buy-more-menus.md).

## Refresh, title flashing or high menu overhead

`menu.menu-update.circle-update` repeatedly rebuilds Inventory button display; enable it only if needed. `menu.title-update` may cause client flashing; `ui.renderers.arcmenu.update-interval` is a separate renderer-specific refresh. Avoid enabling every periodic update without a corresponding dynamic display requirement.

## Related pages

[Menu Configuration](menu-configuration.md) · [Layout and Buttons](layout-and-buttons.md) · [Migration](migration-guide.md)
