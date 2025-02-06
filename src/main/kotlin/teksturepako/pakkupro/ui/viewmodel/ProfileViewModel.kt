package teksturepako.pakkupro.ui.viewmodel

import com.github.michaelbull.result.get
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import teksturepako.pakku.api.data.ConfigFile
import teksturepako.pakku.api.data.workingPath
import teksturepako.pakkupro.data.Profile
import teksturepako.pakkupro.data.ProfileData
import teksturepako.pakkupro.data.ProfileData.CloseDialogData
import teksturepako.pakkupro.ui.application.theme.IntUiThemes
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.pathString

object ProfileViewModel
{
    private var _profileData = MutableStateFlow(ProfileData())
    val profileData: StateFlow<ProfileData> = _profileData.asStateFlow()

    init
    {
        loadFromDisk()
    }

    fun loadFromDisk()
    {
        val profileDataState = ProfileData.readOrNew()

        println("ProfileViewModel loaded from disk")

        _profileData.update {
            profileDataState
        }
    }

    suspend fun writeToDisk()
    {
        println("ProfileViewModel written to disk")

        // Write to disk
        _profileData.value.write()
    }

    private fun addRecentProfile(path: Path) = runBlocking {
        val modpackName = ConfigFile.readToResultFrom(Path("$path/${ConfigFile.FILE_NAME}"))
            .get()?.getName() ?: path.fileName.pathString

        if (path.pathString !in _profileData.value.recentProfiles.map { it.path })
        {
            _profileData.update { currentState ->
                currentState.copy(
                    recentProfiles = currentState.recentProfiles.plus(
                        Profile(
                            name = modpackName,
                            path = path.absolutePathString(),
                            lastOpened = Clock.System.now()
                        )
                    )
                )
            }
        }
        else
        {
            _profileData.update { currentState ->
                val updatedState = currentState.recentProfiles.map { profile ->
                    if (profile.path != path.absolutePathString()) return@map profile // Return the same profile

                    Profile(
                        name = modpackName,
                        path = path.absolutePathString(),
                        lastOpened = Clock.System.now()
                    )
                }

                currentState.copy(
                    recentProfiles = updatedState
                )
            }
        }
    }

    suspend fun updateCurrentProfile(updatedCurrentProfile: Path?)
    {
        loadFromDisk()

        _profileData.value.currentProfilePath?.let { addRecentProfile(it) }

        if (updatedCurrentProfile == null)
        {
            _profileData.update { currentState ->
                currentState.copy(
                    currentProfile = null
                )
            }

            // Update Pakku's working path
            workingPath = "."
        }
        else
        {
            val modpackName = ConfigFile.readToResultFrom(Path("$updatedCurrentProfile/${ConfigFile.FILE_NAME}"))
                .get()?.getName() ?: updatedCurrentProfile.fileName.pathString

            _profileData.update { currentState ->
                currentState.copy(
                    currentProfile = Profile(
                        name = modpackName,
                        path = updatedCurrentProfile.absolutePathString(),
                        lastOpened = Clock.System.now()
                    )
                )
            }

            // Update Pakku's working path
            workingPath = updatedCurrentProfile.toString()
        }

        writeToDisk()

        ModpackViewModel.reset()
    }

    suspend fun updateTheme(updatedTheme: IntUiThemes)
    {
        loadFromDisk()

        _profileData.update { currentState ->
            currentState.copy(
                theme = updatedTheme.toString()
            )
        }

        writeToDisk()
    }

    // -- CLOSE DIALOG --

    fun updateCloseDialog(forceClose: Boolean = false, onClose: suspend () -> Unit)
    {
        _profileData.update { currentState ->
            currentState.copy(
                closeDialog = CloseDialogData(onClose, forceClose)
            )
        }
    }

    fun dismissCloseDialog()
    {
        _profileData.update { currentState ->
            currentState.copy(
                closeDialog = null
            )
        }
    }
}