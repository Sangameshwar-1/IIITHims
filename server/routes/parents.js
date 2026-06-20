const express = require('express');
const router = express.Router();
const prisma = require('../prisma');
const bcrypt = require('bcryptjs');
const { authenticate, authorize } = require('../middleware/auth');

// GET / - ADMIN
router.get('/', authenticate, authorize('ADMIN'), async (req, res) => {
  try { res.json(await prisma.parent.findMany({ include: { student: true } })); } 
  catch (error) { res.status(500).json({ error: error.message }); }
});

// POST / - ADMIN
router.post('/', authenticate, authorize('ADMIN'), async (req, res) => {
  try {
    const { studentId, firstName, lastName, email, phone, relation, ...rest } = req.body;
    const passwordHash = await bcrypt.hash('parent123', 10);
    const result = await prisma.$transaction(async (tx) => {
      const user = await tx.user.create({ data: { email, passwordHash, role: 'PARENT' } });
      return tx.parent.create({ data: { userId: user.id, studentId, firstName, lastName, email, phone, relation, ...rest } });
    });
    res.status(201).json(result);
  } catch (error) { res.status(500).json({ error: error.message }); }
});

// --- PARENT ME ROUTES ---

// GET /me/profile
router.get('/me/profile', authenticate, authorize('PARENT'), async (req, res) => {
  try { res.json(await prisma.parent.findUnique({ where: { userId: req.user.userId }, include: { student: true } })); } 
  catch (error) { res.status(500).json({ error: error.message }); }
});

// GET /me/student
router.get('/me/student', authenticate, authorize('PARENT'), async (req, res) => {
  try { 
    const parent = await prisma.parent.findUnique({ where: { userId: req.user.userId } });
    res.json(await prisma.student.findUnique({ where: { id: parent.studentId }, include: { course: true, batch: true, department: true } }));
  } catch (error) { res.status(500).json({ error: error.message }); }
});

// GET /me/student/attendance
router.get('/me/student/attendance', authenticate, authorize('PARENT'), async (req, res) => {
  try { 
    const parent = await prisma.parent.findUnique({ where: { userId: req.user.userId } });
    res.json(await prisma.attendance.findMany({ where: { studentId: parent.studentId }, include: { subject: true } }));
  } catch (error) { res.status(500).json({ error: error.message }); }
});

// GET /me/student/marks
router.get('/me/student/marks', authenticate, authorize('PARENT'), async (req, res) => {
  try { 
    const parent = await prisma.parent.findUnique({ where: { userId: req.user.userId } });
    res.json(await prisma.mark.findMany({ where: { studentId: parent.studentId }, include: { exam: true } }));
  } catch (error) { res.status(500).json({ error: error.message }); }
});

// GET /me/student/timetable
router.get('/me/student/timetable', authenticate, authorize('PARENT'), async (req, res) => {
  try { 
    const parent = await prisma.parent.findUnique({ where: { userId: req.user.userId } });
    const student = await prisma.student.findUnique({ where: { id: parent.studentId } });
    res.json(await prisma.timetable.findMany({ where: { courseId: student.courseId, batchId: student.batchId }, include: { subject: true } }));
  } catch (error) { res.status(500).json({ error: error.message }); }
});

// GET /me/student/leaves
router.get('/me/student/leaves', authenticate, authorize('PARENT'), async (req, res) => {
  try { 
    const parent = await prisma.parent.findUnique({ where: { userId: req.user.userId } });
    res.json(await prisma.leaveRequest.findMany({ where: { studentId: parent.studentId } }));
  } catch (error) { res.status(500).json({ error: error.message }); }
});

module.exports = router;
