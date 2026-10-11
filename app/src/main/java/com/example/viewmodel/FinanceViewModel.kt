package com.example.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.example.model.CategoryItem
import com.example.model.DEFAULT_RECORDERS
import com.example.model.DEFAULT_SOURCES
import com.example.model.INITIAL_DEFAULT_CATEGORIES
import com.example.model.TransactionItem
import com.example.util.FormatUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

data class FinanceUiState(
  val currentYear: Int,
  val currentMonth: Int,
  val activeTab: Int = 0, // 0: Dashboard, 1: Catat, 2: Riwayat, 3: Pengaturan
  val categories: List<CategoryItem> = emptyList(),
  val allTransactions: List<TransactionItem> = emptyList(),
  val sources: List<String> = DEFAULT_SOURCES,
  val recorders: List<String> = DEFAULT_RECORDERS,
  val lastRecorder: String = "Suami",
  val toastMessage: String? = null
) {
  val monthLabel: String
    get() = "${FormatUtils.MONTH_NAMES.getOrElse(currentMonth - 1) { "" }} $currentYear"

  val currentMonthPrefix: String
    get() = String.format("%04d-%02d-", currentYear, currentMonth)

  val currentMonthTransactions: List<TransactionItem>
    get() = allTransactions.filter { it.tanggal.startsWith(currentMonthPrefix) }
      .sortedWith(compareByDescending<TransactionItem> { it.tanggal }.thenByDescending { it.timestamp })

  val totalPemasukan: Long
    get() = currentMonthTransactions.filter { it.jenis == "Pemasukan" && !isPemindahan(it.kategori) }
      .sumOf { it.nominal }

  val totalPengeluaran: Long
    get() = currentMonthTransactions.filter { it.jenis == "Pengeluaran" }
      .sumOf { it.nominal }

  val totalTabungan: Long
    get() = currentMonthTransactions.filter { it.jenis == "Tabungan" }
      .sumOf { it.nominal }

  val totalMutasiSaldo: Long
    get() = currentMonthTransactions.filter { it.jenis == "Pemasukan" && isPemindahan(it.kategori) }
      .sumOf { it.nominal }

  val sisaBulanIni: Long
    get() = totalPemasukan - totalPengeluaran - totalTabungan

  val plannedIncomeBudget: Long
    get() = categories.filter { it.jenis == "Pemasukan" && it.kelompok != "Pemindahan" }.sumOf { it.anggaran }

  val plannedExpenseBudget: Long
    get() = categories.filter { it.jenis == "Pengeluaran" }.sumOf { it.anggaran }

  val plannedSavingsBudget: Long
    get() = categories.filter { it.jenis == "Tabungan" }.sumOf { it.anggaran }

  val plannedBalance: Long
    get() = plannedIncomeBudget - plannedExpenseBudget - plannedSavingsBudget

  fun isPemindahan(kategoriNama: String): Boolean {
    val cat = categories.find { it.nama.equals(kategoriNama, ignoreCase = true) }
    return cat?.kelompok.equals("Pemindahan", ignoreCase = true) || kategoriNama.contains("mutasi", ignoreCase = true)
  }
}

class FinanceViewModel(application: Application) : AndroidViewModel(application) {
  private val prefs = application.getSharedPreferences("kas_keluarga_prefs", Context.MODE_PRIVATE)

  private val initialYear = FormatUtils.getCurrentYearMonth().first
  private val initialMonth = FormatUtils.getCurrentYearMonth().second
  private val _uiState = MutableStateFlow(
    FinanceUiState(
      currentYear = initialYear,
      currentMonth = initialMonth
    )
  )
  val uiState: StateFlow<FinanceUiState> = _uiState.asStateFlow()

  init {
    loadLocalData()
  }

  fun setActiveTab(tab: Int) {
    _uiState.value = _uiState.value.copy(activeTab = tab)
  }

  fun changeMonth(delta: Int) {
    var m = _uiState.value.currentMonth + delta
    var y = _uiState.value.currentYear
    if (m < 1) {
      m = 12
      y--
    } else if (m > 12) {
      m = 1
      y++
    }
    _uiState.value = _uiState.value.copy(currentYear = y, currentMonth = m)
  }

  fun resetToCurrentMonth() {
    val (y, m) = FormatUtils.getCurrentYearMonth()
    _uiState.value = _uiState.value.copy(currentYear = y, currentMonth = m)
  }

  fun addTransaction(
    tanggal: String,
    jenis: String,
    kategori: String,
    nominal: Long,
    sumber: String,
    oleh: String,
    catatan: String
  ) {
    val newTx = TransactionItem(
      id = UUID.randomUUID().toString(),
      tanggal = tanggal,
      jenis = jenis,
      kategori = kategori,
      nominal = nominal,
      sumber = sumber,
      oleh = oleh,
      catatan = catatan,
      timestamp = System.currentTimeMillis()
    )
    val updated = listOf(newTx) + _uiState.value.allTransactions
    _uiState.value = _uiState.value.copy(
      allTransactions = updated,
      lastRecorder = oleh,
      toastMessage = "Transaksi berhasil dicatat!"
    )
    saveLocalData()
  }

  fun deleteTransaction(id: String) {
    val updated = _uiState.value.allTransactions.filter { it.id != id }
    _uiState.value = _uiState.value.copy(
      allTransactions = updated,
      toastMessage = "Transaksi berhasil dihapus."
    )
    saveLocalData()
  }

  fun addCategory(nama: String, jenis: String, kelompok: String, anggaran: Long): Boolean {
    if (_uiState.value.categories.any { it.nama.equals(nama, ignoreCase = true) }) {
      _uiState.value = _uiState.value.copy(toastMessage = "Kategori dengan nama ini sudah ada!")
      return false
    }
    val newCat = CategoryItem(
      id = UUID.randomUUID().toString(),
      nama = nama,
      jenis = jenis,
      kelompok = kelompok,
      anggaran = anggaran,
      urut = _uiState.value.categories.size + 1
    )
    val updated = _uiState.value.categories + newCat
    _uiState.value = _uiState.value.copy(
      categories = updated,
      toastMessage = "Kategori $nama berhasil ditambahkan!"
    )
    saveLocalData()
    return true
  }

  fun updateCategory(id: String, kelompok: String, anggaran: Long) {
    val updated = _uiState.value.categories.map {
      if (it.id == id) it.copy(kelompok = kelompok, anggaran = anggaran) else it
    }
    _uiState.value = _uiState.value.copy(
      categories = updated,
      toastMessage = "Kategori berhasil diperbarui."
    )
    saveLocalData()
  }

  fun deleteCategory(id: String) {
    val updated = _uiState.value.categories.filter { it.id != id }
    _uiState.value = _uiState.value.copy(
      categories = updated,
      toastMessage = "Kategori berhasil dihapus."
    )
    saveLocalData()
  }

  fun addSource(name: String) {
    if (name.isBlank() || _uiState.value.sources.contains(name)) return
    val updated = _uiState.value.sources + name
    _uiState.value = _uiState.value.copy(sources = updated, toastMessage = "Sumber dana ditambahkan")
    saveLocalData()
  }

  fun deleteSource(name: String) {
    if (_uiState.value.sources.size <= 1) {
      _uiState.value = _uiState.value.copy(toastMessage = "Minimal harus ada 1 sumber dana!")
      return
    }
    val updated = _uiState.value.sources.filter { it != name }
    _uiState.value = _uiState.value.copy(sources = updated)
    saveLocalData()
  }

  fun addRecorder(name: String) {
    if (name.isBlank() || _uiState.value.recorders.contains(name)) return
    val updated = _uiState.value.recorders + name
    _uiState.value = _uiState.value.copy(recorders = updated, toastMessage = "Pencatat ditambahkan")
    saveLocalData()
  }

  fun deleteRecorder(name: String) {
    if (_uiState.value.recorders.size <= 1) {
      _uiState.value = _uiState.value.copy(toastMessage = "Minimal harus ada 1 pencatat!")
      return
    }
    val updated = _uiState.value.recorders.filter { it != name }
    _uiState.value = _uiState.value.copy(recorders = updated)
    saveLocalData()
  }

  fun resetToDefaultCategories() {
    _uiState.value = _uiState.value.copy(
      categories = INITIAL_DEFAULT_CATEGORIES,
      sources = DEFAULT_SOURCES,
      recorders = DEFAULT_RECORDERS,
      toastMessage = "25 kategori awal keluarga berhasil dimuat!"
    )
    saveLocalData()
  }

  fun clearToast() {
    _uiState.value = _uiState.value.copy(toastMessage = null)
  }

  private fun loadLocalData() {
    val catJson = prefs.getString("categories_json", null)
    val cats = if (catJson != null) {
      try {
        val arr = JSONArray(catJson)
        val list = mutableListOf<CategoryItem>()
        for (i in 0 until arr.length()) {
          val obj = arr.getJSONObject(i)
          list.add(
            CategoryItem(
              id = obj.optString("id", UUID.randomUUID().toString()),
              nama = obj.getString("nama"),
              jenis = obj.getString("jenis"),
              kelompok = obj.getString("kelompok"),
              anggaran = obj.getLong("anggaran"),
              urut = obj.optInt("urut", i + 1)
            )
          )
        }
        list
      } catch (e: Exception) {
        INITIAL_DEFAULT_CATEGORIES
      }
    } else {
      INITIAL_DEFAULT_CATEGORIES
    }

    val txJson = prefs.getString("transactions_json", null)
    val txs = if (txJson != null) {
      try {
        val arr = JSONArray(txJson)
        val list = mutableListOf<TransactionItem>()
        for (i in 0 until arr.length()) {
          val obj = arr.getJSONObject(i)
          list.add(
            TransactionItem(
              id = obj.getString("id"),
              tanggal = obj.getString("tanggal"),
              jenis = obj.getString("jenis"),
              kategori = obj.getString("kategori"),
              nominal = obj.getLong("nominal"),
              sumber = obj.getString("sumber"),
              oleh = obj.getString("oleh"),
              catatan = obj.optString("catatan", ""),
              timestamp = obj.optLong("timestamp", System.currentTimeMillis())
            )
          )
        }
        list
      } catch (e: Exception) {
        getSampleTransactions()
      }
    } else {
      getSampleTransactions()
    }

    val lastRec = prefs.getString("last_recorder", "Suami") ?: "Suami"

    _uiState.value = _uiState.value.copy(
      categories = cats,
      allTransactions = txs,
      lastRecorder = lastRec
    )
  }

  private fun getSampleTransactions(): List<TransactionItem> {
    val today = FormatUtils.getTodayDateString()
    return listOf(
      TransactionItem("t1", today, "Pemasukan", "Gaji suami", 5500000L, "BSI", "Suami", "Gaji bulanan"),
      TransactionItem("t2", today, "Pengeluaran", "Belanja bahan masakan", 150000L, "Cash", "Istri", "Sayur & lauk"),
      TransactionItem("t3", today, "Tabungan", "Tabungan dana darurat", 500000L, "BSI", "Suami", "Alokasi awal bulan")
    )
  }

  private fun saveLocalData() {
    val catArr = JSONArray()
    for (c in _uiState.value.categories) {
      val obj = JSONObject()
      obj.put("id", c.id)
      obj.put("nama", c.nama)
      obj.put("jenis", c.jenis)
      obj.put("kelompok", c.kelompok)
      obj.put("anggaran", c.anggaran)
      obj.put("urut", c.urut)
      catArr.put(obj)
    }

    val txArr = JSONArray()
    for (t in _uiState.value.allTransactions) {
      val obj = JSONObject()
      obj.put("id", t.id)
      obj.put("tanggal", t.tanggal)
      obj.put("jenis", t.jenis)
      obj.put("kategori", t.kategori)
      obj.put("nominal", t.nominal)
      obj.put("sumber", t.sumber)
      obj.put("oleh", t.oleh)
      obj.put("catatan", t.catatan)
      obj.put("timestamp", t.timestamp)
      txArr.put(obj)
    }

    prefs.edit()
      .putString("categories_json", catArr.toString())
      .putString("transactions_json", txArr.toString())
      .putString("last_recorder", _uiState.value.lastRecorder)
      .apply()
  }
}
