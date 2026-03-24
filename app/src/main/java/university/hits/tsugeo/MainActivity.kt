package university.hits.tsugeo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import university.hits.tsugeo.core.ui.components.ButtonIconWithText
import university.hits.tsugeo.core.ui.theme.TSUGeoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TSUGeoTheme {
                ButtonIconWithText(
                    onClick = { /* WIP */ },
                    icon = ImageVector.Companion.vectorResource(R.drawable.ic_launcher_foreground),
                    text = "Текст",
                    enabled = true,
                    contentDescription = "Тест"
                )
            }
        }
    }
}