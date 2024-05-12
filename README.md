# PenguinCurrency
Hello, ez a Penguin Currency alkalmazás
Bejelentkezés után 3 oldal fogad:
- Home, itt láthatóak az aktuális árfolymaok forintban (api-val, valós adatok)
- Portfolio, össze állíthatod a saját portfóliódat a pénznem és a mennyiség kiválasztásával, el tudod menteni adatbázisba, frissíteni, törölni, illetve meg is jelenik
- Profile, kijelentkezés

## Részfeladatok hol találhatóak:
- nincs fordítási és futtatási hiba
- Firebase autentikáció: com/example/penguincurrency/LoginActivity.java
- Adatmodell: com/example/penguincurrency/CurrencyModel.java
- Activityk: com/example/penguincurrency/
- Beviteli mezők: Loginnél csillagok, portfóliónál a szám billentyűzet jön fel, emailnél email billentyű
- ConstraintLayout: layout/activity_profile.xml ben van használva, a többi Frame/Linear Layoutot használ
- Reszponzív: Home és Portfólió görgethető lesz elfordított kijelző esetén
- Több féle animáció is van: a menü elemek közötti váltásnál, illetve Logoutnál
- Az activityk között lehet navigálni a navigation menu segítségével, update gombra érhető el a dialog
- Lifycycle hook: com/example/penguincurrency/HomeActivity.java "onResume()": Frissíti az árfolyam adatokat minden alkalommal, amikor az activity előtérbe kerül
- AndroidManifestben van nethez meg notificationhöz permission kérve, ez nem tudom számít e
- Portfólió oldalon a törlés esetén jön egy notificaton, (nem appopn belül, hanem rendszer szinten) kér is engdeélyt bizonyos android verziókon
- CRUD műveletek: Portfólió fülön lehet hozzáadni, törölni, frissíteni a pénz elemeket, valamint meg is jelennek, nem külön szálon vannak
- nincs lekérdezés
- Remélem tetszik az app =D
