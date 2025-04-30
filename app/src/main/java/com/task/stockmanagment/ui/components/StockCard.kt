package com.task.stockmanagment.ui.components

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.task.stockmanagment.data.model.Stock

@Composable
fun StockCard(stock: Stock, index: Int) {
    val context = LocalContext.current
    var isPressed by remember { mutableStateOf(false) }

    val containerColor by animateColorAsState(
        if (isPressed) Color.LightGray else MaterialTheme.colorScheme.surface, label = ""
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp) // should be enough
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        isPressed = !isPressed
                    },
                    onTap = {
                        Toast.makeText(
                            context,
                            "Stock ${index + 1}: ${stock.label}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            },
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(Modifier.padding(8.dp)) {
            Text("Label: ${stock.label}")
            Row {
                Text("UID: ", fontWeight = FontWeight.Bold)
                Text(
                    text = stock.uid,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clickable {
                            Toast.makeText(context, "UID: ${stock.uid}", Toast.LENGTH_SHORT).show()
                        }
                )
            }
            Text("Value: ${stock.value}")
            Text("Category: ${stock.category}")
        }
    }
}