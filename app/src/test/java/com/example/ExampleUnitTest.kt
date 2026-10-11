package com.example

import com.example.model.TransactionItem
import com.example.util.FormatUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testFormatRupiah() {
    val formatted = FormatUtils.formatRupiah(50000)
    assertTrue("Should contain Rp and 50", formatted.contains("Rp") && formatted.contains("50"))
  }

  @Test
  fun testFormatShortDate() {
    val shortDate = FormatUtils.formatShortDate("2026-10-11")
    assertEquals("11/10", shortDate)
  }

  @Test
  fun testGenerateCsv() {
    val transactions = listOf(
      TransactionItem(
        id = "tx1",
        tanggal = "2026-10-11",
        jenis = "Pengeluaran",
        kategori = "Makan & Minum",
        nominal = 25000,
        sumber = "Kas Tunai",
        oleh = "Suami",
        catatan = "Makan siang"
      )
    )
    val csv = FormatUtils.generateCsv(transactions)
    assertTrue(csv.contains("Makan & Minum"))
    assertTrue(csv.contains("25000"))
    assertTrue(csv.contains("Suami"))
  }
}
