/*
 * Copyright 2026 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.ai.edge.gallery.ui.common.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * A Jetpack Compose bottom sheet for collecting specific user feedback on model responses.
 *
 * @param isPositive True if the user clicked "Thumbs Up", false if "Thumbs Down".
 * @param chips Categorical selection chips (preset issues).
 * @param onDismiss Callback invoked when the bottom sheet is dismissed or canceled.
 * @param onSubmit Callback invoked when the user submits their feedback.
 */
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FeedbackDialog(
  isPositive: Boolean,
  chips: List<String>,
  onDismiss: () -> Unit,
  onSubmit: (comment: String, selectedChips: List<String>) -> Unit,
  modifier: Modifier = Modifier,
) {
  var comment by remember { mutableStateOf("") }
  val selectedChips = remember { mutableStateListOf<String>() }
  var legalChecked by remember { mutableStateOf(false) }

  // Enabled only after at least one chip is selected OR text is provided in open-comment field
  // AND the legal disclosure consent checkbox is checked.
  val isSubmitEnabled = (selectedChips.isNotEmpty() || comment.isNotBlank()) && legalChecked

  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState, modifier = modifier) {
    Column(
      modifier =
        Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(top = 8.dp, bottom = 32.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      Text(text = "Why did you choose this rating?", style = MaterialTheme.typography.titleMedium)

      // Taxonomical issue chips
      FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth(),
      ) {
        chips.forEach { chipText ->
          val isSelected = selectedChips.contains(chipText)
          FilterChip(
            selected = isSelected,
            onClick = {
              if (isSelected) {
                selectedChips.remove(chipText)
              } else {
                selectedChips.add(chipText)
              }
            },
            label = { Text(chipText) },
          )
        }
      }

      // Open-text comment field
      OutlinedTextField(
        value = comment,
        onValueChange = { comment = it },
        label = { Text("Add comments (optional)") },
        placeholder = { Text("Tell us more about your experience...") },
        modifier = Modifier.fillMaxWidth(),
        maxLines = 4,
      )

      // Legal disclosure checkbox
      Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth()) {
        Checkbox(checked = legalChecked, onCheckedChange = { legalChecked = it })
        Text(
          text = "[legal disclosure text]",
          style = MaterialTheme.typography.bodySmall,
          modifier = Modifier.padding(start = 8.dp, top = 4.dp),
        )
      }

      // Action buttons
      Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
        TextButton(onClick = onDismiss) { Text("Cancel") }
        Button(
          onClick = { onSubmit(comment, selectedChips.toList()) },
          enabled = isSubmitEnabled,
          modifier = Modifier.padding(start = 8.dp),
        ) {
          Text("Submit")
        }
      }
    }
  }
}
