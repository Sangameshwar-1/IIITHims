const express = require('express');
const router = express.Router();
const prisma = require('../prisma');
const { authenticate } = require('../middleware/auth');

router.get('/stats', authenticate, async (req, res) => {
  try {
    const { role, userId } = req.user;

    if (role === 'ADMIN') {
      const [totalStudents, totalFaculty, totalCourses, totalDepartments, pendingComplaints, pendingLeaves, totalBooks, totalHostels] = await Promise.all([
        prisma.student.count(),
        prisma.faculty.count(),
        prisma.course.count(),
        prisma.department.count(),
        prisma.complaint.count({ where: { status: 'Open' } }),
        prisma.leaveRequest.count({ where: { status: 'Pending' } }),
        prisma.libraryBook.count(),
        prisma.hostel.count()
      ]);
      return res.json({ totalStudents, totalFaculty, totalCourses, totalDepartments, pendingComplaints, pendingLeaves, totalBooks, totalHostels });
    }

    if (role === 'FACULTY') {
      const faculty = await prisma.faculty.findUnique({ where: { userId } });
      const [assignedSubjects, pendingLeaveRequests, recentNotices] = await Promise.all([
        prisma.facultySubject.count({ where: { facultyId: faculty.id } }),
        prisma.leaveRequest.count({ where: { applicantType: 'FACULTY', facultyId: faculty.id, status: 'Pending' } }),
        prisma.notice.findMany({ take: 5, orderBy: { publishedAt: 'desc' }, where: { targetRole: { in: ['ALL', 'FACULTY'] } } })
      ]);
      return res.json({ assignedSubjects, pendingLeaveRequests, recentNotices });
    }

    if (role === 'STUDENT') {
      const student = await prisma.student.findUnique({ where: { userId } });
      const [enrolledCourses, upcomingExams, issuedBooks, pendingComplaints] = await Promise.all([
        prisma.course.count({ where: { id: student.courseId } }),
        prisma.exam.count({ where: { courseId: student.courseId, status: 'Scheduled' } }),
        prisma.issuedBook.count({ where: { studentId: student.id, status: 'Issued' } }),
        prisma.complaint.count({ where: { studentId: student.id, status: { not: 'Closed' } } })
      ]);
      
      const attendance = await prisma.attendance.findMany({ where: { studentId: student.id } });
      const present = attendance.filter(a => a.status === 'Present').length;
      const attendancePercentage = attendance.length ? ((present / attendance.length) * 100).toFixed(2) : 0;
      
      return res.json({ enrolledCourses, upcomingExams, issuedBooks, pendingComplaints, attendancePercentage });
    }

    if (role === 'PARENT') {
      const parent = await prisma.parent.findUnique({ where: { userId }, include: { student: true } });
      const [pendingLeaveRequests, recentMarks] = await Promise.all([
        prisma.leaveRequest.count({ where: { studentId: parent.studentId, status: 'Pending' } }),
        prisma.mark.findMany({ where: { studentId: parent.studentId }, take: 5, orderBy: { createdAt: 'desc' }, include: { exam: true } })
      ]);
      
      const attendance = await prisma.attendance.findMany({ where: { studentId: parent.studentId } });
      const present = attendance.filter(a => a.status === 'Present').length;
      const attendancePercentage = attendance.length ? ((present / attendance.length) * 100).toFixed(2) : 0;

      return res.json({ studentName: `${parent.student.firstName} ${parent.student.lastName}`, attendancePercentage, recentMarks, pendingLeaveRequests });
    }

    res.status(403).json({ error: 'Role not supported for dashboard stats' });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

module.exports = router;
