package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.data.local.Event
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

/**
 * Utility to generate and export Event data to standard CSV format
 * for sharing with event coordinators, clients, vendors, and stakeholders.
 */
object EventCsvExporter {

  /**
   * Converts a list of Event entities into a formatted CSV string.
   */
  fun generateCsvString(events: List<Event>): String {
    val builder = StringBuilder()
    // CSV Header
    builder.append("Event ID,Event Name,Date,Time Slot,Venue,Area Zone,Status,Client / Organizer,Description,Created Timestamp\n")

    for (event in events) {
      builder.append(escapeCsv(event.id)).append(",")
      builder.append(escapeCsv(event.name)).append(",")
      builder.append(escapeCsv(event.date)).append(",")
      builder.append(escapeCsv(event.timeSlot)).append(",")
      builder.append(escapeCsv(event.venue)).append(",")
      builder.append(escapeCsv(event.areaZone)).append(",")
      builder.append(escapeCsv(event.status)).append(",")
      builder.append(escapeCsv(event.clientName)).append(",")
      builder.append(escapeCsv(event.description)).append(",")
      builder.append(event.createdAt).append("\n")
    }

    return builder.toString()
  }

  /**
   * Writes the CSV content to a temporary cache file and returns its content Uri via FileProvider.
   */
  fun writeCsvToCache(context: Context, events: List<Event>): File {
    val exportDir = File(context.cacheDir, "exports")
    if (!exportDir.exists()) {
      exportDir.mkdirs()
    }

    val timestamp = System.currentTimeMillis()
    val file = File(exportDir, "eventos_events_export_$timestamp.csv")

    FileOutputStream(file).use { fos ->
      OutputStreamWriter(fos, StandardCharsets.UTF_8).use { writer ->
        writer.write(generateCsvString(events))
        writer.flush()
      }
    }
    return file
  }

  /**
   * Dispatches an Android Intent.ACTION_SEND to share the CSV file via email, Slack, WhatsApp, Drive, etc.
   */
  fun shareCsvFile(context: Context, events: List<Event>): Boolean {
    return try {
      val file = writeCsvToCache(context, events)
      val uri: Uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
      )

      val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/csv"
        putExtra(Intent.EXTRA_SUBJECT, "Eventos Events Export (${events.size} Records)")
        putExtra(Intent.EXTRA_TEXT, "Attached is the export of ${events.size} event records from Eventos Production Management.")
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
      }

      val chooser = Intent.createChooser(intent, "Export Events CSV to Stakeholders")
      chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      context.startActivity(chooser)
      true
    } catch (e: Exception) {
      e.printStackTrace()
      false
    }
  }

  /**
   * Helper to escape special characters, quotes, commas, and linebreaks for CSV.
   */
  private fun escapeCsv(value: String): String {
    val text = value.replace("\n", " ").replace("\r", " ")
    return if (text.contains(",") || text.contains("\"") || text.contains(";")) {
      "\"" + text.replace("\"", "\"\"") + "\""
    } else {
      text
    }
  }
}
