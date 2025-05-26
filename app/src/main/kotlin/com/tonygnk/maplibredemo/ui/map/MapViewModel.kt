package com.tonygnk.maplibredemo.ui.map

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel



class MapViewModel(): ViewModel() {
    var textFieldState by mutableStateOf(TextFieldState())

}
