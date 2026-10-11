package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CategoryItem
import com.example.ui.theme.BorderPink
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.DarkCrimson
import com.example.ui.theme.SoftPink
import com.example.util.FormatUtils
import com.example.viewmodel.FinanceUiState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PengaturanScreen(
  state: FinanceUiState,
  onResetDefaultCategories: () -> Unit,
  onAddCategory: (nama: String, jenis: String, kelompok: String, anggaran: Long) -> Unit,
  onUpdateCategory: (id: String, kelompok: String, anggaran: Long) -> Unit,
  onDeleteCategory: (id: String) -> Unit,
  onAddSource: (String) -> Unit,
  onDeleteSource: (String) -> Unit,
  onAddRecorder: (String) -> Unit,
  onDeleteRecorder: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var activeCatTab by remember { mutableStateOf("Pengeluaran") } // Pengeluaran, Pemasukan, Tabungan
  var showAddCategoryDialog by remember { mutableStateOf(false) }
  var categoryToEdit by remember { mutableStateOf<CategoryItem?>(null) }
  var categoryToDelete by remember { mutableStateOf<CategoryItem?>(null) }

  var newSourceText by remember { mutableStateOf("") }
  var newRecorderText by remember { mutableStateOf("") }

  LazyColumn(
    modifier = modifier.padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    contentPadding = PaddingValues(top = 8.dp, bottom = 32.dp)
  ) {
    // 1. Initial Data Setup
    item {
      Card(
        modifier = Modifier.fillMaxWidth().testTag("init_data_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderPink)
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Text("Inisialisasi Data Awal", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = DarkCrimson)
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            "Muat 25 kategori standar keluarga (Gaji, Belanja masakan, Dapur, Kendaraan, Pendidikan, Tabungan, dll.) beserta target anggaran per bulannya.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(12.dp))
          Button(
            onClick = onResetDefaultCategories,
            modifier = Modifier.fillMaxWidth().testTag("btn_reset_categories"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed)
          ) {
            Text("✨ Isi Kategori & Pengaturan Awal", fontWeight = FontWeight.Bold, color = Color.White)
          }
        }
      }
    }

    // 2. Manage Categories
    item {
      Card(
        modifier = Modifier.fillMaxWidth().testTag("manage_categories_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderPink)
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("Kelola Kategori", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = DarkCrimson)
            OutlinedButton(
              onClick = { showAddCategoryDialog = true },
              shape = RoundedCornerShape(10.dp),
              contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonRed)
            ) {
              Text("+ Tambah", fontSize = 12.sp, color = CrimsonRed, fontWeight = FontWeight.Bold)
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(SoftPink)
              .padding(8.dp)
          ) {
            Text(
              "⚠️ Nama kategori tidak dapat diubah setelah dibuat. Untuk mengganti nama, hapus lalu buat baru.",
              fontSize = 11.sp,
              color = DarkCrimson
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Tab jenis kategori
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("Pengeluaran", "Pemasukan", "Tabungan").forEach { tab ->
              val isSelected = activeCatTab == tab
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(8.dp))
                  .background(if (isSelected) SoftPink else Color.White)
                  .border(1.dp, if (isSelected) CrimsonRed else BorderPink, RoundedCornerShape(8.dp))
                  .clickable { activeCatTab = tab }
                  .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  tab,
                  fontSize = 12.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                  color = if (isSelected) DarkCrimson else MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          val filtered = state.categories.filter { it.jenis == activeCatTab }
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            filtered.forEach { cat ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(10.dp))
                  .background(SoftPink)
                  .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(cat.nama, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                  Text(
                    "Kelompok: ${cat.kelompok} • Anggaran: ${FormatUtils.formatRupiah(cat.anggaran)}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                  IconButton(onClick = { categoryToEdit = cat }) {
                    Icon(Icons.Default.Edit, contentDescription = "Ubah", tint = DarkCrimson, modifier = Modifier.size(18.dp))
                  }
                  IconButton(onClick = { categoryToDelete = cat }) {
                    Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = CrimsonRed, modifier = Modifier.size(18.dp))
                  }
                }
              }
            }
          }
        }
      }
    }

    // 3. Manage Sources
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderPink)
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Text("Sumber Dana", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = DarkCrimson)
          Spacer(modifier = Modifier.height(8.dp))

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
              value = newSourceText,
              onValueChange = { newSourceText = it },
              placeholder = { Text("Tambah sumber (misal: Gopay)") },
              singleLine = true,
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(12.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CrimsonRed,
                unfocusedBorderColor = BorderPink
              )
            )
            Button(
              onClick = {
                if (newSourceText.isNotBlank()) {
                  onAddSource(newSourceText.trim())
                  newSourceText = ""
                }
              },
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed)
            ) {
              Text("Tambah")
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            state.sources.forEach { src ->
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(20.dp))
                  .background(SoftPink)
                  .border(1.dp, BorderPink, RoundedCornerShape(20.dp))
                  .padding(start = 12.dp, end = 6.dp, top = 4.dp, bottom = 4.dp)
              ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                  Text(src, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DarkCrimson)
                  IconButton(
                    onClick = { onDeleteSource(src) },
                    modifier = Modifier.size(24.dp)
                  ) {
                    Icon(Icons.Default.Close, contentDescription = "Hapus", modifier = Modifier.size(14.dp), tint = DarkCrimson)
                  }
                }
              }
            }
          }
        }
      }
    }

    // 4. Manage Recorders
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderPink)
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Text("Nama Pencatat", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = DarkCrimson)
          Spacer(modifier = Modifier.height(8.dp))

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
              value = newRecorderText,
              onValueChange = { newRecorderText = it },
              placeholder = { Text("Nama pencatat (misal: Suami)") },
              singleLine = true,
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(12.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CrimsonRed,
                unfocusedBorderColor = BorderPink
              )
            )
            Button(
              onClick = {
                if (newRecorderText.isNotBlank()) {
                  onAddRecorder(newRecorderText.trim())
                  newRecorderText = ""
                }
              },
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed)
            ) {
              Text("Tambah")
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            state.recorders.forEach { rec ->
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(20.dp))
                  .background(SoftPink)
                  .border(1.dp, BorderPink, RoundedCornerShape(20.dp))
                  .padding(start = 12.dp, end = 6.dp, top = 4.dp, bottom = 4.dp)
              ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                  Text(rec, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DarkCrimson)
                  IconButton(
                    onClick = { onDeleteRecorder(rec) },
                    modifier = Modifier.size(24.dp)
                  ) {
                    Icon(Icons.Default.Close, contentDescription = "Hapus", modifier = Modifier.size(14.dp), tint = DarkCrimson)
                  }
                }
              }
            }
          }
        }
      }
    }

    // 5. Export CSV
    item {
      Card(
        modifier = Modifier.fillMaxWidth().testTag("export_csv_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderPink)
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Text("Cadangan & Ekspor", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = DarkCrimson)
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            "Bagikan atau simpan seluruh transaksi keluarga ke dalam format file spreadsheet Excel (.csv).",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(12.dp))
          OutlinedButton(
            onClick = {
              val csvData = FormatUtils.generateCsv(state.allTransactions)
              val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, csvData)
                type = "text/csv"
              }
              val shareIntent = Intent.createChooser(sendIntent, "Ekspor Data Transaksi Kas Keluarga")
              context.startActivity(shareIntent)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderPink)
          ) {
            Text("📥 Ekspor Seluruh Transaksi ke CSV", fontWeight = FontWeight.Bold, color = DarkCrimson)
          }
        }
      }
    }
  }

  // Dialog Add Category
  if (showAddCategoryDialog) {
    var newCatName by remember { mutableStateOf("") }
    var newCatGroup by remember { mutableStateOf("Kebutuhan") }
    var newCatBudget by remember { mutableStateOf("0") }

    AlertDialog(
      onDismissRequest = { showAddCategoryDialog = false },
      title = { Text("Tambah Kategori Baru", fontWeight = FontWeight.Bold, color = DarkCrimson) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = newCatName,
            onValueChange = { newCatName = it },
            label = { Text("Nama Kategori") },
            singleLine = true
          )
          OutlinedTextField(
            value = newCatGroup,
            onValueChange = { newCatGroup = it },
            label = { Text("Kelompok (misal: Kebutuhan, Keinginan)") },
            singleLine = true
          )
          OutlinedTextField(
            value = newCatBudget,
            onValueChange = { newCatBudget = it.filter { ch -> ch.isDigit() } },
            label = { Text("Anggaran Bulanan (Rp)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (newCatName.isNotBlank()) {
              onAddCategory(newCatName.trim(), activeCatTab, newCatGroup.trim(), newCatBudget.toLongOrNull() ?: 0L)
              showAddCategoryDialog = false
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed)
        ) {
          Text("Simpan")
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddCategoryDialog = false }) { Text("Batal") }
      }
    )
  }

  // Dialog Edit Category
  if (categoryToEdit != null) {
    val cat = categoryToEdit!!
    var editGroup by remember { mutableStateOf(cat.kelompok) }
    var editBudget by remember { mutableStateOf(cat.anggaran.toString()) }

    AlertDialog(
      onDismissRequest = { categoryToEdit = null },
      title = { Text("Ubah ${cat.nama}", fontWeight = FontWeight.Bold, color = DarkCrimson) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = editGroup,
            onValueChange = { editGroup = it },
            label = { Text("Kelompok") },
            singleLine = true
          )
          OutlinedTextField(
            value = editBudget,
            onValueChange = { editBudget = it.filter { ch -> ch.isDigit() } },
            label = { Text("Anggaran Bulanan (Rp)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            onUpdateCategory(cat.id, editGroup.trim(), editBudget.toLongOrNull() ?: 0L)
            categoryToEdit = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed)
        ) {
          Text("Perbarui")
        }
      },
      dismissButton = {
        TextButton(onClick = { categoryToEdit = null }) { Text("Batal") }
      }
    )
  }

  // Dialog Delete Category
  if (categoryToDelete != null) {
    val cat = categoryToDelete!!
    AlertDialog(
      onDismissRequest = { categoryToDelete = null },
      title = { Text("Hapus Kategori", fontWeight = FontWeight.Bold, color = CrimsonRed) },
      text = { Text("Hapus kategori \"${cat.nama}\"? Transaksi lama dengan kategori ini tetap tersimpan.") },
      confirmButton = {
        TextButton(
          onClick = {
            onDeleteCategory(cat.id)
            categoryToDelete = null
          }
        ) {
          Text("Hapus", color = CrimsonRed, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { categoryToDelete = null }) { Text("Batal") }
      }
    )
  }
}
