package com.hermanowicz.mypantry.components.common.dropdown

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hermanowicz.mypantry.R
import com.hermanowicz.mypantry.ui.theme.LocalSpacing
import com.hermanowicz.mypantry.ui.theme.Shapes
import com.hermanowicz.mypantry.utils.MainCategoriesTypes

@Composable
fun DropdownMainCategory(
    textRight: String,
    onClick: () -> Unit,
    onChange: (String) -> Unit,
    visibleDropdown: Boolean,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val category = enumValueOf<MainCategoriesTypes>(textRight)
    Column() {
        Text(text = stringResource(id = R.string.main_category))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .border(BorderStroke(1.dp, Color.Black), Shapes.medium)
        ) {
            Column(
                modifier = Modifier.padding(LocalSpacing.current.medium)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(id = category.nameResId),
                    )
                    DropdownMenu(expanded = visibleDropdown, onDismissRequest = { onDismiss() }) {
                        enumValues<MainCategoriesTypes>().forEachIndexed { _, itemValue ->
                            DropdownMenuItem(onClick = {
                                onChange(itemValue.name)
                                onDismiss()
                            }) {
                                Text(text = context.getString(itemValue.nameResId))
                            }
                        }
                    }
                }
            }
        }
    }
}
