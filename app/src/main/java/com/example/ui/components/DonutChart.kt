package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BorderPink
import com.example.ui.theme.DarkCrimson
import com.example.util.FormatUtils

data class DonutSlice(
  val label: String,
  val value: Long,
  val color: Color
)

val DONUT_PALETTE = listOf(
  Color(0xFFB4122B), Color(0xFF8F0E22), Color(0xFFD9485F), Color(0xFFE97C8C),
  Color(0xFF5B7083), Color(0xFF3D85C6), Color(0xFFD4A373), Color(0xFF457B9D),
  Color(0xFFE76F51), Color(0xFF2A9D8F), Color(0xFF6D597A), Color(0xFFB56576)
)

@Composable
fun DonutChartCard(
  title: String,
  centerSub: String,
  centerVal: String,
  slices: List<DonutSlice>,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = androidx.compose.foundation.BorderStroke(1.dp, BorderPink),
    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
  ) {
    Column(
      modifier = Modifier.padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.Bold,
          color = DarkCrimson
        ),
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
      )

      Box(
        modifier = Modifier
          .padding(vertical = 12.dp)
          .size(170.dp),
        contentAlignment = Alignment.Center
      ) {
        val total = slices.sumOf { it.value }

        Canvas(modifier = Modifier.size(170.dp)) {
          val strokeWidth = 26.dp.toPx()
          val radius = (size.minDimension - strokeWidth) / 2f
          val centerOffset = androidx.compose.ui.geometry.Offset(size.width / 2f, size.height / 2f)
          val arcTopLeft = androidx.compose.ui.geometry.Offset(strokeWidth / 2f, strokeWidth / 2f)
          val arcSize = androidx.compose.ui.geometry.Size(size.width - strokeWidth, size.height - strokeWidth)

          if (total <= 0) {
            drawCircle(
              color = Color(0xFFF3D4D8),
              radius = radius,
              center = centerOffset,
              style = Stroke(width = strokeWidth)
            )
          } else {
            var startAngle = -90f
            for (slice in slices) {
              if (slice.value <= 0) continue
              val sweepAngle = (slice.value.toFloat() / total.toFloat()) * 360f
              drawArc(
                color = slice.color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = arcTopLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
              )
              startAngle += sweepAngle
            }
          }
        }

        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.padding(horizontal = 14.dp)
        ) {
          Text(
            text = centerSub.uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = centerVal,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = DarkCrimson,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
          )
        }
      }

      // Legend
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        val total = slices.sumOf { it.value }
        if (slices.isEmpty() || total <= 0) {
          Text(
            text = "Belum ada transaksi di bulan ini",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
          )
        } else {
          slices.filter { it.value > 0 }.forEach { slice ->
            val pct = if (total > 0) ((slice.value.toDouble() / total.toDouble()) * 100).toInt() else 0
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(slice.color)
                )
                Text(
                  text = slice.label,
                  style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
              }
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                Text(
                  text = FormatUtils.formatRupiah(slice.value),
                  style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                  text = "($pct%)",
                  style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }
      }
    }
  }
}
