# ➗Math Calculate Format

Unlike most plugins, UltimateShop allows you to fill in the Placeholder API and mathematical calculation format in almost all options applicable to numbers, making it easy to set the price, buy/sell limits, and more you want.

{% hint style="info" %}
You need enable `math.enabled` option in your `config.yml` file to use this feature.
{% endhint %}

## List of match calculate format

`max` - Compare two number and return bigger value. (`max(4,5)`)

`min` - Compare two number and return smaller value. (`min(4,5)`)

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

The math expression system provides several built-in functions for performing common calculations, including summation, minimum and maximum value selection, averages, and random number generation.

These functions are available when the following option is enabled in `config.yml` file:

```yaml
math:
  enable-function: true
```

When this option is disabled, custom math functions will not be registered.

### Available Functions

#### `sum`

Returns the sum of all provided numbers.

**Syntax**

```
sum(number1, number2, ...)
```

**Examples**

```
sum(1, 2, 3)
```

Result:

```
6
```

```
sum(10, -3, 2.5)
```

Result:

```
9.5
```

When no arguments are provided, the result is `0`:

```
sum()
```

Result:

```
0
```

***

#### `max`

Returns the largest number among all provided arguments.

**Syntax**

```
max(number1, number2, ...)
```

**Examples**

```
max(5, 12, 8)
```

Result:

```
12
```

```
max(-10, -3, -20)
```

Result:

```
-3
```

When no arguments are provided, the result is `0`:

```
max()
```

Result:

```
0
```

***

#### `min`

Returns the smallest number among all provided arguments.

**Syntax**

```
min(number1, number2, ...)
```

**Examples**

```
min(5, 12, 8)
```

Result:

```
5
```

```
min(-10, -3, -20)
```

Result:

```
-20
```

When no arguments are provided, the result is `0`:

```
min()
```

Result:

```
0
```

***

#### `avg`

Returns the arithmetic average of all provided numbers.

The function adds all arguments together and divides the result by the number of arguments.

**Syntax**

```
avg(number1, number2, ...)
```

**Examples**

```
avg(10, 20, 30)
```

Result:

```
20
```

```
avg(2, 3, 7)
```

Result:

```
4
```

When no arguments are provided, the result is `0`:

```
avg()
```

Result:

```
0
```

***

#### `random`

Generates a random decimal number.

The behavior depends on the number of arguments provided.

### `random()`

Returns a random decimal number from `0`, inclusive, to `1`, exclusive.

**Example**

```
random()
```

Possible results include:

```
0.137
0.5
0.999
```

The result satisfies the following range:

```
0 <= result < 1
```

### `random(max)`

Returns a random decimal number between `0` and the provided value.

**Example**

```
random(10)
```

The result satisfies:

```
0 <= result < 10
```

Negative values are also supported:

```
random(-10)
```

The result satisfies:

```
-10 <= result < 0
```

If the provided value is `0`, the result is always `0`:

```
random(0)
```

Result:

```
0
```

### `random(min, max)`

Returns a random decimal number between the two provided values.

**Example**

```
random(5, 10)
```

The result satisfies:

```
5 <= result < 10
```

The argument order does not matter. The function automatically determines which value is smaller:

```
random(10, 5)
```

This behaves the same as:

```
random(5, 10)
```

When both values are equal, that value is returned directly:

```
random(5, 5)
```

Result:

```
5
```

Only the first two arguments are used when more than two arguments are provided:

```
random(1, 10, 100)
```

This behaves the same as:

```
random(1, 10)
```

> The upper boundary of a random range is exclusive. For example, `random(1, 5)` can return values greater than or equal to `1`, but it will not return exactly `5`.

### Function Summary

| Function           | Description                                  | No-argument result |
| ------------------ | -------------------------------------------- | -----------------: |
| `sum(...)`         | Adds all provided numbers                    |                `0` |
| `max(...)`         | Returns the largest provided number          |                `0` |
| `min(...)`         | Returns the smallest provided number         |                `0` |
| `avg(...)`         | Returns the arithmetic average               |                `0` |
| `random()`         | Returns a random value from `0` to `1`       |       Random value |
| `random(max)`      | Returns a random value between `0` and `max` |     Not applicable |
| `random(min, max)` | Returns a random value between two values    |     Not applicable |
