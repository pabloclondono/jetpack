package com.sherlock.parcialcorte2.listatareas

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

data class Tarea(
    val id: Int,
    val titulo: String,
    val completada: Boolean = false
)

@Composable
fun ListaTareasScreen(navController: NavController) {
    var tareas by remember { mutableStateOf(listOf<Tarea>()) }
    var mostrarDialogo by remember { mutableStateOf(false) }
    var nuevaTareaTitulo by remember { mutableStateOf("") }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarDialogo = true }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar tarea")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            Text(
                text = "Mis Tareas",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp)
            )
            
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                items(tareas, key = { it.id }) { tarea ->
                    TareaItem(
                        tarea = tarea,
                        onCheckedChange = { checked ->
                            tareas = tareas.map {
                                if (it.id == tarea.id) it.copy(completada = checked) else it
                            }
                        },
                        onDelete = {
                            tareas = tareas.filter { it.id != tarea.id }
                        },
                        onClick = { navController.navigate("detalle/${tarea.id}") }
                    )
                }
            }
        }

        if (mostrarDialogo) {
            AlertDialog(
                onDismissRequest = { mostrarDialogo = false },
                title = { Text("Nueva Tarea") },
                text = {
                    TextField(
                        value = nuevaTareaTitulo,
                        onValueChange = { nuevaTareaTitulo = it },
                        placeholder = { Text("¿Qué hay que hacer?") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (nuevaTareaTitulo.isNotBlank()) {
                                val nuevaId = if (tareas.isEmpty()) 1 else tareas.maxOf { it.id } + 1
                                tareas = tareas + Tarea(id = nuevaId, titulo = nuevaTareaTitulo)
                                nuevaTareaTitulo = ""
                                mostrarDialogo = false
                            }
                        }
                    ) {
                        Text("Agregar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarDialogo = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
fun TareaItem(
    tarea: Tarea,
    onCheckedChange: (Boolean) -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    val alpha by animateFloatAsState(targetValue = if (tarea.completada) 0.5f else 1.0f, label = "fade")

    Card(
        onClick = onClick, // SE USA EL PARÁMETRO NATIVO AQUÍ
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .graphicsLayer(alpha = alpha),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = tarea.completada,
                onCheckedChange = onCheckedChange
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = tarea.titulo,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge.copy(
                    textDecoration = if (tarea.completada) TextDecoration.LineThrough else TextDecoration.None
                )
            )
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar tarea",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleTareaScreen(tareaId: Int, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Tarea") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Estás viendo la tarea con ID: $tareaId", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onBack) {
                Text("Cerrar Detalle")
            }
        }
    }
}
