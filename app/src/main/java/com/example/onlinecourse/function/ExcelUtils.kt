package com.example.onlinecourse.function

import android.content.Context
import android.net.Uri
import android.widget.Toast
import com.example.onlinecourse.network.AppealStatisticsItemResponse
import com.example.onlinecourse.network.UserActivityStatsItemResponse
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook

fun saveStatisticsToExcelWithUri(
    context: Context,
    stats: List<Pair<String, String>>,
    uri: Uri
) {
    try {
        val workbook: Workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Статистика")

        val headerStyle: CellStyle = workbook.createCellStyle().apply {
            setFont(workbook.createFont().apply {
                bold = true
            })
        }

        val headerRow = sheet.createRow(0)
        headerRow.createCell(0).apply {
            setCellValue("Показатель")
            cellStyle = headerStyle
        }
        headerRow.createCell(1).apply {
            setCellValue("Значение")
            cellStyle = headerStyle
        }

        for ((index, pair) in stats.withIndex()) {
            val row = sheet.createRow(index + 1)
            row.createCell(0).setCellValue(pair.first)
            row.createCell(1).setCellValue(pair.second)
        }

        sheet.setColumnWidth(0, 25 * 256)
        sheet.setColumnWidth(1, 15 * 256)

        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            workbook.write(outputStream)
        }

        workbook.close()
        Toast.makeText(context, "Файл успешно сохранён", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Ошибка при сохранении: ${e.message}", Toast.LENGTH_LONG).show()
    }
}

fun saveAppealStatisticsToExcelWithUri(
    context: Context,
    stats: List<AppealStatisticsItemResponse>,
    uri: Uri
) {
    try {
        val workbook: Workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Обращения")

        val headerStyle: CellStyle = workbook.createCellStyle().apply {
            setFont(workbook.createFont().apply { bold = true })
        }

        val headerRow = sheet.createRow(0)
        val headers = listOf(
            "Статус", "Кол-во", "Среднее время ответа (ч)",
            "Просрочено", "Процент решений", "Частая тема", "Файлов на обращение"
        )
        headers.forEachIndexed { index, title ->
            headerRow.createCell(index).apply {
                setCellValue(title)
                cellStyle = headerStyle
            }
        }

        stats.forEachIndexed { rowIndex, stat ->
            val row = sheet.createRow(rowIndex + 1)
            row.createCell(0).setCellValue(stat.status)
            row.createCell(1).setCellValue(stat.appealCount.toDouble())
            row.createCell(2).setCellValue(stat.avgResponseHours)
            row.createCell(3).setCellValue(stat.overdueAppeals.toDouble())
            row.createCell(4).setCellValue(stat.resolutionRate)
            row.createCell(5).setCellValue(stat.mostCommonTopic)
            row.createCell(6).setCellValue(stat.avgFilesPerAppeal)
        }

        for (i in headers.indices) {
            sheet.setColumnWidth(i, 20 * 256)
        }

        context.contentResolver.openOutputStream(uri)?.use { output ->
            workbook.write(output)
        }

        workbook.close()
        Toast.makeText(context, "Файл успешно сохранён", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Ошибка при сохранении: ${e.message}", Toast.LENGTH_LONG).show()
    }
}

fun saveUserActivityStatsToExcelWithUri(
    context: Context,
    stats: List<UserActivityStatsItemResponse>,
    uri: Uri
) {
    try {
        val workbook: Workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Активность")

        val headerStyle: CellStyle = workbook.createCellStyle().apply {
            setFont(workbook.createFont().apply { bold = true })
        }

        val headerRow = sheet.createRow(0)
        val headers = listOf("Дата", "Активные", "Новые", "Ответы на задания", "Записались на курсы")
        headers.forEachIndexed { index, title ->
            headerRow.createCell(index).apply {
                setCellValue(title)
                cellStyle = headerStyle
            }
        }

        stats.forEachIndexed { index, stat ->
            val row = sheet.createRow(index + 1)
            row.createCell(0).setCellValue(stat.day)
            row.createCell(1).setCellValue(stat.activeUsers.toDouble())
            row.createCell(2).setCellValue(stat.newUsers.toDouble())
            row.createCell(3).setCellValue(stat.answeredQuestions.toDouble())
            row.createCell(4).setCellValue(stat.newEnrollments.toDouble())
        }

        for (i in headers.indices) {
            sheet.setColumnWidth(i, 20 * 256)
        }

        context.contentResolver.openOutputStream(uri)?.use { output ->
            workbook.write(output)
        }

        workbook.close()
        Toast.makeText(context, "Файл успешно сохранён", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Ошибка при сохранении: ${e.message}", Toast.LENGTH_LONG).show()
    }
}
