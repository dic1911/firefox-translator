/*
 * Copyright (C) 2024 David V
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package dev.davidv.translator.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.Configuration
import android.util.Log
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import dev.davidv.translator.R
import dev.davidv.translator.TranslatedText
import dev.davidv.translator.ui.theme.TranslatorTheme

class DictionaryActionModeCallback(
  private val onDictionaryLookup: (String) -> Unit,
) : ActionMode.Callback2() {
  private var currentTextView: TextView? = null

  fun setTextView(textView: TextView) {
    currentTextView = textView
  }

  override fun onCreateActionMode(
    mode: ActionMode?,
    menu: Menu?,
  ): Boolean {
    menu?.add(0, DICTIONARY_ID, 0, "Dictionary")
    return true
  }

  override fun onPrepareActionMode(
    mode: ActionMode?,
    menu: Menu?,
  ): Boolean = false

  override fun onActionItemClicked(
    mode: ActionMode?,
    item: MenuItem?,
  ): Boolean =
    when (item?.itemId) {
      DICTIONARY_ID -> {
        val textView = currentTextView
        val selectedText =
          textView
            ?.text
            ?.subSequence(
              textView.selectionStart,
              textView.selectionEnd,
            )?.toString() ?: ""

        Log.i("CustomActionMenu", "TextView: $textView")
        Log.i("CustomActionMenu", "Selection: ${textView?.selectionStart}-${textView?.selectionEnd}")
        Log.i("CustomActionMenu", "Selected text: '$selectedText'")

        if (selectedText.isNotBlank()) {
          onDictionaryLookup(selectedText)
        }

        mode?.finish()
        true
      }

      else -> false
    }

  override fun onDestroyActionMode(mode: ActionMode?) {
  }

  companion object {
    private const val DICTIONARY_ID = 12345
  }
}

@Composable
fun TranslationField(
  text: TranslatedText,
  textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
  onDictionaryLookup: (String) -> Unit = {},
) {
  val context = LocalContext.current

  if (text.translated.isEmpty()) {
    return
  }

  val actionModeCallback =
    remember(onDictionaryLookup) {
      DictionaryActionModeCallback(onDictionaryLookup)
    }

  val textColor = MaterialTheme.colorScheme.onSurface.toArgb()
  val fontSize = textStyle.fontSize.value
  val smallerFontSize = fontSize * 0.7f

  Box(
    modifier = Modifier.fillMaxSize(),
  ) {
    Column(
      modifier =
        Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
          // Leave space for copy button
          .padding(end = 22.dp),
    ) {
      AndroidView(
        factory = { context ->
          TextView(context).apply {
            this.text = text.translated
            this.textSize = fontSize
            this.setTextColor(textColor)
            this.setTextIsSelectable(true)
            this.customSelectionActionModeCallback = actionModeCallback
            this.customInsertionActionModeCallback = actionModeCallback
            actionModeCallback.setTextView(this)
          }
        },
        update = { textView ->
          textView.text = text.translated
          textView.textSize = fontSize
          textView.customSelectionActionModeCallback = actionModeCallback
          actionModeCallback.setTextView(textView)
        },
        modifier = Modifier.fillMaxSize(),
      )

      if (text.transliterated != null) {
        AndroidView(
          factory = { context ->
            TextView(context).apply {
              this.text = text.transliterated
              this.textSize = smallerFontSize
              this.setTextColor(textColor)
              this.setTextIsSelectable(true)
            }
          },
          update = { textView ->
            textView.text = text.transliterated
            textView.textSize = smallerFontSize
          },
          modifier = Modifier.padding(top = 5.dp),
        )
      }
    }

    // Copy button positioned sticky to the right
    IconButton(
      onClick = {
        val clipboard =
          context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Translation", text.translated)
        clipboard.setPrimaryClip(clip)
      },
      modifier =
        Modifier
          .align(Alignment.TopEnd)
          .size(24.dp),
    ) {
      Icon(
        painterResource(id = R.drawable.copy),
        contentDescription = "Copy translation",
        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
      )
    }
  }
}

@Preview(
  showBackground = true,
  uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun TranslationFieldBothWeightedPreview() {
  TranslatorTheme {
    Column(modifier = Modifier.fillMaxSize()) {
      TranslationField(
        text =
          TranslatedText(
            "very long text. very long text. very long text. very long text. very long text. very long text. very long text. very long text. very long text. very long text. very long text. very long text. very long text. very long text. very long text. very long text. very long text. very long text. very long text. very long text. very long text. very long text. very long text. very long text. very long text. very long text. very long text. very long text. very long text. very long text. very long text. very long text. very long text. very long text. very long text. very long text. very long text. very long text. very long text. very long text. very long text. very long text. very long text. very long text. very long text. very long text. very long text. very long text. very long text. very long text. very long text. very long text. very long text. very long text. very long text. very long text. very long text. very long text. very long text. very long text. very long text. very long text. very long text. very long text. very long text. very long text.",
            null,
          ),
      )
    }
  }
}

@Preview(
  showBackground = true,
  uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun WithTransliteration() {
  TranslatorTheme {
    Column(modifier = Modifier.fillMaxSize()) {
      TranslationField(
        text = TranslatedText("some words", "transliterated"),
      )
    }
  }
}
