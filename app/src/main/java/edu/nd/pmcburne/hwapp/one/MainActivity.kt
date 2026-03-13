package edu.nd.pmcburne.hwapp.one

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import edu.nd.pmcburne.hwapp.one.ui.theme.HWStarterRepoTheme
import edu.nd.pmcburne.hwapp.one.ui.ScoresRoute

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HWStarterRepoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    ScoresRoute()
                }
            }
        }
    }
}