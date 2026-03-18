# PokeApp - Piano di Sviluppo

## Panoramica

PokeApp e' un'app Android companion per il gioco da tavolo "Pokemon Master Trainer" (1999, regole custom). Pensata per uso rapido con una mano durante il gioco fisico.

**Stack**: Kotlin, Jetpack Compose + Material 3, Navigation Compose, Room, Hilt, Coroutines + Flow, Coil, Clean Architecture MVVM.

**Build**: `./gradlew assembleDebug` (compileSdk 34, minSdk 24, Kotlin 1.9.24, Compose BOM 2024.06.00)

---

## Stato Attuale (BUILD SUCCESSFUL)

### Architettura (35 file Kotlin)

```
com.app.pokeapp/
├── PokeApplication.kt          # @HiltAndroidApp
├── MainActivity.kt             # Compose entry point
├── di/AppModule.kt             # Hilt: Database + Repository bindings
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt      # Room DB, pre-populated (150 Pokemon, 13 Gym, 29 Random)
│   │   ├── entity/PokemonEntity.kt  # Entities: Pokemon, Challenger, RandomEncounter
│   │   └── dao/Daos.kt         # PokemonDao, ChallengerDao, RandomEncounterDao
│   ├── mapper/Mappers.kt       # Entity -> Domain conversion
│   └── repository/RepositoryImpl.kt  # PokemonRepositoryImpl, ChallengerRepositoryImpl
├── domain/
│   ├── model/Pokemon.kt        # Pokemon + Move data classes
│   ├── model/Challenger.kt     # Challenger + ChallengerType enum + ChallengerPokemon
│   ├── model/enums/PokemonType.kt    # 18 types con color + onColor (WCAG AA)
│   ├── model/enums/TypeEffectiveness.kt  # Matrice efficacia completa
│   └── repository/Repositories.kt    # Interfacce repository
└── presentation/
    ├── navigation/
    │   ├── Screen.kt           # 7 route definitions
    │   └── NavHost.kt          # Navigation graph
    ├── theme/
    │   ├── Color.kt            # PokemonTypes (18 colori) + PokemonColors (M3 completo)
    │   ├── Theme.kt            # Light + Dark scheme con tutti i token M3
    │   ├── Dimens.kt           # Spacing, elevation, touch targets, corners
    │   ├── Shape.kt            # M3 Shapes (extraSmall -> extraLarge)
    │   └── Type.kt             # Typography scale
    ├── components/
    │   ├── PokemonCard.kt      # PokemonCard + PokemonListItem
    │   ├── TypeBadge.kt        # TypeBadge (usa type.onColor) + TypeBadgeList
    │   └── BattleComponents.kt # BattleArena, PokemonBattleSlot, PowerIndicator, TypeEffectivenessTable
    └── screen/
        ├── CoverScreen.kt + (no VM)
        ├── MainMenuScreen.kt + (no VM)
        ├── PokedexScreen.kt + PokedexViewModel.kt
        ├── PokemonDetailScreen.kt + PokemonDetailViewModel.kt
        ├── BattleScreen.kt + BattleViewModel.kt
        ├── GymBattleScreen.kt + GymBattleViewModel.kt
        └── RandomEncounterScreen.kt + RandomEncounterViewModel.kt
```

### Funzionalita' Implementate

| Feature | Stato | Note |
|---------|-------|------|
| F1 Main Menu | Completo | 4 bottoni, gradiente rosso, animazione press |
| F2 Pokedex | Completo | Lista 151, ricerca per nome IT/EN, dettaglio |
| F3 Sfida Allenatori | Completo | 2 slot, selezione, calcolo potenza con tipo |
| F4 Sfida Capopalestra | Completo | Selezione gym, bonus dado, medaglie |
| F5 Incontri Casuali | Completo | Pokemon random (id<=129), bonus dado |
| F6 Sistema Tipi | Completo | 18 tipi, matrice efficacia, TypeEffectiveness |
| F7 Database | Completo | 150 Pokemon + 13 Gym + 29 Random pre-caricati |
| F8 UI/UX | Parziale | Tema rosso OK, Dark mode OK, accessibilita' parziale |

### Formula Potenza (tutte le battaglie)

```
power = basePower + move.baseDamage + dice(1-6)
if (evolutionStage > 1) power += 10
// Solo Gym e Random:
bonusRoll = dice(1-6); if (bonusRoll >= 5) power += bonusRoll
power *= typeEffectivenessMultiplier
```

### Navigazione

```
Cover --tap--> MainMenu ---> Pokedex ---> PokemonDetail/{id}
                        ---> Battle (Sfida Allenatori)
                        ---> GymBattle/{gymId} (Sfida Capopalestra)
                        ---> RandomEncounter (Incontri Casuali)
```

---

## Lavoro Completato in Questa Sessione

### Design System Aggiornato
- **Color.kt**: Aggiunta palette M3 completa (tertiary/Pokemon Yellow, containers, surfaceVariant, outline, semantic success/warning/error) per Light + Dark mode
- **Theme.kt**: Token M3 corretti (primaryContainer ora tono chiaro in light mode, primary piu' chiaro in dark mode #FFB4AB)
- **Dimens.kt**: Aggiunti ElevationRaised, ElevationDialog, ButtonHeightLarge, ButtonCornerRadius, TouchTargetMin, TouchTargetComfortable, CornerPill

### Accessibilita' WCAG AA
- **PokemonType.kt**: Aggiunta proprieta' `onColor` per ogni tipo (testo scuro sui tipi chiari: Electric, Ice, Ground, Normal, Fire, Water, Grass, Flying, Psychic, Bug, Rock, Steel, Fairy)
- **TypeBadge.kt**: Usa `type.onColor` invece di `Color.White` hardcoded

### Bug Fix
- **GymBattleScreen.kt**: Rimossa LazyColumn duplicata nel GymPickerSheet, aggiunto `.clickable` alle righe
- **BattleComponents.kt**: Colori da hardcoded a `PokemonColors` (Success/Error), aggiunti simboli ▲/▼/X per daltonismo nelle etichette efficacia
- **BattleScreen.kt**: Colori da hardcoded a `PokemonColors` (Success/Error/Warning containers)

### Figma MCP
- **claude-talk-to-figma** installato in `.claude.json` (locale al progetto)

---

## Cosa Manca (Priorita')

### ALTA - Bug e UX Critici

1. **Nessuna ricerca nel PokemonPickerSheet** — Scorrere 151 Pokemon nel bottom sheet e' lento per il gioco da tavolo. Aggiungere un campo di ricerca nel BottomSheet (sia BattleScreen che GymBattleScreen).

2. **PokemonPickerSheet duplicato** — La stessa composable e' definita privatamente sia in `BattleScreen.kt` che in `GymBattleScreen.kt`. Estrarre in un componente condiviso in `presentation/components/PokemonPickerSheet.kt`.

3. **Bonus dado NON mutuamente esclusivo** — Spec dice "bonus dado mutuamente esclusivi" ma entrambi i lati calcolano il bonus indipendentemente in GymBattleViewModel e RandomEncounterViewModel. Solo un lato dovrebbe ricevere il bonus per incontro.

4. **Nessuna gestione errori visibile** — Il campo `error` esiste in tutti gli UiState ma non viene mai mostrato nell'UI. Aggiungere un componente di errore condiviso.

### MEDIA - Feature Mancanti

5. **Toggle evoluzione non implementato** — Lo spec richiede un toggle su PokemonDetailScreen per switchare tra forma base/evoluta. `preEvolutionId` e `postEvolutionId` esistono nel model ma sono solo testo.

6. **F5 lista incontri** — Lo spec dice "Lista incontri" ma l'implementazione attuale ha solo generazione random, nessuna lista di RandomEncounter usata.

7. **mapColor non usato in RandomEncounterScreen** — Lo spec F5 menziona mapColor ma la schermata non lo usa.

8. **Breakdown formula potenza** — Il giocatore vede solo il numero finale. Mostrare: basePower + moveDamage + dado + evoBonus + tipoModificatore.

### BASSA - Polish

9. **ic_launcher_background** in colors.xml e' Material Indigo default (#3F51B5). Cambiare in Pokemon Red (#DC0A2D).

10. **Test** — Zero test nel progetto. Priorita':
    - Unit test: `TypeEffectiveness.getEffectiveness()`, `calculateTotalEffectiveness()`
    - Unit test: Power formula nei ViewModel
    - UI test: Navigazione base, ricerca Pokedex

11. **Animazioni** — Aggiungere micro-interazioni Nintendo-style:
    - Press scale (0.95f spring back) sui bottoni menu
    - Crossfade su risultato battaglia
    - SharedElement transition per immagini Pokemon

12. **Performance** — LazyColumn key stabilite su `pokemon.id` (gia' fatto nel Pokedex, verificare nelle picker sheet).

---

## Design System Reference

### Colori Primari
| Ruolo | Light | Dark |
|-------|-------|------|
| Primary | #DC0A2D | #FFB4AB |
| OnPrimary | #FFFFFF | #690005 |
| PrimaryContainer | #FFDAD6 | #8C1D18 |
| Secondary | #1D3557 | #1D3557 |
| Tertiary | #7D5800 | #F5BF48 |
| Background | #FFFBFF | #201A1A |
| Surface | #FFFBFF | #201A1A |
| Error | #BA1A1A | #FFB4AB |
| Success | #2E7D32 | - |
| Warning | #E65100 | - |

### Colori Tipi Pokemon (18)
| Tipo | Colore | Testo |
|------|--------|-------|
| Normal | #A8A77A | scuro #3D3D2A |
| Fire | #EE8130 | scuro #4A2500 |
| Water | #6390F0 | scuro #0D2B5C |
| Electric | #F7D02C | scuro #2D2300 |
| Grass | #7AC74C | scuro #1A3D0E |
| Ice | #96D9D6 | scuro #003330 |
| Fighting | #C22E28 | bianco |
| Poison | #A33EA1 | bianco |
| Ground | #E2BF65 | scuro #3D2E00 |
| Flying | #A98FF3 | scuro #2A1B5C |
| Psychic | #F95587 | scuro #5C0A25 |
| Bug | #A6B91A | scuro #3D4A08 |
| Rock | #B6A136 | scuro #3D3512 |
| Ghost | #735797 | bianco |
| Dragon | #6F35FC | bianco |
| Steel | #B7B7C0 | scuro #2D2D30 |
| Fairy | #EE97AC | scuro #5C1A30 |
| Dark | #705746 | bianco |

### Dimensioni
| Token | Valore | Uso |
|-------|--------|-----|
| PaddingSmall | 8dp | Spacing stretto |
| PaddingMedium | 16dp | Padding standard |
| PaddingLarge | 24dp | Spacing sezioni |
| CardElevation | 4dp | Card standard |
| ElevationRaised | 8dp | Card selezionata |
| ButtonHeight | 48dp | Bottoni standard |
| TouchTargetMin | 48dp | Accessibilita' Android |
| CornerMedium | 12dp | Card |
| CornerPill | 50dp | Bottoni pill Nintendo |

---

## Resources
- 150+ immagini Pokemon in `res/drawable/` (JPG, nome = pokemon.lowercase())
- Database pre-popolato in `AppDatabase.kt` via `createFromAsset` o callback

## Strumenti Configurati
- **Figma MCP**: `claude-talk-to-figma` configurato in `.claude.json`. Per usarlo: aprire Figma Desktop, installare plugin da `src/claude_mcp_plugin/manifest.json`, copiare channel ID, dire "Connect to Figma, channel {ID}".
