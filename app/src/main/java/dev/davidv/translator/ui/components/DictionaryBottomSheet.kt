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

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.davidv.translator.AggregatedWord
import dev.davidv.translator.Gloss
import dev.davidv.translator.PosGlosses
import dev.davidv.translator.R

@Composable
fun DictionaryBottomSheet(
  dictionaryWord: AggregatedWord,
  dictionaryStack: List<AggregatedWord>,
  onDismiss: () -> Unit,
  onDictionaryLookup: (String) -> Unit = {},
  onBackPressed: () -> Unit = {},
) {
  var isVisible by remember { mutableStateOf(false) }
  var isDismissing by remember { mutableStateOf(false) }

  LaunchedEffect(Unit) {
    isVisible = true
  }

  val handleDismiss = {
    isDismissing = true
    isVisible = false
  }

  val handleBackPressed = {
    onBackPressed()
  }

  Box(
    modifier = Modifier.fillMaxSize(),
  ) {
    // Dimmed background
    Box(
      modifier =
        Modifier
          .fillMaxSize()
          .background(Color.Black.copy(alpha = 0.5f))
          .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
          ) { handleDismiss() },
    )

    // Bottom sheet content
    AnimatedVisibility(
      visible = isVisible,
      enter =
        slideInVertically(
          animationSpec = tween(300),
          initialOffsetY = { it },
        ),
      exit =
        slideOutVertically(
          animationSpec = tween(300),
          targetOffsetY = { it },
        ),
      modifier =
        Modifier
          .align(Alignment.BottomCenter)
          .navigationBarsPadding(),
    ) {
      Surface(
        modifier =
          Modifier
            .fillMaxWidth()
            .heightIn(max = (LocalConfiguration.current.screenHeightDp * 0.6f).dp)
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
      ) {
        Box(
          modifier = Modifier.fillMaxWidth(),
        ) {
          AnimatedContent(
            targetState = dictionaryWord,
            transitionSpec = {
              fadeIn(
                animationSpec = tween(150),
              ) togetherWith
                fadeOut(
                  animationSpec = tween(150),
                )
            },
            label = "dictionary_content",
            modifier = Modifier.fillMaxWidth(),
          ) { currentWord ->
            DictionaryEntry(
              dictionaryWord = currentWord,
              showBackButton = dictionaryStack.size > 1,
              onDictionaryLookup = onDictionaryLookup,
              onBackPressed = handleBackPressed,
            )
          }
        }
      }
    }
  }

  LaunchedEffect(isDismissing) {
    if (isDismissing) {
      kotlinx.coroutines.delay(300) // Wait for exit animation
      onDismiss()
    }
  }
}

@Composable
fun DictionaryEntry(
  dictionaryWord: AggregatedWord,
  showBackButton: Boolean = false,
  onDictionaryLookup: (String) -> Unit = {},
  onBackPressed: () -> Unit = {},
) {
  Column(
    modifier =
      Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .verticalScroll(rememberScrollState()),
  ) {
    Box(
      modifier =
        Modifier
          .fillMaxWidth()
          .padding(bottom = 8.dp),
    ) {
      if (showBackButton) {
        IconButton(
          onClick = onBackPressed,
          modifier =
            Modifier
              .align(Alignment.CenterStart)
              .size(24.dp),
        ) {
          Icon(
            painterResource(id = R.drawable.arrow_back),
            contentDescription = "Back",
            tint = MaterialTheme.colorScheme.onSurface,
          )
        }
      }

      Text(
        dictionaryWord.word,
        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
        modifier =
          Modifier
            .align(Alignment.CenterStart)
            .padding(horizontal = if (showBackButton) 32.dp else 0.dp),
      )
    }

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
        )
      }
    }

    var lastCategoryPath = emptyList<String>()

    dictionaryWord.posGlosses.forEach { posGlosses ->
      Row {
        Text(
          text = "${posGlosses.pos}",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
          modifier = Modifier.padding(bottom = 0.dp, top = 0.dp),
        )
        Text(
          text = "category",
          style = MaterialTheme.typography.labelSmall,
          modifier = Modifier.align(Alignment.CenterVertically).padding(start = 8.dp),
        )
      }

      posGlosses.glosses.forEach { gloss ->
        val currentCategoryPath = gloss.getCategoryPath(lastCategoryPath)

        val commonLen =
          lastCategoryPath
            .zip(currentCategoryPath)
            .takeWhile { (a, b) -> a == b }
            .size

        val glossIndent = (currentCategoryPath.size + 1) * 2
        currentCategoryPath.drop(commonLen).forEachIndexed { index, category ->
          InteractiveText(
            text = "› $category",
            style = MaterialTheme.typography.bodyMedium,
            onDictionaryLookup = onDictionaryLookup,
            modifier = Modifier.padding(start = (glossIndent * 2).dp, bottom = 2.dp),
          )
        }

        InteractiveText(
          text = "• ${gloss.gloss.removeSuffix(".")}",
          style = MaterialTheme.typography.bodyMedium,
          onDictionaryLookup = onDictionaryLookup,
          modifier = Modifier.padding(start = (glossIndent * 4).dp, bottom = 2.dp),
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
                  newCategories = emptyList(),
                ),
                Gloss(
                  sharedPrefixCount = 1,
                  gloss = "A book of words in one language with their equivalents in another language",
                  newCategories = emptyList(),
                ),
                Gloss(
                  sharedPrefixCount = 0,
                  gloss = "A data structure that maps keys to values",
                  newCategories = listOf("computing"),
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
      hyphenation = listOf("dic", "tion", "ar", "y"),
      formOf = null,
      ipaSound = listOf("/ˈdɪkʃəˌnɛri/", "/ˈdɪkʃənˌɛri/"),
    )

  MaterialTheme {
    Surface {
      DictionaryEntry(
        dictionaryWord = sampleWord,
      )
    }
  }
}
