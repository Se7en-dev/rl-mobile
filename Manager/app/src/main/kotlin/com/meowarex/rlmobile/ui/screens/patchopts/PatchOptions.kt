package com.meowarex.rlmobile.ui.screens.patchopts

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.meowarex.rlmobile.ui.screens.componentopts.PatchComponent
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Immutable
@Parcelize
@Serializable
data class PatchOptions(
    /**
     * The app name that's user-facing in launchers.
     */
    val appName: String,

    /**
     * Changes the installation package name.
     */
    val packageName: String,

    /**
     * Adding the debuggable APK flag.
     */
    val debuggable: Boolean,

    val customTidalApk: PatchComponent? = null,

    /**
     * A custom smali patches bundle that was used rather than the latest.
     */
    val customPatches: PatchComponent? = null,

    val patchStates: Map<String, Boolean> = emptyMap(),

    val selectedVariants: Map<String, Int> = emptyMap(),
) : Parcelable {

    fun isEnabled(patch: KnownPatch): Boolean =
        patchStates[patch.name] ?: patch.default.isEnabled

    fun isEnabled(subOption: PatchSubOption): Boolean =
        patchStates[subOption.key] ?: subOption.default.isEnabled

    fun disabledPatchFiles(): Set<String> = buildSet<String> {
        for (patch in KnownPatch.All) {
            val enabled = isEnabled(patch)
            if (patch.variants.isEmpty()) {
                if (!enabled) addAll(patch.fileNames)
            } else {
                val selected = (selectedVariants[patch.name] ?: patch.defaultVariantIndex)
                    .coerceIn(0, patch.variants.lastIndex)
                patch.variants.forEachIndexed { index, variant ->
                    if (!enabled || index != selected) addAll(variant.fileNames)
                }
            }
            for (subOption in patch.subOptions) {
                if (!enabled || !isEnabled(subOption)) addAll(subOption.fileNames)
            }
        }
    }

    fun knownExtensionFiles(): Set<String> = buildSet {
        for (patch in KnownPatch.All) {
            addAll(patch.extensionFileNames)
            patch.variants.forEach { addAll(it.extensionFileNames) }
            patch.subOptions.forEach { addAll(it.extensionFileNames) }
        }
    }

    fun enabledExtensionFiles(): Set<String> = buildSet {
        for (patch in KnownPatch.All) {
            if (!isEnabled(patch)) continue

            addAll(patch.extensionFileNames)

            if (patch.variants.isNotEmpty()) {
                val selected = (selectedVariants[patch.name] ?: patch.defaultVariantIndex)
                    .coerceIn(0, patch.variants.lastIndex)
                addAll(patch.variants[selected].extensionFileNames)
            }

            for (subOption in patch.subOptions) {
                if (isEnabled(subOption)) addAll(subOption.extensionFileNames)
            }
        }
    }

    companion object {
        val Default: PatchOptions = PatchOptions(
            appName = "TIDAL",
            packageName = "com.aspiro.tidal",
            debuggable = false,
        )
    }
}
