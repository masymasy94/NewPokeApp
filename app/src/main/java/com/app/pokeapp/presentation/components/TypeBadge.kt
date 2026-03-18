package com.app.pokeapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.app.pokeapp.domain.model.enums.PokemonType
import com.app.pokeapp.presentation.theme.Dimens

@Composable
fun TypeBadge(
    type: PokemonType,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(Dimens.CornerSmall))
            .background(type.color)
            .padding(horizontal = 12.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = type.displayName,
            style = MaterialTheme.typography.labelMedium,
            color = type.onColor
        )
    }
}

@Composable
fun TypeBadgeList(
    types: List<PokemonType>,
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.layout.Row(
        modifier = modifier,
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
    ) {
        types.forEach { type ->
            TypeBadge(type = type)
        }
    }
}
