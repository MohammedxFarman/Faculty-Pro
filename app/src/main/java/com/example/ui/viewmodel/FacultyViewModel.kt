package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.gemini.GeminiService
import com.example.data.model.*
import com.example.data.repository.AcademicRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FacultyViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private val repository = AcademicRepository(db)
    private val geminiService = GeminiService()

    // --- Active User Sessions ---
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _currentScreen = MutableStateFlow("login") // login, overview, students, attendance, marks, profile, ai_insights, presentation, print
    val currentScreen: StateFlow<String> = _currentScreen.asStateFlow()

    // --- Loaded States ---
    private val _subjects = MutableStateFlow<List<SubjectEntity>>(emptyList())
    val subjects: StateFlow<List<SubjectEntity>> = _subjects.asStateFlow()

    private val _selectedSubjectCode = MutableStateFlow<String>("")
    val selectedSubjectCode: StateFlow<String> = _selectedSubjectCode.asStateFlow()

    private val _students = MutableStateFlow<List<StudentEntity>>(emptyList())
    val students: StateFlow<List<StudentEntity>> = _students.asStateFlow()

    private val _assessments = MutableStateFlow<List<AssessmentEntity>>(emptyList())
    val assessments: StateFlow<List<AssessmentEntity>> = _assessments.asStateFlow()

    private val _marks = MutableStateFlow<List<MarkEntity>>(emptyList())
    val marks: StateFlow<List<MarkEntity>> = _marks.asStateFlow()

    private val _attendance = MutableStateFlow<List<AttendanceEntity>>(emptyList())
    val attendance: StateFlow<List<AttendanceEntity>> = _attendance.asStateFlow()

    // --- AI & Export Statuses ---
    private val _aiAnalysisResult = MutableStateFlow<String>("")
    val aiAnalysisResult: StateFlow<String> = _aiAnalysisResult.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val _attendanceDate = MutableStateFlow(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()))
    val attendanceDate: StateFlow<String> = _attendanceDate.asStateFlow()

    // --- Computed Metrics (Realtime calculation caching) ---
    private val _metrics = MutableStateFlow<AnalyticsMetrics?>(null)
    val metrics: StateFlow<AnalyticsMetrics?> = _metrics.asStateFlow()

    init {
        // Automatically default session for Prof Sarah Mitchell
        // to make prototype premium instantly playable.
        viewModelScope.launch {
            repository.preseedDemoData("sarah@college.edu")
            login("sarah@college.edu", "password")
        }
    }

    fun setScreen(screen: String) {
        _currentScreen.value = screen
    }

    fun setAttendanceDate(date: String) {
        _attendanceDate.value = date
    }

    // --- Authentication Actions ---
    fun login(email: String, roleRequired: String = "FACULTY") {
        viewModelScope.launch {
            val user = repository.getUserByEmail(email)
            if (user != null) {
                _currentUser.value = user
                _currentScreen.value = "overview"
                loadFacultyStates(user.email)
            } else {
                // Mock enrollment for any custom collegiate login
                val newUser = UserEntity(
                    email = email,
                    name = email.substringBefore("@").replaceFirstChar { it.uppercase() } + " Professional",
                    role = roleRequired,
                    employeeId = "EMP-${(1000..9999).random()}",
                    department = "CSE Department",
                    designation = if (roleRequired == "ADMIN") "Administrator" else "Lecturer"
                )
                repository.insertUser(newUser)
                _currentUser.value = newUser
                _currentScreen.value = "overview"
                loadFacultyStates(newUser.email)
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        _currentScreen.value = "login"
    }

    fun updateProfile(name: String, empId: String, dept: String, desig: String, phone: String, year: String, sem: String, sec: String) {
        viewModelScope.launch {
            val current = _currentUser.value ?: return@launch
            val updated = current.copy(
                name = name,
                employeeId = empId,
                department = dept,
                designation = desig,
                phone = phone,
                academicYear = year,
                semester = sem,
                section = sec
            )
            repository.insertUser(updated)
            _currentUser.value = updated
        }
    }

    // --- State Collectors ---
    private fun loadFacultyStates(email: String) {
        viewModelScope.launch {
            // Collect subjects reactively
            repository.getSubjectsForFaculty(email).collect { list ->
                _subjects.value = list
                if (list.isNotEmpty() && _selectedSubjectCode.value.isEmpty()) {
                    selectSubject(list.first().subjectCode)
                }
            }
        }
    }

    fun selectSubject(subjectCode: String) {
        _selectedSubjectCode.value = subjectCode
        // Start collection for assessments, students, marks and attendance
        viewModelScope.launch {
            launch {
                repository.getStudentsForSubject(subjectCode).collect { list ->
                    _students.value = list
                    recomputeMetrics()
                }
            }
            launch {
                repository.getAssessmentsForSubject(subjectCode).collect { list ->
                    _assessments.value = list
                    recomputeMetrics()
                }
            }
            launch {
                repository.getAttendanceForSubject(subjectCode).collect { list ->
                    _attendance.value = list
                    recomputeMetrics()
                }
            }
            launch {
                repository.getMarksForSubject(subjectCode).collect { list ->
                    _marks.value = list
                    recomputeMetrics()
                }
            }
        }
    }

    // --- Subjects handling ---
    fun addSubject(name: String, code: String, sem: String, branch: String, sec: String, year: String) {
        viewModelScope.launch {
            val email = _currentUser.value?.email ?: "sarah@college.edu"
            val numNewStudents = 15
            val newSub = SubjectEntity(
                facultyEmail = email,
                subjectName = name,
                subjectCode = code,
                semester = sem,
                branch = branch,
                section = sec,
                academicYear = year,
                numStudents = numNewStudents
            )
            repository.insertSubject(newSub)
            // populate template students immediately for visual richness
            repository.insertStudents(
                (1..numNewStudents).map { i ->
                    val roll = "${year.takeLast(2)}${branch}%03d".format(i)
                    StudentEntity(
                        rollNumber = roll,
                        subjectCode = code,
                        studentName = getFakeName(i),
                        department = branch,
                        semester = sem,
                        section = sec,
                        attendancePercentage = 70f + (i * 1.5f).coerceAtMost(30f),
                        totalMarks = 68.0f + (i % 5)*5.0f,
                        quizMarks = 8f,
                        assignmentMarks = 8f,
                        labMarks = 16f,
                        internalMarks = 22f,
                        externalMarks = 45f,
                        grade = "A"
                    )
                }
            )
            // Set Quiz & End Sem auto milestones columns
            repository.insertAssessment(AssessmentEntity(subjectCode = code, name = "Internal Exam 1", maxMarks = 30f, weightage = 25f))
            repository.insertAssessment(AssessmentEntity(subjectCode = code, name = "Assignment 1", maxMarks = 10f, weightage = 15f))
            repository.insertAssessment(AssessmentEntity(subjectCode = code, name = "End Semester", maxMarks = 100f, weightage = 50f))

            _selectedSubjectCode.value = code
        }
    }

    fun deleteSubject(id: Int) {
        viewModelScope.launch {
            repository.deleteSubject(id)
            if (_subjects.value.isNotEmpty()) {
                val rem = _subjects.value.filter { it.id != id }
                if (rem.isNotEmpty()) selectSubject(rem.first().subjectCode)
            }
        }
    }

    // --- Student record inserts/inputs ---
    fun addStudent(roll: String, name: String, dept: String, sem: String, sec: String, attPct: Float) {
        viewModelScope.launch {
            val subCode = _selectedSubjectCode.value
            repository.insertStudent(
                StudentEntity(
                    rollNumber = roll,
                    subjectCode = subCode,
                    studentName = name,
                    department = dept,
                    semester = sem,
                    section = sec,
                    attendancePercentage = attPct,
                    grade = "B"
                )
            )
        }
    }

    fun deleteStudent(roll: String) {
        viewModelScope.launch {
            repository.deleteStudentFromSubject(roll, _selectedSubjectCode.value)
        }
    }

    fun mockImportExcel(dataString: String) {
        viewModelScope.launch {
            // Excel import Simulation engine
            val parsedLines = dataString.lines().filter { it.isNotBlank() }
            val currentSubCode = _selectedSubjectCode.value
            val currentSub = _subjects.value.find { it.subjectCode == currentSubCode } ?: return@launch
            
            val importedStudents = parsedLines.mapIndexed { idx, line ->
                val parts = line.split(",")
                val roll = parts.getOrNull(0)?.trim() ?: "IMP${(1000..9999).random()}"
                val name = parts.getOrNull(1)?.trim() ?: "Imported Student #$idx"
                val attPct = parts.getOrNull(2)?.trim()?.toFloatOrNull() ?: 85.0f
                
                StudentEntity(
                    rollNumber = roll,
                    subjectCode = currentSubCode,
                    studentName = name,
                    department = currentSub.branch,
                    semester = currentSub.semester,
                    section = currentSub.section,
                    attendancePercentage = attPct
                )
            }
            repository.insertStudents(importedStudents)
        }
    }

    // --- Dynamic Assessment Columns ---
    fun addAssessment(name: String, maxMarks: Float, weightage: Float, questions: Int, passCrit: Float) {
        viewModelScope.launch {
            val service = _selectedSubjectCode.value
            repository.insertAssessment(
                AssessmentEntity(
                    subjectCode = service,
                    name = name,
                    maxMarks = maxMarks,
                    weightage = weightage,
                    numQuestions = questions,
                    passingCriteria = passCrit
                )
            )
        }
    }

    fun removeAssessment(id: Int) {
        viewModelScope.launch {
            repository.deleteAssessment(id)
        }
    }

    fun saveStudentMarks(roll: String, assessmentId: Int, mark: Float) {
        viewModelScope.launch {
            val subCode = _selectedSubjectCode.value
            repository.insertMark(
                MarkEntity(
                    rollNumber = roll,
                    subjectCode = subCode,
                    assessmentId = assessmentId,
                    marksObtained = mark
                )
            )
            updateStudentTotalMarks(roll)
        }
    }

    private suspend fun updateStudentTotalMarks(roll: String) {
        val subCode = _selectedSubjectCode.value
        val studentRecords = repository.getStudentsForSubjectList(subCode)
        val assocStudent = studentRecords.find { it.rollNumber == roll } ?: return

        val stdMarks = repository.getMarksForSubjectList(subCode).filter { it.rollNumber == roll }
        val allAssessments = _assessments.value

        var computedTotal = 0f
        var weightTotal = 0f
        for (ass in allAssessments) {
            val mark = stdMarks.find { it.assessmentId == ass.id }?.marksObtained ?: 0f
            // Weightage-based score adjustment:
            // Score = (obtained / maxMarks) * weightage
            val scoreContrib = if (ass.maxMarks > 0) (mark / ass.maxMarks) * ass.weightage else 0f
            computedTotal += scoreContrib
            weightTotal += ass.weightage
        }

        // Add default/fixed items if weightage doesn't sum to 100
        val finalGrade = when {
            computedTotal >= 85 -> "A+"
            computedTotal >= 75 -> "A"
            computedTotal >= 65 -> "B"
            computedTotal >= 50 -> "C"
            computedTotal >= 40 -> "D"
            else -> "F"
        }

        repository.insertStudent(
            assocStudent.copy(
                totalMarks = computedTotal,
                grade = finalGrade
            )
        )
    }

    // --- Attendance Mark/Submit ---
    fun saveAttendance(roll: String, isPresent: Boolean) {
        viewModelScope.launch {
            val subCode = _selectedSubjectCode.value
            val date = _attendanceDate.value
            repository.insertAttendance(
                AttendanceEntity(
                    rollNumber = roll,
                    subjectCode = subCode,
                    date = date,
                    isPresent = isPresent
                )
            )
            updateStudentAttendancePercentage(roll)
        }
    }

    private suspend fun updateStudentAttendancePercentage(roll: String) {
        val subCode = _selectedSubjectCode.value
        val allLogs = repository.getAttendanceForSubjectList(subCode).filter { it.rollNumber == roll }
        if (allLogs.isEmpty()) return

        val presents = allLogs.count { it.isPresent }
        val pct = (presents.toFloat() / allLogs.size.toFloat()) * 100f
        
        val list = repository.getStudentsForSubjectList(subCode)
        val studentObj = list.find { it.rollNumber == roll } ?: return
        repository.insertStudent(studentObj.copy(attendancePercentage = pct))
    }

    // --- Advanced Automated Math Engine (Power BI/Tableau KPIs) ---
    private fun recomputeMetrics() {
        val allStudents = _students.value
        if (allStudents.isEmpty()) {
            _metrics.value = null
            return
        }

        val totalRecords = allStudents.size
        val avgScore = allStudents.map { it.totalMarks }.average().toFloat()
        val maxScore = allStudents.maxOfOrNull { it.totalMarks } ?: 0.0f
        val minScore = allStudents.minOfOrNull { it.totalMarks } ?: 0.0f
        val passThreshold = 40.0f
        val passQty = allStudents.count { it.totalMarks >= passThreshold }
        val passPct = (passQty.toFloat() / totalRecords) * 100.0f
        val failPct = 100.0f - passPct

        // Grade Distro
        val dist = allStudents.groupBy { it.grade }.mapValues { it.value.size }
        val topPerformers = allStudents.sortedByDescending { it.totalMarks }.take(5)
        val riskStudents = allStudents.filter { it.totalMarks < passThreshold || it.attendancePercentage < 75.0f }

        // Attendance stats
        val avgAttendance = allStudents.map { it.attendancePercentage }.average().toFloat()
        val lowAttendanceQty = allStudents.count { it.attendancePercentage < 75.0f }

        // KPI Subject Performance Index (SPI)
        // Calculated out of 10
        val spi = (avgScore / 100.0f) * 10.0f

        // Correlation computation
        // Attendance-Performance coefficient simulation
        val highAttendanceStudents = allStudents.filter { it.attendancePercentage >= 75.0f }
        val lowAttendanceStudents = allStudents.filter { it.attendancePercentage < 75.0f }
        val correlationFact = if (highAttendanceStudents.isNotEmpty() && lowAttendanceStudents.isNotEmpty()) {
            val highAvg = highAttendanceStudents.map { it.totalMarks }.average()
            val lowAvg = lowAttendanceStudents.map { it.totalMarks }.average()
            "Average grade for active students (attendance >= 75%%) is %.1f%% compared to %.1f%% for students below threshold.".format(highAvg, lowAvg)
        } else {
            "Not enough student data variation to draw correlations. Ensure both low and high attendance records exist."
        }

        _metrics.value = AnalyticsMetrics(
            totalStudents = totalRecords,
            avgMarks = avgScore,
            highestMarks = maxScore,
            lowestMarks = minScore,
            passPercentage = passPct,
            failPercentage = failPct,
            avgAttendance = avgAttendance,
            lowAttendanceCount = lowAttendanceQty,
            gradeDistribution = dist,
            topPerformers = topPerformers,
            studentsAtRisk = riskStudents,
            spiValue = spi.coerceIn(0f, 10f),
            correlationInsight = correlationFact
        )
    }

    // --- AI Analytics Generation ---
    fun fetchGeminiInsights() {
        viewModelScope.launch {
            _isAnalyzing.value = true
            _aiAnalysisResult.value = ""

            // Gather dashboard analytical contextual strings to craft prompt
            val subName = _subjects.value.find { it.subjectCode == _selectedSubjectCode.value }?.subjectName ?: "Science"
            val numStudents = _students.value.size
            val avg = _metrics.value?.avgMarks ?: 0f
            val hi = _metrics.value?.highestMarks ?: 0f
            val passRate = _metrics.value?.passPercentage ?: 0f
            val lowAtt = _metrics.value?.lowAttendanceCount ?: 0
            val weaklings = _metrics.value?.studentsAtRisk?.joinToString { "${it.studentName} (Roll: ${it.rollNumber}, Total: %.1f%%, Att: %.0f%%)".format(it.totalMarks, it.attendancePercentage) } ?: "None"
            val gradeDist = _metrics.value?.gradeDistribution?.entries?.joinToString { "${it.key}: ${it.value}" } ?: "None"

            val prompt = """
                You are the AI Analytics Engine built into Faculty Pro, a premium academic ERP dashboard.
                Please compile an executive-level performance summary and teaching brief for the Course: $subName.
                
                Current Performance Context:
                - Class Strength: $numStudents Students
                - Class Average Grade Point: ${"%.2f".format(avg)}%
                - Highest Score Scored: ${"%.1f".format(hi)}/100
                - Pass Percentile: ${"%.1f".format(passRate)}%
                - Students with < 75% Lecture Attendance: $lowAtt
                - Current distribution of grades in the class: $gradeDist
                - Students at Academic or Attendance Risk: $weaklings

                Format the output strictly into these 5 beautifully compiled sections without repeating details:
                1. COURSE PERFORMANCE OVERVIEW: Write a concise, 3-sentence summary highlighting the current class success.
                2. TOPIC STRENGTHS & DISCREPANCIES: Identify potential learning gaps (e.g., are tests too high, are students struggling on End Semester assessments?).
                3. CORRELATION RECOGNITION: Summarize how attendance under 75% appears to hurt internal grades.
                4. TARGETED RECOVERY LIST: List the specific names of students at academic risk and a fast remedy action for each.
                5. ACCREDITATION INSIGHTS (NBA/NAAC): Note if this SPI conforms to elite college milestones.
                
                Keep the tone extremely helpful, technical, professional, and clear. Avoid writing long intro/outro text.
            """.trimIndent()

            val responseText = geminiService.generateAnalysis(prompt)
            _aiAnalysisResult.value = responseText
            _isAnalyzing.value = false
        }
    }

    private fun getFakeName(index: Int): String {
        val names = listOf("Alexander Wright", "Sophia Bennet", "Liam Patel", "Olivia Chen", "Noah Jenkins", "Isabella Ross", "Mason G.", "Ava Ramirez", "Ethan Vance", "Mia Lopez", "Yusuf K.", "Chloe Dubois", "Jackson Thorne", "Emily Sato", "Aiden Gallagher")
        return names.getOrNull(index % names.size) ?: "Student #$index"
    }
}

// Complex enterprise metrics data-transfer object definition
data class AnalyticsMetrics(
    val totalStudents: Int,
    val avgMarks: Float,
    val highestMarks: Float,
    val lowestMarks: Float,
    val passPercentage: Float,
    val failPercentage: Float,
    val avgAttendance: Float,
    val lowAttendanceCount: Int,
    val gradeDistribution: Map<String, Int>,
    val topPerformers: List<StudentEntity>,
    val studentsAtRisk: List<StudentEntity>,
    val spiValue: Float,
    val correlationInsight: String
)
