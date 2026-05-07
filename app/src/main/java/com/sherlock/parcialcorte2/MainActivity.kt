package com.sherlock.parcialcorte2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sherlock.parcialcorte2.clima.WeatherScreen
import com.sherlock.parcialcorte2.listatareas.DetalleTareaScreen
import com.sherlock.parcialcorte2.listatareas.ListaTareasScreen

// Definimos las rutas de navegación
object Destinos {
    const val MENU = "menu"
    const val BIENVENIDA = "bienvenida"
    const val PERFIL = "perfil"
    const val LISTA_TAREAS = "lista_tareas"
    const val DETALLE_TAREA = "detalle/{tareaId}"
    const val CLIMA = "clima"
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

            MaterialTheme {
                NavHost(navController = navController, startDestination = Destinos.MENU) {
                    composable(Destinos.MENU) {
                        MenuPrincipal(
                            onNavigate = { ruta -> navController.navigate(ruta) }
                        )
                    }
                    composable(Destinos.BIENVENIDA) {
                        ScaffoldWrapper(titulo = "Bienvenida", onBack = { navController.popBackStack() }) {
                            TarjetaBienvenida(nombre = "Usuario")
                        }
                    }
                    composable(Destinos.PERFIL) {
                        ScaffoldWrapper(titulo = "Mi Perfil", onBack = { navController.popBackStack() }) {
                            TarjetaPerfil(
                                nombre = "Pablo Londoño",
                                cargo = "Programador sin panza, no genera confianza",
                            )
                        }
                    }
                    composable(Destinos.LISTA_TAREAS) {
                        ListaTareasScreen(navController = navController)
                    }
                    composable(
                        route = Destinos.DETALLE_TAREA,
                        arguments = listOf(navArgument("tareaId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val tareaId = backStackEntry.arguments?.getInt("tareaId") ?: 0
                        DetalleTareaScreen(tareaId = tareaId, onBack = { navController.popBackStack() })
                    }
                    composable(Destinos.CLIMA) {
                        WeatherScreen(onBack = { navController.popBackStack() })
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldWrapper(titulo: String, onBack: () -> Unit, content: @Composable (PaddingValues) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(titulo) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            content(innerPadding)
        }
    }
}

@Composable
fun MenuPrincipal(onNavigate: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Mis Aplicaciones",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 32.dp)
        )

        val itemsMenu = listOf(
            MenuOption("Bienvenida", Icons.Default.Home, Destinos.BIENVENIDA, Color(0xFF4CAF50)),
            MenuOption("Perfil", Icons.Default.Person, Destinos.PERFIL, Color(0xFF2196F3)),
            MenuOption("Lista Tareas", Icons.Default.List, Destinos.LISTA_TAREAS, Color(0xFFFF9800)),
            MenuOption("Clima", Icons.Default.Cloud, Destinos.CLIMA, Color(0xFF03A9F4))
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(itemsMenu) { opcion ->
                CardMenu(opcion) { onNavigate(opcion.ruta) }
            }
        }
    }
}

data class MenuOption(
    val titulo: String,
    val icono: ImageVector,
    val ruta: String,
    val color: Color
)

@Composable
fun CardMenu(opcion: MenuOption, onClick: () -> Unit) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = opcion.icono,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = opcion.color
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = opcion.titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun TarjetaBienvenida(nombre: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Hola, $nombre!",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Bienvenido a Jetpack Compose",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun TarjetaPerfil(
    nombre: String,
    cargo: String,
    modifier: Modifier = Modifier
) {
    val azulOscuro = Color(0xFF1A237E)
    Card(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = azulOscuro, contentColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(painter = painterResource(id = R.drawable.sherlock), contentDescription = null, modifier = Modifier.size(50.dp), tint = Color.Unspecified)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = nombre, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(text = cargo, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.8f))
            
            Spacer(modifier = Modifier.height(12.dp))
            

            Text(
                text = """Posible ingeniero de sistemas de la universidad libre, con experiencia 
                    en desarrollo de aplicaciones web, manejo de bases de datos y manejo de lenguajes
                    como Python, C++ y Kotlin""",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))
            
            // Fila de iconos para redes sociales
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* Acción 1 */ }) {
                    Icon(painter = painterResource(id = R.drawable.ic_instagram), contentDescription = "Instagram", tint = Color.Unspecified)
                }
                IconButton(onClick = { /* Acción 2 */ }) {
                    Icon(painterResource(id = R.drawable.ic_github), contentDescription = "GitHub", tint = Color.White)
                }
                IconButton(onClick = { /* Acción 3 */ }) {
                    Icon(painterResource(id = R.drawable.ic_linkedin), contentDescription = "LinkedIn", tint = Color.White)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMenu() {
    MaterialTheme {
        MenuPrincipal(onNavigate = {})
    }
}
