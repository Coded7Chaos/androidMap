package com.tonygnk.maplibredemo

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.Style
import org.ramani.compose.CameraPosition
import org.ramani.compose.MapLibre

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyMap()
        }
    }
}

@Composable
fun MyMap(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    val styleBuilder = remember {
        val styleManager = MapStyleManager(context)
        val style = when (val result = styleManager.setupStyle()) {
            is MapStyleManager.StyleSetupResult.Error -> {
                throw result.exception
            }

            is MapStyleManager.StyleSetupResult.Success -> result.styleFile
        }
        Style.Builder().fromUri(
            Uri.fromFile(style).toString()
        )
    }

    val cameraPosition = rememberSaveable {
       CameraPosition(
            target = LatLng(35.9103, 14.419),
            zoom = 11.0,
        )
    }

    MapLibre(
        modifier = modifier,
        styleBuilder = styleBuilder,
        cameraPosition = cameraPosition
    ) {
        // Add map markers, polylines, etc.
    }
}
