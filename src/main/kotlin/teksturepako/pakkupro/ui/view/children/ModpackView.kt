package teksturepako.pakkupro.ui.view.children

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.michaelbull.result.getError
import io.github.vinceglb.filekit.compose.PickerResultLauncher
import io.github.vinceglb.filekit.compose.rememberDirectoryPickerLauncher
import io.github.vinceglb.filekit.core.FileKitPlatformSettings
import kotlinx.coroutines.launch
import org.jetbrains.jewel.ui.component.CircularProgressIndicator
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.VerticalSplitLayout
import teksturepako.pakku.api.actions.errors.FileNotFound
import teksturepako.pakkupro.ui.application.PakkuApplicationScope
import teksturepako.pakkupro.ui.application.titlebar.MainTitleBar
import teksturepako.pakkupro.ui.component.Error
import teksturepako.pakkupro.ui.component.SonnerToastHost
import teksturepako.pakkupro.ui.component.dialog.CreateModpackDialog
import teksturepako.pakkupro.ui.component.dropdown.ModpackDropdown
import teksturepako.pakkupro.ui.component.dropdown.VcsDropdown
import teksturepako.pakkupro.ui.component.modpack.ModpackSideBar
import teksturepako.pakkupro.ui.modifier.subtractTopHeight
import teksturepako.pakkupro.ui.view.children.modpackTabs.GitTab
import teksturepako.pakkupro.ui.view.children.modpackTabs.ModpackTab
import teksturepako.pakkupro.ui.view.children.modpackTabs.ProjectsTab
import teksturepako.pakkupro.ui.viewmodel.GitViewModel
import teksturepako.pakkupro.ui.viewmodel.ModpackViewModel
import teksturepako.pakkupro.ui.viewmodel.ProfileViewModel
import teksturepako.pakkupro.ui.viewmodel.state.SelectedTab
import kotlin.io.path.Path

@Composable
fun PakkuApplicationScope.ModpackView()
{
    val titleBarHeight = 40.dp

    val modpackUiState by ModpackViewModel.modpackUiState.collectAsState()
    val profileData by ProfileViewModel.profileData.collectAsState()

    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(lifecycleState, profileData.currentProfile)
    {
        ModpackViewModel.loadFromDisk()
        GitViewModel.load()
    }

    val pickerLauncher: PickerResultLauncher = rememberDirectoryPickerLauncher(
        title = "Open modpack directory",
        platformSettings = FileKitPlatformSettings(parentWindow = this.decoratedWindowScope.window)
    ) { directory ->
        if (directory?.path == null) return@rememberDirectoryPickerLauncher

        if (modpackUiState.action.first != null)
        {
            ProfileViewModel.updateCloseDialog {
                ProfileViewModel.updateCurrentProfile(Path(directory.path!!))
            }
        }
        else
        {
            coroutineScope.launch {
                ProfileViewModel.updateCurrentProfile(Path(directory.path!!))
            }
        }
    }

    MainTitleBar(Modifier.height(titleBarHeight), withGradient = true) {
        ModpackDropdown(pickerLauncher)
        VcsDropdown()
        if (modpackUiState.action.first != null)
        {
            Box(Modifier.padding(4.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(modpackUiState.action.first!!)
                    CircularProgressIndicator()
                }
            }
        }
    }

    if (modpackUiState.lockFile?.isErr == true)
    {
        Row(
            Modifier
                .fillMaxSize()
                .subtractTopHeight(titleBarHeight)
        ) {
            if (modpackUiState.lockFile!!.getError() is FileNotFound)
            {
                CreateModpackDialog()
            }
            else
            {
                modpackUiState.lockFile!!.getError()?.let { Error(it) }
            }
        }

        return
    }

    Row(
        Modifier
            .fillMaxSize()
            .subtractTopHeight(titleBarHeight)
    ) {
        ModpackSideBar()

        VerticalSplitLayout(
            state = ModpackViewModel.actionSplitState,
            first = {
                Column {
                    Row {
                        when (modpackUiState.selectedTab)
                        {
                            SelectedTab.PROJECTS -> ProjectsTab()
                            SelectedTab.MODPACK -> ModpackTab()
                            SelectedTab.COMMIT  -> GitTab()
                        }
                    }
                }
            },
            second = {
                Column {
                    Row {
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            firstPaneMinWidth = 100.dp,
            secondPaneMinWidth = 40.dp,
            draggableWidth = 16.dp
        )

    }

    SonnerToastHost(
        ModpackViewModel.toasts,
        Modifier
            .fillMaxSize()
            .subtractTopHeight(titleBarHeight),
        alignment = Alignment.TopEnd,
        spacing = 8.dp
    )

}
