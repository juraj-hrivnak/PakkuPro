package teksturepako.pakkupro.ui.view.children.modpackTabs

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.jewel.ui.component.HorizontalSplitLayout
import org.jetbrains.jewel.ui.component.Text
import teksturepako.pakkupro.ui.component.HorizontalBar
import teksturepako.pakkupro.ui.component.modpack.project.ProjectDisplay
import teksturepako.pakkupro.ui.component.modpack.project.list.ProjectsList
import teksturepako.pakkupro.ui.viewmodel.ModpackViewModel

@Composable
fun ProjectsTab()
{
    Column(Modifier.fillMaxSize()) {
        HorizontalSplitLayout(
            state = ModpackViewModel.projectsTabSplitState,
            first = {
                Column {
                    HorizontalBar {
                        Text("Projects", Modifier.padding(4.dp))
                    }

                    Row {
                        ProjectDisplay()
                    }
                }
            },
            second = {
                Column {
                    Row {
                        ProjectsList()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            firstPaneMinWidth = 200.dp,
            secondPaneMinWidth = 200.dp,
            draggableWidth = 16.dp
        )
    }
}
