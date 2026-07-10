# 🔑Give Item Method

You can change give item method at `config.yml` file with below content:

```yaml
give-item:
  # Support value: BUKKIT, SMART
  # SMART will cost more server performance but will follow the vanilla max stack to give player item, also support check full.
  give-method: BUKKIT
  # Only support SMART give method.
  check-full: false
```

Here are 2 give item method in plugin.

## BUKKIT

The most stable way to give player item, but it will has problem when you buy more than the max stack amount of the item in once.

For example, you buy x64 ender peral, plugin will stack them in one slot which is not correct.

In this picture, the first slot shows you the problem.

<figure><img src="../.gitbook/assets/image (2).png" alt=""><figcaption></figcaption></figure>

## SMART - Early Alpha

Smart method is provided by UltimateShop self, it will follow the vanilla max stack amount, for the same example, in this method, the item will be split into 4 stacks because the max stack limit for the Ender Pearl is 16 instead of 64. Also, this method support check inventory will full after buy.

This method will cost extra plugin performance, be careful to use if you are running large server and have much online players.

<figure><img src="../.gitbook/assets/image (4).png" alt=""><figcaption></figcaption></figure>

It is very difficult to attempt to restore the vanilla mechanism without contacting NMS, so this method may have a lot of problems and is still under testing.
