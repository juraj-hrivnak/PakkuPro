/*
 * Copyright (c) Juraj Hrivnák. All Rights Reserved unless otherwise explicitly stated.
 */

package teksturepako.pakkuDesktop.app.ui.application.window

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jetbrains.jewel.intui.window.styling.light
import org.jetbrains.jewel.ui.component.painterResource
import org.jetbrains.jewel.window.DecoratedWindow
import org.jetbrains.jewel.window.styling.DecoratedWindowStyle
import teksturepako.pakkuDesktop.app.ui.application.PakkuApplicationScope
import teksturepako.pakkuDesktop.app.ui.application.appNameWithVersion
import teksturepako.pakkuDesktop.app.ui.application.theme.ThemedBox
import teksturepako.pakkuDesktop.app.ui.component.dialog.CloseDialog
import teksturepako.pakkuDesktop.app.ui.viewmodel.ModpackViewModel
import teksturepako.pakkuDesktop.app.ui.viewmodel.ProfileViewModel
import teksturepako.pakkuDesktop.app.ui.viewmodel.WindowViewModel
import java.awt.Dimension
import kotlin.system.exitProcess

@Composable
fun ApplicationScope.MainWindow(content: @Composable PakkuApplicationScope.() -> Unit)
{
    val profileData by ProfileViewModel.profileData.collectAsState()
    val windowData by WindowViewModel.windowData.collectAsState()
    val modpackUiState by ModpackViewModel.modpackUiState.collectAsState()

    CoroutineScope(Dispatchers.IO).launch {
        WindowViewModel.loadFromDisk()
    }

    val windowState = rememberWindowState(
        placement = windowData.placement,
        isMinimized = false,
        position = if (windowData.x != null && windowData.y != null)
        {
            WindowPosition.Absolute(x = windowData.x!!.dp, y = windowData.y!!.dp)
        }
        else WindowPosition(Alignment.Center),
        width = windowData.width.dp,
        height = windowData.height.dp
    )

    DecoratedWindow(
        state = windowState,
        onCloseRequest = {
            if (modpackUiState.action.first != null)
            {
                ProfileViewModel.updateCloseDialog(forceClose = true) {
                    runBlocking {
                        WindowViewModel.updateWindowData(windowState)

                        exitApplication()
                        exitProcess(0)
                    }
                }
            }
            else
            {
                runBlocking {
                    WindowViewModel.updateWindowData(windowState)

                    exitApplication()
                    exitProcess(0)
                }
            }
        },
        title = profileData.currentProfile?.name ?: appNameWithVersion,
        icon = painterResource("icons/pakku.svg"),
        style = DecoratedWindowStyle.light()
    ) {
        this.window.minimumSize = Dimension(600, 400)

        WindowViewModel.updateWindowScope(this)

        ThemedBox(Modifier.fillMaxSize()) {
            content(
                object : PakkuApplicationScope
                {
                    override val applicationScope = this@MainWindow
                    override val decoratedWindowScope = this@DecoratedWindow
                }
            )
        }

        CloseDialog()
    }

    WindowViewModel.applyInitialWindowPlacement(rememberCoroutineScope())
}
