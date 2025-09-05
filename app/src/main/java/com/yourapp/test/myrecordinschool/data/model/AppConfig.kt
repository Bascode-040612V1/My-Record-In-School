package com.yourapp.test.myrecordinschool.data.model

data class AppConfig(
    val ipAddress: String = "localhost",
    val port: String = "8080"
) {
    val baseUrl: String
        get() = "http://$ipAddress:$port/backend/"
}

data class ConnectionTestResponse(
    val success: Boolean,
    val message: String
)

// Dropdown options for registration and updates
object DropdownOptions {
    val YEARS = listOf(
        "Grade 11",
        "Grade 12", 
        "1st Year",
        "2nd Year",
        "3rd Year",
        "4th Year"
    )
    
    val COURSES = listOf(
        "ICT",
        "BSCS",
        "BSEntrep"
    )
    
    fun getSectionsForCourseAndYear(course: String, year: String): List<String> {
        return when {
            course == "ICT" && year == "Grade 11" -> listOf("IC1MA")
            course == "ICT" && year == "Grade 12" -> listOf("IC2MA")
            course == "BSCS" && year == "1st Year" -> listOf("BS1MA", "BS2MA", "BS1AA", "BS2AA", "BS1EA", "BS2EA")
            course == "BSCS" && year == "2nd Year" -> listOf("BS3MA", "BS4MA", "BS3AA", "BS4AA", "BS3EA", "BS4EA")
            course == "BSCS" && year == "3rd Year" -> listOf("BS5MA", "BS6MA", "BS5AA", "BS6AA", "BS5EA", "BS6EA")
            course == "BSCS" && year == "4th Year" -> listOf("BS7MA", "BS8MA", "BS7AA", "BS8AA", "BS7EA", "BS8EA")
            course == "BSEntrep" && year == "1st Year" -> listOf("BN1MA", "BN2MA", "BN1AA", "BN2AA", "BN1EA", "BN2EA")
            course == "BSEntrep" && year == "2nd Year" -> listOf("BN3MA", "BN4MA", "BN3AA", "BN4AA", "BN3EA", "BN4EA")
            course == "BSEntrep" && year == "3rd Year" -> listOf("BN5MA", "BN6MA", "BN5AA", "BN6AA", "BN5EA", "BN6EA")
            course == "BSEntrep" && year == "4th Year" -> listOf("BN7MA", "BN8MA", "BN7AA", "BN8AA", "BN7EA", "BN8EA")
            else -> emptyList()
        }
    }
}