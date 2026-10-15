# 🌍Localized Item Name - Premium

Requires Premium and Minecraft 1.16+.

Vanilla language files are downloaded automatically when a feature needs server-side item names. Set `config-files.language` to the server language and enable `display-item.auto-translate-item-name`. Paper retains client-side `<lang:...>` names, so each player sees their own Minecraft language without downloading a server language file for that path.

```yaml
config-files:
  language: zh_CN
display-item:
  auto-translate-item-name: true
```

Downloads run in the background. Until the requested language is available, the existing fallback name is used. Files are cached in `plugins/UltimateShop/minecraft-resources/<version>/languages/`. Unknown vanilla locales fall back to English. Custom item names continue to take precedence.

Minecraft client JARs, language assets, material mappings and automatic Ore icons share the same version directory. Downloads are reused across features and after restart. A server upgrade uses a new version directory automatically. Ore uses the client's default font and the 5.1.0 width estimates; it does not package vanilla fonts or language width compensation tables.

The old `minecraft-locate-file` and `minecraft-item-material-file` download controls are no longer used. No `enabled`, `generate-new-one` or resource filename setting is required. Network failures are logged and retried on the next request after a cooldown; they do not block the game thread.
