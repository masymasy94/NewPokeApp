package com.app.pokeapp.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.pokeapp.R
import com.app.pokeapp.presentation.components.AnimatedLightsBackground

val PokemonFontFamily = FontFamily(
    Font(R.font.pokemon_solid, FontWeight.Normal),
    Font(R.font.pokemon_hollow, FontWeight.Light)
)

@Composable
fun CoverScreen(
    onCoverClicked: () -> Unit
) {
    AnimatedLightsBackground {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            tryAwaitRelease()
                            onCoverClicked()
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_pokemon),
                    contentDescription = "Pokemon Logo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    contentScale = ContentScale.FillWidth
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Master Trainer",
                    fontFamily = PokemonFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 28.sp,
                    color = Color(0xFFFFCB05),
                    textAlign = TextAlign.Center,
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Support App",
                    fontFamily = PokemonFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 28.sp,
                    color = Color(0xFFFFCB05),
                    textAlign = TextAlign.Center,
                    letterSpacing = 2.sp
                )
            }
        }
    }
}
