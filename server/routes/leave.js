const express = require('express');
const router = express.Router();
const prisma = require('../prisma');
const { authenticate, authorize } = require('../middleware/auth');

// POST /
router.post('/', authenticate, authorize('STUDENT', 'FACULTY'), async (req, res) => {
  try {
    let studentId = null;
    let facultyId = null;
    let applicantType = req.user.role;

    if (applicantType === 'STUDENT') {
      const student = await prisma.student.findUnique({ where: { userId: req.user.userId } });
      studentId = student.id;
    } else {
      const faculty = await prisma.faculty.findUnique({ where: { userId: req.user.userId } });
      facultyId = faculty.id;
    }

    const { leaveType, startDate, endDate, reason } = req.body;
    res.status(201).json(await prisma.leaveRequest.create({ 
      data: { applicantType, studentId, facultyId, leaveType, startDate, endDate, reason } 
    }));
  } catch (err) { res.status(500).json({ error: err.message }); }
});

// GET /me
router.get('/me', authenticate, authorize('STUDENT', 'FACULTY'), async (req, res) => {
  try {
    if (req.user.role === 'STUDENT') {
      const student = await prisma.student.findUnique({ where: { userId: req.user.userId } });
      res.json(await prisma.leaveRequest.findMany({ where: { studentId: student.id } }));
    } else {
      const faculty = await prisma.faculty.findUnique({ where: { userId: req.user.userId } });
      res.json(await prisma.leaveRequest.findMany({ where: { facultyId: faculty.id } }));
    }
  } catch (err) { res.status(500).json({ error: err.message }); }
});

// GET / - ADMIN
router.get('/', authenticate, authorize('ADMIN'), async (req, res) => {
  try {
    const { status, applicantType } = req.query;
    const where = {};
    if (status) where.status = status;
    if (applicantType) where.applicantType = applicantType;
    res.json(await prisma.leaveRequest.findMany({ where, include: { student: true, faculty: true }, orderBy: { createdAt: 'desc' } }));
  } catch (err) { res.status(500).json({ error: err.message }); }
});

// PUT /:id/approve - ADMIN
router.put('/:id/approve', authenticate, authorize('ADMIN'), async (req, res) => {
  try { res.json(await prisma.leaveRequest.update({ where: { id: req.params.id }, data: { status: 'Approved', approvedBy: req.user.userId } })); } catch (err) { res.status(500).json({ error: err.message }); }
});

// PUT /:id/reject - ADMIN
router.put('/:id/reject', authenticate, authorize('ADMIN'), async (req, res) => {
  try { res.json(await prisma.leaveRequest.update({ where: { id: req.params.id }, data: { status: 'Rejected', remarks: req.body.remarks } })); } catch (err) { res.status(500).json({ error: err.message }); }
});

module.exports = router;
