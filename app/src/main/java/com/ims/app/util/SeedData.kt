package com.ims.app.util

import com.ims.app.data.database.ImsDatabase
import com.ims.app.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

/**
 * Populates the database with sample data for demonstration.
 * Called once on first app launch; skips if data already exists.
 */
object SeedData {

    suspend fun seedIfEmpty(database: ImsDatabase) = withContext(Dispatchers.IO) {
        val courseDao = database.courseDao()
        val studentDao = database.studentDao()
        val examDao = database.examDao()
        val newsDao = database.newsDao()

        // Only seed if database is empty
        val existingCourses = courseDao.getAllCourses().firstOrNull()
        if (!existingCourses.isNullOrEmpty()) return@withContext

        // ─── Courses ────────────────────────────────────────
        val cseId = courseDao.insertCourse(Course(name = "Computer Science & Engineering", code = "CSE", department = "Engineering", duration = "4 Years"))
        val eceId = courseDao.insertCourse(Course(name = "Electronics & Communication", code = "ECE", department = "Engineering", duration = "4 Years"))
        val meId = courseDao.insertCourse(Course(name = "Mechanical Engineering", code = "ME", department = "Engineering", duration = "4 Years"))
        val mbaId = courseDao.insertCourse(Course(name = "Master of Business Administration", code = "MBA", department = "Management", duration = "2 Years"))

        // ─── Batches ────────────────────────────────────────
        val batch1 = courseDao.insertBatch(Batch(name = "CSE 2024-2028 A", courseId = cseId, startYear = 2024, endYear = 2028, maxStrength = 60, currentStrength = 45))
        val batch2 = courseDao.insertBatch(Batch(name = "CSE 2024-2028 B", courseId = cseId, startYear = 2024, endYear = 2028, maxStrength = 60, currentStrength = 38))
        val batch3 = courseDao.insertBatch(Batch(name = "ECE 2024-2028", courseId = eceId, startYear = 2024, endYear = 2028, maxStrength = 60, currentStrength = 52))
        val batch4 = courseDao.insertBatch(Batch(name = "ME 2023-2027", courseId = meId, startYear = 2023, endYear = 2027, maxStrength = 60, currentStrength = 48))
        val batch5 = courseDao.insertBatch(Batch(name = "MBA 2024-2026", courseId = mbaId, startYear = 2024, endYear = 2026, maxStrength = 40, currentStrength = 32))

        // ─── Subjects ───────────────────────────────────────
        val dsaId = courseDao.insertSubject(Subject(name = "Data Structures & Algorithms", code = "CS201", courseId = cseId, creditHours = 4, semester = 3))
        val dbmsId = courseDao.insertSubject(Subject(name = "Database Management Systems", code = "CS301", courseId = cseId, creditHours = 3, semester = 5))
        val osId = courseDao.insertSubject(Subject(name = "Operating Systems", code = "CS302", courseId = cseId, creditHours = 3, semester = 5))
        val cnId = courseDao.insertSubject(Subject(name = "Computer Networks", code = "CS401", courseId = cseId, creditHours = 3, semester = 7))
        val mathId = courseDao.insertSubject(Subject(name = "Engineering Mathematics III", code = "MA201", courseId = cseId, creditHours = 4, semester = 3))
        courseDao.insertSubject(Subject(name = "Digital Signal Processing", code = "EC301", courseId = eceId, creditHours = 3, semester = 5))
        courseDao.insertSubject(Subject(name = "Thermodynamics", code = "ME201", courseId = meId, creditHours = 4, semester = 3))
        courseDao.insertSubject(Subject(name = "Marketing Management", code = "MB101", courseId = mbaId, creditHours = 3, semester = 1, type = "Core"))

        // ─── Students ───────────────────────────────────────
        val students = listOf(
            Student(studentId = "STU-2024-0001", firstName = "Aarav", lastName = "Sharma", email = "aarav.s@institute.edu", phone = "9876543210", dateOfBirth = "2005-03-15", gender = "Male", address = "123 MG Road", city = "Hyderabad", state = "Telangana", pinCode = "500001", bloodGroup = "B+", batchId = batch1, courseId = cseId, admissionDate = "2024-07-01"),
            Student(studentId = "STU-2024-0002", firstName = "Priya", lastName = "Patel", email = "priya.p@institute.edu", phone = "9876543211", dateOfBirth = "2005-06-22", gender = "Female", address = "45 Tank Bund", city = "Hyderabad", state = "Telangana", pinCode = "500003", bloodGroup = "A+", batchId = batch1, courseId = cseId, admissionDate = "2024-07-01"),
            Student(studentId = "STU-2024-0003", firstName = "Rohan", lastName = "Gupta", email = "rohan.g@institute.edu", phone = "9876543212", dateOfBirth = "2005-01-10", gender = "Male", address = "78 Jubilee Hills", city = "Hyderabad", state = "Telangana", pinCode = "500033", bloodGroup = "O+", batchId = batch2, courseId = cseId, admissionDate = "2024-07-02"),
            Student(studentId = "STU-2024-0004", firstName = "Ananya", lastName = "Reddy", email = "ananya.r@institute.edu", phone = "9876543213", dateOfBirth = "2005-09-05", gender = "Female", address = "22 Banjara Hills", city = "Hyderabad", state = "Telangana", pinCode = "500034", bloodGroup = "AB+", batchId = batch3, courseId = eceId, admissionDate = "2024-07-01"),
            Student(studentId = "STU-2024-0005", firstName = "Vikram", lastName = "Singh", email = "vikram.s@institute.edu", phone = "9876543214", dateOfBirth = "2004-12-20", gender = "Male", address = "56 Secunderabad", city = "Hyderabad", state = "Telangana", pinCode = "500009", bloodGroup = "B-", batchId = batch4, courseId = meId, admissionDate = "2023-07-15"),
            Student(studentId = "STU-2024-0006", firstName = "Neha", lastName = "Kumar", email = "neha.k@institute.edu", phone = "9876543215", dateOfBirth = "2005-04-18", gender = "Female", address = "89 Gachibowli", city = "Hyderabad", state = "Telangana", pinCode = "500032", bloodGroup = "A-", batchId = batch1, courseId = cseId, admissionDate = "2024-07-03"),
            Student(studentId = "STU-2024-0007", firstName = "Arjun", lastName = "Nair", email = "arjun.n@institute.edu", phone = "9876543216", dateOfBirth = "2005-07-30", gender = "Male", address = "34 Madhapur", city = "Hyderabad", state = "Telangana", pinCode = "500081", bloodGroup = "O-", batchId = batch2, courseId = cseId, admissionDate = "2024-07-02"),
            Student(studentId = "STU-2024-0008", firstName = "Kavya", lastName = "Iyer", email = "kavya.i@institute.edu", phone = "9876543217", dateOfBirth = "2003-11-12", gender = "Female", address = "67 Ameerpet", city = "Hyderabad", state = "Telangana", pinCode = "500016", bloodGroup = "B+", batchId = batch5, courseId = mbaId, admissionDate = "2024-07-05"),
            Student(studentId = "STU-2024-0009", firstName = "Rahul", lastName = "Deshmukh", email = "rahul.d@institute.edu", phone = "9876543218", dateOfBirth = "2005-02-25", gender = "Male", address = "12 Kukatpally", city = "Hyderabad", state = "Telangana", pinCode = "500072", bloodGroup = "A+", batchId = batch3, courseId = eceId, admissionDate = "2024-07-01"),
            Student(studentId = "STU-2024-0010", firstName = "Sneha", lastName = "Joshi", email = "sneha.j@institute.edu", phone = "9876543219", dateOfBirth = "2005-08-07", gender = "Female", address = "90 Kondapur", city = "Hyderabad", state = "Telangana", pinCode = "500084", bloodGroup = "O+", batchId = batch1, courseId = cseId, admissionDate = "2024-07-04")
        )

        val studentIds = mutableListOf<Long>()
        students.forEach { studentIds.add(studentDao.insertStudent(it)) }

        // ─── Guardians ──────────────────────────────────────
        studentDao.insertGuardian(Guardian(studentId = studentIds[0], name = "Rajesh Sharma", relation = "Father", phone = "9800000001", email = "rajesh.sharma@email.com", occupation = "Software Engineer", isEmergencyContact = true))
        studentDao.insertGuardian(Guardian(studentId = studentIds[0], name = "Meera Sharma", relation = "Mother", phone = "9800000002", email = "meera.sharma@email.com", occupation = "Doctor"))
        studentDao.insertGuardian(Guardian(studentId = studentIds[1], name = "Amit Patel", relation = "Father", phone = "9800000003", occupation = "Business Owner", isEmergencyContact = true))
        studentDao.insertGuardian(Guardian(studentId = studentIds[2], name = "Suresh Gupta", relation = "Father", phone = "9800000004", occupation = "Teacher", isEmergencyContact = true))
        studentDao.insertGuardian(Guardian(studentId = studentIds[3], name = "Lakshmi Reddy", relation = "Mother", phone = "9800000005", occupation = "Professor", isEmergencyContact = true))

        // ─── Exams ──────────────────────────────────────────
        val exam1 = examDao.insertExam(Exam(name = "DSA Mid-Semester", type = "Marks", subjectId = dsaId, courseId = cseId, batchId = batch1, date = "2025-03-15", startTime = "09:00", endTime = "12:00", totalMarks = 100, passingMarks = 40, venue = "Hall A", status = "Completed"))
        val exam2 = examDao.insertExam(Exam(name = "DBMS End-Semester", type = "Marks", subjectId = dbmsId, courseId = cseId, batchId = batch1, date = "2025-05-20", startTime = "14:00", endTime = "17:00", totalMarks = 100, passingMarks = 40, venue = "Hall B", status = "Scheduled"))
        val exam3 = examDao.insertExam(Exam(name = "OS Quiz 1", type = "Marks", subjectId = osId, courseId = cseId, batchId = batch2, date = "2025-04-10", startTime = "10:00", endTime = "11:00", totalMarks = 30, passingMarks = 12, venue = "Room 201", status = "Completed"))
        examDao.insertExam(Exam(name = "CN Lab Assessment", type = "Grade", subjectId = cnId, courseId = cseId, batchId = batch1, date = "2025-06-01", startTime = "09:00", endTime = "12:00", totalMarks = 50, passingMarks = 20, venue = "Lab 3", status = "Scheduled"))
        examDao.insertExam(Exam(name = "Math III Supplementary", type = "Marks", subjectId = mathId, courseId = cseId, batchId = batch1, date = "2025-06-15", startTime = "09:00", endTime = "12:00", totalMarks = 100, passingMarks = 40, venue = "Hall A", status = "Scheduled"))

        // ─── Exam Results ───────────────────────────────────
        examDao.insertResults(listOf(
            ExamResult(examId = exam1, studentId = studentIds[0], marksObtained = 87.0, grade = "A", status = "Passed", evaluatedAt = System.currentTimeMillis()),
            ExamResult(examId = exam1, studentId = studentIds[1], marksObtained = 92.0, grade = "A+", status = "Passed", evaluatedAt = System.currentTimeMillis()),
            ExamResult(examId = exam1, studentId = studentIds[5], marksObtained = 78.0, grade = "B+", status = "Passed", evaluatedAt = System.currentTimeMillis()),
            ExamResult(examId = exam1, studentId = studentIds[9], marksObtained = 35.0, grade = "F", status = "Failed", evaluatedAt = System.currentTimeMillis()),
            ExamResult(examId = exam3, studentId = studentIds[2], marksObtained = 25.0, grade = "A", status = "Passed", evaluatedAt = System.currentTimeMillis()),
            ExamResult(examId = exam3, studentId = studentIds[6], marksObtained = 18.0, grade = "B", status = "Passed", evaluatedAt = System.currentTimeMillis())
        ))

        // ─── News ───────────────────────────────────────────
        newsDao.insertNews(News(title = "Welcome to Academic Year 2024-25!", content = "We are excited to welcome all students to the new academic year. Orientation sessions will begin from July 5th.", author = "Admin", category = "Academic"))
        newsDao.insertNews(News(title = "Annual Tech Fest - InnoVerse 2025", content = "The annual technology festival InnoVerse 2025 will be held from March 20-22. Register your teams now!", author = "Student Council", category = "Event"))
        newsDao.insertNews(News(title = "Mid-Semester Exam Schedule Released", content = "The mid-semester examination schedule for all departments has been published. Exams begin March 10th.", author = "Examination Cell", category = "Academic"))
        newsDao.insertNews(News(title = "Holi Holiday Notice", content = "The institute will remain closed on March 14th on account of Holi. Classes resume on March 17th.", author = "Admin", category = "Holiday"))
        newsDao.insertNews(News(title = "Campus Placement Drive - TCS", content = "TCS will be conducting a campus placement drive on April 5th. Eligible students can register by March 25th.", author = "Placement Cell", category = "General"))
    }
}
