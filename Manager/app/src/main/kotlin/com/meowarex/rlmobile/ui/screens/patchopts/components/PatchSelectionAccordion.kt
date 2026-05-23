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
import androidx.compose.ui.unit.dp
import com.meowarex.rlmobile.R
import com.meowarex.rlmobile.ui.screens.patchopts.KnownPatch

@Composable
fun PatchSelectionAccordion(
    enabledCount: Int,
    totalCount: Int,
    isEnabled: (KnownPatch) -> Boolean,
    onToggle: (KnownPatch, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "patch-accordion-arrow",
    )

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
                    PatchCheckboxRow(
                        title = stringResource(patch.titleRes),
                        description = stringResource(patch.descRes),
                        checked = isEnabled(patch),
                        onCheckedChange = { onToggle(patch, it) },
                    )
                }
            }
        }
    }
}

@Composable
private fun PatchCheckboxRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    val interactionSource = remember(::MutableInteractionSource)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Checkbox,
            ) { onCheckedChange(!checked) }
            .padding(vertical = 4.dp),
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            interactionSource = interactionSource,
        )

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
    }
}
