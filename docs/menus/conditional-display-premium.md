# 🔀Conditional Display - Premium

Sometimes, you may want to display Product A when the first condition is met, Product B when the second condition is met, and Product C when neither condition is met, or follow a similar logic. This feature can assist you in achieving this.

## Set display condition

You can set `display-conditions` option both in button configs or product configs. This section is following [Condition Format](../format/condition-format.md). For example:

```yaml
  D: 
    price-mode: CLASSIC_ALL
    product-mode: CLASSIC_ALL
    products:
      1:
        material: ROOTED_DIRT
        amount: 1
    buy-prices:
      1:
        economy-plugin: Vault
        amount: '0.58'
        placeholder: '{amount}$'
        start-apply: 0
    sell-prices:
      1:
        economy-plugin: Vault
        amount: '0.53'
        placeholder: '{amount}$'
        start-apply: 0
    display-conditions: # <--- Added content
      1: 
        type: world
        world: 'world1'
```

## Set menu configs layout option

You should use this new format in layout option:

```yaml
`D||E||F`
```

This means that the products or buttons with IDs D, E, and F will be checked in order until the display conditions of the corresponding product or button are met. For example, if the display conditions for D are not met, but the display conditions for E are met, then the corresponding slot will display the product or button with ID E.

Also, you need set menu config's `dynamic-layout` option to `true` to use this feature.

```yaml
dynamic-layout: true
layout:
  - '000000000'
  - '0ABC`D||E||F`EFG0'
  - '0HIJKLMN0'
  - '0OPQRSTU0'
  - '000000000'
  - 'a0003000b'
```
