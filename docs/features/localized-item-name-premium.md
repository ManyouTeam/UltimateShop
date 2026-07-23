# 🌍Localized Item Name - Premium

## Requirements

* Server version 1.16+.

## Configs

* Open `config.yml` file, and fine below contents:

```yaml
config-files:
  generate-default-files: true
  language: en_US
  minecraft-locate-file:
    enabled: true
    generate-new-one: true
    file: 'zh_cn.json'
```

* Please set `minecraft-locate-file.enabled` option to `true`.
* Then also set `minecraft-locate-file.generate-new-one` option to `true`.
* Finally, set file option to your language locate file name. Support value:
  * af\_za.json
  * ar\_sa.json
  * ast\_es.json
  * az\_az.json
  * ba\_ru.json
  * bar.json
  * be\_by.json
  * bg\_bg.json
  * br\_fr.json
  * brb.json
  * bs\_ba.json
  * ca\_es.json
  * cs\_cz.json
  * cy\_gb.json
  * da\_dk.json
  * de\_at.json
  * de\_ch.json
  * de\_de.json
  * el\_gr.json
  * en\_au.json
  * en\_ca.json
  * en\_gb.json
  * en\_nz.json
  * en\_pt.json
  * en\_ud.json
  * enp.json
  * enws.json
  * eo\_uy.json
  * es\_ar.json
  * es\_cl.json
  * es\_ec.json
  * es\_es.json
  * es\_mx.json
  * es\_uy.json
  * es\_ve.json
  * esan.json
  * et\_ee.json
  * eu\_es.json
  * fa\_ir.json
  * fi\_fi.json
  * fil\_ph.json
  * fo\_fo.json
  * fr\_ca.json
  * fr\_fr.json
  * fra\_de.json
  * fur\_it.json
  * fy\_nl.json
  * ga\_ie.json
  * gd\_gb.json
  * gl\_es.json
  * haw\_us.json
  * he\_il.json
  * hi\_in.json
  * hr\_hr.json
  * hu\_hu.json
  * hy\_am.json
  * id\_id.json
  * ig\_ng.json
  * io\_en.json
  * is\_is.json
  * isv.json
  * it\_it.json
  * ja\_jp.json
  * jbo\_en.json
  * ka\_ge.json
  * kk\_kz.json
  * kn\_in.json
  * ko\_kr.json
  * ksh.json
  * kw\_gb.json
  * la\_la.json
  * lb\_lu.json
  * li\_li.json
  * lmo.json
  * lo\_la.json
  * lol\_us.json
  * lt\_lt.json
  * lv\_lv.json
  * lzh.json
  * mk\_mk.json
  * mn\_mn.json
  * ms\_my.json
  * mt\_mt.json
  * nah.json
  * nds\_de.json
  * nl\_be.json
  * nl\_nl.json
  * nn\_no.json
  * no\_no.json
  * oc\_fr.json
  * ovd.json
  * pl\_pl.json
  * pt\_br.json
  * pt\_pt.json
  * qya\_aa.json
  * ro\_ro.json
  * rpr.json
  * ru\_ru.json
  * ry\_ua.json
  * sah\_sah.json
  * se\_no.json
  * sk\_sk.json
  * sl\_si.json
  * so\_so.json
  * sq\_al.json
  * sr\_cs.json
  * sr\_sp.json
  * sv\_se.json
  * sxu.json
  * szl.json
  * ta\_in.json
  * th\_th.json
  * tl\_ph.json
  * tlh\_aa.json
  * tok.json
  * tr\_tr.json
  * tt\_ru.json
  * uk\_ua.json
  * val\_es.json
  * vi\_vn.json
  * vp\_vl.json
  * yi\_de.json
  * yo\_ng.json
  * zh\_hk.json
  * zh\_cn.json
  * zh\_tw.json
  * zlm\_arab.json
* Like I put `zh_cn.json` here. Start the server, the plugin will say it starting download the file.
* If sucessfully download, you will see new localized item name, like me:

<figure><img src="../.gitbook/assets/image (5).png" alt=""><figcaption></figcaption></figure>

## Problems

Do you have problems? Try:

* Delete the json file generated at `plugins/UltimateShop` folder then regenerate this file.
* Are you using premium version? Only premium version support this for now.
* Did plugin print error message when generate the locate file? If yes, try restart the server and plugin will auto regenerate.
* If the server gets stuck in the "downloading" phase during startup, you can manually drag the language JSON file into the `plugins/UltimateShop` folder.

## Note

* **After generate this file, please keep generate-new-one option to false.**
* **If your server upgraded game version, you need delete old locate file and regenerate new one.**
* Server or plugin maybe lag when **first time** open the shop menu after restart the server, because we need build a cache for those localized item name. Under normal circumstances, you should not feel it unless you have a lot of products.

## Alternative methods

### Set custom display-item for product

* Use `display-name` option in product configs.
* This method requires manual setting of localized names for items.

For example:

```yaml
items:
  A:
    display-name: 'Apple' # Added line!
    products:
      1:
        material: APPLE
    buy-prices:
      1:
        economy-plugin: Vault
        amount: 10
```
