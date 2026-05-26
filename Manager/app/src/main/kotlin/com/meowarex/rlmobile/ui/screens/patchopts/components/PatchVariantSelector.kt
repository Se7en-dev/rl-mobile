package com.meowarex.rlmobile.ui.screens.patchopts.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.meowarex.rlmobile.ui.screens.patchopts.PatchVariant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatchVariantSelector(
    variants: List<PatchVariant>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    SingleChoiceSegmentedButtonRow(
        modifier = modifier.fillMaxWidth(),
    ) {
        variants.forEachIndexed { index, variant ->
            SegmentedButton(
                selected = index == selectedIndex,
                onClick = { onSelect(index) },
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = variants.size,
                ),
                icon = {},
                label = {
                    Text(
                        text = stringResource(variant.titleRes),
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
