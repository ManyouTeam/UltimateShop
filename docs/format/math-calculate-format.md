# ➗Math Calculate Format

Unlike most plugins, UltimateShop allows you to fill in the Placeholder API and mathematical calculation format in almost all options applicable to numbers, making it easy to set the price, buy/sell limits, and more you want.

{% hint style="info" %}
You need enable `math.enabled` option in your `config.yml` file to use this feature.
{% endhint %}

## Result precision

`math.scale` controls the number of decimal places in mathematical results. UltimateShop uses `HALF_UP` rounding. Applying a multiplier also counts as a mathematical calculation, so multiplier results—including sell price modifiers, previews, and final rewards—use the same scale.

```yaml
math:
  scale: 2
```

For example, with `scale: 2`, multiplying `10` by `0.333333` produces `3.33`.

## List of match calculate format

`()` - Create a parenthetical expression which will be evaluated first (`3 * (4 + 1)`)

`$` - Denotes a variable (`$1 / 3`)

`e` - Euler's constant (`log(e)`)

`pi` - pi (`sin(pi)`)

`+` - Add two numbers (`1 + 1`)

`-` - Subtract two numbers, or negate one (`3-2`, `-(4+2)`)

`/` - Divide two numbers (`3 / 4`)

`*` - Multiply two numbers (`2 * 3`)

`^` - Raise one number to the power of another (`3^3`)

`%` - Take the modulus, or division remainder, of one number with another (`7 % 4`)

`abs` - Take the absolute value of a number (`abs$1`, `abs-1`)

`round` - Rounds a number to the nearest integer (`round1.5`, `round(2.3)`)

`ceil` - Rounds a number up to the nearest integer (`ceil1.05`)

`floor` - Rounds a number down to the nearest integer (`floor0.95`)

`rand` - Generate a random number between 0 and the specified upper bound (`rand4`)

`log` - Get the natural logarithm of a number (`log(e)`)

`sqrt` - Get the square root of a number (`sqrt4`)

`cbrt` - Get the cube root of a number (`cbrt(8)`)

`sin` - Get the sine of a number (`sin$2`)

`cos` - Get the cosine of a number (`cos(2*pi)`)

`tan` - Get the tangent of a number (`tanpi`)

`asin` - Get the arcsine of a number (`asin$2`)

`acos` - Get the arccosine of a number (`acos0.45`)

`atan` - Get the arctangent of a number (`atan1`)

`sinh` - Get the hyperbolic sine of a number (`sinh(4)`)

`cosh` - Get the hyperbolic cosine of a number (`sinh(4)`)

`true` - Boolean constant representing 1

`false` - Boolean constant representing 0

`=` - Compare if two numbers are equal (`1 = 1` will be `1`, `1 = 3` will be `0`), also accepts `==`

`!=` - Compare if two numbers are not equal (`1 != 2` will be `1`, `1 != 1` will be `0`)

`>` - Compare if one number is greater than another (`1 > 0`)

`<` - Compare if one number is less than another (`0 < 1`)

`>=` - Compare if one number is greater than or equal to another (`1 >= 1`)

`<=` - Compare if one number is less than or equal to another (`0 <= 1`)

`|` - Boolean or (`true | false`), also accepts `||`

`&` - Boolean and (`true & true`), also accepts `&&`

`!` - Boolean not/inverse (`!true`)

## Functions

Every function in this table, including `piecewise()`, requires both `math.enabled: true` and `math.enable-function: true`.

| Function | What it returns | Example |
| --- | --- | --- |
| `sum(...)` | The sum of all arguments | `sum(1, 2, 3)` → `6` |
| `max(...)` | The largest argument | `max(5, 12, 8)` → `12` |
| `min(...)` | The smallest argument | `min(5, 12, 8)` → `5` |
| `avg(...)` | The arithmetic average | `avg(10, 20, 30)` → `20` |
| `random(...)` | A random decimal in the given range | `random(2, 5)` → at least 2 and less than 5 |
| `piecewise(...)` | The value for the first true condition | `piecewise(3 < 5, 10, 20)` → `10` |

`sum`, `max`, `min`, and `avg` return `0` when called without arguments.

### Random numbers

- `random()` returns a decimal from 0 up to 1.
- `random(4)` uses the range from 0 up to 4. With a negative argument, such as `random(-4)`, the range runs from -4 up to 0.
- `random(2, 5)` uses the range from 2 up to 5. You can give the endpoints in either order. Extra arguments after the first two are ignored.

The upper endpoint is excluded. Equal endpoints return that value, so `random(0)` returns 0 and `random(5, 5)` returns 5.

### Piecewise calculations

`piecewise(condition1, value1, condition2, value2, fallback)` returns the value for the first true condition or the fallback. For example, `piecewise(3 < 5, 10, 20)` returns 10. The engine evaluates all arguments before choosing the result, so every branch expression must be valid.

For a practical example using `min`, `max`, `log`, and `floor` together, see the [Dynamic Price YAML](../dynamic-prices/dynamic-price.md#full-configuration-for-a-price-that-falls-by-a-fixed-ratio).
