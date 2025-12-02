package com.gorrotowi.webpng.ui_components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@Composable
fun RadioGroup(options: List<String>, selectedIndex: Int, onSelected: (Int, String) -> Unit) {
//    val (selectedOption, onSelectedOption) = remember { mutableStateOf(options[0]) }
    Row(
        modifier = Modifier
            .selectableGroup()
    ) {
        options.forEachIndexed { index: Int, text: String ->
            Row(
                modifier = Modifier
                    .height(56.dp)
                    .selectable(
                        selected = (text == options[selectedIndex]),
                        onClick = {
//                            onSelectedOption(text)
                            onSelected(index, text)
                        },
                        role = Role.RadioButton
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    modifier = Modifier.padding(start = 16.dp),
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color.White,
                        unselectedColor = Color.White
                    ),
                    selected = (text == options[selectedIndex]),
                    onClick = null
                )
                Text(
                    text = text,
                    color = Color.White,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun RadioGroupPreview() {
    val radioOptions = listOf("Webp to PNG", "Any Image to PNG", "Any Image to Webp")
    var selectedIndex by remember { mutableIntStateOf(0) }
    RadioGroup(radioOptions, selectedIndex) { index, text ->
        selectedIndex = index
        println("Selected $index $text")
    }

}