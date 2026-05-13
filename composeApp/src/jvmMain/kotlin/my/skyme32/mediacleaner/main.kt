package my.skyme32.mediacleaner

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import mediacleaner.composeapp.generated.resources.Res
import mediacleaner.composeapp.generated.resources.app_image
import org.jetbrains.compose.resources.painterResource

fun main() {
    System.setProperty("apple.awt.application.name", "MediaCleaner")
    System.setProperty("compose.desktop.wmClass", "MediaCleaner")
    System.setProperty("sun.awt.wm.classname", "MediaCleaner")
    
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "MediaCleaner",
            state = rememberWindowState(size = DpSize(720.dp, 620.dp)),
            icon = painterResource(Res.drawable.app_image)
        ) {
            App()
        }
    }
}
