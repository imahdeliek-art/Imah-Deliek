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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TransactionItem
import com.example.ui.theme.BorderPink
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.DarkCrimson
import com.example.ui.theme.GreenIncome
import com.example.ui.theme.SoftPink
import com.example.util.FormatUtils
import com.example.viewmodel.FinanceUiState

@Composable
fun RiwayatScreen(
  state: FinanceUiState,
  onChangeMonth: (Int) -> Unit,
  onResetMonth: () -> Unit,
  onDeleteTransaction: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  var searchQuery by remember { mutableStateOf("") }
  var selectedFilter by remember { mutableStateOf("Semua") } // Semua, Pengeluaran, Pemasukan, Tabungan
  var transactionToDelete by remember { mutableStateOf<TransactionItem?>(null) }

  // Filtering
  val filteredList = state.currentMonthTransactions.filter { item ->
    val matchesType = when (selectedFilter) {
      "Pengeluaran" -> item.jenis == "Pengeluaran"
      "Pemasukan" -> item.jenis == "Pemasukan"
      "Tabungan" -> item.jenis == "Tabungan"
      else -> true
    }
    val matchesSearch = if (searchQuery.isBlank()) true else {
      item.kategori.contains(searchQuery, ignoreCase = true) ||
        item.catatan.contains(searchQuery, ignoreCase = true) ||
        item.sumber.contains(searchQuery, ignoreCase = true) ||
        item.oleh.contains(searchQuery, ignoreCase = true)
    }
    matchesType && matchesSearch
  }

  LazyColumn(
    modifier = modifier.padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
    contentPadding = PaddingValues(top = 8.dp, bottom = 32.dp)
  ) {
    // 1. Month Picker
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderPink)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          IconButton(onClick = { onChangeMonth(-1) }) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Sebelumnya", tint = DarkCrimson)
          }
          Text(
            text = state.monthLabel,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = DarkCrimson,
            modifier = Modifier.clickable { onResetMonth() }
          )
          IconButton(onClick = { onChangeMonth(1) }) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Berikutnya", tint = DarkCrimson)
          }
        }
      }
    }

    // 2. Search & Filter Bar
    item {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("🔍 Cari transaksi, kategori...") },
          leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = DarkCrimson) },
          singleLine = true,
          modifier = Modifier.fillMaxWidth().testTag("search_history"),
          shape = RoundedCornerShape(14.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = CrimsonRed,
            unfocusedBorderColor = BorderPink,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
          )
        )

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          listOf("Semua", "Pengeluaran", "Pemasukan", "Tabungan").forEach { type ->
            val isSelected = selectedFilter == type
            val label = when (type) {
              "Pengeluaran" -> "🔴 Pengeluaran"
              "Pemasukan" -> "🟢 Pemasukan"
              "Tabungan" -> "🔵 Tabungan"
              else -> "Semua"
            }
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(if (isSelected) CrimsonRed else Color.White)
                .border(1.dp, if (isSelected) CrimsonRed else BorderPink, RoundedCornerShape(20.dp))
                .clickable { selectedFilter = type }
                .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
              Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }
    }

    // 3. Transactions List
    if (filteredList.isEmpty()) {
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = androidx.compose.foundation.BorderStroke(1.dp, BorderPink)
        ) {
          Column(
            modifier = Modifier.padding(32.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text("📭", fontSize = 36.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Tidak ada transaksi ditemukan", fontWeight = FontWeight.Bold, color = DarkCrimson)
          }
        }
      }
    } else {
      items(filteredList, key = { it.id }) { item ->
        val amountColor = when (item.jenis) {
          "Pemasukan" -> GreenIncome
          "Tabungan" -> Color(0xFF1E6091)
          else -> CrimsonRed
        }
        val prefix = when (item.jenis) {
          "Pemasukan" -> "+"
          "Tabungan" -> ""
          else -> "-"
        }
        val iconEmoji = when (item.jenis) {
          "Pemasukan" -> "💰"
          "Tabungan" -> "🏦"
          else -> "💸"
        }

        Card(
          modifier = Modifier.fillMaxWidth().testTag("tx_item_${item.id}"),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = androidx.compose.foundation.BorderStroke(1.dp, BorderPink)
        ) {
          Row(
            modifier = Modifier.padding(14.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp),
              modifier = Modifier.weight(1f)
            ) {
              Box(
                modifier = Modifier
                  .size(40.dp)
                  .clip(CircleShape)
                  .background(SoftPink),
                contentAlignment = Alignment.Center
              ) {
                Text(iconEmoji, fontSize = 18.sp)
              }

              Column {
                Text(
                  text = item.kategori,
                  style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                  horizontalArrangement = Arrangement.spacedBy(6.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = FormatUtils.formatShortDate(item.tanggal),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                  Box(
                    modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(SoftPink).padding(horizontal = 6.dp, vertical = 1.dp)
                  ) {
                    Text(item.sumber, fontSize = 10.sp, color = DarkCrimson, fontWeight = FontWeight.Medium)
                  }
                  Box(
                    modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(SoftPink).padding(horizontal = 6.dp, vertical = 1.dp)
                  ) {
                    Text("Oleh ${item.oleh}", fontSize = 10.sp, color = DarkCrimson, fontWeight = FontWeight.Medium)
                  }
                }
                if (item.catatan.isNotBlank()) {
                  Text(
                    text = "\"${item.catatan}\"",
                    fontSize = 11.sp,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Text(
                text = "$prefix${FormatUtils.formatRupiah(item.nominal)}",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = amountColor
              )
              IconButton(onClick = { transactionToDelete = item }) {
                Icon(
                  imageVector = Icons.Default.Delete,
                  contentDescription = "Hapus",
                  tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                  modifier = Modifier.size(20.dp)
                )
              }
            }
          }
        }
      }
    }
  }

  // Delete Confirmation Dialog
  if (transactionToDelete != null) {
    val tx = transactionToDelete!!
    AlertDialog(
      onDismissRequest = { transactionToDelete = null },
      title = { Text("Hapus Transaksi", color = CrimsonRed, fontWeight = FontWeight.Bold) },
      text = {
        Text("Apakah Anda yakin ingin menghapus transaksi \"${tx.kategori}\" senilai ${FormatUtils.formatRupiah(tx.nominal)}?")
      },
      confirmButton = {
        TextButton(
          onClick = {
            onDeleteTransaction(tx.id)
            transactionToDelete = null
          }
        ) {
          Text("Hapus", color = CrimsonRed, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { transactionToDelete = null }) {
          Text("Batal")
        }
      }
    )
  }
}
