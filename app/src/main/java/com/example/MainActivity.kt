package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.CatatScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.PengaturanScreen
import com.example.ui.screens.RiwayatScreen
import com.example.ui.theme.BorderPink
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.DarkCrimson
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SoftPink
import com.example.viewmodel.FinanceViewModel

class MainActivity : ComponentActivity() {
  private val viewModel: FinanceViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    // Handle initial deep link shortcut if launched from widget
    val initialType = intent?.data?.let { uri ->
      if (uri.host == "catat") {
        viewModel.setActiveTab(1)
        uri.getQueryParameter("type") ?: "Pengeluaran"
      } else "Pengeluaran"
    } ?: "Pengeluaran"

    setContent {
      MyApplicationTheme {
        MainAppContent(viewModel = viewModel, initialCatatType = initialType)
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContent(
  viewModel: FinanceViewModel,
  initialCatatType: String
) {
  val uiState by viewModel.uiState.collectAsState()
  val snackbarHostState = remember { SnackbarHostState() }
  var catatSelectedType by remember { mutableStateOf(initialCatatType) }

  // Show toast if message is set
  LaunchedEffect(uiState.toastMessage) {
    uiState.toastMessage?.let { msg ->
      snackbarHostState.showSnackbar(msg)
      viewModel.clearToast()
    }
  }

  Scaffold(
    modifier = Modifier.fillMaxSize().testTag("main_scaffold"),
    containerColor = Color(0xFFFFFAFA),
    topBar = {
      TopAppBar(
        title = {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Box(
              modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(CrimsonRed),
              contentAlignment = Alignment.Center
            ) {
              Text("💰", fontSize = 18.sp)
            }
            Text(
              text = "Kas Keluarga",
              fontWeight = FontWeight.Bold,
              color = DarkCrimson,
              fontSize = 19.sp
            )
          }
        },
        actions = {
          Box(
            modifier = Modifier
              .padding(end = 12.dp)
              .clip(RoundedCornerShape(20.dp))
              .background(Color(0xFFE6F4EA))
              .padding(horizontal = 10.dp, vertical = 4.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .clip(CircleShape)
                  .background(Color(0xFF137333))
              )
              Text("Tersambung", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF137333))
            }
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = Color(0xFFFFFAFA)
        )
      )
    },
    bottomBar = {
      NavigationBar(
        containerColor = Color.White,
        tonalElevation = 6.dp,
        modifier = Modifier.testTag("bottom_nav_bar")
      ) {
        NavigationBarItem(
          icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
          label = { Text("Dashboard", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
          selected = uiState.activeTab == 0,
          onClick = { viewModel.setActiveTab(0) },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = CrimsonRed,
            selectedTextColor = CrimsonRed,
            indicatorColor = SoftPink
          ),
          modifier = Modifier.testTag("nav_dashboard")
        )
        NavigationBarItem(
          icon = { Icon(Icons.Default.AddCircle, contentDescription = "Catat") },
          label = { Text("Catat", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
          selected = uiState.activeTab == 1,
          onClick = { viewModel.setActiveTab(1) },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = CrimsonRed,
            selectedTextColor = CrimsonRed,
            indicatorColor = SoftPink
          ),
          modifier = Modifier.testTag("nav_catat")
        )
        NavigationBarItem(
          icon = { Icon(Icons.Default.ReceiptLong, contentDescription = "Riwayat") },
          label = { Text("Riwayat", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
          selected = uiState.activeTab == 2,
          onClick = { viewModel.setActiveTab(2) },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = CrimsonRed,
            selectedTextColor = CrimsonRed,
            indicatorColor = SoftPink
          ),
          modifier = Modifier.testTag("nav_riwayat")
        )
        NavigationBarItem(
          icon = { Icon(Icons.Default.Settings, contentDescription = "Pengaturan") },
          label = { Text("Pengaturan", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
          selected = uiState.activeTab == 3,
          onClick = { viewModel.setActiveTab(3) },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = CrimsonRed,
            selectedTextColor = CrimsonRed,
            indicatorColor = SoftPink
          ),
          modifier = Modifier.testTag("nav_pengaturan")
        )
      }
    },
    snackbarHost = { SnackbarHost(snackbarHostState) }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      when (uiState.activeTab) {
        0 -> DashboardScreen(
          state = uiState,
          onChangeMonth = { delta -> viewModel.changeMonth(delta) },
          onResetMonth = { viewModel.resetToCurrentMonth() },
          onQuickRecord = { type ->
            catatSelectedType = type
            viewModel.setActiveTab(1)
          }
        )
        1 -> CatatScreen(
          state = uiState,
          initialType = catatSelectedType,
          onSave = { tgl, jns, kat, nom, src, by, note ->
            viewModel.addTransaction(tgl, jns, kat, nom, src, by, note)
            viewModel.setActiveTab(0)
          }
        )
        2 -> RiwayatScreen(
          state = uiState,
          onChangeMonth = { delta -> viewModel.changeMonth(delta) },
          onResetMonth = { viewModel.resetToCurrentMonth() },
          onDeleteTransaction = { id -> viewModel.deleteTransaction(id) }
        )
        3 -> PengaturanScreen(
          state = uiState,
          onResetDefaultCategories = { viewModel.resetToDefaultCategories() },
          onAddCategory = { nm, jn, grp, ang -> viewModel.addCategory(nm, jn, grp, ang) },
          onUpdateCategory = { id, grp, ang -> viewModel.updateCategory(id, grp, ang) },
          onDeleteCategory = { id -> viewModel.deleteCategory(id) },
          onAddSource = { src -> viewModel.addSource(src) },
          onDeleteSource = { src -> viewModel.deleteSource(src) },
          onAddRecorder = { rec -> viewModel.addRecorder(rec) },
          onDeleteRecorder = { rec -> viewModel.deleteRecorder(rec) }
        )
      }
    }
  }
}
