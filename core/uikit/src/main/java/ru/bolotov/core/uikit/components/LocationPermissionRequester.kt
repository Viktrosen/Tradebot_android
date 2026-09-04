package ru.bolotov.core.uikit.components

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun RequestPermissions(
    permissions: List<String>,
    onPermissionsResult: (Map<String, Boolean>) -> Unit = {}
) {
    val context = LocalContext.current
    var handled by remember { mutableStateOf(false) }
    val currentPermissions by rememberUpdatedState(permissions)

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        if (!handled) {
            handled = true
            onPermissionsResult(results)
        }
    }

    LaunchedEffect(currentPermissions) {
        val allGranted = context.isAllPermissionsGranted(currentPermissions)

        if (allGranted) {
            if (!handled) {
                handled = true
                onPermissionsResult(currentPermissions.associateWith { true })
            }
        } else {
            handled = false
            launcher.launch(currentPermissions.toTypedArray())
        }
    }
}

fun Context.isAllPermissionsGranted(
    permissions: List<String>,
) = permissions.all { permission ->
    ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}
