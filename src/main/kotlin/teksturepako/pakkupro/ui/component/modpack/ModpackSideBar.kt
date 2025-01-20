package teksturepako.pakkupro.ui.component.modpack

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.component.IconButton
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.Tooltip
import teksturepako.pakkupro.ui.PakkuDesktopIcons
import teksturepako.pakkupro.ui.viewmodel.ModpackViewModel
import teksturepako.pakkupro.ui.viewmodel.state.SelectedTab

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ModpackSideBar()
{
    val modpackUiState by ModpackViewModel.modpackUiState.collectAsState()

    Row {
        Column(
            Modifier
                .fillMaxHeight()
                .width(40.dp)
                .padding(vertical = 4.dp)
                .background(JewelTheme.globalColors.panelBackground),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Tooltip({ Text("Manage Projects") }) {
                IconButton(
                    onClick = { ModpackViewModel.selectTab(SelectedTab.MODPACK) },
                    Modifier.size(30.dp),
                    enabled = modpackUiState.selectedTab != SelectedTab.MODPACK,
                ) {
                    Icon(
                        key = PakkuDesktopIcons.Modpack.manage,
                        contentDescription = "icon",
                        tint = if (modpackUiState.selectedTab == SelectedTab.MODPACK) Color.LightGray else Color.Gray,
                        hints = arrayOf()
                    )
                }
            }
            Tooltip({ Text("Add Projects") }) {
                IconButton(
                    onClick = { ModpackViewModel.selectTab(SelectedTab.PROJECTS) },
                    Modifier.size(30.dp),
                    enabled = modpackUiState.selectedTab != SelectedTab.PROJECTS,
                ) {
                    Icon(
                        key = PakkuDesktopIcons.Modpack.search,
                        contentDescription = "icon",
                        tint = if (modpackUiState.selectedTab == SelectedTab.PROJECTS) Color.LightGray else Color.Gray,
                        hints = arrayOf()
                    )
                }
            }
        }
        Column {
            Spacer(Modifier.background(JewelTheme.globalColors.borders.normal).width(1.dp).fillMaxHeight())
        }
    }
}