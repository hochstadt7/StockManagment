package com.task.stockmanagement.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.task.stockmanagement.ui.components.StockCard
import com.task.stockmanagement.viewmodel.StockViewModel
import kotlinx.coroutines.delay
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockScreen(viewModel: StockViewModel = hiltViewModel()) {
    val stocks = viewModel.stockList
    val isLoading = viewModel.isLoading
    val error = viewModel.errorMessage
    val options = viewModel.options
    var expanded by remember { mutableStateOf(false) }

    val clickEnabled = remember { mutableStateOf(true) }
    val cooldownId = remember { mutableIntStateOf(0) }

    LaunchedEffect(cooldownId.intValue) {
        delay(1500)
        clickEnabled.value = true
    }

    Column(Modifier.fillMaxSize().padding(WindowInsets.systemBars.asPaddingValues())) {
        ExposedDropdownMenuBox(
            expanded = expanded && options.isNotEmpty(),
            onExpandedChange = { expanded = it }
        ) {
            StaticLabelTextField(
                value = viewModel.searchQuery,
                onValueChange = {
                    viewModel.onSearchQueryChanged(it)
                    expanded = true
                },
                label = "Stock",
                placeholderText = "Microsoft",
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(type = MenuAnchorType.PrimaryEditable, enabled = true)
            )

            ExposedDropdownMenu(
                expanded = expanded && options.isNotEmpty(),
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { stock ->
                    DropdownMenuItem(
                        text = { Text(stock.label) },
                        onClick = {
                            expanded = false
                            viewModel.loadOptions(stock)
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))
        } else if (error != null) {
            Timber.d("Error: $error")
            Text("Error: $error", color = Color.Red)
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(150.dp),
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(stocks) { index, stock ->
                    StockCard(stock, index, clickEnabled, cooldownId)
                }
            }
        }
    }
}

@Composable
fun StaticLabelTextField(
    modifier: Modifier = Modifier,
    value:String,
    onValueChange : (String)->Unit,
    placeholderText: String = "",
    singleLine:Boolean = false,
    label: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null
){

    val textColor = if (value.isEmpty())
        MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f)
    else MaterialTheme.colorScheme.primary

    val placeHolder = remember {
        mutableStateOf(placeholderText)
    }

    OutlinedTextField(
        modifier = modifier
            .onFocusChanged {
                placeHolder.value =
                    if (it.isFocused) ""
                    else placeholderText
            }
            .fillMaxWidth()
            .heightIn(max = 200.dp),
        value = value,
        onValueChange = {
            onValueChange(it)
        },
        label = {
            Text(
                text = label
            )
        },
        //added for label always be visible
        visualTransformation = if (value.isEmpty())
            PlaceholderTransformation(placeholder = placeHolder.value)
        else VisualTransformation.None,
        textStyle = TextStyle(color = textColor, fontSize = 18.sp),
        singleLine = singleLine,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        keyboardOptions = keyboardOptions
    )
}

private class PlaceholderTransformation(private val placeholder: String) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        return placeholderFilter(placeholder)
    }
}

fun placeholderFilter(placeholder: String): TransformedText {

    val numberOffsetTranslator = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            return 0
        }

        override fun transformedToOriginal(offset: Int): Int {
            return 0
        }
    }

    return TransformedText(AnnotatedString(placeholder), numberOffsetTranslator)
}