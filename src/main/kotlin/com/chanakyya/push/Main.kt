package com.chanakyya.push

import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

data class CropProductionData(val cropName: String, val districtName: String, val quantity: Double)

fun isOnlyCell(row: Row?): Boolean {
    return !row?.getCell(0)?.stringCellValue.isNullOrEmpty() && row?.getCell(1)?.stringCellValue.isNullOrEmpty()
}

fun isDistrictName(row: Row?): Boolean {
    return !row?.getCell(0)?.stringCellValue.isNullOrEmpty() && !row?.getCell(1)?.stringCellValue.isNullOrEmpty()
}

fun rowForMultSeasons(row: Row?): Boolean {
    return row?.getCell(0)?.stringCellValue.isNullOrEmpty()
            && row?.getCell(1)?.stringCellValue.isNullOrEmpty()
            && !row?.getCell(2)?.stringCellValue.isNullOrEmpty()
}

fun totalValueRow(row: Row?): Boolean {
    return row?.getCell(0)?.stringCellValue.isNullOrEmpty()
            && row?.getCell(1)?.stringCellValue.isNullOrEmpty()
            && row?.getCell(2)?.stringCellValue?.trim().equals("Total")
}

fun main(args: Array<String>) {
    val x = grabCropData()
    val y = grabDistrictMapping()
    val z = mapOf(
        "KADAPA" to "Kadapa",
        "SAMBHAL" to "Sambhal",
        "VILLUPURAM" to "Viluppuram (SC),Kallakurichi,Arani",
        "KANCHIPURAM" to "Chennai South,Sriperumbudur,Kancheepuram (SC)",
        "KASGANJ" to "Etah",
        "HATHRAS" to "Hathras(SC)",
        "KADAPA" to "Kadapa",
        "NAGARKURNOOL" to "Nagarkurnool (SC)",
        "ARAVALLI" to "SABARKANTHA",
        "MEDINIPUR EAST" to "TAMLUK,GHATAL,KANTHI,MEDINIPUR",
        "GONDIA" to "Bhandara-Gondiya,Gadchiroli-Chimur(ST)",
        "AHMADABAD" to "SURENDRANAGAR,GANDHINAGAR,AHMEDABAD EAST,AHMEDABAD WEST(SC),KHEDA",
        "RANGAREDDI" to "Malkajgiri,Bhongir,Chevella",
        "PANCH MAHALS" to "PANCHMAHAL,Chhota Udaipur (ST)",
        "DINAJPUR UTTAR" to "DARJEELING,RAIGANJ,BALURGHAT",
        "SIDDIPET" to "Medak",
        "DOHAD" to "DAHOD(ST)",
        "NORTH AND MIDDLE ANDAMAN" to "",
        "TUTICORIN" to "Thoothukkudi",
        "PURBA BARDHAMAN" to "BARDHAMAN PURBA (SC)",
        "MEDINIPUR WEST" to "MEDINIPUR,JHARGRAM (ST),GHATAL,ARAMBAG (SC)",
        "24 PARAGANAS SOUTH" to "JAYNAGAR (SC),MATHURAPUR (SC),JADAVPUR,DIAMOND HARBOUR,KOLKATA DAKSHIN,DIAMOND HARBOUR",
        "24 PARAGANAS NORTH" to "BANGAON (SC),BASIRHAT,BARASAT,BARRACKPUR,DUM DUM,",
        "COOCHBEHAR" to "JALPAIGURI (SC),Cooch Behar(SC)",
        "SPSR NELLORE" to "Nellore",
        "FIROZEPUR" to "Khadoor Sahib,Firozpur",
        "BALESHWAR" to "Balasore,Bhadrak (SC)",
        "UDAM SINGH NAGAR" to "Nainital-Udhamsingh Nagar",
        "BASTAR" to "Bastar-Jagdalpur(ST)",
        "VISAKHAPATANAM" to "Araku(ST)",
        "ASHOKNAGAR" to "Guna",
        "DAKSHIN KANNAD" to "Dakhshina Kanada",
        "UTTAR KANNAD" to "Uttar Kanada",
        "UDUPI" to "Shimoga,Udapi Chigmagalur",
        "RAMANAGARA" to "Bangalore Rural",
        "SIVASAGAR" to "Jorhat",
        "BENGALURU URBAN" to "Bangalore Central,Bangalore South,Bangalore North",
        "VIKARABAD" to "Chevella",
        "CHAMARAJANAGAR" to "Chamarajanagar(SC)",
        "KARIMGANJ" to "Karimganj(SC)",
        "JAYASHANKAR" to "Warangal (SC)",
        "MAHABUBABAD" to "Mahaboobabad (ST)",
        "BHADRADRI" to "Khammam",
        "JOGULAMBA" to "Nagarkurnool (SC)",
        "SURYAPET" to "Nalgonda",
        "KHERI" to "Kheri,Dhaurahra",
        "PASHCHIM CHAMPARAN" to "Valmiki Nagar,Paschim Champaran",
        "AMROHA" to "Amroha",
        "BAGHPAT" to "Bagpat",
        "KENDUJHAR" to "Keonjhar (ST)",
        "ANUGUL" to "Dhenkanal",
        "AMETHI" to "Amethi",
        "JAGITIAL" to "Nizamabad",
        "NIRMAL" to "Adilabad(ST)",
        "SANGAREDDY" to "Medak",
        "AGAR MALWA" to "Dewas(SC)",
        "JAGATSINGHAPUR" to "Jagatsinghpur (SC)",
        "JAJAPUR" to "Jajpur (SC)",
        "SIRMAUR" to "Shimla(SC)",
        "KHORDHA" to "Bhubaneswar,Puri",
        "KAIMUR (BHABUA)" to "Buxar,Sasaram (SC),",
        "DINAJPUR DAKSHIN" to "BALURGHAT",
        "PURBI CHAMPARAN" to "Paschim Champaran,Purvi Champaran",
        "ALIPURDUAR" to "ALIPURDUARS (ST)",
        "BULANDSHAHR" to "Gautam Buddha Nagar,Bulandshahr(SC)",
        "DANG" to "VALSAD(ST)",
        "KAMAREDDY" to "Zaheerabad",
        "NAWANSHAHR" to "Anandpur Sahib",
        "SHRAVASTI" to "Shrawasti",
        "LAHUL AND SPITI" to "Mandi",
        "SANT KABEER NAGAR" to "Sant Kabir Nagar",
        "GAUTAM BUDDHA NAGAR" to "Gautam Buddha Nagar",
        "LAHUL AND SPITI" to "Mandi"

    )
    val s = x.flatMap { key ->
        listOf(
            y[key.key.replace(" ", "").toLowerCase()],
            y[key.key.toLowerCase()], z[key.key]
        ).filterNotNull()
            .flatMap { parName ->
                parName.split(",").map { l -> Pair(l, key.value) }
            }

    }.distinct().groupBy({ it.first }, { it.second })
        .mapValues { (_, values) -> values.joinToString(",").split(",").distinct().joinToString(",") }

    println(s)
    writeToExcel(s)
}

private fun writeToExcel(data: Map<String, String>) {
    val workbook = XSSFWorkbook()
    val sheet = workbook.createSheet("result")
    val headerRow = sheet.createRow(0)
    headerRow.createCell(0).setCellValue("Parliament Constituency name")
    headerRow.createCell(1).setCellValue("Crop names")
    var rowIdx = 1
    data.forEach { key, value ->
        val row = sheet.createRow(rowIdx++)
        row.createCell(0).setCellValue(key)
        row.createCell(1).setCellValue(value)
    }
    val fileOut = FileOutputStream("cropDataForParliament.xlsx")
    workbook.write(fileOut)
    fileOut.close()
    workbook.close()
}

private fun grabDistrictMapping(): Map<String, String> {
    return File("/Users/debaditya.bhattachar/Downloads/districttoparliamentmapping (1)/").listFiles()
        .flatMap { file ->
            //            println("processing : ${file.path}")
            val inputStream = file.inputStream()
            val xlWb = WorkbookFactory.create(inputStream)
            val xlWs = xlWb.getSheetAt(0)
            xlWs.drop(1).filter { it.getCell(0) != null }
                .map { row ->
                    Pair(row.getCell(3).stringCellValue.toLowerCase(), row.getCell(2).stringCellValue)
                }
        }.distinct().groupBy({ it.first }, { it.second })
        .mapValues { (_, values) -> values.joinToString(",") }
}

private fun grabCropData(): Map<String, String> {
    return File("/Users/debaditya.bhattachar/Downloads/aggreculture-data/").listFiles()
        .flatMap { file ->
            //            println("processing : ${file.path}")
            val inputStream = file.inputStream()
            val xlWb = WorkbookFactory.create(inputStream)
            val xlWs = xlWb.getSheetAt(0)
            var districtName: String? = null
            var cropName: String? = null
            var amount: Double? = null
            xlWs.drop(3)
                .filter { it.getCell(0) != null }
                .map { row ->
                    if (isOnlyCell(row) && !isTotalRow(row)) {
                        cropName = row?.getCell(0)?.stringCellValue
                    } else if (isDistrictName(row) && !isTotalRow(row)) {
                        districtName = row?.getCell(0)?.stringCellValue
                    }

                    if (!isOnlyCell(row) && !isTotalRow(row)) {
                        amount = row.getCell(4)?.numericCellValue
                    }

                    if (!isOnlyCell(row) && !isTotalRow(row)) {
                        if (amount == null) {
                            print(row.first().stringCellValue + file.name)
                        }
                        CropProductionData(
                            cropName = cropName!!,
                            districtName = districtName?.split(".")?.last()!!,
                            quantity = amount!!
                        )
                    } else {
                        null
                    }
                }
        }.filterNotNull().map { it }.groupBy { it.cropName.toLowerCase() }
        .map {
            it.key to
                    it.value.sortedByDescending { xx -> xx.quantity }
                        .map { yy -> yy.districtName }
                        .distinct()
                        .take(50)

        }.toMap()
        .map { entry ->
            entry.value.map { xx -> xx to entry.key }.toMap()
        }.reduce { one, two ->
            (one.asSequence() + two.asSequence())
                .groupBy({ it.key }, { it.value })
                .mapValues { (_, values) -> values.joinToString(",") }
        }
}

private fun isTotalRow(row: Row?) = !row?.getCell(0)?.stringCellValue.isNullOrEmpty() &&
        row?.getCell(0)?.stringCellValue!!.startsWith("Total")