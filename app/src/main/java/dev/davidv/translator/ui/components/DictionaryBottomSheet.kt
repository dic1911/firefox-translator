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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.davidv.translator.AggregatedWord
import dev.davidv.translator.Gloss
import dev.davidv.translator.PosGlosses

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DictionaryBottomSheet(
  dictionaryWord: AggregatedWord,
  onDismiss: () -> Unit,
) {
  val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = bottomSheetState,
    modifier = Modifier.heightIn(max = (LocalConfiguration.current.screenHeightDp * 0.6f).dp),
  ) {
    DictionaryEntry(dictionaryWord)
  }
}

@Composable
fun DictionaryEntry(dictionaryWord: AggregatedWord) {
  Column(
    modifier =
      Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .verticalScroll(rememberScrollState()),
  ) {
    Text(
      text = dictionaryWord.word,
      style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
      modifier = Modifier.padding(bottom = 8.dp),
    )

    val ipa = dictionaryWord.ipaSound?.firstOrNull()
    val hyphenation = dictionaryWord.hyphenation?.joinToString("-")

    Row {
      if (!ipa.isNullOrEmpty()) {
        Text(
          text = ipa,
          style = MaterialTheme.typography.bodyMedium,
          modifier = Modifier.padding(bottom = 4.dp, end = if (hyphenation.isNullOrEmpty()) 0.dp else 16.dp),
        )
      }

      if (!hyphenation.isNullOrEmpty()) {
        Text(
          text = hyphenation,
          style = MaterialTheme.typography.bodyMedium,
          modifier = Modifier.padding(bottom = 16.dp),
        )
      }
    }

    var lastCategoryPath = emptyList<String>()

    dictionaryWord.posGlosses.forEach { posGlosses ->
      Text(
        text = "${posGlosses.pos}:",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
        modifier = Modifier.padding(bottom = 4.dp, top = if (posGlosses != dictionaryWord.posGlosses.first()) 8.dp else 0.dp),
      )

      posGlosses.glosses.forEach { gloss ->
        val currentCategoryPath = gloss.getCategoryPath(lastCategoryPath)

        val commonLen =
          lastCategoryPath
            .zip(currentCategoryPath)
            .takeWhile { (a, b) -> a == b }
            .size

        currentCategoryPath.drop(commonLen).forEachIndexed { index, category ->
          val indent = (commonLen + index + 1) * 2
          Text(
            text = category,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = (indent * 8).dp, bottom = 4.dp),
          )
        }

        val glossIndent = (currentCategoryPath.size + 1) * 2
        Text(
          text = "- ${gloss.gloss}",
          style = MaterialTheme.typography.bodyMedium,
          modifier = Modifier.padding(start = (glossIndent * 8).dp, bottom = 4.dp),
        )

        lastCategoryPath = currentCategoryPath
      }
    }

    Spacer(modifier = Modifier.height(16.dp))
  }
}

@Preview(showBackground = true)
@Composable
fun DictionaryBottomSheetPreview() {
  val sampleWord =
    AggregatedWord(
      word = "dictionary",
      posGlosses =
        listOf(
          PosGlosses(
            pos = "noun",
            glosses =
              listOf(
                Gloss(
                  sharedPrefixCount = 0,
                  gloss = "A reference book containing an alphabetical list of words with information about their meanings, pronunciations, etymologies, etc.",
                  newCategories = arrayOf(),
                ),
                Gloss(
                  sharedPrefixCount = 1,
                  gloss = "A book of words in one language with their equivalents in another language",
                  newCategories = arrayOf(),
                ),
                Gloss(
                  sharedPrefixCount = 0,
                  gloss = "A data structure that maps keys to values",
                  newCategories = arrayOf("computing"),
                ),
              ),
          ),
          PosGlosses(
            pos = "verb",
            glosses =
              listOf(
                Gloss(
                  sharedPrefixCount = 0,
                  gloss = "To compile a dictionary",
                  newCategories = null,
                ),
              ),
          ),
        ),
      hyphenation = arrayOf("dic", "tion", "ar", "y"),
      formOf = null,
      ipaSound = arrayOf("/ˈdɪkʃəˌnɛri/", "/ˈdɪkʃənˌɛri/"),
    )

  MaterialTheme {
    Surface {
      DictionaryEntry(
        dictionaryWord = sampleWord,
      )
    }
  }
}
