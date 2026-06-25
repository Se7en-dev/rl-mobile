package com.meowarex.rlmobile.ui.screens.patchopts

import androidx.annotation.StringRes

sealed interface PatchOption {
    /** Stable identifier, unique within a single patch. */
    val key: String

    @get:StringRes
    val titleRes: Int

    @get:StringRes
    val descRes: Int

    /** A simple on/off switch. */
    data class Toggle(
        override val key: String,
        @StringRes override val titleRes: Int,
        @StringRes override val descRes: Int,
        val default: Boolean,
        /** Patch files applied only while this toggle is on (and its patch is enabled). */
        val fileNames: List<String> = emptyList(),
        /** Helper smali extracted only while this toggle is on. */
        val extensionFiles: List<String> = emptyList(),
        /** Render inline beneath the variant selector instead of inside the advanced sheet. */
        val inline: Boolean = false,
        /** Greyed out (with the lock dialog) unless this variant index is the selected one. */
        val requiresVariant: Int? = null,
        /** Greyed out unless the sibling option with this key is currently on. */
        val requiresOption: String? = null,
        /** Variant indices hidden from the picker while this toggle is on. */
        val hidesVariants: List<Int> = emptyList(),
        /** Variant title overrides (index -> @StringRes) applied while this toggle is on. */
        val relabelVariants: Map<Int, Int> = emptyMap(),
        /** Placeholder name (without the surrounding `__`) baked into the `.patch` files. */
        val token: String? = null,
    ) : PatchOption

    /** A continuous (or stepped) numeric value within [valueRange]. */
    data class Slider(
        override val key: String,
        @StringRes override val titleRes: Int,
        @StringRes override val descRes: Int,
        val default: Float,
        val valueRange: ClosedFloatingPointRange<Float>,
        /** Number of discrete steps between the range ends (0 = continuous). */
        val steps: Int = 0,
        /** Render the value as a rounded percentage (e.g. "50%"). */
        val displayAsPercent: Boolean = false,
        /** Optional unit suffix shown after the value (e.g. "dp"). */
        @StringRes val unitRes: Int? = null,
        /** Placeholder name (without the surrounding `__`) baked into the `.patch` files. */
        val token: String? = null,
        /** How the chosen value becomes the smali literal that replaces the [token]. */
        val encode: SmaliEncode? = null,
    ) : PatchOption

    /** A single choice out of a small list, rendered as a segmented control. */
    data class Choice(
        override val key: String,
        @StringRes override val titleRes: Int,
        @StringRes override val descRes: Int,
        val entries: List<ChoiceEntry>,
        val defaultIndex: Int = 0,
    ) : PatchOption
}

data class ChoiceEntry(
    @StringRes val labelRes: Int,
)
