package com.meowarex.rlmobile.ui.screens.patchopts

import android.content.Context
import android.content.pm.PackageManager.NameNotFoundException
import androidx.compose.runtime.*
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.navigator.Navigator
import com.meowarex.rlmobile.manager.PreferencesManager
import com.meowarex.rlmobile.ui.screens.componentopts.ComponentOptionsScreen
import com.meowarex.rlmobile.ui.screens.componentopts.PatchComponent
import com.meowarex.rlmobile.ui.util.pushForResult
import com.meowarex.rlmobile.util.*
import kotlinx.coroutines.launch

class PatchOptionsModel(
    prefilledOptions: PatchOptions,
    private val context: Context,
    private val prefs: PreferencesManager,
) : ScreenModel {
    var packageName by mutableStateOf(prefilledOptions.packageName)
        private set

    var packageNameState by mutableStateOf(PackageNameState.Ok)
        private set

    fun changePackageName(newPackageName: String) {
        packageName = newPackageName
        fetchPkgNameStateDebounced()
    }

    var appName by mutableStateOf(prefilledOptions.appName)
        private set

    var appNameIsError by mutableStateOf(false)
        private set

    fun changeAppName(newAppName: String) {
        appName = newAppName
        appNameIsError = newAppName.length !in (1..150)
    }

    var debuggable by mutableStateOf(prefilledOptions.debuggable)
        private set

    fun changeDebuggable(value: Boolean) {
        debuggable = value
    }

    var patchStates by mutableStateOf(prefilledOptions.patchStates)
        private set

    var selectedVariants by mutableStateOf(prefilledOptions.selectedVariants)
        private set

    fun variantIndex(patch: KnownPatch): Int = selectedVariants[patch.name]
        ?.coerceIn(0, patch.variants.lastIndex.coerceAtLeast(0))
        ?: patch.defaultVariantIndex.coerceIn(0, patch.variants.lastIndex.coerceAtLeast(0))

    fun isPatchEnabled(patch: KnownPatch): Boolean =
        patchStates[patch.name] ?: patch.default.isEnabled

    fun setPatchEnabled(patch: KnownPatch, enabled: Boolean) {
        fun closure(seed: KnownPatch, step: (KnownPatch) -> List<KnownPatch>): Set<KnownPatch> =
            buildSet {
                fun walk(p: KnownPatch) { if (add(p)) step(p).forEach(::walk) }
                walk(seed)
            }

        val enableUnits: Set<KnownPatch>
        val disableUnits: Set<KnownPatch>
        if (enabled) {
            enableUnits = closure(patch) { it.requires }
            disableUnits = enableUnits.flatMap { it.disables }
                .flatMapTo(mutableSetOf()) { d ->
                    closure(d) { dep -> KnownPatch.All.filter { dep in it.requires } }
                }
        } else {
            enableUnits = emptySet()
            disableUnits = closure(patch) { p -> KnownPatch.All.filter { p in it.requires } }
        }

        patchStates = patchStates.toMutableMap().apply {
            enableUnits.forEach { this[it.name] = true }
            disableUnits.forEach { this[it.name] = false }
        }
    }

    fun selectVariant(patch: KnownPatch, index: Int) {
        if (patch.variants.isEmpty() || index !in patch.variants.indices) return
        selectedVariants = selectedVariants + (patch.name to index)
    }

    fun lockState(patch: KnownPatch): PatchLock {
        if (patch.variants.isNotEmpty()) return PatchLock.Free

        fun closure(seed: KnownPatch, step: (KnownPatch) -> List<KnownPatch>): Set<KnownPatch> =
            buildSet {
                fun walk(p: KnownPatch) { if (add(p)) step(p).forEach(::walk) }
                walk(seed)
            }

        for (other in KnownPatch.All) {
            if (other == patch || !isPatchEnabled(other)) continue

            val requiresClosure = closure(other) { it.requires }
            if (patch in requiresClosure - other) return PatchLock.LockedOn(other)

            val disablesClosure = requiresClosure.flatMap { it.disables }
                .flatMapTo(mutableSetOf()) { d ->
                    closure(d) { dep -> KnownPatch.All.filter { dep in it.requires } }
                }
            if (patch in disablesClosure) return PatchLock.LockedOff(other)
        }
        return PatchLock.Free
    }

    val enabledPatchCount: Int
        get() = KnownPatch.All.count { isPatchEnabled(it) }

    var customTidalApk by mutableStateOf<PatchComponent?>(null)
        private set
    var customPatches by mutableStateOf<PatchComponent?>(null)
        private set

    fun selectCustomTidalApk(navigator: Navigator) = screenModelScope.launch {
        customTidalApk = navigator.pushForResult(
            ComponentOptionsScreen(
                default = customTidalApk,
                componentType = PatchComponent.Type.TidalApk,
            )
        )
    }

    fun selectCustomPatches(navigator: Navigator) = screenModelScope.launch {
        customPatches = navigator.pushForResult(
            ComponentOptionsScreen(
                default = customPatches,
                componentType = PatchComponent.Type.Patches,
            )
        )
    }

    val isConfigValid by derivedStateOf {
        val invalidChecks = arrayOf(
            packageNameState == PackageNameState.Invalid,
            appNameIsError,
        )

        invalidChecks.none { it }
    }

    fun generateConfig(): PatchOptions {
        if (!isConfigValid) error("invalid config state")

        return PatchOptions(
            appName = appName,
            packageName = packageName,
            debuggable = debuggable,
            customTidalApk = customTidalApk,
            customPatches = customPatches,
            patchStates = patchStates,
            selectedVariants = selectedVariants,
        )
    }

    val isDevMode: Boolean
        get() = prefs.devMode

    private val fetchPkgNameStateDebounced: () -> Unit =
        screenModelScope.debounce(100L, function = ::fetchPkgNameState)

    private suspend fun fetchPkgNameState() {
        val state = if (packageName.length !in (3..150) || !PACKAGE_REGEX.matches(this.packageName)) {
            PackageNameState.Invalid
        } else {
            try {
                context.packageManager.getPackageInfo(packageName, 0)
                PackageNameState.Taken
            } catch (_: NameNotFoundException) {
                PackageNameState.Ok
            }
        }

        mainThread { packageNameState = state }
    }

    private fun validatePatchSelection() {
        for (patch in KnownPatch.All) {
            if (isPatchEnabled(patch)) {
                setPatchEnabled(patch, true)
            }
        }
    }

    init {
        validatePatchSelection()
        screenModelScope.launchBlock { fetchPkgNameState() }
    }

    companion object {
        private val PACKAGE_REGEX = """^[a-z]\w*(\.[a-z]\w*)+$"""
            .toRegex(RegexOption.IGNORE_CASE)
    }
}

enum class PackageNameState {
    Ok,
    Invalid,
    Taken,
}

sealed class PatchLock {
    object Free : PatchLock()
    data class LockedOn(val by: KnownPatch) : PatchLock()
    data class LockedOff(val by: KnownPatch) : PatchLock()
}
