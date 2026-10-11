package com.example.model

data class TransactionItem(
  val id: String,
  val tanggal: String, // yyyy-MM-dd
  val jenis: String,   // Pemasukan, Pengeluaran, Tabungan
  val kategori: String,
  val nominal: Long,
  val sumber: String,
  val oleh: String,
  val catatan: String = "",
  val timestamp: Long = System.currentTimeMillis()
)

data class CategoryItem(
  val id: String,
  val nama: String,
  val jenis: String, // Pemasukan, Pengeluaran, Tabungan
  val kelompok: String,
  val anggaran: Long,
  val urut: Int = 0
)

val DEFAULT_SOURCES = listOf("BSI", "Dana", "Shopeepay", "Ovo", "Cash")
val DEFAULT_RECORDERS = listOf("Suami", "Istri")

val INITIAL_DEFAULT_CATEGORIES = listOf(
  CategoryItem("c1", "Gaji suami", "Pemasukan", "Pemasukan", 5500000L, 1),
  CategoryItem("c2", "Gaji pokok istri", "Pemasukan", "Pemasukan", 800000L, 2),
  CategoryItem("c3", "Insentif istri", "Pemasukan", "Pemasukan", 0L, 3),
  CategoryItem("c4", "Insentif suami", "Pemasukan", "Pemasukan", 0L, 4),
  CategoryItem("c5", "Mutasi saldo", "Pemasukan", "Pemindahan", 0L, 5),
  CategoryItem("c6", "Dari Allah", "Pemasukan", "Rezeki Tak Terduga", 0L, 6),
  CategoryItem("c7", "Belanja makanan rumah", "Pengeluaran", "Kebutuhan", 1200000L, 7),
  CategoryItem("c8", "Belanja kebutuhan toiletries", "Pengeluaran", "Kebutuhan", 200000L, 8),
  CategoryItem("c9", "Belanja kebutuhan dapur", "Pengeluaran", "Kebutuhan", 250000L, 9),
  CategoryItem("c10", "Belanja bahan masakan", "Pengeluaran", "Kebutuhan", 850000L, 10),
  CategoryItem("c11", "Belanja estetika (baju, make up, bodycare)", "Pengeluaran", "Keinginan", 250000L, 11),
  CategoryItem("c12", "Bensin", "Pengeluaran", "Kebutuhan", 250000L, 12),
  CategoryItem("c13", "Perawatan kendaraan", "Pengeluaran", "Kebutuhan", 150000L, 13),
  CategoryItem("c14", "Perawatan rumah", "Pengeluaran", "Kebutuhan", 100000L, 14),
  CategoryItem("c15", "Kuota", "Pengeluaran", "Kebutuhan", 150000L, 15),
  CategoryItem("c16", "Listrik", "Pengeluaran", "Kebutuhan", 300000L, 16),
  CategoryItem("c17", "PDAM/air", "Pengeluaran", "Kebutuhan", 100000L, 17),
  CategoryItem("c18", "Rokok", "Pengeluaran", "Keinginan", 300000L, 18),
  CategoryItem("c19", "Jajan", "Pengeluaran", "Keinginan", 250000L, 19),
  CategoryItem("c20", "Makan di luar", "Pengeluaran", "Keinginan", 200000L, 20),
  CategoryItem("c21", "Sosial", "Pengeluaran", "Sosial", 150000L, 21),
  CategoryItem("c22", "Biaya pendidikan umma", "Pengeluaran", "Kebutuhan", 150000L, 22),
  CategoryItem("c23", "Biaya pendidikan kugy", "Pengeluaran", "Kebutuhan", 200000L, 23),
  CategoryItem("c24", "Tabungan sekolah", "Tabungan", "Tabungan", 250000L, 24),
  CategoryItem("c25", "Tabungan dana darurat", "Tabungan", "Tabungan", 500000L, 25)
)
