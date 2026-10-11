package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BorderPink
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.DarkCrimson
import com.example.ui.theme.GreenIncome
import com.example.ui.theme.SoftPink
import com.example.util.FormatUtils
import com.example.viewmodel.FinanceUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatatScreen(
  state: FinanceUiState,
  initialType: String = "Pengeluaran",
  onSave: (tanggal: String, jenis: String, kategori: String, nominal: Long, sumber: String, oleh: String, catatan: String) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedType by remember(initialType) { mutableStateOf(initialType) }
  var nominalText by remember { mutableStateOf("") }
  var tanggalText by remember { mutableStateOf(FormatUtils.getTodayDateString()) }
  var selectedSource by remember { mutableStateOf(state.sources.firstOrNull() ?: "BSI") }
  var selectedRecorder by remember { mutableStateOf(state.lastRecorder) }
  var notesText by remember { mutableStateOf("") }

  // Filter categories by selected type
  val filteredCategories = state.categories.filter { it.jenis == selectedType }
  var selectedCategoryName by remember(selectedType, filteredCategories) {
    mutableStateOf(filteredCategories.firstOrNull()?.nama ?: "")
  }

  var categoryDropdownExpanded by remember { mutableStateOf(false) }
  var sourceDropdownExpanded by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = modifier.padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    contentPadding = PaddingValues(top = 8.dp, bottom = 32.dp)
  ) {
    item {
      Card(
        modifier = Modifier.fillMaxWidth().testTag("catat_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderPink)
      ) {
        Column(modifier = Modifier.padding(20.dp)) {
          Text(
            text = "Catat Transaksi",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = DarkCrimson
          )

          Spacer(modifier = Modifier.height(16.dp))

          // 1. Type Switcher
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            TypeTabButton(
              label = "🔴 Keluar",
              selected = selectedType == "Pengeluaran",
              activeColor = CrimsonRed,
              modifier = Modifier.weight(1f).testTag("btn_type_keluar")
            ) { selectedType = "Pengeluaran" }

            TypeTabButton(
              label = "🟢 Masuk",
              selected = selectedType == "Pemasukan",
              activeColor = GreenIncome,
              modifier = Modifier.weight(1f).testTag("btn_type_masuk")
            ) { selectedType = "Pemasukan" }

            TypeTabButton(
              label = "🔵 Tabungan",
              selected = selectedType == "Tabungan",
              activeColor = Color(0xFF1E6091),
              modifier = Modifier.weight(1f).testTag("btn_type_tabungan")
            ) { selectedType = "Tabungan" }
          }

          Spacer(modifier = Modifier.height(20.dp))

          // 2. Big Nominal Input
          Text("Nominal (Rupiah)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DarkCrimson)
          Spacer(modifier = Modifier.height(6.dp))
          OutlinedTextField(
            value = nominalText,
            onValueChange = { input ->
              val clean = input.filter { it.isDigit() }
              nominalText = clean
            },
            prefix = {
              Text("Rp ", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = DarkCrimson)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, color = DarkCrimson),
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("input_nominal"),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = CrimsonRed,
              unfocusedBorderColor = BorderPink
            )
          )

          // Quick Chips (+10rb, +20rb, +50rb, +100rb, +250rb, +500rb, +1jt)
          Spacer(modifier = Modifier.height(8.dp))
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            listOf(10000L, 20000L, 50000L, 100000L, 250000L, 500000L, 1000000L).forEach { addVal ->
              val label = when (addVal) {
                10000L -> "+10 rb"
                20000L -> "+20 rb"
                50000L -> "+50 rb"
                100000L -> "+100 rb"
                250000L -> "+250 rb"
                50000L -> "+500 rb"
                else -> "+1 jt"
              }
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(20.dp))
                  .background(SoftPink)
                  .border(1.dp, BorderPink, RoundedCornerShape(20.dp))
                  .clickable {
                    val cur = nominalText.toLongOrNull() ?: 0L
                    nominalText = (cur + addVal).toString()
                  }
                  .padding(horizontal = 12.dp, vertical = 6.dp)
              ) {
                Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = DarkCrimson)
              }
            }
          }

          Spacer(modifier = Modifier.height(18.dp))

          // 3. Category Selector
          Text("Kategori", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DarkCrimson)
          Spacer(modifier = Modifier.height(6.dp))
          ExposedDropdownMenuBox(
            expanded = categoryDropdownExpanded,
            onExpandedChange = { categoryDropdownExpanded = !categoryDropdownExpanded }
          ) {
            OutlinedTextField(
              value = selectedCategoryName.ifBlank { "Pilih Kategori" },
              onValueChange = {},
              readOnly = true,
              trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
              modifier = Modifier.menuAnchor().fillMaxWidth().testTag("select_kategori"),
              shape = RoundedCornerShape(14.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CrimsonRed,
                unfocusedBorderColor = BorderPink
              )
            )
            ExposedDropdownMenu(
              expanded = categoryDropdownExpanded,
              onDismissRequest = { categoryDropdownExpanded = false }
            ) {
              filteredCategories.forEach { cat ->
                DropdownMenuItem(
                  text = { Text("${cat.nama} (${cat.kelompok})") },
                  onClick = {
                    selectedCategoryName = cat.nama
                    categoryDropdownExpanded = false
                  }
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(18.dp))

          // 4. Tanggal
          Text("Tanggal (YYYY-MM-DD)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DarkCrimson)
          Spacer(modifier = Modifier.height(6.dp))
          OutlinedTextField(
            value = tanggalText,
            onValueChange = { tanggalText = it },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("input_tanggal"),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = CrimsonRed,
              unfocusedBorderColor = BorderPink
            )
          )

          Spacer(modifier = Modifier.height(18.dp))

          // 5. Sumber Dana
          Text("Sumber Dana", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DarkCrimson)
          Spacer(modifier = Modifier.height(6.dp))
          ExposedDropdownMenuBox(
            expanded = sourceDropdownExpanded,
            onExpandedChange = { sourceDropdownExpanded = !sourceDropdownExpanded }
          ) {
            OutlinedTextField(
              value = selectedSource,
              onValueChange = {},
              readOnly = true,
              trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sourceDropdownExpanded) },
              modifier = Modifier.menuAnchor().fillMaxWidth().testTag("select_sumber"),
              shape = RoundedCornerShape(14.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CrimsonRed,
                unfocusedBorderColor = BorderPink
              )
            )
            ExposedDropdownMenu(
              expanded = sourceDropdownExpanded,
              onDismissRequest = { sourceDropdownExpanded = false }
            ) {
              state.sources.forEach { src ->
                DropdownMenuItem(
                  text = { Text(src) },
                  onClick = {
                    selectedSource = src
                    sourceDropdownExpanded = false
                  }
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(18.dp))

          // 6. Dicatat Oleh (Radio Segments)
          Text("Dicatat Oleh", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DarkCrimson)
          Spacer(modifier = Modifier.height(6.dp))
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            state.recorders.forEach { rec ->
              val isRecSelected = selectedRecorder == rec
              val emoji = if (rec.contains("Suami", ignoreCase = true)) "👨 " else if (rec.contains("Istri", ignoreCase = true)) "👩 " else "👤 "
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(12.dp))
                  .background(if (isRecSelected) SoftPink else Color.White)
                  .border(1.5.dp, if (isRecSelected) CrimsonRed else BorderPink, RoundedCornerShape(12.dp))
                  .clickable { selectedRecorder = rec }
                  .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  "$emoji$rec",
                  fontWeight = if (isRecSelected) FontWeight.Bold else FontWeight.Medium,
                  color = if (isRecSelected) DarkCrimson else MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(18.dp))

          // 7. Catatan Opsional
          Text("Catatan (Opsional)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DarkCrimson)
          Spacer(modifier = Modifier.height(6.dp))
          OutlinedTextField(
            value = notesText,
            onValueChange = { notesText = it },
            placeholder = { Text("Contoh: Belanja mingguan pasar") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("input_catatan"),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = CrimsonRed,
              unfocusedBorderColor = BorderPink
            )
          )

          Spacer(modifier = Modifier.height(24.dp))

          // Submit Button
          val nominalVal = nominalText.toLongOrNull() ?: 0L
          Button(
            onClick = {
              if (nominalVal > 0 && selectedCategoryName.isNotBlank()) {
                onSave(
                  tanggalText.ifBlank { FormatUtils.getTodayDateString() },
                  selectedType,
                  selectedCategoryName,
                  nominalVal,
                  selectedSource,
                  selectedRecorder,
                  notesText
                )
                nominalText = ""
                notesText = ""
              }
            },
            enabled = nominalVal > 0 && selectedCategoryName.isNotBlank(),
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .testTag("btn_simpan_transaksi"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = CrimsonRed,
              disabledContainerColor = CrimsonRed.copy(alpha = 0.5f)
            )
          ) {
            Text("💾 Simpan Transaksi", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
          }
        }
      }
    }
  }
}

@Composable
private fun TypeTabButton(
  label: String,
  selected: Boolean,
  activeColor: Color,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .background(if (selected) activeColor else Color.White)
      .border(1.dp, if (selected) activeColor else BorderPink, RoundedCornerShape(12.dp))
      .clickable { onClick() }
      .padding(vertical = 12.dp),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = label,
      fontWeight = FontWeight.Bold,
      fontSize = 12.sp,
      color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
    )
  }
}
