package com.meowarex.rlmobile.ui.screens.patchopts.components


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.unit.dp
import com.meowarex.rlmobile.R
import com.meowarex.rlmobile.ui.screens.patchopts.KnownPatch
import com.meowarex.rlmobile.ui.screens.patchopts.PatchLock
import com.meowarex.rlmobile.ui.screens.patchopts.PatchSubOption

private data class LockInfo(val patch: KnownPatch, val lock: PatchLock)

@Composable
fun PatchSelectionAccordion(
    enabledCount: Int,
    totalCount: Int,
    isEnabled: (KnownPatch) -> Boolean,
    onToggle: (KnownPatch, Boolean) -> Unit,
    lockState: (KnownPatch) -> PatchLock,
    variantIndex: (KnownPatch) -> Int,
    onSelectVariant: (KnownPatch, Int) -> Unit,
    isSubOptionEnabled: (KnownPatch, PatchSubOption) -> Boolean,
    onToggleSubOption: (KnownPatch, PatchSubOption, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "patch-accordion-arrow",
    )

    var lockInfo by remember { mutableStateOf<LockInfo?>(null) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceContainerLow),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable(role = Role.Button) { expanded = !expanded }
                .padding(horizontal = 16.dp, vertical = 14.dp),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = stringResource(R.string.patchopts_patches_title),
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = stringResource(
                        R.string.patchopts_patches_summary,
                        enabledCount,
                        totalCount,
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.alpha(.7f),
                )
            }

            Icon(
                painter = painterResource(R.drawable.ic_arrow_down_small),
                contentDescription = stringResource(
                    if (expanded) R.string.action_collapse else R.string.action_expand,
                ),
                modifier = Modifier.rotate(rotation),
            )
        }

        AnimatedVisibility(visible = expanded) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background.copy(0.4f))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                Text(
                    text = stringResource(R.string.patchopts_patches_desc),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .alpha(.7f)
                        .padding(bottom = 8.dp),
                )

                for (patch in KnownPatch.All) key(patch) {
                    val checked = isEnabled(patch)
                    val lock = lockState(patch)
                    PatchSwitchRow(
                        title = stringResource(patch.titleRes),
                        description = stringResource(patch.descRes),
                        checked = checked,
                        lock = lock,
                        onCheckedChange = { onToggle(patch, it) },
                        onLockedTap = { lockInfo = LockInfo(patch, lock) },
                    )

                    if (patch.variants.isNotEmpty()) {
                        AnimatedVisibility(visible = checked) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 4.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
                            ) {
                                PatchVariantSelector(
                                    variants = patch.variants,
                                    selectedIndex = variantIndex(patch),
                                    onSelect = { idx -> onSelectVariant(patch, idx) },
                                )
                            }
                        }
                    }

                    if (patch.subOptions.isNotEmpty()) {
                        AnimatedVisibility(visible = checked) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 12.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
                            ) {
                                for (subOption in patch.subOptions) key(subOption) {
                                    PatchSwitchRow(
                                        title = stringResource(subOption.titleRes),
                                        description = stringResource(subOption.descRes),
                                        checked = isSubOptionEnabled(patch, subOption),
                                        lock = PatchLock.Free,
                                        onCheckedChange = {
                                            onToggleSubOption(patch, subOption, it)
                                        },
                                        onLockedTap = {},
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    lockInfo?.let { info ->
        PatchLockDialog(
            thisPatch = info.patch,
            lock = info.lock,
            onDismiss = { lockInfo = null },
        )
    }
}

@Composable
private fun PatchSwitchRow(
    title: String,
    description: String,
    checked: Boolean,
    lock: PatchLock,
    onCheckedChange: (Boolean) -> Unit,
    onLockedTap: () -> Unit,
) {
    val interactionSource = remember(::MutableInteractionSource)
    val isLocked = lock !is PatchLock.Free

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Switch,
            ) {
                if (isLocked) onLockedTap() else onCheckedChange(!checked)
            }
            .alpha(if (isLocked) 0.45f else 1f)
            .padding(vertical = 6.dp),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.alpha(.7f),
            )
        }

        Box {
            Switch(
                checked = checked,
                enabled = !isLocked,
                onCheckedChange = onCheckedChange,
                interactionSource = interactionSource,
            )
            if (isLocked) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PatchLockDialog(
    thisPatch: KnownPatch,
    lock: PatchLock,
    onDismiss: () -> Unit,
) {
    val (titleRes, msgRes, blockerTitle) = when (lock) {
        is PatchLock.LockedOn -> Triple(
            R.string.patch_lock_required_title,
            R.string.patch_lock_required_msg,
            stringResource(lock.by.titleRes),
        )
        is PatchLock.LockedOff -> Triple(
            R.string.patch_lock_blocked_title,
            R.string.patch_lock_blocked_msg,
            stringResource(lock.by.titleRes),
        )
        PatchLock.Free -> return
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
                    text = stringResource(titleRes),
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(
                    text = AnnotatedString.fromHtml(stringResource(msgRes, blockerTitle)),
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
