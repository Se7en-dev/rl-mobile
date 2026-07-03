package com.meowarex.rlmobile.ui.screens.patchopts

import androidx.annotation.StringRes
import com.meowarex.rlmobile.R
import com.meowarex.rlmobile.ui.screens.patchopts.PatchDefault.Disabled
import com.meowarex.rlmobile.ui.screens.patchopts.PatchDefault.Enabled

data class PatchVariant(
    @StringRes val titleRes: Int,
    val fileNames: List<String>,
    val extensionFiles: List<String> = emptyList(),
)


enum class KnownPatch(
    val order: Int, // Patch order in the UI List (lower = higher up) [Main Patches: multiples of 10 | Sub Patches: multiples of 1]
    val fileNames: List<String>,
    val extensionFiles: List<String> = emptyList(),
    @StringRes val titleRes: Int,
    @StringRes val descRes: Int,
    val default: PatchDefault, // Default state of the patch in the UI List (enabled/disabled)
    val requires: List<KnownPatch> = emptyList(),
    val disables: List<KnownPatch> = emptyList(),
    val variants: List<PatchVariant> = emptyList(),
    val defaultVariantIndex: Int = 0,
    val advancedOptions: List<PatchOption> = emptyList(),
    val category: String = PatchSpec.CATEGORY_PATCH,
    val pathLocked: Boolean = false,
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
        advancedOptions = listOf(
            PatchOption.Slider(
                key = "blur_strength",
                titleRes = R.string.patch_opt_backdrop_blur_title,
                descRes = R.string.patch_opt_backdrop_blur_desc,
                default = 50f,
                valueRange = 0f..100f,
                steps = 19, // dots every 5% (0,5,…,100); snap only when locked
                displayAsPercent = true,
                token = "RL_BLUR_BITS",
                encode = SmaliEncode(EncodeKind.FloatBits, scale = 1.8f),
            ),
            PatchOption.Slider(
                key = "dimming",
                titleRes = R.string.patch_opt_backdrop_dimming_title,
                descRes = R.string.patch_opt_backdrop_dimming_desc,
                default = 50f, // 50% == the original -0x80000000
                valueRange = 0f..100f,
                steps = 19, // dots every 5%
                displayAsPercent = true,
                token = "RL_SCRIM_ARGB",
                encode = SmaliEncode(EncodeKind.ArgbAlpha),
            ),
            PatchOption.Toggle(
                key = "cover_everywhere",
                titleRes = R.string.patch_cover_everywhere_title,
                descRes = R.string.patch_cover_everywhere_desc,
                default = false,
                inline = true,
                fileNames = listOf(
                    "home-backdrop.patch",
                    "collection-backdrop.patch",
                    "cover-capture.patch",
                ),
            ),
        ),
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
    DebugMenuUnlock(
        order = 100,
        fileNames = listOf("debug-menu-unlock.patch"),
        titleRes = R.string.patch_debug_menu_unlock_title,
        descRes = R.string.patch_debug_menu_unlock_desc,
        default = Disabled,
    ),
    WazeIntegration(
        order = 90,
        fileNames = listOf("waze-media-browser.patch"),
        extensionFiles = listOf(
            "radiant/WazeInitReceiver.smali",
            "radiant/WazeServiceConnection.smali",
        ),
        titleRes = R.string.patch_waze_integration_title,
        descRes = R.string.patch_waze_integration_desc,
        default = Disabled,
        category = PatchSpec.CATEGORY_INTEGRATION,
        pathLocked = true,
        advancedOptions = listOf(
            PatchOption.Choice(
                key = "browse_root",
                titleRes = R.string.patch_waze_browse_root_title,
                entries = listOf(
                    ChoiceEntry(R.string.patch_waze_root_auto, value = "ROOT_AUTO"),
                    ChoiceEntry(R.string.patch_waze_root_home, value = "HOME_V2::home_page_v2_id"),
                    ChoiceEntry(
                        R.string.patch_waze_root_recents,
                        value = "RECENTLY_PLAYED::home/pages/CONTINUE_LISTEN_TO/view-all",
                    ),
                ),
                defaultIndex = 0,
                token = "RL_WAZE_ROOT_ID",
            ),
            PatchOption.Color(
                key = "accent_color",
                titleRes = R.string.patch_waze_accent_color_title,
                default = -16747037, // Waze default #ff0075e3
                token = "RL_WAZE_THEME_COLOR",
            ),
        ),
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
            PatchVariant( // 0: Floating — stock rounded pill
                titleRes = R.string.patch_mini_player_variant_floating_title,
                fileNames = emptyList(),
            ),
            PatchVariant( // 1: Grey — square
                titleRes = R.string.patch_mini_player_variant_square_grey_title,
                fileNames = listOf("mini-player-grey.patch"),
            ),
            PatchVariant( // 2: Black — square black background
                titleRes = R.string.patch_mini_player_variant_square_black_title,
                fileNames = listOf("mini-player-black.patch"),
            ),
        ),
        advancedOptions = listOf(
            PatchOption.Toggle(
                key = "dynamic_bg",
                titleRes = R.string.patch_mini_player_dynamic_bg_title,
                descRes = R.string.patch_mini_player_dynamic_bg_desc,
                default = false,
                inline = true,
                fileNames = listOf("mini-player-dynamic-bg.patch"),
                extensionFiles = listOf("radiant/MiniPlayerBackground.smali"),
                hidesVariants = listOf(2),
                relabelVariants = mapOf(1 to R.string.patch_mini_player_variant_legacy_title),
            ),
            // Animated progress border around the floating pill
            PatchOption.Toggle(
                key = "border",
                titleRes = R.string.patch_mini_player_border_title,
                descRes = R.string.patch_mini_player_border_desc,
                default = false,
                requiresVariant = 0,
                fileNames = listOf("mini-player-floating-border.patch"),
                extensionFiles = listOf("radiant/MiniSeekerFloating.smali"),
            ),
            // Swipe up to open the full player
            PatchOption.Toggle(
                key = "gestures",
                titleRes = R.string.patch_mini_player_gestures_title,
                descRes = R.string.patch_mini_player_gestures_desc,
                default = true,
                fileNames = listOf("mini-player-gestures.patch"),
                extensionFiles = listOf(
                    "radiant/MiniPlayerGestures.smali",
                    "radiant/MiniPlayerGestures\$Gesture.smali",
                    "radiant/MiniPlayerGestures\$FeedbackLayer.smali",
                    "radiant/MiniPlayerGestures\$FeedbackResetAnimator.smali",
                    "radiant/MiniPlayerGestures\$RootGesture.smali",
                    "radiant/MiniPlayerGestures\$ApplyPending.smali",
                ),
            ),
            PatchOption.Choice(
                key = "swipe_up_drag",
                titleRes = R.string.patch_mini_player_drag_title,
                descRes = R.string.patch_mini_player_drag_desc,
                entries = listOf(
                    ChoiceEntry(R.string.patch_mini_player_drag_static, value = "0x0"),
                    ChoiceEntry(R.string.patch_mini_player_drag_drag, value = "0x1"),
                ),
                defaultIndex = 1,
                requiresOption = "gestures",
                token = "RL_MINI_PLAYER_SWIPE_UP_DRAG",
            ),
            // Swipe left/right to skip
            PatchOption.Toggle(
                key = "next_prev",
                titleRes = R.string.patch_mini_player_left_right_gestures_title,
                descRes = R.string.patch_mini_player_left_right_gestures_desc,
                default = false,
                requiresOption = "gestures",
                fileNames = listOf("mini-player-gestures-left-right.patch"),
                extensionFiles = listOf(
                    "radiant/MiniPlayerTrackGestures.smali",
                    "radiant/MiniPlayerTrackGestures\$Gesture.smali",
                    "radiant/MiniPlayerTrackGestures\$OffsetLayer.smali",
                    "radiant/MiniPlayerTrackGestures\$ResetAnimator.smali",
                    "radiant/MiniPlayerTrackGestures\$TextDraw.smali",
                    "com/tidal/android/feature/appscaffold/ui/q\$c.smali",
                ),
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
            MiniPlayerRedesign,
        ),
    );

    companion object {
        val All: List<KnownPatch> = entries.sortedWith(
            compareBy({ it.order }, { it.fileNames.firstOrNull() ?: it.name })
        )
    }
}
