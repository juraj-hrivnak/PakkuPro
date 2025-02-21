package teksturepako.pakkupro.actions

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.michaelbull.result.getOrElse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.jewel.ui.component.Text
import teksturepako.pakku.api.actions.errors.ActionError
import teksturepako.pakku.api.actions.errors.IOExportingError
import teksturepako.pakku.api.actions.export.ExportProfile
import teksturepako.pakku.api.actions.export.exportDefaultProfiles
import teksturepako.pakku.cli.ui.shortForm
import teksturepako.pakku.io.toHumanReadableSize
import teksturepako.pakkupro.ui.component.showToast
import teksturepako.pakkupro.ui.viewmodel.ModpackViewModel
import teksturepako.pakkupro.ui.viewmodel.state.ModpackUiState
import java.nio.file.Path
import kotlin.io.path.fileSize
import kotlin.io.path.pathString
import kotlin.time.Duration

fun exportImpl(modpackUiState: ModpackUiState)
{
    if (modpackUiState.action.first != null) return

    runAction("Exporting") {
        launch {
            val lockFile = modpackUiState.lockFile?.getOrElse {
                withContext(Dispatchers.Main) {
                    ModpackViewModel.toasts.showToast {
                        Box(
                            modifier = Modifier
                                .padding(16.dp)
                                .width(300.dp)
                        ) {
                            Text(it.rawMessage)
                        }
                    }
                }
                return@launch
            } ?: return@launch

            val configFile = modpackUiState.configFile?.getOrElse {
                withContext(Dispatchers.Main) {
                    ModpackViewModel.toasts.showToast {
                        Box(
                            modifier = Modifier
                                .padding(16.dp)
                                .width(300.dp)
                        ) {
                            Text(it.rawMessage)
                        }
                    }
                }
                return@launch
            } ?: return@launch

            val platforms = lockFile.getPlatforms().getOrElse {
                withContext(Dispatchers.Main) {
                    ModpackViewModel.toasts.showToast {
                        Box(
                            modifier = Modifier
                                .padding(16.dp)
                                .width(300.dp)
                        ) {
                            Text(it.rawMessage)
                        }
                    }
                }
                return@launch
            }

            exportDefaultProfiles(
                onError = { profile: ExportProfile, error: ActionError ->
                    if (error !is IOExportingError)
                    {
                        val message = "[${profile.name} profile] ${error.rawMessage}"
                        withContext(Dispatchers.Main) {
                            ModpackViewModel.toasts.showToast {
                                Box(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .width(300.dp)
                                ) {
                                    Text(message)
                                }
                            }
                        }
                        println(message)
                    }
                },
                onSuccess = { profile: ExportProfile, path: Path, duration: Duration ->
                    val fileSize = path.fileSize().toHumanReadableSize()
                    val filePath = path.pathString

                    val message = "[${profile.name} profile] exported to '$filePath' " +
                            "($fileSize) in ${duration.shortForm()}"

                    withContext(Dispatchers.Main) {
                        ModpackViewModel.toasts.showToast {
                            Box(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .width(300.dp)
                            ) {
                                Text(message)
                            }
                        }
                    }
                    println(message)
                },
                lockFile, configFile, platforms,
            ).joinAll()
        }
    }
}
