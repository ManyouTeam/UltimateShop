# 🔍Search Menus - Premium

## Search Menus Config

Search menus have those special options compare than common menu:

```yaml
input-item: '1'
result-item: '6'

action-items:
  2:
    action-type: input-name
    display-item:
      material: HOPPER
      name: '&eSearch'
  4:
    action-type: clear-search
    display-item:
      material: BARRIER
      name: '&cClear'

state-items:
  3:
    empty-input:
      display-item:
        material: PAPER
        name: '&eWaiting'
    has-input:
      display-item:
        material: COMPASS
        name: '&aResult'

no-result-item:
  slot: 31
  display-item:
    material: BARRIER
    name: '&cNo Result'
```

* input-item: Defines the marker used for input slot, these slots allow players to place items for matching. Must be a single char, use this char in `layout` option to set where it will display in menu.
* result-item: Defines the marker used for result slots. Matched products are displayed into these slots in order. Must be a single char, use this char in `layout` option to set where it will display in menu.
* action-items: Defines special Search GUI buttons. Current supported `action-type` values:
  * `input-name` : open a chat prompt to search by text
  * `clear-search`: return input items and clear the current text filt
* state-items: Displays the current search status. Each state button supports:
  * `empty-input`
  * `has-input`
  * Useful placeholders:
    * `{result-amount}` total matched products
    * `{showing-amount}` currently displayed amount
    * `{input-amount}` total amount of input items
    * `{name-keyword}` current text filter
* no-result-item: Displayed when the current search has no matches. This is a fixed slot item defined by `slot`.
* result-lore: Extra lore added to each result item.
  * Useful placeholders:
    * `{shop}`
    * `{product}`

## Set Search Menu

You should put seach menu ID in `menu.search-gui.menu` option in `config.yml` file.

```yaml
menu:
  search-gui:
    menu:
      - 'search'
      - 'search2' # Put more search menu ID here.
```

The default search menu file is `menus/search.yml`, and you can duplicate it to create more search menus.

Command:

```yaml
/shop menu <menuName>
```

Examples:

```yaml
/shop menu search
/shop menu search2
```

## Search Behavior

The Search GUI uses two matching sources:

### **Item Search**

When players place items into input slots, the plugin looks for shop products linked to those items.

### **Text Search**

When players click an `input-name` or `search-name` button, they can type text in chat.\
That text is used to match product display names, item names, and vanilla item names.

If no input items are present, text-only search can still search across all visible products.

{% hint style="info" %}
Vanilla item name only supports English ID by default, if you want to support other language, you need to enable [Localized Item Name ](../features/localized-item-name-premium.md)feature.
{% endhint %}

Also, in Text Search we uses the shared prompt system.

Unified cancel keyword in `config.yml`:

```yml
menu:
  prompt:
    cancel-keyword: 'cancel'
```

Unified clear keyword for Search GUI:

```yml
menu:
  search-gui:
    prompt:
      clear-keyword: '{lang}'
```

Meaning:

* typing `cancel-keyword`: cancels the current prompt
* typing `clear-keyword`: clears the current name filter
