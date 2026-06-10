package com.example.data.repository

import com.example.data.db.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class AcademicRepository(private val db: AppDatabase) {
    private val userDao = db.userDao()
    private val subjectDao = db.subjectDao()
    private val studentDao = db.studentDao()
    private val attendanceDao = db.attendanceDao()
    private val assessmentDao = db.assessmentDao()
    private val markDao = db.markDao()

    // --- Users ---
    suspend fun getUserByEmail(email: String): UserEntity? = userDao.getUserByEmail(email)
    fun getAllUsersFlow(): Flow<List<UserEntity>> = userDao.getAllUsersFlow()
    suspend fun insertUser(user: UserEntity) = userDao.insertUser(user)
    suspend fun deleteUser(email: String) = userDao.deleteUserByEmail(email)

    // --- Subjects ---
    fun getSubjectsForFaculty(email: String): Flow<List<SubjectEntity>> = subjectDao.getSubjectsForFacultyFlow(email)
    suspend fun getSubjectByCode(subjectCode: String): SubjectEntity? = subjectDao.getSubjectByCode(subjectCode)
    fun getAllSubjects(): Flow<List<SubjectEntity>> = subjectDao.getAllSubjectsFlow()
    suspend fun insertSubject(subject: SubjectEntity) = subjectDao.insertSubject(subject)
    suspend fun deleteSubject(id: Int) = subjectDao.deleteSubjectById(id)

    // --- Students ---
    fun getStudentsForSubject(subjectCode: String): Flow<List<StudentEntity>> = studentDao.getStudentsForSubjectFlow(subjectCode)
    suspend fun getStudentsForSubjectList(subjectCode: String): List<StudentEntity> = studentDao.getStudentsForSubject(subjectCode)
    suspend fun insertStudent(student: StudentEntity) = studentDao.insertStudent(student)
    suspend fun insertStudents(students: List<StudentEntity>) = studentDao.insertStudents(students)
    suspend fun deleteStudentFromSubject(rollNumber: String, subjectCode: String) = studentDao.deleteStudentFromSubject(rollNumber, subjectCode)

    // --- Attendance ---
    fun getAttendanceForSubject(subjectCode: String): Flow<List<AttendanceEntity>> = attendanceDao.getAttendanceForSubjectFlow(subjectCode)
    suspend fun getAttendanceForSubjectList(subjectCode: String): List<AttendanceEntity> = attendanceDao.getAttendanceForSubject(subjectCode)
    suspend fun insertAttendance(attendance: AttendanceEntity) = attendanceDao.insertAttendance(attendance)
    suspend fun insertAttendanceRecords(records: List<AttendanceEntity>) = attendanceDao.insertAttendanceRecords(records)

    // --- Assessments ---
    fun getAssessmentsForSubject(subjectCode: String): Flow<List<AssessmentEntity>> = assessmentDao.getAssessmentsForSubjectFlow(subjectCode)
    suspend fun insertAssessment(assessment: AssessmentEntity) = assessmentDao.insertAssessment(assessment)
    suspend fun deleteAssessment(id: Int) = assessmentDao.deleteAssessmentById(id)

    // --- Marks ---
    fun getMarksForSubject(subjectCode: String): Flow<List<MarkEntity>> = markDao.getMarksForSubjectFlow(subjectCode)
    suspend fun getMarksForSubjectList(subjectCode: String): List<MarkEntity> = markDao.getMarksForSubject(subjectCode)
    suspend fun insertMark(mark: MarkEntity) = markDao.insertMark(mark)
    suspend fun insertMarks(marks: List<MarkEntity>) = markDao.insertMarks(marks)

    // --- Demo Data Initializer ---
    suspend fun preseedDemoData(facultyEmail: String) {
        // Pre-seed a default user
        if (getUserByEmail(facultyEmail) == null) {
            insertUser(
                UserEntity(
                    email = facultyEmail,
                    name = "Prof. Sarah Mitchell",
                    role = "FACULTY",
                    employeeId = "EMP-9082",
                    department = "Computer Science",
                    designation = "Associate Professor",
                    phone = "+1 (555) 732-8492",
                    academicYear = "2025-2026",
                    semester = "Semester VI",
                    section = "Section A"
                )
            )
        }

        // Add subjects if none exist
        val subjects = listOf(
            SubjectEntity(
                facultyEmail = facultyEmail,
                subjectName = "Data Structures & Algorithms",
                subjectCode = "CS-301",
                semester = "Semester V",
                branch = "CSE",
                section = "Sec A",
                academicYear = "2025-2026",
                numStudents = 20
            ),
            SubjectEntity(
                facultyEmail = facultyEmail,
                subjectName = "Database Management Systems",
                subjectCode = "CS-302",
                semester = "Semester V",
                branch = "CSE",
                section = "Sec B",
                academicYear = "2025-2026",
                numStudents = 15
            ),
            SubjectEntity(
                facultyEmail = facultyEmail,
                subjectName = "Artificial Intelligence & ML",
                subjectCode = "CS-401",
                semester = "Semester VII",
                branch = "CSE",
                section = "Sec A",
                academicYear = "2025-2026",
                numStudents = 18
            )
        )

        for (subj in subjects) {
            if (getSubjectByCode(subj.subjectCode) == null) {
                insertSubject(subj)
                preseedStudentsForSubject(subj.subjectCode, subj.branch, subj.section)
            }
        }
    }

    private suspend fun preseedStudentsForSubject(subjectCode: String, branch: String, section: String) {
        val lastNames = listOf("Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis", "Rodriguez", "Martinez", "Hernandez", "Lopez", "Gonzalez", "Wilson", "Anderson", "Thomas", "Taylor", "Moore", "Jackson", "Martin")
        val firstNames = listOf("James", "Mary", "John", "Patricia", "Robert", "Jennifer", "Michael", "Linda", "William", "Elizabeth", "David", "Barbara", "Richard", "Susan", "Joseph", "Jessica", "Thomas", "Sarah", "Charles", "Karen")

        val students = mutableListOf<StudentEntity>()
        val attendances = mutableListOf<AttendanceEntity>()
        val assessments = mutableListOf<AssessmentEntity>()
        val marks = mutableListOf<MarkEntity>()

        // Create 15-20 realistic students per subject
        val count = if (subjectCode == "CS-302") 15 else if (subjectCode == "CS-401") 18 else 20
        for (i in 1..count) {
            val roll = "2024$branch%03d".format(i)
            val name = "${firstNames[(i * 3) % firstNames.size]} ${lastNames[(i * 7) % lastNames.size]}"
            
            // Random reasonable marks
            val attPct = 65.0f + (i * 1.7f) % 35.0f
            val isLowAtt = attPct < 75.0f
            val basePerformance = if (isLowAtt) 0.6f else 0.85f // Simulate correlation

            val quiz = (4.0f + (i * 3.1f) % 6.0f) * basePerformance
            val assignment = (7.0f + (i * 1.9f) % 3.0f) * basePerformance
            val lab = (14.0f + (i * 2.5f) % 6.0f) * basePerformance
            val internal = (15.0f + (i * 4.3f) % 15.0f) * basePerformance
            val external = (30.0f + (i * 9.2f) % 45.0f) * basePerformance
            val total = quiz + assignment + lab + internal + external
            
            val grade = when {
                total >= 85 -> "A+"
                total >= 75 -> "A"
                total >= 65 -> "B"
                total >= 50 -> "C"
                total >= 40 -> "D"
                else -> "F"
            }

            students.add(
                StudentEntity(
                    rollNumber = roll,
                    subjectCode = subjectCode,
                    studentName = name,
                    department = branch,
                    semester = if (subjectCode.startsWith("CS-3")) "Semester V" else "Semester VII",
                    section = section,
                    attendancePercentage = attPct,
                    quizMarks = quiz,
                    assignmentMarks = assignment,
                    labMarks = lab,
                    internalMarks = internal,
                    externalMarks = external,
                    totalMarks = total,
                    grade = grade
                )
            )

            // Create some attendance logs (last 5 lectures)
            val dates = listOf("2026-06-01", "2026-06-02", "2026-06-03", "2026-06-04", "2026-06-05")
            for (dateIndex in dates.indices) {
                // If attendance percentage is lower, make them absent more often
                val present = (i * dateIndex * 7) % 100 < attPct
                attendances.add(
                    AttendanceEntity(
                        rollNumber = roll,
                        subjectCode = subjectCode,
                        date = dates[dateIndex],
                        isPresent = present
                    )
                )
            }
        }

        insertStudents(students)
        insertAttendanceRecords(attendances)

        // Create Assessments
        val quizAss = AssessmentEntity(subjectCode = subjectCode, name = "Quiz 1", maxMarks = 10.0f, weightage = 10.0f, numQuestions = 5)
        val assignAss = AssessmentEntity(subjectCode = subjectCode, name = "Assignment 1", maxMarks = 10.0f, weightage = 15.0f, numQuestions = 1)
        val internalAss = AssessmentEntity(subjectCode = subjectCode, name = "Mid Exam", maxMarks = 30.0f, weightage = 25.0f, numQuestions = 3)
        val extAss = AssessmentEntity(subjectCode = subjectCode, name = "End Semester", maxMarks = 100.0f, weightage = 50.0f, numQuestions = 5)

        // Rather than getting IDs directly, we insert and let Room generate them,
        // but for seeding we can manually map. Wait, we can insert them:
        assessmentDao.insertAssessment(quizAss)
        assessmentDao.insertAssessment(assignAss)
        assessmentDao.insertAssessment(internalAss)
        assessmentDao.insertAssessment(extAss)
    }
}
