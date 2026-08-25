package com.dertefter.settings.presentation.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.TheTheme
import com.dertefter.settings.presentation.BalanceState

@Composable
fun BalanceCard(
    modifier: Modifier = Modifier,
    balanceState: BalanceState,
    onRefresh: () -> Unit = {}
){
    Surface(
        modifier = modifier
            .fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                "Количество токенов GigaChat",
                modifier = Modifier
                    .padding(bottom = 64.dp)
                    .fillMaxWidth()
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            )
            {

                val balanceText: String = balanceState.balance?.toString() ?: ""

                AnimatedVisibility(
                    visible = balanceText != ""
                ) {
                    Text(
                        text = balanceText,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.displaySmall
                    )
                }

                Crossfade(
                    targetState = balanceState.isLoading,

                ) { isLoading ->
                    if (isLoading){
                        CircularProgressIndicator(
                            modifier = Modifier.size(36.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Refresh,
                            contentDescription = null,
                            modifier = Modifier.clickable(
                                onClick = onRefresh
                            ).size(36.dp)
                        )
                    }
                }



            }

            AnimatedVisibility(
                visible = balanceState.isError
            ) {
                Text(
                    text = "Не удалось загрузить данные...",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Preview
@Composable
fun BalanceCardPreview() {
    TheTheme {
        BalanceCard(
            balanceState = BalanceState(
                balance = 200,
                isError = true
            )
        )
    }
}



