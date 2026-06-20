const express = require('express');
const router = express.Router();
const prisma = require('../prisma');
const bcrypt = require('bcryptjs');

router.get('/', async (req, res) => {
  try {
    // 1. Delete all existing data in reverse order of dependencies
    await prisma.auditLog.deleteMany();
    await prisma.notification.deleteMany();
    await prisma.placement.deleteMany();
    await prisma.company.deleteMany();
    await prisma.leaveRequest.deleteMany();
    await prisma.issuedBook.deleteMany();
    await prisma.libraryBook.deleteMany();
    
    // Clear foreign keys first for rooms and students to avoid cycle issues
    await prisma.student.updateMany({ data: { roomId: null } });
    await prisma.room.deleteMany();
    await prisma.hostel.deleteMany();
    
    await prisma.transportRoute.deleteMany();
    await prisma.complaint.deleteMany();
    await prisma.notice.deleteMany();
    await prisma.mark.deleteMany();
    await prisma.attendance.deleteMany();
    await prisma.exam.deleteMany();
    await prisma.timetable.deleteMany();
    await prisma.facultySubject.deleteMany();
    await prisma.section.deleteMany();
    await prisma.batch.deleteMany();
    await prisma.subject.deleteMany();
    await prisma.course.deleteMany();
    await prisma.department.deleteMany();
    await prisma.parent.deleteMany();
    await prisma.student.deleteMany();
    await prisma.faculty.deleteMany();
    await prisma.user.deleteMany();

    // 2. Create Test Data
    const hash = await bcrypt.hash('password123', 10);

    // Users
    const adminUser = await prisma.user.create({ data: { email: 'admin@iiith.ac.in', passwordHash: hash, role: 'ADMIN' } });
    const facUser1 = await prisma.user.create({ data: { email: 'rajesh@iiith.ac.in', passwordHash: hash, role: 'FACULTY' } });
    const facUser2 = await prisma.user.create({ data: { email: 'priya@iiith.ac.in', passwordHash: hash, role: 'FACULTY' } });
    const stuUser1 = await prisma.user.create({ data: { email: 'aarav@students.iiith.ac.in', passwordHash: hash, role: 'STUDENT' } });
    const stuUser2 = await prisma.user.create({ data: { email: 'diya@students.iiith.ac.in', passwordHash: hash, role: 'STUDENT' } });
    const stuUser3 = await prisma.user.create({ data: { email: 'vivaan@students.iiith.ac.in', passwordHash: hash, role: 'STUDENT' } });
    const parentUser = await prisma.user.create({ data: { email: 'parent.sharma@email.com', passwordHash: hash, role: 'PARENT' } });

    // Departments
    const deptCSE = await prisma.department.create({ data: { name: 'Computer Science', code: 'CSE' } });
    const deptECE = await prisma.department.create({ data: { name: 'Electronics', code: 'ECE' } });

    // Courses
    const courseBTCSE = await prisma.course.create({ data: { name: 'B.Tech CSE', code: 'BTCSE', departmentId: deptCSE.id, duration: '4 Years', totalSemesters: 8 } });
    const courseBTECE = await prisma.course.create({ data: { name: 'B.Tech ECE', code: 'BTECE', departmentId: deptECE.id, duration: '4 Years', totalSemesters: 8 } });
    const courseMTCSE = await prisma.course.create({ data: { name: 'M.Tech CSE', code: 'MTCSE', departmentId: deptCSE.id, duration: '2 Years', totalSemesters: 4 } });

    // Subjects
    const subDS = await prisma.subject.create({ data: { name: 'Data Structures', code: 'CS201', courseId: courseBTCSE.id, semester: 3, credits: 4, type: 'Core' } });
    const subDBMS = await prisma.subject.create({ data: { name: 'DBMS', code: 'CS301', courseId: courseBTCSE.id, semester: 5, credits: 4, type: 'Core' } });
    const subDE = await prisma.subject.create({ data: { name: 'Digital Electronics', code: 'EC201', courseId: courseBTECE.id, semester: 3, credits: 3, type: 'Core' } });
    const subML = await prisma.subject.create({ data: { name: 'Machine Learning', code: 'CS501', courseId: courseMTCSE.id, semester: 1, credits: 4, type: 'Core' } });

    // Batches
    const batchCSE24 = await prisma.batch.create({ data: { name: 'CSE 2024', courseId: courseBTCSE.id, startYear: '2024', endYear: '2028', currentSemester: 1, capacity: 60 } });
    const batchECE24 = await prisma.batch.create({ data: { name: 'ECE 2024', courseId: courseBTECE.id, startYear: '2024', endYear: '2028', currentSemester: 1, capacity: 40 } });

    // Sections
    const secA = await prisma.section.create({ data: { name: 'A', batchId: batchCSE24.id, courseId: courseBTCSE.id, capacity: 30 } });
    const secB = await prisma.section.create({ data: { name: 'B', batchId: batchCSE24.id, courseId: courseBTCSE.id, capacity: 30 } });

    // Faculty Profiles
    const fac1 = await prisma.faculty.create({ data: { userId: facUser1.id, facultyId: 'FAC-001', firstName: 'Rajesh', lastName: 'Kumar', email: 'rajesh@iiith.ac.in', departmentId: deptCSE.id, designation: 'Associate Professor' } });
    const fac2 = await prisma.faculty.create({ data: { userId: facUser2.id, facultyId: 'FAC-002', firstName: 'Priya', lastName: 'Sharma', email: 'priya@iiith.ac.in', departmentId: deptECE.id, designation: 'Assistant Professor' } });

    // Student Profiles
    const stu1 = await prisma.student.create({ data: { userId: stuUser1.id, studentId: 'STU-2024-0001', firstName: 'Aarav', lastName: 'Sharma', email: 'aarav@students.iiith.ac.in', courseId: courseBTCSE.id, batchId: batchCSE24.id, sectionId: secA.id, departmentId: deptCSE.id } });
    const stu2 = await prisma.student.create({ data: { userId: stuUser2.id, studentId: 'STU-2024-0002', firstName: 'Diya', lastName: 'Patel', email: 'diya@students.iiith.ac.in', courseId: courseBTCSE.id, batchId: batchCSE24.id, sectionId: secA.id, departmentId: deptCSE.id } });
    const stu3 = await prisma.student.create({ data: { userId: stuUser3.id, studentId: 'STU-2024-0003', firstName: 'Vivaan', lastName: 'Singh', email: 'vivaan@students.iiith.ac.in', courseId: courseBTECE.id, batchId: batchECE24.id, departmentId: deptECE.id } });

    // Parent Profile
    await prisma.parent.create({ data: { userId: parentUser.id, studentId: stu1.id, firstName: 'Sharma', lastName: 'Parent', email: 'parent.sharma@email.com' } });

    // Faculty Subject Assignments
    await prisma.facultySubject.create({ data: { facultyId: fac1.id, subjectId: subDS.id, batchId: batchCSE24.id, sectionId: secA.id } });
    await prisma.facultySubject.create({ data: { facultyId: fac1.id, subjectId: subDBMS.id, batchId: batchCSE24.id, sectionId: secA.id } });
    await prisma.facultySubject.create({ data: { facultyId: fac2.id, subjectId: subDE.id, batchId: batchECE24.id } });

    // Timetable
    await prisma.timetable.create({ data: { courseId: courseBTCSE.id, batchId: batchCSE24.id, subjectId: subDS.id, day: 'Monday', startTime: '09:00', endTime: '10:00', room: 'Lecture Hall 1', facultyName: 'Rajesh Kumar' } });
    await prisma.timetable.create({ data: { courseId: courseBTCSE.id, batchId: batchCSE24.id, subjectId: subDBMS.id, day: 'Tuesday', startTime: '11:00', endTime: '12:00', room: 'Lecture Hall 2', facultyName: 'Rajesh Kumar' } });

    // Attendance
    await prisma.attendance.create({ data: { studentId: stu1.id, subjectId: subDS.id, facultyId: fac1.id, date: '2024-08-01', status: 'Present' } });
    await prisma.attendance.create({ data: { studentId: stu1.id, subjectId: subDS.id, facultyId: fac1.id, date: '2024-08-03', status: 'Present' } });
    await prisma.attendance.create({ data: { studentId: stu1.id, subjectId: subDS.id, facultyId: fac1.id, date: '2024-08-05', status: 'Absent' } });
    await prisma.attendance.create({ data: { studentId: stu1.id, subjectId: subDS.id, facultyId: fac1.id, date: '2024-08-08', status: 'Present' } });
    await prisma.attendance.create({ data: { studentId: stu1.id, subjectId: subDS.id, facultyId: fac1.id, date: '2024-08-10', status: 'Late' } });

    // Exams & Marks
    const exam1 = await prisma.exam.create({ data: { name: 'Mid-Term CSE 2024', type: 'MidTerm', subjectId: subDS.id, courseId: courseBTCSE.id, batchId: batchCSE24.id, date: '2024-10-15', totalMarks: 100, passingMarks: 40 } });
    await prisma.mark.create({ data: { examId: exam1.id, studentId: stu1.id, subjectId: subDS.id, marksObtained: 85, grade: 'A' } });
    await prisma.mark.create({ data: { examId: exam1.id, studentId: stu2.id, subjectId: subDS.id, marksObtained: 92, grade: 'A+' } });

    // Notices
    await prisma.notice.create({ data: { title: 'Welcome to New Academic Year', content: 'Classes begin August 1st.', category: 'General', targetRole: 'ALL', authorId: fac1.id } });
    await prisma.notice.create({ data: { title: 'Campus Placement Drive', content: 'Google is coming to campus.', category: 'Event', targetRole: 'STUDENT', authorId: fac1.id } });

    // Library
    const book1 = await prisma.libraryBook.create({ data: { title: 'Introduction to Algorithms', author: 'Cormen', isbn: '9780262033848', totalCopies: 5, availableCopies: 4, category: 'CS' } });
    await prisma.libraryBook.create({ data: { title: 'Database System Concepts', author: 'Silberschatz', totalCopies: 3, availableCopies: 3, category: 'CS' } });
    await prisma.libraryBook.create({ data: { title: 'Digital Design', author: 'Morris Mano', totalCopies: 4, availableCopies: 4, category: 'ECE' } });
    await prisma.libraryBook.create({ data: { title: 'Clean Code', author: 'Robert Martin', totalCopies: 2, availableCopies: 2, category: 'CS' } });
    await prisma.libraryBook.create({ data: { title: 'Design Patterns', author: 'GoF', totalCopies: 3, availableCopies: 3, category: 'CS' } });
    
    await prisma.issuedBook.create({ data: { bookId: book1.id, studentId: stu1.id, issueDate: '2024-12-01', dueDate: '2025-01-15' } });

    // Hostel & Rooms
    const hostel1 = await prisma.hostel.create({ data: { name: 'Nilgiri Hostel', type: 'Boys', capacity: 200 } });
    const room101 = await prisma.room.create({ data: { hostelId: hostel1.id, roomNumber: '101', floor: 1, capacity: 2, type: 'Double', occupancy: 1 } });
    await prisma.room.create({ data: { hostelId: hostel1.id, roomNumber: '102', floor: 1, capacity: 2, type: 'Double' } });
    await prisma.student.update({ where: { id: stu1.id }, data: { roomId: room101.id } });

    // Transport
    await prisma.transportRoute.create({ data: { routeName: 'Route 1', busNumber: 'IIT-001', startPoint: 'Secunderabad', endPoint: 'Campus' } });

    // Leave
    await prisma.leaveRequest.create({ data: { applicantType: 'STUDENT', studentId: stu1.id, leaveType: 'Sick', startDate: '2024-11-01', endDate: '2024-11-03', reason: 'Viral fever' } });

    // Placement
    const compGoogle = await prisma.company.create({ data: { name: 'Google', industry: 'Tech' } });
    await prisma.company.create({ data: { name: 'Microsoft', industry: 'Tech' } });
    await prisma.placement.create({ data: { companyId: compGoogle.id, studentId: stu1.id, position: 'SDE Intern' } });

    // Complaint
    await prisma.complaint.create({ data: { studentId: stu1.id, title: 'Library hours too short', description: 'Please extend library hours during exams.', category: 'Infrastructure' } });

    const counts = {
      users: await prisma.user.count(),
      students: await prisma.student.count(),
      faculty: await prisma.faculty.count(),
      parents: await prisma.parent.count(),
      departments: await prisma.department.count(),
      courses: await prisma.course.count(),
      subjects: await prisma.subject.count(),
      batches: await prisma.batch.count(),
      exams: await prisma.exam.count(),
      libraryBooks: await prisma.libraryBook.count(),
      hostels: await prisma.hostel.count(),
      companies: await prisma.company.count(),
    };

    res.json({
      message: 'Database seeded successfully!',
      credentials: {
        admin: { email: 'admin@iiith.ac.in', password: 'password123' },
        faculty: [{ email: 'rajesh@iiith.ac.in' }, { email: 'priya@iiith.ac.in' }],
        students: [{ email: 'aarav@students.iiith.ac.in' }, { email: 'diya@students.iiith.ac.in' }, { email: 'vivaan@students.iiith.ac.in' }],
        parent: { email: 'parent.sharma@email.com' },
        allPasswords: 'password123'
      },
      counts
    });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

module.exports = router;
