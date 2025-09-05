package com.yourapp.test.myrecordinschool.data.model

data class Violation(
    val id: Int = 0,
    val student_id: String = "",
    val student_name: String = "",   // ✅ add
    val year_level: String = "",     // ✅ add
    val course: String = "",         // ✅ add
    val section: String = "",        // ✅ add
    val violation_type: String = "",
    val violation_description: String = "",
    val offense_count: Int = 1,
    val original_offense_count: Int = 1,
    val penalty: String = "",
    val recorded_by: String = "",
    val date_recorded: String = "",
    val acknowledged: Int = 0,
    val category: String = ""
)


data class ViolationResponse(
    val success: Boolean,
    val message: String,
    val violations: List<Violation> = emptyList()
)

data class ViolationCategory(
    val name: String,
    val violations: List<String>
)

// Violation categories with their specific violations
object ViolationCategories {
    val DRESS_CODE = ViolationCategory(
        name = "DRESS CODE VIOLATION",
        violations = listOf(
            "No ID",
            "Wearing of rubber slippers",
            "Improper wearing of uniform",
            "Non-prescribed haircut",
            "Wearing of earring",
            "Wearing of multiple earrings"
        )
    )
    
    val CONDUCT = ViolationCategory(
        name = "CONDUCT VIOLATION",
        violations = listOf(
            "Cutting Classes",
            "Cheating/Academic Dishonesty",
            "Theft/Stealing",
            "Inflicting/Direct Assault",
            "Gambling",
            "Smoking within the school vicinity",
            "Possession/Use of Prohibited Drugs",
            "Possession/Use of Liquor/Alcoholic Beverages",
            "Others"
        )
    )
    
    val MINOR_OFFENSE = ViolationCategory(
        name = "MINOR OFFENSE",
        violations = listOf(
            "Using cellphones/gadgets during class hours",
            "Eating inside the laboratories",
            "Improper not wearing/tampering of ID",
            "Improper hairstyle",
            "Improper Uniform"
        )
    )
    
    val MAJOR_OFFENSE = ViolationCategory(
        name = "MAJOR OFFENSE",
        violations = listOf(
            "Stealing",
            "Vandalism",
            "Verbal assault",
            "Organizing, planning or joining to any group or fraternity activity"
        )
    )
    
    fun getAllCategories() = listOf(DRESS_CODE, CONDUCT, MINOR_OFFENSE, MAJOR_OFFENSE)
}

// Penalty matrix for different offenses
object PenaltyMatrix {
    fun getPenalty(violationType: String, offenseCount: Int): String {
        return when {
            ViolationCategories.DRESS_CODE.violations.contains(violationType) -> {
                when (offenseCount) {
                    1 -> "Warning"
                    2 -> "Grounding"
                    else -> "Suspension"
                }
            }
            ViolationCategories.CONDUCT.violations.contains(violationType) -> {
                when (violationType) {
                    "Cutting Classes" -> when (offenseCount) {
                        1 -> "Warning"
                        2 -> "Grounding"
                        else -> "Suspension"
                    }
                    "Cheating/Academic Dishonesty" -> when (offenseCount) {
                        1 -> "Suspension"
                        2 -> "Probation"
                        else -> "Expulsion"
                    }
                    "Theft/Stealing", "Inflicting/Direct Assault" -> when (offenseCount) {
                        1 -> "Suspension"
                        2 -> "Non-readmission"
                        else -> "Expulsion"
                    }
                    else -> when (offenseCount) {
                        1 -> "Warning/Probation"
                        2 -> "Suspension"
                        else -> "Expulsion"
                    }
                }
            }
            ViolationCategories.MINOR_OFFENSE.violations.contains(violationType) -> {
                when (offenseCount) {
                    1 -> "Warning"
                    2 -> "Grounding"
                    else -> "Probation"
                }
            }
            ViolationCategories.MAJOR_OFFENSE.violations.contains(violationType) -> {
                when (offenseCount) {
                    1 -> "Suspension"
                    2 -> "Non-readmission"
                    else -> "Expulsion"
                }
            }
            else -> "Warning"
        }
    }
}