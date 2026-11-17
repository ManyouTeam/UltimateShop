# 🌏高级语言管理

## 默认语言

在 `config.yml` 中设置默认语言：

```yaml
config-files:
  language: en_US
  # Premium version only.
  per-player-language: true
```

这里填写语言文件名，不含扩展名。所有语言文件均存放在 `languages` 文件夹中。要创建新语言文件，复制 `en_US.yml` 并改为对应语言代码即可，例如 `zh_CN.yml`。

{% hint style="warning" %}
以下功能自插件 **4.2.0** 起加入。
{% endhint %}

## 玩家独立语言 <mark style="color:red;">- 付费版</mark>

在 `config.yml` 中启用 `per-player-language` 后，插件会根据玩家客户端语言选择显示内容。服务器必须预先准备对应的语言文件；缺少时会使用默认语言文件。

可通过下面的视频了解效果：

{% file src="../.gitbook/assets/QQ202613-144954.mp4" %}

## 语言占位符

插件消息中可使用 `{lang:<langKey>}`，让不同地区的玩家看到与客户端语言相符的文本。

在各语言文件的 `override-lang` 下，以 `ID: 文本内容` 的格式添加自定义文本，例如：

```yaml
// ... The content originally present in the language file

# Added content
override-lang:
  # This means shop-title it's language key.
  shop-title: 'Item Shop'
```

```yaml
// ... The content originally present in the language file

# Added content
override-lang:
  # This means shop-title it's language key.
  shop-title: '物品商店'
```

然后在需要的位置使用 `{lang:shop-title}`。

在 `config.yml` 中，也可以直接用 `{lang}` 引用对应语言文件中的自定义文本。此时语言 ID 应与 `config.yml` 的配置路径保持一致。例如：

`config.yml` 中：

```yaml
placeholder:
  # Premium version only
  compare:
    up: '{lang}'
    down: '{lang}'
    same: '{lang}'
```

各语言文件中：

```yaml
// ... The content originally present in the language file

override-lang:
  placeholder:
    compare:
      up: '↑'
      down: '↓'
      same: '-'
```

若对应语言文件中找不到该文本，插件会继续查找默认语言文件；仍找不到时，就不会解析该占位符。

## 高级消息格式 <mark style="color:red;">- 付费版</mark>

普通语言文本直接填写要输出的内容，并可附带[颜色代码](color-code.md)。高级消息格式不仅能发送聊天消息，还能发送动作栏、标题、Boss 栏、声音等内容。

使用高级消息格式时，仍可使用颜色代码，包括 MiniMessage。

### 普通用法

```yaml
welcome: 'Welcome to the server!'
```

### 聊天消息

```yaml
welcome: '[message]&aWelcome![/message]'
```

### 标题&#x20;

<pre class="language-yaml"><code class="lang-yaml"><strong>welcome: '[title=20,60,20]&#x26;6Welcome;;&#x26;eThis is sub title[/title]'
</strong># Format: [title=fadeIn,Stay,fadeOut]title;;subTitle[/title]
</code></pre>

### 同时发送聊天消息与标题

可以像下面这样混合不同的消息类型：

```yaml
welcome: '[message]&aGood day![/message][title=20,60,20]&6Welcome;;&eThis is sub title[/title]'
```

### 动作栏

```yaml
welcome: '[actionbar]&7Please wait...[/actionbar]'
```

### Boss 栏 - 早期测试

```yaml
welcome: '[bossbar=GREEN,SOLID,1.0]&aHappy Today[/bossbar]'
```

### 声音

```yaml
welcome: '[sound=ENTITY_EXPERIENCE_ORB_PICKUP,1,1][/sound][sound=ENTITY_PLAYER_LEVELUP,1,1][/sound]'
```
