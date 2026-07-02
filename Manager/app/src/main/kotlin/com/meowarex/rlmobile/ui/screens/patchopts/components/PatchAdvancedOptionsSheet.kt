package com.meowarex.rlmobile.ui.screens.patchopts.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.meowarex.rlmobile.R
import com.meowarex.rlmobile.ui.components.ResetToDefaultButton
import com.meowarex.rlmobile.ui.screens.patchopts.OptionLock
import com.meowarex.rlmobile.ui.screens.patchopts.OptionSpec
import com.meowarex.rlmobile.ui.screens.patchopts.PatchOptionState
import com.meowarex.rlmobile.ui.screens.patchopts.optionLock
import com.meowarex.rlmobile.ui.screens.patchopts.PatchSpec
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatchAdvancedOptionsSheet(
    patch: PatchSpec,
    state: PatchOptionState,
    selectedVariant: Int,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var lockDialog by remember { mutableStateOf<OptionLock?>(null) }

    fun dismiss() {
        scope.launch { sheetState.hide() }.invokeOnCompletion {
            if (!sheetState.isVisible) onDismiss()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = stringResource(R.string.patchopts_advanced_sheet_label),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = patch.title,
                        style = MaterialTheme.typography.headlineSmall,
                    )
                }

                ResetToDefaultButton(
                    enabled = state.isModified(patch),
                    onClick = { state.reset(patch) },
                )
            }

            HorizontalDivider()

            val parentKeys = patch.advancedOptions
                .filterIsInstance<OptionSpec.Toggle>()
                .filter { !it.inline }
                .map { it.key }
                .toSet()
            val sheetOptions = patch.advancedOptions.filter { option ->
                when {
                    option is OptionSpec.Toggle && option.inline -> false
                    option is OptionSpec.Toggle && option.requiresOption in parentKeys -> false
                    else -> true
                }
            }
            for (option in sheetOptions) key(option.key) {
                val lock = patch.optionLock(option, selectedVariant) { state.toggle(patch, it) }
                when (option) {
                    is OptionSpec.Toggle -> {
                        val toggleOn = state.toggle(patch, option)
                        val gatedChoices = patch.advancedOptions.filterIsInstance<OptionSpec.Choice>()
                            .filter { it.requiresOption == option.key }
                        val gatedToggles = patch.advancedOptions.filterIsInstance<OptionSpec.Toggle>()
                            .filter { it.requiresOption == option.key && !it.inline }
                        val hasSubOptions = gatedChoices.isNotEmpty() || gatedToggles.isNotEmpty()
                        val carded = hasSubOptions && toggleOn

                        Column(
                            modifier = if (carded) {
                                Modifier
                                    .fillMaxWidth()
                                    .clip(MaterialTheme.shapes.medium)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.07f))
                                    .border(
                                        width = 1.5.dp,
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.55f),
                                        shape = MaterialTheme.shapes.medium,
                                    )
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            } else {
                                Modifier.fillMaxWidth()
                            },
                        ) {
                            ToggleOptionRow(
                                title = option.title,
                                description = option.description,
                                checked = toggleOn,
                                onCheckedChange = { state.setToggle(patch, option, it) },
                                locked = lock != OptionLock.Free,
                                onLockedTap = { lockDialog = lock },
                                modifier = if (hasSubOptions) Modifier.padding(vertical = 6.dp) else Modifier,
                            )
                            if (hasSubOptions) {
                                AnimatedVisibility(visible = toggleOn) {
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(10.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 8.dp),
                                    ) {
                                        gatedChoices.forEach { choice ->
                                            key(choice.key) {
                                                ChoiceOptionRow(
                                                    title = choice.title,
                                                    entries = choice.entries,
                                                    selectedIndex = state.choice(patch, choice),
                                                    onSelect = { state.setChoice(patch, choice, it) },
                                                )
                                            }
                                        }
                                        gatedToggles.forEach { sub ->
                                            key(sub.key) {
                                                val subLock = patch.optionLock(sub, selectedVariant) { state.toggle(patch, it) }
                                                ToggleOptionRow(
                                                    title = sub.title,
                                                    description = sub.description,
                                                    checked = state.toggle(patch, sub),
                                                    onCheckedChange = { state.setToggle(patch, sub, it) },
                                                    locked = subLock != OptionLock.Free,
                                                    onLockedTap = { lockDialog = subLock },
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    is OptionSpec.Slider -> SliderOptionRow(
                        title = option.title,
                        description = option.description,
                        value = state.slider(patch, option),
                        valueLabel = formatSliderValue(option, state.slider(patch, option)),
                        valueRange = option.valueRange,
                        steps = option.steps,
                        onValueChange = { state.setSlider(patch, option, it) },
                    )

                    is OptionSpec.Choice ->
                        if (option.requiresOption == null) {
                            ChoiceOptionRow(
                                title = option.title,
                                entries = option.entries,
                                selectedIndex = state.choice(patch, option),
                                onSelect = { state.setChoice(patch, option, it) },
                            )
                        }
                }
            }

            FilledTonalButton(
                onClick = { dismiss() },
                colors = ButtonDefaults.filledTonalButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary,
                ),
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 4.dp),
            ) {
                Text(stringResource(R.string.action_done))
            }
        }
    }

    lockDialog?.let { lock ->
        OptionLockDialog(lock = lock, onDismiss = { lockDialog = null })
    }
}

@Composable
private fun ToggleOptionRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    locked: Boolean = false,
    onLockedTap: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember(::MutableInteractionSource)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Switch,
            ) { if (locked) onLockedTap() else onCheckedChange(!checked) }
            .alpha(if (locked) 0.45f else 1f),
    ) {
        OptionText(
            title = title,
            description = description,
            modifier = Modifier.weight(1f),
        )

        Box {
            Switch(
                checked = checked,
                enabled = !locked,
                onCheckedChange = onCheckedChange,
                interactionSource = interactionSource,
            )
            if (locked) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable(
                            interactionSource = remember(::MutableInteractionSource),
                            indication = null,
                            role = Role.Switch,
                        ) { onLockedTap() }
                )
            }
        }
    }
}

@Composable
private fun SliderOptionRow(
    title: String,
    description: String,
    value: Float,
    valueLabel: String,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    onValueChange: (Float) -> Unit,
) {
    // Whether the slider snaps to the step dots
    var snap by rememberSaveable { mutableStateOf(true) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = valueLabel,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            if (steps > 0) {
                IconButton(
                    onClick = {
                        snap = !snap
                        if (snap) onValueChange(snapToNearestStep(value, valueRange, steps))
                    },
                    modifier = Modifier.size(32.dp),
                ) {
                    Icon(
                        painter = painterResource(
                            if (snap) R.drawable.ic_lock else R.drawable.ic_lock_open,
                        ),
                        contentDescription = stringResource(R.string.patchopts_toggle_snap),
                        tint = if (snap) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }

        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.alpha(.7f),
        )

        // Locked -> native stepped slider (dots + snapping), left exactly as-is
        // Unlocked -> plain continuous slider with the trailing stop-indicator dot removed
        if (snap) {
            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = valueRange,
                steps = steps,
            )
        } else {
            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = valueRange,
                track = { SliderDefaults.Track(sliderState = it, drawStopIndicator = null) },
            )
        }
    }
}

private fun snapToNearestStep(
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    steps: Int,
): Float {
    if (steps <= 0) return value
    val stepSize = (range.endInclusive - range.start) / (steps + 1)
    if (stepSize <= 0f) return value
    val snapped = range.start + Math.round((value - range.start) / stepSize) * stepSize
    return snapped.coerceIn(range.start, range.endInclusive)
}

@Composable
private fun ChoiceOptionRow(
    entries: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    title: String = "",
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (title.isNotEmpty()) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
        if (entries.size > 3) {
            var expanded by rememberSaveable { mutableStateOf(false) }
            val selectedLabel = entries.getOrNull(selectedIndex).orEmpty()

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = selectedLabel,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    entries.forEachIndexed { index, entry ->
                        DropdownMenuItem(
                            text = { Text(entry) },
                            onClick = {
                                onSelect(index)
                                expanded = false
                            },
                        )
                    }
                }
            }
            return@Column
        }
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            entries.forEachIndexed { index, entry ->
                SegmentedButton(
                    selected = index == selectedIndex,
                    onClick = { onSelect(index) },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = entries.size),
                    icon = {},
                    label = {
                        Text(
                            text = entry,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelMedium,
                        )
                    },
                )
            }
        }
    }
}

@Composable
private fun OptionText(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.alpha(.7f),
        )
    }
}

private fun formatSliderValue(option: OptionSpec.Slider, value: Float): String {
    val rounded = value.roundToInt()
    val unit = option.unit
    return when {
        option.displayAsPercent -> "$rounded%"
        unit != null -> "$rounded $unit"
        else -> rounded.toString()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OptionLockDialog(lock: OptionLock, onDismiss: () -> Unit) {
    val message = when (lock) {
        is OptionLock.NeedsVariant -> stringResource(R.string.patch_opt_lock_needs_variant, lock.title)
        is OptionLock.NeedsOption -> stringResource(R.string.patch_opt_lock_needs_option, lock.title)
        OptionLock.Free -> return
    }

    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            tonalElevation = 6.dp,
        ) {
            Column(
                modifier = Modifier.padding(start = 24.dp, end = 12.dp, top = 20.dp, bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = stringResource(R.string.patch_opt_lock_title),
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(
                    text = AnnotatedString.fromHtml(message),
                    style = MaterialTheme.typography.titleMedium,
                )
                Box(modifier = Modifier.fillMaxWidth()) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.align(Alignment.CenterEnd),
                    ) {
                        Text(stringResource(R.string.action_got_it))
                    }
                }
            }
        }
    }
}
