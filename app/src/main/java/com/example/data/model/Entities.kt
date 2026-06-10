package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val email: String,
    val name: String,
    val role: String, // "FACULTY" or "ADMIN"
    val employeeId: String = "",
    val department: String = "",
    val designation: String = "",
    val phone: String = "",
    val academicYear: String = "2025-2026",
    val semester: String = "",
    val section: String = ""
)

@Entity(tableName = "subjects")
data class SubjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val facultyEmail: String,
    val subjectName: String,
    val subjectCode: String,
    val semester: String,
    val branch: String,
    val section: String,
    val academicYear: String,
    val numStudents: Int = 0
)

@Entity(tableName = "students", primaryKeys = ["rollNumber", "subjectCode"])
data class StudentEntity(
    val rollNumber: String,
    val subjectCode: String,
    val studentName: String,
    val department: String,
    val semester: String,
    val section: String,
    val attendancePercentage: Float = 0.0f,
    val assignmentMarks: Float = 0.0f,
    val labMarks: Float = 0.0f,
    val quizMarks: Float = 0.0f,
    val projectMarks: Float = 0.0f,
    val internalMarks: Float = 0.0f,
    val externalMarks: Float = 0.0f,
    val totalMarks: Float = 0.0f,
    val grade: String = "F"
)

@Entity(tableName = "attendance", primaryKeys = ["rollNumber", "subjectCode", "date"])
data class AttendanceEntity(
    val rollNumber: String,
    val subjectCode: String,
    val date: String, // YYYY-MM-DD
    val isPresent: Boolean
)

@Entity(tableName = "assessments")
data class AssessmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val subjectCode: String,
    val name: String,
    val maxMarks: Float,
    val weightage: Float,
    val numQuestions: Int = 5,
    val passingCriteria: Float = 40.0f // custom parameter
)

@Entity(tableName = "marks", primaryKeys = ["rollNumber", "subjectCode", "assessmentId"])
data class MarkEntity(
    val rollNumber: String,
    val subjectCode: String,
    val assessmentId: Int,
    val marksObtained: Float
)
