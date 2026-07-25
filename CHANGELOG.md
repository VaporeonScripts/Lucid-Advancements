## v1.2.0

* Improved config screen by creating more concise entries #27
* Implemented auto-release scripts for Curseforge, Modrinth & Github

---

## v1.1.9

* Implemented a comprehensive config menu GUI to modify mod settings directly in-game.
* Added full translation support for Spanish (`es_es`), Russian (`ru_ru`), German (`de_de`), French (`fr_fr`), and Simplified Chinese (`zh_cn`).
* Removed the legacy 'Clear Tracked' button. Replaced it with a new action button positioned adjacent to the configuration gear icon in the main UI.
* Added an option to toggle the config watcher (hot-reload) off.
* Changed default config values `screenSidebarRowHeight=32` and `screenSidebarItemHeight=28`

* Revised the underlying config file comments to be more concise and straightforward for manual edits.

---

## v1.1.8

* Fixed the mod trying to load on the server side. #15
* Fixed GUI missing 1 pixel on the right/bottom edges. #19
* Fixed the search box suggestion placeholder never rendering.
* Fixed a visual glitch where the advancement screen would repeatedly open/close when Clickable Advancements was also installed. #16

* Changed the GUI Scale button to a dropdown, supporting `Auto` and `1` - `8`.
* Added support for filtering by multiple mods in the search bar (e.g. `@create @farmersdelight`). #18
* Added support for remembering the search query across menu reopens. #17

* Updated NeoForge version from `21.0.37-beta` to `21.1.230`.

---