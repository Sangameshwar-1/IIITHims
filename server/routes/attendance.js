const express = require('express');
const router = express.Router();
const prisma = require('../prisma');
const { authenticate, authorize } = require('../middleware/auth');

// POST / — FACULTY: mark attendance for multiple students
router.post('/', authenticate, authorize('FACULTY'), async (req, res) => {
  try {
    const { subjectId, date, records } = req.body;
    const faculty = await prisma.faculty.findUnique({ where: { userId: req.user.id } });
    if (!faculty) return res.status(404).json({ error: 'Faculty profile not found' });

    const data = records.map((r) => ({
      subjectId,
      studentId: r.studentId,
      facultyId: faculty.id,
      date: new Date(date),
      status: r.status,
      remarks: r.remarks || null,
    }));

    const result = await prisma.attendance.createMany({ data });
    res.status(201).json({ count: result.count });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// GET /me — STUDENT: get own attendance
router.get('/me', authenticate, authorize('STUDENT'), async (req, res) => {
  try {
    const student = await prisma.student.findUnique({ where: { userId: req.user.id } });
    if (!student) return res.status(404).json({ error: 'Student profile not found' });

    const attendance = await prisma.attendance.findMany({
      where: { studentId: student.id },
      include: { subject: true },
      orderBy: { date: 'desc' },
    });
    res.json(attendance);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// GET /report — FACULTY/ADMIN: generate attendance report
router.get('/report', authenticate, authorize('FACULTY', 'ADMIN'), async (req, res) => {
  try {
    const { subjectId, batchId, startDate, endDate } = req.query;
    const where = {};
    if (subjectId) where.subjectId = subjectId;
    if (startDate || endDate) {
      where.date = {};
      if (startDate) where.date.gte = new Date(startDate);
      if (endDate) where.date.lte = new Date(endDate);
    }
    if (batchId) where.student = { batchId };

    const records = await prisma.attendance.findMany({
      where,
      include: { student: { include: { user: true } } },
    });

    const statsMap = {};
    for (const r of records) {
      if (!statsMap[r.studentId]) {
        const name = r.student?.user
          ? `${r.student.user.firstName} ${r.student.user.lastName}`
          : r.studentId;
        statsMap[r.studentId] = {
          studentId: r.studentId,
          studentName: name,
          totalClasses: 0,
          present: 0,
          absent: 0,
        };
      }
      const s = statsMap[r.studentId];
      s.totalClasses++;
      if (r.status === 'PRESENT') s.present++;
      else if (r.status === 'ABSENT') s.absent++;
    }

    const report = Object.values(statsMap).map((s) => ({
      ...s,
      percentage: s.totalClasses ? Math.round((s.present / s.totalClasses) * 10000) / 100 : 0,
    }));

    res.json(report);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// GET /subject/:subjectId — FACULTY: get attendance for a subject
router.get('/subject/:subjectId', authenticate, authorize('FACULTY'), async (req, res) => {
  try {
    const where = { subjectId: req.params.subjectId };
    if (req.query.date) where.date = new Date(req.query.date);

    const attendance = await prisma.attendance.findMany({
      where,
      include: { student: true },
      orderBy: { date: 'desc' },
    });
    res.json(attendance);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// PUT /:id — FACULTY: edit single attendance record
router.put('/:id', authenticate, authorize('FACULTY'), async (req, res) => {
  try {
    const record = await prisma.attendance.update({
      where: { id: req.params.id },
      data: req.body,
    });
    res.json(record);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

module.exports = router;
