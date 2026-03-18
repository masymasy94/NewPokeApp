# NewPokeApp - Claude Code Instructions

## Testing & Verifica Automatica

Quando scrivi o modifichi codice Android, usa **in autonomia** (senza chiedere conferma) i seguenti strumenti per verificare il risultato:

### mobile-mcp (MCP Server)
- Usa mobile-mcp per fare build, installare sull'emulatore, navigare la UI e fare screenshot di verifica
- Dopo modifiche alla UI o alla navigazione, installa l'APK sull'emulatore e verifica visivamente il risultato
- Usa gli screenshot per controllare che il layout sia corretto prima di considerare il task completato

### Skills di Testing Android
- Usa la skill `android-testing` per scrivere test (Unit, Integration, Hilt, Screenshot) quando crei o modifichi logica significativa
- Usa la skill `android-kotlin-compose` (android-ninja) come riferimento architetturale principale: architettura, Compose patterns, Navigation3, Gradle, testing
- Usa la skill `compose-performance-audit` quando tocchi codice Compose per individuare recomposition storms o problemi di performance
- Usa la skill `android-emulator-skill` per automazione ADB, build e log monitoring

### Quando testare
- **Sempre**: dopo aver scritto/modificato codice, fai almeno `./gradlew assembleDebug` per verificare la compilazione
- **UI changes**: installa sull'emulatore e verifica visivamente con mobile-mcp
- **Logica/Repository/ViewModel**: scrivi o aggiorna unit test
- **Bug fix**: scrivi un test che riproduce il bug prima di fixarlo
