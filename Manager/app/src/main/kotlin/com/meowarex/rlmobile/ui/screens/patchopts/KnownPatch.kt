package com.meowarex.rlmobile.ui.screens.patchopts

import androidx.annotation.StringRes
import com.meowarex.rlmobile.R
import com.meowarex.rlmobile.ui.screens.patchopts.PatchDefault.Disabled
import com.meowarex.rlmobile.ui.screens.patchopts.PatchDefault.Enabled

data class PatchVariant(
    @StringRes val titleRes: Int,
    val fileNames: List<String>,
)


enum class KnownPatch(
    val order: Int, // Patch order in the UI List (lower = higher up) [Main Patches: multiples of 10 | Sub Patches: multiples of 1]
    val fileNames: List<String>,
    @StringRes val titleRes: Int,
    @StringRes val descRes: Int,
    val default: PatchDefault, // Default state of the patch in the UI List (enabled/disabled)
    val requires: List<KnownPatch> = emptyList(),
    val disables: List<KnownPatch> = emptyList(),
    val variants: List<PatchVariant> = emptyList(),
    val defaultVariantIndex: Int = 0,
) {
    LyricsDisableCover(
        order = 41,
        fileNames = listOf("lyrics-disable-cover.patch"),
        titleRes = R.string.patch_lyrics_disable_cover_title,
        descRes = R.string.patch_lyrics_disable_cover_desc,
        default = Enabled,
    ),
    LyricsReplaceLyricsButton(
        order = 42,
        fileNames = listOf(
            "lyrics-replace-lyrics-button.patch",
            "lyrics-sparkle-conditional-visibility.patch",
        ),
        titleRes = R.string.patch_lyrics_replace_button_title,
        descRes = R.string.patch_lyrics_replace_button_desc,
        default = Enabled,
    ),
    LyricsReplaceShareButton(
        order = 43,
        fileNames = listOf("lyrics-replace-share-button.patch"),
        titleRes = R.string.patch_lyrics_replace_share_button_title,
        descRes = R.string.patch_lyrics_replace_share_button_desc,
        default = Enabled,
    ),
    LyricsRlApi(
        order = 20,
        fileNames = listOf(
            "lyrics-rl-api.patch",
            "lyrics-rl-api-observer.patch",
        ),
        titleRes = R.string.patch_lyrics_rl_api_title,
        descRes = R.string.patch_lyrics_rl_api_desc,
        default = Disabled,
    ),
    LyricsKeepControlsVisible(
        order = 60,
        fileNames = listOf("lyrics-keep-controls-visible.patch"),
        titleRes = R.string.patch_lyrics_keep_controls_title,
        descRes = R.string.patch_lyrics_keep_controls_desc,
        default = Enabled,
    ),
    PlayerBackdrop(
        order = 30,
        fileNames = listOf("player-backdrop.patch"),
        titleRes = R.string.patch_player_backdrop_title,
        descRes = R.string.patch_player_backdrop_desc,
        default = Enabled,
    ),
    QualityBadgeColors(
        order = 36,
        fileNames = listOf("player-quality-badge-colors.patch"),
        titleRes = R.string.patch_quality_badge_colors_title,
        descRes = R.string.patch_quality_badge_colors_desc,
        default = Enabled,
    ),
    PlayerOneHanded(
        order = 37,
        fileNames = listOf("player-one-handed.patch"),
        titleRes = R.string.patch_player_one_handed_title,
        descRes = R.string.patch_player_one_handed_desc,
        default = Disabled,
    ),
    CoverEverywhere(
        order = 35,
        fileNames = listOf(
            "home-backdrop.patch",
            "collection-backdrop.patch",
            "cover-capture.patch",
        ),
        titleRes = R.string.patch_cover_everywhere_title,
        descRes = R.string.patch_cover_everywhere_desc,
        default = Disabled,
    ),
    DebugMenuUnlock(
        order = 100,
        fileNames = listOf("debug-menu-unlock.patch"),
        titleRes = R.string.patch_debug_menu_unlock_title,
        descRes = R.string.patch_debug_menu_unlock_desc,
        default = Disabled,
    ),
    LyricsProgressPill(
        order = 40,
        fileNames = listOf(
            "lyrics-progress-pill.patch",
            "lyrics-fade-region.patch",
        ),
        titleRes = R.string.patch_lyrics_progress_pill_title,
        descRes = R.string.patch_lyrics_progress_pill_desc,
        default = Enabled,
        requires = listOf(LyricsDisableCover, LyricsReplaceLyricsButton, LyricsReplaceShareButton),
    ),
    MiniPlayerRedesign(
        order = 50,
        fileNames = emptyList(),
        titleRes = R.string.patch_mini_player_redesign_title,
        descRes = R.string.patch_mini_player_redesign_desc,
        default = Disabled,
        defaultVariantIndex = 2,
        variants = listOf(
            PatchVariant(
                titleRes = R.string.patch_mini_player_variant_floating_title,
                fileNames = listOf("mini-player-floating.patch"),
            ),
            PatchVariant(
                titleRes = R.string.patch_mini_player_variant_square_grey_title,
                fileNames = listOf("mini-player-grey.patch"),
            ),
            PatchVariant(
                titleRes = R.string.patch_mini_player_variant_square_black_title,
                fileNames = listOf("mini-player-black.patch"),
            ),
        ),
    ),
    EnableLegacyUi(
        order = 10,
        fileNames = listOf("enable-legacy-ui.patch"),
        titleRes = R.string.patch_enable_legacy_ui_title,
        descRes = R.string.patch_enable_legacy_ui_desc,
        default = Disabled,
        requires = listOf(DebugMenuUnlock),
        disables = listOf(
            LyricsDisableCover,
            LyricsReplaceLyricsButton,
            LyricsReplaceShareButton,
            LyricsRlApi,
            LyricsKeepControlsVisible,
            PlayerBackdrop,
            QualityBadgeColors,
            LyricsProgressPill,
            CoverEverywhere,
        ),
    );

    companion object {
        val All: List<KnownPatch> = entries.sortedWith(
            compareBy({ it.order }, { it.fileNames.firstOrNull() ?: it.name })
        )
    }
}
