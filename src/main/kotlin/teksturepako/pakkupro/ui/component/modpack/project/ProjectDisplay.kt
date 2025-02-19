package teksturepako.pakkupro.ui.component.modpack.project

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.VerticalScrollbar
import teksturepako.pakku.api.projects.ProjectFile
import teksturepako.pakkupro.ui.PakkuDesktopIcons
import teksturepako.pakkupro.ui.component.button.CopyToClipboardButton
import teksturepako.pakkupro.ui.component.text.GradientHeader
import teksturepako.pakkupro.ui.viewmodel.ModpackViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProjectDisplay() {
    val modpackUiState by ModpackViewModel.modpackUiState.collectAsState()

    val project = modpackUiState.selectedProject ?: return

    LaunchedEffect(project.pakkuId) {
        ModpackViewModel.editProject(false)
    }

    val scrollState = rememberScrollState()

    Box(
        Modifier.fillMaxSize()
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
        ) {
            project.name.values.firstOrNull()?.let { projectName ->
                FlowRow(
                    Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    SelectionContainer {
                        GradientHeader(projectName)
                    }
                    CopyToClipboardButton(
                        projectName,
                        Modifier
                            .size(25.dp)
                            .padding(4.dp),
                    )
                }
            }
            project.files.forEach { projectFile ->
                ProjectFileName(projectFile)
            }
            Spacer(
                Modifier
                    .padding(top = 16.dp)
                    .background(JewelTheme.globalColors.borders.normal)
                    .height(1.dp)
                    .fillMaxWidth()
            )
            ProjectProperties()
        }

        VerticalScrollbar(
            scrollState,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight(),
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProjectFileName(projectFile: ProjectFile)
{
    FlowRow(
        Modifier.padding(horizontal = 16.dp),
        overflow = FlowRowOverflow.Visible
    ) {
        Column {
            val provIcon = when (projectFile.type)
            {
                "curseforge" -> PakkuDesktopIcons.Platforms.curseForge
                "github"     -> PakkuDesktopIcons.Platforms.gitHub
                "modrinth"   -> PakkuDesktopIcons.Platforms.modrinth
                else         -> null
            }
            provIcon
                ?.let {
                    Icon(it, projectFile.type, Modifier.padding(4.dp).size(25.dp))
                }
                ?: Text(projectFile.type)
        }
        Column(Modifier.offset(y = 3.dp)) { Text(": ") }
        SelectionContainer {
            Column(Modifier.offset(y = 3.dp)) { Text(projectFile.fileName) }
        }
        Column {
            CopyToClipboardButton(
                projectFile.fileName,
                Modifier
                    .size(25.dp)
                    .padding(4.dp),
            )
        }
    }
}

