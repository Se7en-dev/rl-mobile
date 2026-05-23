package com.meowarex.rlmobile.ui.screens.patchopts

import androidx.annotation.StringRes
import com.meowarex.rlmobile.R

enum class KnownPatch(
    val fileNames: List<String>,
    @StringRes val titleRes: Int,
    @StringRes val descRes: Int,
    val requires: List<KnownPatch> = emptyList(),
    val disables: List<KnownPatch> = emptyList(),
) {
    // Dependency-first order (later refs need backward resolution)
    LyricsDisableCover(
        fileNames = listOf("lyrics-disable-cover.patch"),
        titleRes = R.string.patch_lyrics_disable_cover_title,
        descRes = R.string.patch_lyrics_disable_cover_desc,
    ),
    LyricsReplaceLyricsButton(
        fileNames = listOf(
            "lyrics-replace-lyrics-button.patch",
            "lyrics-sparkle-conditional-visibility.patch",
        ),
        titleRes = R.string.patch_lyrics_replace_button_title,
        descRes = R.string.patch_lyrics_replace_button_desc,
    ),
    LyricsReplaceShareButton(
        fileNames = listOf("lyrics-replace-share-button.patch"),
        titleRes = R.string.patch_lyrics_replace_share_button_title,
        descRes = R.string.patch_lyrics_replace_share_button_desc,
    ),
    PlayerBackdrop(
        fileNames = listOf("player-backdrop.patch"),
        titleRes = R.string.patch_player_backdrop_title,
        descRes = R.string.patch_player_backdrop_desc,
    ),
    DebugMenuUnlock(
        fileNames = listOf("debug-menu-unlock.patch"),
        titleRes = R.string.patch_debug_menu_unlock_title,
        descRes = R.string.patch_debug_menu_unlock_desc,
    ),
    LyricsProgressPill(
        fileNames = listOf(
            "lyrics-progress-pill.patch",
            "lyrics-fade-region.patch",
        ),
        titleRes = R.string.patch_lyrics_progress_pill_title,
        descRes = R.string.patch_lyrics_progress_pill_desc,
        requires = listOf(LyricsDisableCover, LyricsReplaceShareButton),
    ),
    EnableLegacyUi(
        fileNames = listOf("enable-legacy-ui.patch"),
        titleRes = R.string.patch_enable_legacy_ui_title,
        descRes = R.string.patch_enable_legacy_ui_desc,
        requires = listOf(DebugMenuUnlock),
        disables = listOf(
            LyricsDisableCover,
            LyricsReplaceLyricsButton,
            LyricsReplaceShareButton,
            PlayerBackdrop,
            LyricsProgressPill,
        ),
    );

    companion object {
        // Alphabetical by first filename, but pin DebugMenuUnlock to the bottom
        val All: List<KnownPatch> = entries.sortedWith(
            compareBy({ it == DebugMenuUnlock }, { it.fileNames.first() })
        )
    }
}
