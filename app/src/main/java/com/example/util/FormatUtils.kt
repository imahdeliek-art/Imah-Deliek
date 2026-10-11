package com.example.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object FormatUtils {
  private val indonesianLocale = Locale("id", "ID")
  val MONTH_NAMES = listOf(
    "Januari", "Februari", "Maret", "April", "Mei", "Juni",
    "Juli", "Agustus", "September", "Oktober", "November", "Desember"
  )

  fun formatRupiah(amount: Long): String {
    val formatter = NumberFormat.getNumberInstance(indonesianLocale)
    val absVal = kotlin.math.abs(amount)
    return "Rp " + formatter.format(absVal)
  }

  fun getTodayDateString(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    return sdf.format(Date())
  }

  fun formatShortDate(dateStr: String): String {
    val parts = dateStr.split("-")
    if (parts.size == 3) {
      return "${parts[2]}/${parts[1]}"
    }
    return dateStr
  }

  fun getCurrentYearMonth(): Pair<Int, Int> {
    val cal = Calendar.getInstance()
    val year = cal.get(Calendar.YEAR)
    val month = cal.get(Calendar.MONTH) + 1 // 1..12
    return Pair(year, month)
  }

  fun generateCsv(transactions: List<com.example.model.TransactionItem>): String {
    val sb = StringBuilder()
    // UTF-8 BOM
    sb.append('\uFEFF')
    sb.append("Tanggal,Jenis,Kategori,Nominal,Sumber Dana,Dicatat Oleh,Catatan\r\n")
    for (t in transactions) {
      val noteClean = t.catatan.replace("\"", "\"\"")
      val catClean = t.kategori.replace("\"", "\"\"")
      val srcClean = t.sumber.replace("\"", "\"\"")
      val byClean = t.oleh.replace("\"", "\"\"")
      sb.append("${t.tanggal},${t.jenis},\"$catClean\",${t.nominal},\"$srcClean\",\"$byClean\",\"$noteClean\"\r\n")
    }
    return sb.toString()
  }
}
