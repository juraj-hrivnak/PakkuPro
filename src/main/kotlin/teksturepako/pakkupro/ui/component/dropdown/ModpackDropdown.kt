package teksturepako.pakkupro.ui.component.dropdown

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.vinceglb.filekit.compose.PickerResultLauncher
import kotlinx.coroutines.launch
import org.jetbrains.jewel.ui.component.Dropdown
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.separator
import teksturepako.pakkupro.actions.exportImpl
import teksturepako.pakkupro.ui.PakkuDesktopIcons
import teksturepako.pakkupro.ui.viewmodel.ModpackViewModel
import teksturepako.pakkupro.ui.viewmodel.ProfileViewModel
import kotlin.io.path.Path

@Composable
fun ModpackDropdown(
    pickerLauncher: PickerResultLauncher,
    enabled: Boolean = true,
)
{
    val profileData by ProfileViewModel.profileData.collectAsState()
    val modpackUiState by ModpackViewModel.modpackUiState.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    Dropdown(
        Modifier.padding(vertical = 4.dp),
        enabled = enabled,
        content = {
            Row(
                Modifier.align(Alignment.Center),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                modpackUiState.configFile?.getName()?.let { Text(it) } ?: Text("Modpack")
            }
        },
        menuModifier = Modifier
            .offset(x = 12.dp)
            .width(160.dp),
        menuContent = {

            // -- OPEN --

            selectableItem(false, onClick = {
                pickerLauncher.launch()
            }) {
                Row {
                    Column(Modifier.fillMaxWidth(0.2f)) {
                        Icon(
                            key = PakkuDesktopIcons.open,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp),
                            tint = if (profileData.intUiTheme.isDark()) Color.White else Color.Black
                        )
                    }
                    Column {
                        Text(
                            "Open...",
                            Modifier,
                            color = if (profileData.intUiTheme.isDark()) Color.White else Color.Black
                        )
                    }
                }
            }

            // -- CLOSE --

            selectableItem(false, onClick = {
                if (modpackUiState.action.first != null)
                {
                    ProfileViewModel.updateCloseDialog {
                        ProfileViewModel.updateCurrentProfile(null)
                    }
                }
                else
                {
                    coroutineScope.launch {
                        ProfileViewModel.updateCurrentProfile(null)
                    }
                }
            }) {
                Row {
                    Column(Modifier.fillMaxWidth(0.2f)) {
                    }
                    Column {
                        Text(
                            "Close",
                            Modifier,
                            color = if (profileData.intUiTheme.isDark()) Color.White else Color.Black
                        )
                    }
                }
            }

            separator()

            // -- EXPORT --

            selectableItem(
                selected = false,
                onClick = {
                    exportImpl(modpackUiState)
                },
                enabled = modpackUiState.action.first == null
            ) {
                Row {
                    Column(Modifier.fillMaxWidth(0.2f)) {
                        Icon(
                            key = PakkuDesktopIcons.cube,
                            "export",
                            modifier = Modifier.size(15.dp),
                            tint = if (profileData.intUiTheme.isDark()) Color.White else Color.Black
                        )
                    }
                    Column {
                        Text(
                            "Export",
                            Modifier,
                            color = if (profileData.intUiTheme.isDark()) Color.White else Color.Black
                        )
                    }
                }
            }

            separator()

            // -- FETCH --

            selectableItem(false, onClick = {

            }) {
                Row {
                    Column(Modifier.fillMaxWidth(0.2f)) {
                        Icon(
                            key = PakkuDesktopIcons.cloudDownload,
                            contentDescription = "fetch",
                            Modifier.size(15.dp),
                            tint = if (profileData.intUiTheme.isDark()) Color.White else Color.Black
                        )
                    }
                    Column {
                        Text(
                            "Fetch",
                            Modifier,
                            color = if (profileData.intUiTheme.isDark()) Color.White else Color.Black
                        )
                    }
                }
            }

            if (profileData.recentProfilesFiltered.isNotEmpty())
            {
                separator()

                // -- RECENT MODPACKS --

                passiveItem {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "Recent Modpacks",
                            color = if (profileData.intUiTheme.isDark()) Color.White else Color.Black
                        )
                    }
                }

                profileData.recentProfilesFiltered.map { profile ->
                    selectableItem(false, onClick = {
                        if (modpackUiState.action.first != null)
                        {
                            ProfileViewModel.updateCloseDialog {
                                ProfileViewModel.updateCurrentProfile(Path(profile.path))
                            }
                        }
                        else
                        {
                            coroutineScope.launch {
                                ProfileViewModel.updateCurrentProfile(Path(profile.path))
                            }
                        }
                    }) {
                        Row {
                            Column(Modifier.fillMaxWidth(0.2f)) {}
                            Column {
                                Text(
                                    profile.name,
                                    color = if (profileData.intUiTheme.isDark()) Color.White else Color.Black
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}
