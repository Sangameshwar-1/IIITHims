const express = require('express');
const router = express.Router();
const prisma = require('../prisma');
const bcrypt = require('bcryptjs');
const { authenticate, authorize } = require('../middleware/auth');

// GET / - ADMIN/FACULTY: list all students
router.get('/', authenticate, authorize('ADMIN', 'FACULTY'), async (req, res) => {
  try {
    const students = await prisma.student.findMany({ include: { course: true, batch: true, department: true } });
    res.json(students);
  } catch (error) { res.status(500).json({ error: error.message }); }
});

// GET /:id - ADMIN/FACULTY: single student
router.get('/:id', authenticate, authorize('ADMIN', 'FACULTY'), async (req, res) => {
  try {
    if (req.params.id === 'me') return next(); // Fallthrough to /me routes
    const student = await prisma.student.findUnique({ where: { id: req.params.id }, include: { course: true, batch: true, department: true } });
    res.json(student);
  } catch (error) { res.status(500).json({ error: error.message }); }
});

// POST / - ADMIN: create student
router.post('/', authenticate, authorize('ADMIN'), async (req, res) => {
  try {
    const { studentId, firstName, lastName, email, phone, courseId, batchId, departmentId, ...rest } = req.body;
    const passwordHash = await bcrypt.hash('student123', 10);
    const result = await prisma.$transaction(async (tx) => {
      const user = await tx.user.create({ data: { email, passwordHash, role: 'STUDENT' } });
      return tx.student.create({ data: { userId: user.id, studentId, firstName, lastName, email, phone, courseId, batchId, departmentId, ...rest } });
    });
    res.status(201).json(result);
  } catch (error) { res.status(500).json({ error: error.message }); }
});

// PUT /:id - ADMIN: update student
router.put('/:id', authenticate, authorize('ADMIN'), async (req, res) => {
  try {
    if (req.params.id === 'me') return next(); // Fallthrough
    const student = await prisma.student.update({ where: { id: req.params.id }, data: req.body });
    res.json(student);
  } catch (error) { res.status(500).json({ error: error.message }); }
});

// DELETE /:id - ADMIN: delete student
router.delete('/:id', authenticate, authorize('ADMIN'), async (req, res) => {
  try {
    const student = await prisma.student.findUnique({ where: { id: req.params.id } });
    if (student) await prisma.user.delete({ where: { id: student.userId } });
    res.json({ success: true });
  } catch (error) { res.status(500).json({ error: error.message }); }
});

// --- STUDENT ME ROUTES ---

// GET /me/profile
router.get('/me/profile', authenticate, authorize('STUDENT'), async (req, res) => {
  try {
    const student = await prisma.student.findUnique({ where: { userId: req.user.userId }, include: { course: true, batch: true, department: true } });
    res.json(student);
  } catch (error) { res.status(500).json({ error: error.message }); }
});

// PUT /me/profile
router.put('/me/profile', authenticate, authorize('STUDENT'), async (req, res) => {
  try {
    const { phone, address, city, state, pinCode, photoUrl } = req.body;
    const student = await prisma.student.update({ where: { userId: req.user.userId }, data: { phone, address, city, state, pinCode, photoUrl } });
    res.json(student);
  } catch (error) { res.status(500).json({ error: error.message }); }
});

// GET /me/courses
router.get('/me/courses', authenticate, authorize('STUDENT'), async (req, res) => {
  try {
    const student = await prisma.student.findUnique({ where: { userId: req.user.userId } });
    const course = await prisma.course.findUnique({ where: { id: student.courseId }, include: { subjects: true } });
    res.json(course);
  } catch (error) { res.status(500).json({ error: error.message }); }
});

// GET /me/subjects
router.get('/me/subjects', authenticate, authorize('STUDENT'), async (req, res) => {
  try {
    const student = await prisma.student.findUnique({ where: { userId: req.user.userId } });
    const subjects = await prisma.subject.findMany({ where: { courseId: student.courseId } });
    res.json(subjects);
  } catch (error) { res.status(500).json({ error: error.message }); }
});

// GET /me/timetable
router.get('/me/timetable', authenticate, authorize('STUDENT'), async (req, res) => {
  try {
    const student = await prisma.student.findUnique({ where: { userId: req.user.userId } });
    const timetable = await prisma.timetable.findMany({ where: { courseId: student.courseId, batchId: student.batchId }, include: { subject: true } });
    res.json(timetable);
  } catch (error) { res.status(500).json({ error: error.message }); }
});

// GET /me/attendance
router.get('/me/attendance', authenticate, authorize('STUDENT'), async (req, res) => {
  try {
    const student = await prisma.student.findUnique({ where: { userId: req.user.userId } });
    const attendance = await prisma.attendance.findMany({ where: { studentId: student.id }, include: { subject: true } });
    res.json(attendance);
  } catch (error) { res.status(500).json({ error: error.message }); }
});

// GET /me/marks
router.get('/me/marks', authenticate, authorize('STUDENT'), async (req, res) => {
  try {
    const student = await prisma.student.findUnique({ where: { userId: req.user.userId } });
    const marks = await prisma.mark.findMany({ where: { studentId: student.id }, include: { exam: { include: { subject: true } } } });
    res.json(marks);
  } catch (error) { res.status(500).json({ error: error.message }); }
});

// GET /me/gpa
router.get('/me/gpa', authenticate, authorize('STUDENT'), async (req, res) => {
  try {
    const student = await prisma.student.findUnique({ where: { userId: req.user.userId } });
    const marks = await prisma.mark.findMany({ where: { studentId: student.id }, include: { exam: true } });
    
    let totalScore = 0;
    let validExams = 0;
    marks.forEach(m => {
      if (m.marksObtained != null && m.exam.totalMarks) {
        totalScore += (m.marksObtained / m.exam.totalMarks) * 10;
        validExams++;
      }
    });
    
    const gpa = validExams > 0 ? (totalScore / validExams).toFixed(2) : 0;
    res.json({ gpa, totalExams: validExams, marksDetails: marks });
  } catch (error) { res.status(500).json({ error: error.message }); }
});

module.exports = router;
