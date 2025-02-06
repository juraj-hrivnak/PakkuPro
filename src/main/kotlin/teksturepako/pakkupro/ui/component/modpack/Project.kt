package teksturepako.pakkupro.ui.component.modpack

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.component.OutlinedButton
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.VerticalScrollbar
import teksturepako.pakku.api.data.ConfigFile
import teksturepako.pakku.api.projects.*
import teksturepako.pakkupro.ui.PakkuDesktopIcons
import teksturepako.pakkupro.ui.component.button.CopyToClipboardButton
import teksturepako.pakkupro.ui.component.modpack.projectPropSelection.NullableProjectEnumSelection
import teksturepako.pakkupro.ui.component.modpack.projectPropSelection.NullableProjectStringSelection
import teksturepako.pakkupro.ui.component.modpack.projectPropSelection.ProjectBooleanSelection
import teksturepako.pakkupro.ui.component.modpack.projectPropSelection.ProjectEnumSelection
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

@Composable
fun ProjectProperties()
{
    val modpackUiState by ModpackViewModel.modpackUiState.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    Column(
        Modifier.padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                Modifier.weight(1F)
            ) {
                Text("Properties", fontWeight = FontWeight.Bold)
            }
            Column(
                horizontalAlignment = Alignment.End
            ) {
                OutlinedButton(
                    onClick = {
                        if (!modpackUiState.editingProject)
                        {
                            ModpackViewModel.editProject(true)
                        }
                        else
                        {
                            ModpackViewModel.editProject(false)
                            coroutineScope.launch {
                                ModpackViewModel.loadFromDisk()
                            }
                        }
                    }
                ) {
                    Text("Edit")
                }
            }
        }

        ProjectEnumSelection(
            label = "Type:",
            enumEntries = ProjectType.entries,
            projectRef = Project::type,
            projectConfigRef = ConfigFile.ProjectConfig::type
        )

        NullableProjectEnumSelection(
            label = "Side:",
            enumEntries = ProjectSide.entries,
            projectRef = Project::side,
            projectConfigRef = ConfigFile.ProjectConfig::side
        )

        ProjectEnumSelection(
            label = "Update Strategy:",
            enumEntries = UpdateStrategy.entries,
            projectRef = Project::updateStrategy,
            projectConfigRef = ConfigFile.ProjectConfig::updateStrategy
        )

        ProjectBooleanSelection(
            label = "Redistributable:",
            projectRef = Project::redistributable,
            projectConfigRef = ConfigFile.ProjectConfig::redistributable
        )

        NullableProjectStringSelection(
            label = "Subpath:",
            projectRef = Project::getSubpath,
            projectConfigRef = ConfigFile.ProjectConfig::subpath
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

