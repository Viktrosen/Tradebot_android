package ru.bolotov.features.more.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.bolotov.features.more.presentation.core.MoreMenuItem

@Composable
fun MoreMenuItem(
    item: MoreMenuItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val icon = when (item.id) {
        "instruments" -> Icons.Default.Star           // вместо TrendingUp
        "history" -> Icons.Default.Favorite           // вместо History
        "settings" -> Icons.Default.Settings
        "about" -> Icons.Default.Info
        "logout" -> Icons.Default.Close               // вместо Logout
        else -> Icons.Default.Build
    }

    val textColor = when (item.id) {
        "logout" -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurface
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (item.id == "logout") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = textColor
                )
            }

            if (item.badge != null) {
                Badge {
                    Text(item.badge)
                }
            } else {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}