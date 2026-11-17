# ArcMenu 公共底层维护

US、S2W、FC 使用同一份标记区块维护公共 ArcMenu 实现，不新增运行时公共插件或额外依赖。

## 分层

`ArcMenuBridge` 选择本插件的业务 GUI，业务 GUI 通过 `ArcMenuGUI.tryOpen` 调用 `ArcMenuRuntime.open`。Runtime 注册 ArcMenu 应用，管理待打开和当前会话；ArcMenuGUI 管理 Surface、鼠标、实体池及渲染；业务 GUI 负责商店、轮盘、预览、打乱和翻牌。

`ArcMenuGUI` 接各插件现有的业务基类：US 为 `AbstractGUI`，S2W 为 `AbstractSpinGUI`，FC 为 `AbstractFlipGUI`。S2W、FC 的 Inventory GUI 与 ArcMenu GUI 为独立分支。

## 必须同步的代码

以下文件中的 `BEGIN ARCMENU SHARED` / `END ARCMENU SHARED` 区块，在三个项目中保持逐字一致：

- `ArcMenuGUI.java`：数值与颜色读取规则、会话回调与视图替换、鼠标命中与提示框、显示实体的创建/缓存/定位/变换、Node 和 Hit。
- `ArcMenuRuntime.java`：应用注册、能力基础检查、指针参数、打开与替换、关闭和清理。
- `ArcMenuLayout.java`：坐标、颜色混合、缩放、文本锚点及边界计算。

数值缺失或非有限时使用默认值；有限数值按上下限裁剪。颜色支持整数、`#RRGGBB` 和 `#AARRGGBB`；无效值使用默认颜色。

实体统一使用 Surface 原点加变换矩阵定位，不开启实体插值；隐藏实体通过清空内容保留在池中，随后重新使用。视图切换统一复用活动会话，并将回调转交新视图。Surface 重建时清空已失效的实体缓存。

## 允许保留的插件适配

- 包名、主插件实例、应用 ID、默认画布和额外能力要求。US 搜索需要原生文字输入能力。
- `setting`、`labelText`、`itemTransform`、`nodeDepth`、`animationOffset`：读取已有配置路径、接入文字解析、匹配资源包模型和业务层级。
- `initializeSession`、`tickView`、`sessionClosed`、`beforeReplace`、`beforeShutdown`、`canLeaveView`、`canReuseView`：接入现有状态管理和业务结算。S2W 禁止转动中切走；FC 的预览、打乱、翻牌传递同一奖励池。
- US 的菜单权限、打开/关闭动作、冷却和状态恢复；S2W 的转动动画状态；FC 的翻牌价格、次数、计时和奖励状态。
- US 的文本宽度和折行使用现有字体资源服务；S2W、FC 使用默认字体度量。

这些适配位于公共区块之外。业务配置键和方法入口签名可按插件原有 API 保留，例如 US 的 `tryOpen(boolean reopen)`。

## 检查与更新

三个项目默认作为同级目录：`UltimateShop`、`SpinToWin`、`FlipCard`。在任意一个项目内运行：

```powershell
python scripts/sync_arcmenu.py
```

检查区块名称和内容；任何分叉都返回非零退出码。只希望检查时无需传其他参数。

修改某一项目的公共区块后，可明确指定该项目为同步来源：

```powershell
python scripts/sync_arcmenu.py --sync-from FC
```

同步只替换标记区块，不替换 imports、插件适配或业务代码。来源也可为 `US` 或 `S2W`。目录不在同级时使用 `--projects-dir <三个项目的父目录>`。

每次同步后，分别完成三个项目的 Maven 构建；渲染和输入改动还需在游戏客户端检查 Mouse/Touch/Vision 模式、Surface 重建、提示框、视图切换及关闭流程。本地逻辑检查不能替代客户端渲染验证。

## FC 使用方式

默认配置 `flip-gui.gui-type: INVENTORY` 保留原来的 8 张卡牌和箱子界面。全局可设置 `ARCMENU`，每个菜单也可覆盖：

```yaml
settings:
  gui-type: ARCMENU
  arcmenu:
    columns: 7
    rows: 4
    shuffle-ticks: 20
```

旧的 `option` 配置段同样有效。`columns * rows` 表示显示容量，实际数量仍由各 `card_N.amount` 决定。完整示例为 `ui/arcmenu_example.yml`，显示 28 张卡牌。

ArcMenu 模式需要支持应用 API 的 ArcMenu。更新后重新生成并应用 FC 资源包：原有 `display.gui` 保留，额外生成 `display.fixed` 供 ArcMenu 显示卡牌和边框。预览、打乱和翻牌在同一会话完成；主动关闭预览会取消后续打乱。
