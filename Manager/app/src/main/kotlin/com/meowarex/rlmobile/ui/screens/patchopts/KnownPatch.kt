package com.meowarex.rlmobile.ui.screens.patchopts

import androidx.annotation.StringRes
import com.meowarex.rlmobile.R

enum class KnownPatch(
    val fileNames: List<String>,
    @StringRes val titleRes: Int,
    @StringRes val descRes: Int,
    val requires: List<KnownPatch> = emptyList(),
) {
    LyricsDisableCover(
        fileNames = listOf("lyrics-disable-cover.patch"),
        titleRes = R.string.patch_lyrics_disable_cover_title,
        descRes = R.string.patch_lyrics_disable_cover_desc,
    ),
    LyricsProgressPill(
        fileNames = listOf(
            "lyrics-progress-pill.patch",
            "lyrics-fade-region.patch",
            "lyrics-active-line-only.patch",
        ),
        titleRes = R.string.patch_lyrics_progress_pill_title,
        descRes = R.string.patch_lyrics_progress_pill_desc,
        requires = listOf(LyricsDisableCover),
    ),
    LyricsReplaceLyricButton(
        fileNames = listOf(
            "lyrics-replace-lyric-button-1-remove.patch",
            "lyrics-replace-lyric-button-2-sparkle.patch",
        ),
        titleRes = R.string.patch_lyrics_replace_button_title,
        descRes = R.string.patch_lyrics_replace_button_desc,
    ),
    PlayerBackdrop(
        fileNames = listOf("player-backdrop.patch"),
        titleRes = R.string.patch_player_backdrop_title,
        descRes = R.string.patch_player_backdrop_desc,
    );

    companion object {
        val All: List<KnownPatch> = entries.sortedBy { it.fileNames.first() }
    }
}
