package com.example.data.db

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users")
    fun getAllUsersFlow(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("DELETE FROM users WHERE email = :email")
    suspend fun deleteUserByEmail(email: String)
}

@Dao
interface SubjectDao {
    @Query("SELECT * FROM subjects WHERE facultyEmail = :email")
    fun getSubjectsForFacultyFlow(email: String): Flow<List<SubjectEntity>>

    @Query("SELECT * FROM subjects WHERE subjectCode = :subjectCode LIMIT 1")
    suspend fun getSubjectByCode(subjectCode: String): SubjectEntity?

    @Query("SELECT * FROM subjects")
    fun getAllSubjectsFlow(): Flow<List<SubjectEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: SubjectEntity)

    @Query("DELETE FROM subjects WHERE id = :id")
    suspend fun deleteSubjectById(id: Int)
}

@Dao
interface StudentDao {
    @Query("SELECT * FROM students WHERE subjectCode = :subjectCode")
    fun getStudentsForSubjectFlow(subjectCode: String): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE subjectCode = :subjectCode")
    suspend fun getStudentsForSubject(subjectCode: String): List<StudentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: StudentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudents(students: List<StudentEntity>)

    @Query("DELETE FROM students WHERE rollNumber = :rollNumber AND subjectCode = :subjectCode")
    suspend fun deleteStudentFromSubject(rollNumber: String, subjectCode: String)
}

@Dao
interface AttendanceDao {
    @Query("SELECT * FROM attendance WHERE subjectCode = :subjectCode")
    fun getAttendanceForSubjectFlow(subjectCode: String): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance WHERE subjectCode = :subjectCode")
    suspend fun getAttendanceForSubject(subjectCode: String): List<AttendanceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: AttendanceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendanceRecords(records: List<AttendanceEntity>)

    @Query("DELETE FROM attendance WHERE rollNumber = :rollNumber AND subjectCode = :subjectCode AND date = :date")
    suspend fun deleteAttendanceRecord(rollNumber: String, subjectCode: String, date: String)
}

@Dao
interface AssessmentDao {
    @Query("SELECT * FROM assessments WHERE subjectCode = :subjectCode")
    fun getAssessmentsForSubjectFlow(subjectCode: String): Flow<List<AssessmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssessment(assessment: AssessmentEntity)

    @Query("DELETE FROM assessments WHERE id = :id")
    suspend fun deleteAssessmentById(id: Int)
}

@Dao
interface MarkDao {
    @Query("SELECT * FROM marks WHERE subjectCode = :subjectCode")
    fun getMarksForSubjectFlow(subjectCode: String): Flow<List<MarkEntity>>

    @Query("SELECT * FROM marks WHERE subjectCode = :subjectCode")
    suspend fun getMarksForSubject(subjectCode: String): List<MarkEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMark(mark: MarkEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMarks(marks: List<MarkEntity>)
}
