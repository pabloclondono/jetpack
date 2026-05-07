package com.sherlock.parcialcorte2.clima

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

// ─── Data ────────────────────────────────────────────────────────────────────

enum class WeatherCondition { SUNNY, RAIN, NIGHT }

data class WeatherState(
    val condition: WeatherCondition = WeatherCondition.SUNNY,
    val temperature: Int = 28,
    val city: String = "Cali, Colombia",
    val humidity: Int = 65,
    val windKmh: Int = 12,
    val uvIndex: Int = 7,
    val conditionLabel: String = "Soleado",
)

// ─── Color palettes per condition ────────────────────────────────────────────

private val SunnyGradientTop    = Color(0xFFFF9B4A)
private val SunnyGradientBottom = Color(0xFFFF7043)
private val RainGradientTop     = Color(0xFF1565C0)
private val RainGradientBottom  = Color(0xFF0D47A1)
private val NightGradientTop    = Color(0xFF4A148C)
private val NightGradientBottom = Color(0xFF1A0040)

// ─── Main Screen ─────────────────────────────────────────────────────────────

@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel = viewModel(),
    onBack: () -> Unit = {}
) {
    val state = viewModel.state

    // animateColorAsState for smooth gradient transitions
    val topColor by animateColorAsState(
        targetValue = when (state.condition) {
            WeatherCondition.SUNNY -> SunnyGradientTop
            WeatherCondition.RAIN  -> RainGradientTop
            WeatherCondition.NIGHT -> NightGradientTop
        },
        animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing),
        label = "gradientTop"
    )
    val bottomColor by animateColorAsState(
        targetValue = when (state.condition) {
            WeatherCondition.SUNNY -> SunnyGradientBottom
            WeatherCondition.RAIN  -> RainGradientBottom
            WeatherCondition.NIGHT -> NightGradientBottom
        },
        animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing),
        label = "gradientBottom"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(topColor, bottomColor),
                    start = Offset(0f, 0f),
                    end = Offset(400f, 1200f)
                )
            )
    ) {
        // Back Button
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .statusBarsPadding()
                .padding(8.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = Color.White
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Location
            LocationHeader(city = state.city)

            Spacer(Modifier.height(32.dp))

            // Animated weather icon
            AnimatedWeatherIcon(condition = state.condition)

            Spacer(Modifier.height(16.dp))

            // Temperature (large, centered)
            TemperatureDisplay(temp = state.temperature)

            Spacer(Modifier.height(4.dp))

            // Condition label
            Text(
                text = state.conditionLabel,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White.copy(alpha = 0.9f),
                fontWeight = FontWeight.SemiBold,
            )

            Spacer(Modifier.height(32.dp))

            // Info chips
            WeatherChipsRow(
                humidity = state.humidity,
                wind = state.windKmh,
                uv = state.uvIndex,
            )

            Spacer(Modifier.weight(1f))

            // 4-day forecast
            ForecastRow(condition = state.condition)
        }
    }
}

// ─── Location Header ─────────────────────────────────────────────────────────

@Composable
private fun LocationHeader(city: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Outlined.LocationOn,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.9f),
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = city,
            color = Color.White.copy(alpha = 0.9f),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

// ─── Animated Weather Icon ────────────────────────────────────────────────────

@Composable
private fun AnimatedWeatherIcon(condition: WeatherCondition) {
    val infiniteTransition = rememberInfiniteTransition(label = "iconAnim")

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "iconScale"
    )
    val rotation by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "iconRotation"
    )

    Box(
        modifier = Modifier
            .size(120.dp)
            .scale(scale)
            .rotate(if (condition == WeatherCondition.SUNNY) rotation else 0f),
        contentAlignment = Alignment.Center,
    ) {
        val icon = when (condition) {
            WeatherCondition.SUNNY -> Icons.Outlined.WbSunny
            WeatherCondition.RAIN  -> Icons.Outlined.WaterDrop
            WeatherCondition.NIGHT -> Icons.Outlined.NightsStay
        }
        Icon(
            imageVector = icon,
            contentDescription = condition.name,
            modifier = Modifier.size(100.dp),
            tint = Color.White
        )
    }
}

// ─── Temperature Display ──────────────────────────────────────────────────────

@Composable
private fun TemperatureDisplay(temp: Int) {
    Row(verticalAlignment = Alignment.Top) {
        Text(
            text = "$temp",
            fontSize = 96.sp,
            fontWeight = FontWeight.Light,
            color = Color.White,
            lineHeight = 96.sp,
        )
        Text(
            text = "°C",
            fontSize = 32.sp,
            fontWeight = FontWeight.Normal,
            color = Color.White.copy(alpha = 0.85f),
            modifier = Modifier.padding(top = 16.dp),
        )
    }
}

// ─── Weather Info Chips ───────────────────────────────────────────────────────

@Composable
private fun WeatherChipsRow(humidity: Int, wind: Int, uv: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        WeatherChip(icon = Icons.Outlined.WaterDrop,  label = "$humidity%")
        WeatherChip(icon = Icons.Outlined.Air,        label = "$wind km/h")
        WeatherChip(icon = Icons.Outlined.WbSunny,    label = "UV $uv")
    }
}

@Composable
private fun WeatherChip(icon: ImageVector, label: String) {
    Surface(
        shape = RoundedCornerShape(50),
        color = Color.White.copy(alpha = 0.18f),
        tonalElevation = 0.dp,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = label,
                color = Color.White,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

// ─── 4-Day Forecast Row ───────────────────────────────────────────────────────

private data class ForecastDay(val day: String, val icon: ImageVector, val temp: String)

@Composable
private fun ForecastRow(condition: WeatherCondition) {
    val forecast = when (condition) {
        WeatherCondition.SUNNY -> listOf(
            ForecastDay("LUN", Icons.Outlined.WbSunny, "30°"),
            ForecastDay("MAR", Icons.Outlined.Cloud, "26°"),
            ForecastDay("MIÉ", Icons.Outlined.WaterDrop, "22°"),
            ForecastDay("JUE", Icons.Outlined.NightsStay, "19°"),
        )
        WeatherCondition.RAIN -> listOf(
            ForecastDay("LUN", Icons.Outlined.WaterDrop, "18°"),
            ForecastDay("MAR", Icons.Outlined.WaterDrop, "16°"),
            ForecastDay("MIÉ", Icons.Outlined.Cloud, "20°"),
            ForecastDay("JUE", Icons.Outlined.WbSunny, "23°"),
        )
        WeatherCondition.NIGHT -> listOf(
            ForecastDay("LUN", Icons.Outlined.NightsStay, "17°"),
            ForecastDay("MAR", Icons.Outlined.NightsStay, "16°"),
            ForecastDay("MIÉ", Icons.Outlined.WbSunny, "25°"),
            ForecastDay("JUE", Icons.Outlined.Cloud, "22°"),
        )
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        forecast.forEach { day ->
            ForecastCard(day = day, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun ForecastCard(day: ForecastDay, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.15f),
        modifier = modifier,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
        ) {
            Text(
                text = day.day,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.7f),
            )
            Spacer(Modifier.height(6.dp))
            Icon(
                imageVector = day.icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = day.temp,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
        }
    }
}

// ─── Preview ─────────────────────────────────────────────────────────────────

@Preview(showSystemUi = true)
@Composable
fun WeatherScreenPreview() {
    MaterialTheme {
        WeatherScreen(
            viewModel = WeatherViewModel(),
            onBack = {}
        )
    }
}
