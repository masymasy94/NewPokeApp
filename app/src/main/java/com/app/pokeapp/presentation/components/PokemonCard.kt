package com.app.pokeapp.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.app.pokeapp.domain.model.Pokemon
import com.app.pokeapp.presentation.theme.Dimens

@Composable
fun PokemonCard(
    pokemon: Pokemon,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val imageRes = remember(pokemon.id) {
        val imageName = when (pokemon.name) {
            "Farfetch'd" -> "farfetchd"
            "MrMime" -> "mr_mime"
            "Nidoran" -> if (pokemon.id == 29) "nidoran_f" else "nidoran_m"
            else -> pokemon.name.lowercase()
        }
        context.resources.getIdentifier(imageName, "drawable", context.packageName)
    }
    val typeColor = pokemon.types.firstOrNull()?.color ?: MaterialTheme.colorScheme.primary

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Type-colored accent stripe
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(80.dp)
                    .background(typeColor)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Pokemon image in circle
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(typeColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (imageRes != 0) {
                        Image(
                            painter = painterResource(id = imageRes),
                            contentDescription = pokemon.nameItalian,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Text(
                            text = "#${pokemon.id}",
                            style = MaterialTheme.typography.labelSmall,
                            color = typeColor
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "#${pokemon.id.toString().padStart(3, '0')}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = pokemon.nameItalian,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    TypeBadgeList(types = pokemon.types)
                }

                // Power badge
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${pokemon.basePower}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "PWR",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun PokemonListItem(
    pokemon: Pokemon,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val imageRes = remember(pokemon.id) {
        val imageName = when (pokemon.name) {
            "Farfetch'd" -> "farfetchd"
            "MrMime" -> "mr_mime"
            "Nidoran" -> if (pokemon.id == 29) "nidoran_f" else "nidoran_m"
            else -> pokemon.name.lowercase()
        }
        context.resources.getIdentifier(imageName, "drawable", context.packageName)
    }
    val typeColor = pokemon.types.firstOrNull()?.color ?: MaterialTheme.colorScheme.primary

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Small circular image
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(typeColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            if (imageRes != 0) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = pokemon.nameItalian,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Fit
                )
            } else {
                Text(
                    text = "#${pokemon.id}",
                    style = MaterialTheme.typography.labelSmall,
                    color = typeColor
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = "#${pokemon.id.toString().padStart(3, '0')}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = pokemon.nameItalian,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )

        TypeBadgeList(types = pokemon.types)
    }
}
