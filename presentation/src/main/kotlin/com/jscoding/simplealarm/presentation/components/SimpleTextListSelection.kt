package com.jscoding.simplealarm.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun SimpleTextListSelection(
    modifier: Modifier = Modifier,
    selectedIndex: Int,
    snoozeLengthsValues: List<String>,
    onIndexSelected: (Int) -> Unit,
) {
    LazyColumn(
        modifier = modifier
    ) {
        itemsIndexed(snoozeLengthsValues) { index, item ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .selectable(
                        selected = (index == selectedIndex), onClick = {
                            onIndexSelected(index)
                        }, role = Role.RadioButton
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                SelectedCheckbox(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    isSelected = (index == selectedIndex)
                )
            }
        }
    }
}
