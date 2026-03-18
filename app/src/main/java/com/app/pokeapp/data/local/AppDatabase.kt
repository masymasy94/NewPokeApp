package com.app.pokeapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.app.pokeapp.data.local.dao.ChallengerDao
import com.app.pokeapp.data.local.dao.PokemonDao
import com.app.pokeapp.data.local.dao.RandomEncounterDao
import com.app.pokeapp.data.local.entity.ChallengerEntity
import com.app.pokeapp.data.local.entity.PokemonEntity
import com.app.pokeapp.data.local.entity.RandomEncounterEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [PokemonEntity::class, ChallengerEntity::class, RandomEncounterEntity::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pokemonDao(): PokemonDao
    abstract fun challengerDao(): ChallengerDao
    abstract fun randomEncounterDao(): RandomEncounterDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pokeapp_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : Callback() {
        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    val pokemonDao = database.pokemonDao()
                    val challengerDao = database.challengerDao()
                    val randomEncounterDao = database.randomEncounterDao()
                    if (pokemonDao.getCount() == 0) {
                        pokemonDao.insertAll(getPokemonList())
                        challengerDao.insertAll(getChallengerList())
                        randomEncounterDao.insertAll(getRandomEncounterList())
                    }
                }
            }
        }

        private fun getPokemonList(): List<PokemonEntity> {
            return listOf(
                PokemonEntity(1, "Bulbasaur", "Bulbasaur", "GRASS,POISON", 4, 1, null, 2, "parassiseme", "GRASS"),
                PokemonEntity(2, "Ivysaur", "Ivysaur", "GRASS,POISON", 7, 2, 1, 3, "foglielama", "GRASS"),
                PokemonEntity(3, "Venusaur", "Venusaur", "GRASS,POISON", 8, 3, 2, null, "solarraggio", "GRASS"),
                PokemonEntity(4, "Charmander", "Charmander", "FIRE", 4, 1, null, 5, "braciere", "FIRE"),
                PokemonEntity(5, "Charmeleon", "Charmeleon", "FIRE", 7, 2, 4, 6, "lanciafiamme", "FIRE"),
                PokemonEntity(6, "Charizard", "Charizard", "FIRE,FLYING", 8, 3, 5, null, "turbofuoco", "FIRE"),
                PokemonEntity(7, "Squirtle", "Squirtle", "WATER", 4, 1, null, 8, "pistolacqua", "WATER"),
                PokemonEntity(8, "Wartortle", "Wartortle", "WATER", 7, 2, 7, 9, "capocciata", "WATER"),
                PokemonEntity(9, "Blastoise", "Blastoise", "WATER", 8, 3, 8, null, "idropompa", "WATER"),
                PokemonEntity(10, "Caterpie", "Caterpie", "BUG", 1, 1, null, 11, "millebave", "BUG"),
                PokemonEntity(11, "Metapod", "Metapod", "BUG", 3, 2, 10, 12, "rafforzatore", "NORMAL"),
                PokemonEntity(12, "Butterfree", "Butterfree", "BUG,FLYING", 5, 3, 11, null, "sonnifero", "ICE"),
                PokemonEntity(13, "Weedle", "Weedle", "BUG,POISON", 2, 1, null, 14, "velenospina", "POISON"),
                PokemonEntity(14, "Kakuna", "Kakuna", "BUG,POISON", 3, 2, 13, 15, "rafforzatore", "NORMAL"),
                PokemonEntity(15, "Beedrill", "Beedrill", "BUG,POISON", 5, 3, 14, null, "doppio ago", "BUG"),
                PokemonEntity(16, "Pidgey", "Pidgey", "NORMAL,FLYING", 2, 1, null, 17, "raffica", "NORMAL"),
                PokemonEntity(17, "Pidgeotto", "Pidgeotto", "NORMAL,FLYING", 4, 2, 16, 18, "turbine", "FLYING"),
                PokemonEntity(18, "Pidgeot", "Pidgeot", "NORMAL,FLYING", 6, 3, 17, null, "attacco d ala", "FLYING"),
                PokemonEntity(19, "Rattata", "Rattata", "NORMAL", 2, 1, null, 20, "attacco rapido", "NORMAL"),
                PokemonEntity(20, "Raticate", "Raticate", "NORMAL", 6, 2, 19, null, "iperzanna", "NORMAL"),
                PokemonEntity(21, "Spearow", "Spearow", "NORMAL,FLYING", 3, 1, null, 22, "beccata", "FLYING"),
                PokemonEntity(22, "Fearow", "Fearow", "NORMAL,FLYING", 5, 2, 21, null, "perforbecco", "FLYING"),
                PokemonEntity(23, "Ekans", "Ekans", "POISON", 3, 1, null, 24, "morso", "POISON"),
                PokemonEntity(24, "Arbok", "Arbok", "POISON", 5, 2, 23, null, "acido", "POISON"),
                PokemonEntity(25, "Pikachu", "Pikachu", "ELECTRIC", 3, 1, null, 26, "tuonoshock", "ELECTRIC"),
                PokemonEntity(26, "Raichu", "Raichu", "ELECTRIC", 6, 2, 25, null, "tuononda", "ELECTRIC"),
                PokemonEntity(27, "Sandshrew", "Sandshrew", "GROUND", 2, 1, null, 28, "graffio", "NORMAL"),
                PokemonEntity(28, "Sandslash", "Sandslash", "GROUND", 5, 2, 27, null, "lacerazione", "GROUND"),
                PokemonEntity(29, "Nidoran", "Nidoran", "POISON", 2, 1, null, 30, "azione", "NORMAL"),
                PokemonEntity(30, "Nidorina", "Nidorina", "POISON", 5, 2, 29, 31, "morso", "DARK"),
                PokemonEntity(31, "Nidoqueen", "Nidoqueen", "POISON,GROUND", 5, 3, 30, null, "body slam", "NORMAL"),
                PokemonEntity(32, "Nidoran", "Nidoran", "POISON", 3, 1, null, 33, "azione", "NORMAL"),
                PokemonEntity(33, "Nidorino", "Nidorino", "POISON", 5, 2, 32, 34, "furia", "NORMAL"),
                PokemonEntity(34, "Nidoking", "Nidoking", "POISON,GROUND", 6, 3, 33, null, "colpo", "NORMAL"),
                PokemonEntity(35, "Clefairy", "Clefairy", "FAIRY", 3, 1, null, 36, "libbra", "NORMAL"),
                PokemonEntity(36, "Clefable", "Clefable", "FAIRY", 4, 2, 35, null, "doppiasberla", "NORMAL"),
                PokemonEntity(37, "Vulpix", "Vulpix", "FIRE", 1, 1, null, 38, "braciere", "FIRE"),
                PokemonEntity(38, "Ninetales", "Ninetales", "FIRE", 5, 2, 37, null, "attacco rapido", "NORMAL"),
                PokemonEntity(39, "Jigglypuff", "Jigglypuff", "NORMAL,FAIRY", 2, 1, null, 40, "canto", "NORMAL"),
                PokemonEntity(40, "Wigglytuff", "Wigglytuff", "NORMAL,FAIRY", 4, 2, 39, null, "inibitore", "NORMAL"),
                PokemonEntity(41, "Zubat", "Zubat", "POISON,FLYING", 3, 1, null, 42, "sanguisuga", "BUG"),
                PokemonEntity(42, "Golbat", "Golbat", "POISON,FLYING", 4, 2, 41, null, "stordiraggio", "FLYING"),
                PokemonEntity(43, "Oddish", "Oddish", "GRASS,POISON", 2, 1, null, 44, "assorbimento", "GRASS"),
                PokemonEntity(44, "Gloom", "Gloom", "GRASS,POISON", 5, 2, 43, 45, "acido", "POISON"),
                PokemonEntity(45, "Vileplume", "Vileplume", "GRASS,POISON", 5, 3, 44, null, "paralizzante", "GRASS"),
                PokemonEntity(46, "Paras", "Paras", "BUG,GRASS", 1, 1, null, 47, "graffio", "NORMAL"),
                PokemonEntity(47, "Parasect", "Parasect", "BUG,GRASS", 5, 2, 46, null, "spora", "BUG"),
                PokemonEntity(48, "Venonat", "Venonat", "BUG,POISON", 2, 1, null, 49, "azione", "NORMAL"),
                PokemonEntity(49, "Venomoth", "Venomoth", "BUG,POISON", 5, 2, 48, null, "psicoraggio", "PSYCHIC"),
                PokemonEntity(50, "Diglett", "Diglett", "GROUND", 3, 1, null, 51, "fossa", "GROUND"),
                PokemonEntity(51, "Dugtrio", "Dugtrio", "GROUND", 4, 2, 50, null, "lacerazione", "GROUND"),
                PokemonEntity(52, "Meowth", "Meowth", "NORMAL", 3, 1, null, 53, "morso", "DARK"),
                PokemonEntity(53, "Persian", "Persian", "NORMAL", 6, 2, 52, null, "sfuriate", "NORMAL"),
                PokemonEntity(54, "Psyduck", "Psyduck", "WATER", 2, 1, null, 55, "graffio", "NORMAL"),
                PokemonEntity(55, "Golduck", "Golduck", "WATER", 5, 2, 54, null, "confusione", "PSYCHIC"),
                PokemonEntity(56, "Mankey", "Mankey", "FIGHTING", 5, 1, null, 57, "colpo-karate", "FIGHTING"),
                PokemonEntity(57, "Primeape", "Primeape", "FIGHTING", 7, 2, 56, null, "movimento sismico", "FIGHTING"),
                PokemonEntity(58, "Growlithe", "Growlithe", "FIRE", 4, 1, null, 59, "braciere", "FIRE"),
                PokemonEntity(59, "Arcanine", "Arcanine", "FIRE", 7, 2, 58, null, "riduttore", "NORMAL"),
                PokemonEntity(60, "Poliwag", "Poliwag", "WATER", 2, 1, null, 61, "bolla", "WATER"),
                PokemonEntity(61, "Poliwhirl", "Poliwhirl", "WATER", 4, 2, 60, 62, "pistolacqua", "WATER"),
                PokemonEntity(62, "Poliwrath", "Poliwrath", "WATER,FIGHTING", 7, 3, 61, null, "pistolacqua", "WATER"),
                PokemonEntity(63, "Abra", "Abra", "PSYCHIC", 2, 1, null, 64, "teletrasporto", "PSYCHIC"),
                PokemonEntity(64, "Kadabra", "Kadabra", "PSYCHIC", 4, 2, 63, 65, "psicoraggio", "PSYCHIC"),
                PokemonEntity(65, "Alakazam", "Alakazam", "PSYCHIC", 7, 3, 64, null, "psichico", "PSYCHIC"),
                PokemonEntity(66, "Machop", "Machop", "FIGHTING", 5, 1, null, 67, "colpo-karate", "FIGHTING"),
                PokemonEntity(67, "Machoke", "Machoke", "FIGHTING", 7, 2, 66, 68, "sottomissione", "FIGHTING"),
                PokemonEntity(68, "Machamp", "Machamp", "FIGHTING", 8, 3, 67, null, "sottomissione", "FIGHTING"),
                PokemonEntity(69, "Bellsprout", "Bellsprout", "GRASS,POISON", 1, 1, null, 70, "frustata", "GRASS"),
                PokemonEntity(70, "Weepinbell", "Weepinbell", "GRASS,POISON", 3, 2, 69, 71, "avvolgibotta", "GRASS"),
                PokemonEntity(71, "Victreebel", "Victreebel", "GRASS,POISON", 6, 3, 70, null, "schianto", "NORMAL"),
                PokemonEntity(72, "Tentacool", "Tentacool", "WATER,POISON", 1, 1, null, 73, "acido", "POISON"),
                PokemonEntity(73, "Tentacruel", "Tentacruel", "WATER,POISON", 4, 2, 72, null, "limitazione", "POISON"),
                PokemonEntity(74, "Geodude", "Geodude", "ROCK,GROUND", 1, 1, null, 75, "azione", "NORMAL"),
                PokemonEntity(75, "Graveler", "Graveler", "ROCK,GROUND", 4, 2, 74, 76, "sassata", "ROCK"),
                PokemonEntity(76, "Golem", "Golem", "ROCK,GROUND", 6, 3, 75, null, "terremoto", "GROUND"),
                PokemonEntity(77, "Ponyta", "Ponyta", "FIRE", 5, 1, null, 78, "pestone", "NORMAL"),
                PokemonEntity(78, "Rapidash", "Rapidash", "FIRE", 7, 2, 77, null, "turbofuoco", "FIRE"),
                PokemonEntity(79, "Slowpoke", "Slowpoke", "WATER,PSYCHIC", 2, 1, null, 80, "confusione", "PSYCHIC"),
                PokemonEntity(80, "Slowbro", "Slowbro", "WATER,PSYCHIC", 3, 2, 79, null, "pistolacqua", "WATER"),
                PokemonEntity(81, "Magnemite", "Magnemite", "ELECTRIC,STEEL", 2, 1, null, 82, "tuonoshock", "ELECTRIC"),
                PokemonEntity(82, "Magneton", "Magneton", "ELECTRIC,STEEL", 4, 2, 81, null, "tuononda", "ELECTRIC"),
                PokemonEntity(83, "Farfetch'd", "Farfetch'd", "NORMAL,FLYING", 5, 1, null, null, "furia", "NORMAL"),
                PokemonEntity(84, "Doduo", "Doduo", "NORMAL,FLYING", 3, 1, null, 85, "beccata", "FLYING"),
                PokemonEntity(85, "Dodrio", "Dodrio", "NORMAL,FLYING", 5, 2, 84, null, "tripletta", "NORMAL"),
                PokemonEntity(86, "Seel", "Seel", "WATER", 5, 1, null, 87, "raggiaurora", "ICE"),
                PokemonEntity(87, "Dewgong", "Dewgong", "WATER,ICE", 7, 2, 86, null, "geloraggio", "ICE"),
                PokemonEntity(88, "Grimer", "Grimer", "POISON", 3, 1, null, 89, "velenogas", "POISON"),
                PokemonEntity(89, "Muk", "Muk", "POISON", 4, 2, 88, null, "fango", "POISON"),
                PokemonEntity(90, "Shellder", "Shellder", "WATER", 2, 1, null, 91, "tenaglia", "NORMAL"),
                PokemonEntity(91, "Cloyster", "Cloyster", "WATER,ICE", 5, 2, 90, null, "sparalance", "ICE"),
                PokemonEntity(92, "Gastly", "Gastly", "GHOST,POISON", 2, 1, null, 93, "leccata", "GHOST"),
                PokemonEntity(93, "Haunter", "Haunter", "GHOST,POISON", 4, 2, 92, 94, "ombra notturna", "GHOST"),
                PokemonEntity(94, "Gengar", "Gengar", "GHOST,POISON", 7, 3, 93, null, "mangiasogni", "GHOST"),
                PokemonEntity(95, "Onix", "Onix", "ROCK,GROUND", 7, 1, null, null, "schianto", "NORMAL"),
                PokemonEntity(96, "Drowzee", "Drowzee", "PSYCHIC", 4, 1, null, 97, "confusione", "PSYCHIC"),
                PokemonEntity(97, "Hypno", "Hypno", "PSYCHIC", 6, 2, 96, null, "psichico", "PSYCHIC"),
                PokemonEntity(98, "Krabby", "Krabby", "WATER", 3, 1, null, 99, "presa", "NORMAL"),
                PokemonEntity(99, "Kingler", "Kingler", "WATER", 5, 2, 98, null, "martellata", "NORMAL"),
                PokemonEntity(100, "Voltorb", "Voltorb", "ELECTRIC", 3, 1, null, 101, "sonic boom", "NORMAL"),
                PokemonEntity(101, "Electrode", "Electrode", "ELECTRIC", 5, 2, 100, null, "esplosione", "NORMAL"),
                PokemonEntity(102, "Exeggcute", "Exeggcute", "GRASS,PSYCHIC", 1, 1, null, 103, "attacco pioggia", "GRASS"),
                PokemonEntity(103, "Exeggutor", "Exeggutor", "GRASS,PSYCHIC", 7, 2, 102, null, "pestone", "NORMAL"),
                PokemonEntity(104, "Cubone", "Cubone", "GROUND", 3, 1, null, 105, "ossoclava", "GROUND"),
                PokemonEntity(105, "Marowak", "Marowak", "GROUND", 6, 2, 104, null, "ossomerang", "GROUND"),
                PokemonEntity(106, "Hitmonlee", "Hitmonlee", "FIGHTING", 7, 1, null, null, "megacalcio", "FIGHTING"),
                PokemonEntity(107, "Hitmonchan", "Hitmonchan", "FIGHTING", 7, 1, null, null, "megapugno", "FIGHTING"),
                PokemonEntity(108, "Lickitung", "Lickitung", "NORMAL", 4, 1, null, null, "schianto", "NORMAL"),
                PokemonEntity(109, "Koffing", "Koffing", "POISON", 4, 1, null, 110, "fango", "POISON"),
                PokemonEntity(110, "Weezing", "Weezing", "POISON", 5, 2, 109, null, "autodistruzione", "NORMAL"),
                PokemonEntity(111, "Rhyhorn", "Rhyhorn", "ROCK,GROUND", 6, 1, null, 112, "perforcorno", "NORMAL"),
                PokemonEntity(112, "Rhydon", "Rhydon", "ROCK,GROUND", 7, 2, 111, null, "perforcorno", "GROUND"),
                PokemonEntity(113, "Chansey", "Chansey", "NORMAL", 7, 1, null, null, "sdoppiatore", "NORMAL"),
                PokemonEntity(114, "Tangela", "Tangela", "GRASS", 3, 1, null, null, "legatutto", "GRASS"),
                PokemonEntity(115, "Kangaskhan", "Kangaskhan", "NORMAL", 7, 1, null, null, "stordipugno", "NORMAL"),
                PokemonEntity(116, "Horsea", "Horsea", "WATER", 1, 1, null, 117, "bolla", "WATER"),
                PokemonEntity(117, "Seadra", "Seadra", "WATER", 5, 2, 116, null, "idropompa", "WATER"),
                PokemonEntity(118, "Goldeen", "Goldeen", "WATER", 2, 1, null, 119, "beccata", "FLYING"),
                PokemonEntity(119, "Seaking", "Seaking", "WATER", 4, 2, 118, null, "incornata", "NORMAL"),
                PokemonEntity(120, "Staryu", "Staryu", "WATER", 2, 1, null, 121, "pistolacqua", "WATER"),
                PokemonEntity(121, "Starmie", "Starmie", "WATER,PSYCHIC", 4, 2, 120, null, "comete", "NORMAL"),
                PokemonEntity(122, "MrMime", "MrMime", "PSYCHIC", 6, 1, null, null, "confusione", "PSYCHIC"),
                PokemonEntity(123, "Scyther", "Scyther", "BUG,FLYING", 7, 1, null, null, "lacerazione", "BUG"),
                PokemonEntity(124, "Jynx", "Jynx", "ICE,PSYCHIC", 5, 1, null, null, "gelopugno", "ICE"),
                PokemonEntity(125, "Electabuzz", "Electabuzz", "ELECTRIC", 8, 1, null, null, "tuono", "ELECTRIC"),
                PokemonEntity(126, "Magmar", "Magmar", "FIRE", 7, 1, null, null, "lanciafiamme", "FIRE"),
                PokemonEntity(127, "Pinsir", "Pinsir", "BUG", 5, 1, null, null, "ghigliottina", "BUG"),
                PokemonEntity(128, "Tauros", "Tauros", "NORMAL", 8, 1, null, null, "riduttore", "NORMAL"),
                PokemonEntity(129, "Magikarp", "Magikarp", "WATER", 1, 1, null, 130, "azione", "NORMAL"),
                PokemonEntity(130, "Gyarados", "Gyarados", "WATER,FLYING", 8, 2, 129, null, "ira di drago", "DRAGON"),
                PokemonEntity(131, "Lapras", "Lapras", "WATER,ICE", 7, 1, null, null, "geloraggio", "ICE"),
                PokemonEntity(132, "Ditto", "Ditto", "NORMAL", 5, 1, null, null, "trasformazione", "NORMAL"),
                PokemonEntity(133, "Eevee", "Eevee", "NORMAL", 3, 1, null, null, "morso", "DARK"),
                PokemonEntity(134, "Vaporeon", "Vaporeon", "WATER", 6, 1, null, null, "idropompa", "WATER"),
                PokemonEntity(135, "Jolteon", "Jolteon", "ELECTRIC", 6, 1, null, null, "tuono", "ELECTRIC"),
                PokemonEntity(136, "Flareon", "Flareon", "FIRE", 6, 1, null, null, "lanciafiamme", "FIRE"),
                PokemonEntity(137, "Porygon", "Porygon", "NORMAL", 4, 1, null, null, "azione", "NORMAL"),
                PokemonEntity(138, "Omanyte", "Omanyte", "ROCK,WATER", 5, 1, null, 139, "incornata", "NORMAL"),
                PokemonEntity(139, "Omastar", "Omastar", "ROCK,WATER", 7, 2, 138, null, "idropompa", "WATER"),
                PokemonEntity(140, "Kabuto", "Kabuto", "ROCK,WATER", 5, 1, null, 141, "lacerazione", "BUG"),
                PokemonEntity(141, "Kabutops", "Kabutops", "ROCK,WATER", 7, 2, 140, null, "idropompa", "WATER"),
                PokemonEntity(142, "Aerodactyl", "Aerodactyl", "ROCK,FLYING", 7, 1, null, null, "supersuono", "NORMAL"),
                PokemonEntity(143, "Snorlax", "Snorlax", "NORMAL", 7, 1, null, null, "body slam", "NORMAL"),
                PokemonEntity(144, "Articuno", "Articuno", "ICE,FLYING", 9, 1, null, null, "bora", "ICE"),
                PokemonEntity(145, "Zapdos", "Zapdos", "ELECTRIC,FLYING", 9, 1, null, null, "tuono", "ELECTRIC"),
                PokemonEntity(146, "Moltres", "Moltres", "FIRE,FLYING", 9, 1, null, null, "aeroattacco", "FLYING"),
                PokemonEntity(147, "Dratini", "Dratini", "DRAGON", 5, 1, null, 148, "avvolgibotta", "DRAGON"),
                PokemonEntity(148, "Dragonair", "Dragonair", "DRAGON", 6, 2, 147, 149, "ira di drago", "DRAGON"),
                PokemonEntity(149, "Dragonite", "Dragonite", "DRAGON,FLYING", 7, 3, 148, null, "ira di drago", "DRAGON"),
                PokemonEntity(150, "Mewtwo", "Mewtwo", "PSYCHIC", 9, 1, null, null, "psichico", "PSYCHIC")
            )
        }

        private fun getChallengerList(): List<ChallengerEntity> {
            return listOf(
                ChallengerEntity(1, "Lorelei", "GYM_LEADER", "131", "Cascade Badge", "#4169E1", basePower = 10, firstBonus = 6, secondBonus = 7, thirdBonus = 8),
                ChallengerEntity(2, "Bruno", "GYM_LEADER", "68", "Boulder Badge", "#8B4513", basePower = 11, firstBonus = 6, secondBonus = 7, thirdBonus = 8),
                ChallengerEntity(3, "Agatha", "GYM_LEADER", "94", "Soul Badge", "#800080", basePower = 12, firstBonus = 6, secondBonus = 7, thirdBonus = 8),
                ChallengerEntity(4, "Lance", "GYM_LEADER", "148", "Thunder Badge", "#FFD700", basePower = 14, firstBonus = 6, secondBonus = 7, thirdBonus = 8),
                ChallengerEntity(5, "Gary", "TRAINER", "6,9,59,68,89,95,103,112", null, null, basePower = 15, firstBonus = 6, secondBonus = 7, thirdBonus = 8),
                ChallengerEntity(6, "Brock", "GYM_LEADER", "74,75,76,95", "Boulder Badge", "#8B4513", basePower = 2, firstBonus = 2, secondBonus = 3, thirdBonus = 4),
                ChallengerEntity(7, "Misty", "GYM_LEADER", "8,9,55,72,73,80,90", "Cascade Badge", "#4169E1", basePower = 4, firstBonus = 2, secondBonus = 3, thirdBonus = 4),
                ChallengerEntity(8, "Lt. Surge", "GYM_LEADER", "25,26,81,82,100,125", "Thunder Badge", "#FFD700", basePower = 7, firstBonus = 3, secondBonus = 4, thirdBonus = 5),
                ChallengerEntity(9, "Erika", "GYM_LEADER", "1,2,3,43,44,45,69,70,71", "Rainbow Badge", "#90EE90", basePower = 8, firstBonus = 3, secondBonus = 4, thirdBonus = 5),
                ChallengerEntity(10, "Koga", "GYM_LEADER", "33,34,41,42,48,49,73,89", "Soul Badge", "#800080", basePower = 8, firstBonus = 3, secondBonus = 5, thirdBonus = 7),
                ChallengerEntity(11, "Sabrina", "GYM_LEADER", "63,64,65,79,80,96,97,102", "Marsh Badge", "#FF69B4", basePower = 6, firstBonus = 1, secondBonus = 3, thirdBonus = 5),
                ChallengerEntity(12, "Blaine", "GYM_LEADER", "58,59,77,78,126", "Volcano Badge", "#FF4500", basePower = 11, firstBonus = 4, secondBonus = 6, thirdBonus = 7),
                ChallengerEntity(13, "Giovanni", "GYM_LEADER", "50,51,74,75,76,95,112,115", "Earth Badge", "#DAA520", basePower = 12, firstBonus = 6, secondBonus = 7, thirdBonus = 9)
            )
        }

        private fun getRandomEncounterList(): List<RandomEncounterEntity> {
            return listOf(
                RandomEncounterEntity(1, "giovane", 19, 2, "rosa"),
                RandomEncounterEntity(2, "pigliamosche", 10, 1, "rosa"),
                RandomEncounterEntity(3, "pigliamosche", 13, 2, "rosa"),
                RandomEncounterEntity(4, "recluta rocket", 41, 3, "rosa"),
                RandomEncounterEntity(5, "pupa", 29, 2, "rosa"),
                RandomEncounterEntity(6, "campeggiatore", 27, 2, "rosa"),
                RandomEncounterEntity(7, "pic nic girl", 43, 2, "rosa"),
                RandomEncounterEntity(8, "recluta rocket", 23, 3, "rosa"),
                RandomEncounterEntity(9, "recluta rocket", 110, 4, "rosa"),
                RandomEncounterEntity(10, "cervellone", 93, 4, "verde"),
                RandomEncounterEntity(11, "cinturanera", 56, 5, "verde"),
                RandomEncounterEntity(12, "maestro karate", 107, 7, "verde"),
                RandomEncounterEntity(13, "maestro karate", 106, 7, "verde"),
                RandomEncounterEntity(14, "medium", 93, 4, "verde"),
                RandomEncounterEntity(15, "montanaro", 75, 4, "verde"),
                RandomEncounterEntity(16, "recluta rocket", 96, 4, "verde"),
                RandomEncounterEntity(17, "psiche", 80, 3, "verde"),
                RandomEncounterEntity(18, "rischiatutto", 61, 4, "verde"),
                RandomEncounterEntity(19, "scienziato", 101, 5, "verde"),
                RandomEncounterEntity(20, "avicoltore", 22, 5, "blu"),
                RandomEncounterEntity(21, "bellezza", 2, 7, "blu"),
                RandomEncounterEntity(22, "gentiluomo", 58, 4, "blu"),
                RandomEncounterEntity(23, "giocoliere", 122, 6, "blu"),
                RandomEncounterEntity(24, "marinaio", 66, 5, "blu"),
                RandomEncounterEntity(25, "pescatore", 129, 1, "blu"),
                RandomEncounterEntity(26, "recluta rocket", 24, 5, "blu"),
                RandomEncounterEntity(27, "rischiatutto", 100, 3, "blu"),
                RandomEncounterEntity(28, "rockettaro", 101, 5, "blu"),
                RandomEncounterEntity(29, "domatore", 112, 6, "blu")
            )
        }
    }
}
