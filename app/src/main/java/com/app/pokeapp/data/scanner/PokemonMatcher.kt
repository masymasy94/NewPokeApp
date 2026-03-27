package com.app.pokeapp.data.scanner

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.app.pokeapp.domain.model.Pokemon
import com.app.pokeapp.domain.model.ScanResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.sqrt

@Singleton
class PokemonMatcher @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var embeddings: Map<Int, FloatArray> = emptyMap()
    var isInitialized: Boolean = false
        private set

    suspend fun initialize(pokemonList: List<Pokemon>) {
        if (isInitialized) return
        withContext(Dispatchers.Default) {
            embeddings = pokemonList.mapNotNull { pokemon ->
                val resourceName = PokemonImageNameResolver.resolve(pokemon)
                val resId = context.resources.getIdentifier(resourceName, "drawable", context.packageName)
                if (resId != 0) {
                    val bitmap = BitmapFactory.decodeResource(context.resources, resId)
                    val features = extractFeatures(bitmap)
                    bitmap.recycle()
                    pokemon.id to features
                } else null
            }.toMap()
            isInitialized = true
        }
    }

    fun findMatch(bitmap: Bitmap): ScanResult? {
        if (!isInitialized) return null
        val queryFeatures = extractFeatures(bitmap)
        var bestId = -1
        var bestSimilarity = -1f

        for ((id, embedding) in embeddings) {
            val similarity = cosineSimilarity(queryFeatures, embedding)
            if (similarity > bestSimilarity) {
                bestSimilarity = similarity
                bestId = id
            }
        }

        return if (bestId != -1 && bestSimilarity > 0.3f) {
            ScanResult(bestId, bestSimilarity)
        } else null
    }

    private fun extractFeatures(bitmap: Bitmap): FloatArray {
        val size = 32
        val scaled = Bitmap.createScaledBitmap(bitmap, size, size, true)
        val features = FloatArray(size * size * 3)
        var idx = 0
        for (y in 0 until size) {
            for (x in 0 until size) {
                val pixel = scaled.getPixel(x, y)
                features[idx++] = android.graphics.Color.red(pixel) / 255f
                features[idx++] = android.graphics.Color.green(pixel) / 255f
                features[idx++] = android.graphics.Color.blue(pixel) / 255f
            }
        }
        scaled.recycle()

        // Normalize to zero mean and unit variance
        val mean = features.average().toFloat()
        var variance = 0f
        for (f in features) {
            val diff = f - mean
            variance += diff * diff
        }
        val std = sqrt(variance / features.size)
        if (std > 0f) {
            for (i in features.indices) {
                features[i] = (features[i] - mean) / std
            }
        }
        return features
    }

    private fun cosineSimilarity(a: FloatArray, b: FloatArray): Float {
        var dot = 0f
        var normA = 0f
        var normB = 0f
        for (i in a.indices) {
            dot += a[i] * b[i]
            normA += a[i] * a[i]
            normB += b[i] * b[i]
        }
        return if (normA > 0f && normB > 0f) dot / (sqrt(normA) * sqrt(normB)) else 0f
    }
}
