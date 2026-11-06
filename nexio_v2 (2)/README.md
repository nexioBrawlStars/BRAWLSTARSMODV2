# NEXIO HUD V2

Lebegő, áthelyezhető HUD-overlay a hivatalos Brawl Stars mellé. Külön alkalmazás: a játék
kliensét nem módosítja, nem olvas belőle és nem ír bele. Csak a saját képernyődön jelenik meg.

## 1. APK készítése GitHubon
1. Hozz létre egy új GitHub repót, és töltsd fel a zip TARTALMÁT a repó gyökerébe
   (a `build.gradle.kts`, `settings.gradle.kts`, `app/`, `.github/` közvetlenül a gyökérben legyen).
2. Menj az **Actions** fülre → **Build APK** → **Run workflow** (push-ra is automatikusan fut).
3. A futás végén az **Artifacts** alatt töltsd le: `nexio-hud-debug-apk` (zip-ben az `app-debug.apk`).

## 2. Telepítés
1. Másold az `app-debug.apk`-t a telefonra, nyisd meg, engedélyezd az ismeretlen forrásból telepítést.
2. Ha a régi NEXIO HUD más csomagnévvel volt telepítve, a V2 mellette külön alkalmazásként jelenik meg.
   Ha ugyanazzal a csomagnévvel, de másik aláírással, előbb töröld a régit.
   Új GitHub-build más debug-aláírású lehet: frissítés előtt a régit töröld.

## 3. Használat (a főképernyő lépései)
1. **Engedély**: „Engedély megadása" → kapcsold be a megjelenítést más alkalmazások fölött.
2. **HUD bekapcsolása**: kapcsoló. A panel megjelenik a képernyő felső-közép részén.
3. **Megjelenés**: átlátszóság és méret csúszka. A panel fejlécében:
   `▾` összecsukás, `⠿` húzófogó (pozíció), `✕` bezárás.
4. **Játék indítása**: elindítja a Brawl Starst; a HUD a játék fölött marad.
5. **Modulok**: Óra, Akkumulátor, Brawl TV (vizuális, szimulált), Kijelző FPS, Hálózati ping, Jegyzetfüzet.
6. **Téma**: Nexio lila / Óceán / Bíbor / Toxic, RGB betűanimáció, Brawl TV opciók.

Időmérő: Indít → Szünet → Folytat; Nullázás visszaállítja 00:00.0-ra.

## 4. Hibakeresés
- **A HUD nem jelenik meg**: ellenőrizd az 1. lépés engedélyét; kapcsold ki-be a 2. lépés kapcsolóját.
- **A HUD eltűnik a játék indításakor**: telefongyártói akkumulátor-optimalizálás leállíthatja –
  Beállítások → Alkalmazások → NEXIO HUD → Akkumulátor → „Nincs korlátozás".
- **Nem látom az értesítést**: Android 13+ alatt engedélyezd az értesítéseket (a HUD ettől még fut).
- **A panel elcsúszott / kilóg**: főképernyő → „Pozíció visszaállítása".
- **A ping mindig „–"**: nincs internet, vagy a hálózat tiltja a 1.1.1.1:53 kapcsolatot.
- **Az FPS a kijelző frissítési ütemét mutatja** (pl. 60/90/120), nem a játék belső FPS-ét.
- **A Brawl Stars nem indul a gombról**: nincs telepítve, vagy más a csomagnév (`com.supercell.brawlstars`).
- **GitHub build hiba**: másold ki a hibaüzenetet az Actions logból, és küldd el.

## 5. Új modul írása
`object Xyz : HudModule` (id, name, description, category, `Content()`), majd regisztráld a
`ModuleRegistry.registerAll()`-ban (`modules/HudModule.kt`).
