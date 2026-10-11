package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.DONUT_PALETTE
import com.example.ui.components.DonutChartCard
import com.example.ui.components.DonutSlice
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.BorderPink
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.DarkCrimson
import com.example.ui.theme.GreenIncome
import com.example.ui.theme.SoftPink
import com.example.util.FormatUtils
import com.example.viewmodel.FinanceUiState

@Composable
fun DashboardScreen(
  state: FinanceUiState,
  onChangeMonth: (Int) -> Unit,
  onResetMonth: () -> Unit,
  onQuickRecord: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  LazyColumn(
    modifier = modifier.padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
  ) {
    // 1. Month Picker Bar
    item {
      Card(
        modifier = Modifier.fillMaxWidth().testTag("month_picker_card"),
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
          IconButton(
            onClick = { onChangeMonth(-1) },
            modifier = Modifier.testTag("btn_prev_month")
          ) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Bulan Sebelumnya", tint = DarkCrimson)
          }

          Text(
            text = state.monthLabel,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = DarkCrimson,
            modifier = Modifier
              .clickable { onResetMonth() }
              .testTag("label_month")
          )

          IconButton(
            onClick = { onChangeMonth(1) },
            modifier = Modifier.testTag("btn_next_month")
          ) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Bulan Berikutnya", tint = DarkCrimson)
          }
        }
      }
    }

    // 2. Quick Record Widget
    item {
      Card(
        modifier = Modifier.fillMaxWidth().testTag("quick_record_widget"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDFD)),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderPink)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "⚡ Pintasan Cepat Mencatat",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = DarkCrimson
            )
            Text(
              text = "1-Ketukan",
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
          Spacer(modifier = Modifier.height(10.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            QuickRecordButton(
              label = "+ Keluar",
              emoji = "🔴",
              textColor = CrimsonRed,
              bgColor = Color(0xFFFFF5F5),
              borderColor = Color(0xFFFCA5A5),
              modifier = Modifier.weight(1f).testTag("quick_expense_btn"),
              onClick = { onQuickRecord("Pengeluaran") }
            )
            QuickRecordButton(
              label = "+ Masuk",
              emoji = "🟢",
              textColor = GreenIncome,
              bgColor = Color(0xFFF0FDF4),
              borderColor = Color(0xFF86EFAC),
              modifier = Modifier.weight(1f).testTag("quick_income_btn"),
              onClick = { onQuickRecord("Pemasukan") }
            )
            QuickRecordButton(
              label = "+ Tabungan",
              emoji = "🔵",
              textColor = Color(0xFF1D4ED8),
              bgColor = Color(0xFFEFF6FF),
              borderColor = Color(0xFF93C5FD),
              modifier = Modifier.weight(1f).testTag("quick_savings_btn"),
              onClick = { onQuickRecord("Tabungan") }
            )
          }
        }
      }
    }

    // 3. Hero Red Card
    item {
      val isDeficit = state.sisaBulanIni < 0
      Card(
        modifier = Modifier.fillMaxWidth().testTag("hero_red_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(
              Brush.linearGradient(
                colors = listOf(CrimsonRed, DarkCrimson)
              )
            )
            .padding(22.dp)
        ) {
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = if (isDeficit) "DEFISIT BULAN INI" else "SISA BULAN INI",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.9f),
                letterSpacing = 0.5.sp
              )
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(12.dp))
                  .background(if (isDeficit) Color(0xFFFFCDD2) else Color.White.copy(alpha = 0.22f))
                  .padding(horizontal = 10.dp, vertical = 3.dp)
              ) {
                Text(
                  text = if (isDeficit) "DEFISIT" else "SURPLUS",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isDeficit) Color(0xFFB71C1C) else Color.White
                )
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
              text = FormatUtils.formatRupiah(state.sisaBulanIni),
              fontSize = 28.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White,
              modifier = Modifier.testTag("hero_balance_text")
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 3-Column Summary
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .border(
                  width = 0.dp,
                  color = Color.Transparent
                )
                .padding(top = 4.dp),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              MetricCol(label = "Pemasukan", value = FormatUtils.formatRupiah(state.totalPemasukan), color = Color(0xFFD1FAE5))
              MetricCol(label = "Pengeluaran", value = FormatUtils.formatRupiah(state.totalPengeluaran), color = Color(0xFFFECDD3))
              MetricCol(label = "Ditabung", value = FormatUtils.formatRupiah(state.totalTabungan), color = Color(0xFFDBEAFE))
            }

            if (state.totalMutasiSaldo > 0) {
              Spacer(modifier = Modifier.height(12.dp))
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(8.dp))
                  .background(Color.Black.copy(alpha = 0.15f))
                  .padding(horizontal = 10.dp, vertical = 6.dp)
              ) {
                Text(
                  text = "ℹ️ Mutasi Saldo: ${FormatUtils.formatRupiah(state.totalMutasiSaldo)} (tidak dihitung sebagai pemasukan)",
                  fontSize = 11.sp,
                  color = Color.White.copy(alpha = 0.95f)
                )
              }
            }
          }
        }
      }
    }

    // 4. Monthly Plan Card
    item {
      val isPlanDeficit = state.plannedBalance < 0
      Card(
        modifier = Modifier.fillMaxWidth().testTag("plan_card"),
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
            Text(
              text = "Rencana Bulanan",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
              color = DarkCrimson
            )
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(if (isPlanDeficit) Color(0xFFFFCDD2) else SoftPink)
                .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
              Text(
                text = if (isPlanDeficit) "Peringatan: Rencana Defisit" else "Rencana Seimbang",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isPlanDeficit) Color(0xFFB71C1C) else DarkCrimson
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PlanItemBox("Pemasukan Tetap", FormatUtils.formatRupiah(state.plannedIncomeBudget), Modifier.weight(1f))
            PlanItemBox("Anggaran Belanja", FormatUtils.formatRupiah(state.plannedExpenseBudget), Modifier.weight(1f))
          }
          Spacer(modifier = Modifier.height(8.dp))
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PlanItemBox("Target Tabungan", FormatUtils.formatRupiah(state.plannedSavingsBudget), Modifier.weight(1f))
            PlanItemBox(
              "Sisa Rencana",
              FormatUtils.formatRupiah(state.plannedBalance),
              Modifier.weight(1f),
              highlightColor = if (isPlanDeficit) CrimsonRed else GreenIncome
            )
          }
        }
      }
    }

    // 5. 6 Donut Charts Section
    item {
      Text(
        text = "Diagram Keuangan Bulan Ini",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = DarkCrimson
      )
    }

    // Chart 1: Anggaran Terpakai
    item {
      val spent = state.totalPengeluaran
      val totalBudget = state.plannedExpenseBudget
      val pct = if (totalBudget > 0) ((spent.toDouble() / totalBudget.toDouble()) * 100).toInt() else 0
      val remaining = maxOf(0L, totalBudget - spent)

      val slices = listOf(
        DonutSlice("Terpakai", spent, if (pct > 100) DarkCrimson else CrimsonRed),
        DonutSlice("Sisa Anggaran", remaining, Color(0xFFF3D4D8))
      )
      DonutChartCard(
        title = "Anggaran Terpakai",
        centerSub = "Terpakai",
        centerVal = "$pct%",
        slices = slices
      )
    }

    // Chart 2: Alokasi Pemasukan
    item {
      val sisaPositif = maxOf(0L, state.sisaBulanIni)
      val slices = listOf(
        DonutSlice("Pengeluaran", state.totalPengeluaran, CrimsonRed),
        DonutSlice("Ditabung", state.totalTabungan, Color(0xFF1E6091)),
        DonutSlice("Sisa Pemasukan", sisaPositif, GreenIncome)
      )
      DonutChartCard(
        title = "Alokasi Pemasukan",
        centerSub = "Pemasukan",
        centerVal = FormatUtils.formatRupiah(state.totalPemasukan),
        slices = slices
      )
    }

    // Chart 3: Sumber Pemasukan
    item {
      val incomeTx = state.currentMonthTransactions.filter { it.jenis == "Pemasukan" && !state.isPemindahan(it.kategori) }
      val groupMap = incomeTx.groupBy { it.kategori }.mapValues { entry -> entry.value.sumOf { it.nominal } }
      val slices = groupMap.entries.sortedByDescending { it.value }.mapIndexed { idx, (kat, sum) ->
        DonutSlice(kat, sum, DONUT_PALETTE[idx % DONUT_PALETTE.size])
      }
      DonutChartCard(
        title = "Sumber Pemasukan",
        centerSub = "Pemasukan",
        centerVal = FormatUtils.formatRupiah(state.totalPemasukan),
        slices = slices
      )
    }

    // Chart 4: Pengeluaran per Kelompok
    item {
      val expenseTx = state.currentMonthTransactions.filter { it.jenis == "Pengeluaran" }
      val groupMap = expenseTx.groupBy { tx ->
        val catObj = state.categories.find { it.nama.equals(tx.kategori, ignoreCase = true) }
        catObj?.kelompok ?: "Kebutuhan"
      }.mapValues { entry -> entry.value.sumOf { it.nominal } }

      val slices = groupMap.entries.sortedByDescending { it.value }.mapIndexed { idx, (grp, sum) ->
        DonutSlice(grp, sum, DONUT_PALETTE[idx % DONUT_PALETTE.size])
      }
      DonutChartCard(
        title = "Pengeluaran per Kelompok",
        centerSub = "Kelompok",
        centerVal = FormatUtils.formatRupiah(state.totalPengeluaran),
        slices = slices
      )
    }

    // Chart 5: Pengeluaran per Kategori
    item {
      val expenseTx = state.currentMonthTransactions.filter { it.jenis == "Pengeluaran" }
      val catMap = expenseTx.groupBy { it.kategori }.mapValues { entry -> entry.value.sumOf { it.nominal } }
      val slices = catMap.entries.sortedByDescending { it.value }.mapIndexed { idx, (kat, sum) ->
        DonutSlice(kat, sum, DONUT_PALETTE[idx % DONUT_PALETTE.size])
      }
      DonutChartCard(
        title = "Pengeluaran per Kategori",
        centerSub = "Belanja",
        centerVal = FormatUtils.formatRupiah(state.totalPengeluaran),
        slices = slices
      )
    }

    // Chart 6: Pengeluaran per Sumber Dana
    item {
      val expenseTx = state.currentMonthTransactions.filter { it.jenis == "Pengeluaran" }
      val srcMap = expenseTx.groupBy { it.sumber }.mapValues { entry -> entry.value.sumOf { it.nominal } }
      val slices = srcMap.entries.sortedByDescending { it.value }.mapIndexed { idx, (src, sum) ->
        DonutSlice(src, sum, DONUT_PALETTE[idx % DONUT_PALETTE.size])
      }
      DonutChartCard(
        title = "Pengeluaran per Sumber Dana",
        centerSub = "Sumber",
        centerVal = FormatUtils.formatRupiah(state.totalPengeluaran),
        slices = slices
      )
    }

    // 6. Progres Anggaran Belanja
    item {
      Text(
        text = "Progres Anggaran Belanja",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = DarkCrimson
      )
    }
    item {
      val expCats = state.categories.filter { it.jenis == "Pengeluaran" }
      val spentByCat = state.currentMonthTransactions.filter { it.jenis == "Pengeluaran" }
        .groupBy { it.kategori }.mapValues { it.value.sumOf { tx -> tx.nominal } }

      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderPink)
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
          if (expCats.isEmpty()) {
            Text("Belum ada kategori pengeluaran", color = MaterialTheme.colorScheme.onSurfaceVariant)
          } else {
            expCats.forEach { cat ->
              val budget = cat.anggaran
              val spent = spentByCat[cat.nama] ?: 0L
              val pct = if (budget > 0) ((spent.toDouble() / budget.toDouble()) * 100).toInt() else 0
              val barColor = when {
                pct > 100 -> DarkCrimson
                pct > 80 -> AmberWarning
                else -> CrimsonRed
              }

              Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                  Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(cat.nama, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                    Box(
                      modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(SoftPink).padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                      Text(cat.kelompok, fontSize = 10.sp, color = DarkCrimson, fontWeight = FontWeight.SemiBold)
                    }
                  }
                  Text(
                    "${FormatUtils.formatRupiah(spent)} / ${FormatUtils.formatRupiah(budget)}",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }

                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF1E2E4))
                ) {
                  val fillWidth = (pct.toFloat() / 100f).coerceIn(0f, 1f)
                  Box(
                    modifier = Modifier
                      .fillMaxWidth(fillWidth)
                      .height(8.dp)
                      .clip(CircleShape)
                      .background(barColor)
                  )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                  Text("$pct% terpakai", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                  val note = if (pct > 100) "Lebih ${FormatUtils.formatRupiah(spent - budget)}" else "Sisa ${FormatUtils.formatRupiah(budget - spent)}"
                  Text(note, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (pct > 100) DarkCrimson else MaterialTheme.colorScheme.onSurfaceVariant)
                }
              }
            }
          }
        }
      }
    }

    // 7. Top 5 Expenses
    item {
      Text(
        text = "5 Pengeluaran Terbesar",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = DarkCrimson
      )
    }
    item {
      val top5 = state.currentMonthTransactions
        .filter { it.jenis == "Pengeluaran" }
        .sortedByDescending { it.nominal }
        .take(5)

      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderPink)
      ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          if (top5.isEmpty()) {
            Text("Belum ada pengeluaran di bulan ini.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
          } else {
            top5.forEachIndexed { idx, tx ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(10.dp))
                  .background(SoftPink)
                  .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                  Text("#${idx + 1}", fontWeight = FontWeight.Bold, color = DarkCrimson, fontSize = 14.sp)
                  Column {
                    Text(tx.kategori, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(
                      "${FormatUtils.formatShortDate(tx.tanggal)} • ${tx.sumber} ${if (tx.catatan.isNotBlank()) "• \"${tx.catatan}\"" else ""}",
                      fontSize = 11.sp,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                }
                Text(
                  FormatUtils.formatRupiah(tx.nominal),
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp,
                  color = CrimsonRed
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun QuickRecordButton(
  label: String,
  emoji: String,
  textColor: Color,
  bgColor: Color,
  borderColor: Color,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .background(bgColor)
      .border(1.dp, borderColor, RoundedCornerShape(12.dp))
      .clickable { onClick() }
      .padding(vertical = 10.dp, horizontal = 4.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(emoji, fontSize = 18.sp)
      Spacer(modifier = Modifier.height(2.dp))
      Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textColor)
    }
  }
}

@Composable
private fun MetricCol(label: String, value: String, color: Color) {
  Column {
    Text(label, fontSize = 11.sp, color = Color.White.copy(alpha = 0.85f))
    Spacer(modifier = Modifier.height(2.dp))
    Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color)
  }
}

@Composable
private fun PlanItemBox(label: String, value: String, modifier: Modifier = Modifier, highlightColor: Color? = null) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(10.dp))
      .background(SoftPink)
      .padding(10.dp)
  ) {
    Column {
      Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
      Spacer(modifier = Modifier.height(2.dp))
      Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = highlightColor ?: Color(0xFF2B1D1F))
    }
  }
}
