package com.example.teo_7_11

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import com.example.teo_7_11.ui.theme.Teo711Theme

val Context.dataStore by preferencesDataStore(name = "user_prefs")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Teo711Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MedicationForm(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun MedicationForm(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var nombre by remember { mutableStateOf(TextFieldValue("")) }
    var dosis by remember { mutableStateOf(TextFieldValue("")) }
    var hora by remember { mutableStateOf(TextFieldValue("")) }
    var mensaje by remember { mutableStateOf("") }


    val FONT_SIZE_KEY = floatPreferencesKey("font_size")


    var fontSize by remember { mutableStateOf(16f) }


    LaunchedEffect(Unit) {
        val prefs = context.dataStore.data.first()
        fontSize = prefs[FONT_SIZE_KEY] ?: 16f
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Recordatorio de Medicamentos",
            style = MaterialTheme.typography.headlineSmall.copy(fontSize = fontSize.sp),
            modifier = Modifier.padding(bottom = 20.dp)
        )

        Text(
            text = "Tamaño de fuente: ${fontSize.toInt()} sp",
            fontSize = fontSize.sp
        )

        // 🔹 Control deslizante (slider)
        Slider(
            value = fontSize,
            onValueChange = { newSize ->
                fontSize = newSize
                // Guardar el nuevo tamaño de fuente en DataStore
                scope.launch {
                    context.dataStore.edit { prefs ->
                        prefs[FONT_SIZE_KEY] = newSize
                    }
                }
            },
            valueRange = 12f..28f,
            steps = 8,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre del medicamento", fontSize = fontSize.sp) },
            textStyle = LocalTextStyle.current.copy(fontSize = fontSize.sp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = dosis,
            onValueChange = { dosis = it },
            label = { Text("Dosis (mg, pastillas, etc.)", fontSize = fontSize.sp) },
            textStyle = LocalTextStyle.current.copy(fontSize = fontSize.sp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = hora,
            onValueChange = { hora = it },
            label = { Text("Hora de toma (HH:MM)", fontSize = fontSize.sp) },
            textStyle = LocalTextStyle.current.copy(fontSize = fontSize.sp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {
            if (nombre.text.isNotBlank() && dosis.text.isNotBlank() && hora.text.isNotBlank()) {
                mensaje = "Medicamento registrado correctamente"
            } else {
                mensaje = "Por favor, complete todos los campos "
            }
        }) {
            Text("Guardar Recordatorio", fontSize = fontSize.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = mensaje, fontSize = fontSize.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun MedicationFormPreview() {
    Teo711Theme {
        MedicationForm()
    }
}
