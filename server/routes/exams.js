const express = require('express');
const router = express.Router();
const prisma = require('../prisma');
const { authenticate, authorize } = require('../middleware/auth');

// GET /exams
router.get('/exams', authenticate, async (req, res) => {
  try {
    const exams = await prisma.exam.findMany({
      include: { subject: true, course: true, batch: true },
    });
    res.json(exams);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// POST /exams
router.post('/exams', authenticate, authorize('FACULTY', 'ADMIN'), async (req, res) => {
  try {
    const exam = await prisma.exam.create({
      data: req.body,
      include: { subject: true, course: true, batch: true },
    });
    res.status(201).json(exam);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// GET /exams/:id
router.get('/exams/:id', authenticate, async (req, res) => {
  try {
    const exam = await prisma.exam.findUnique({
      where: { id: req.params.id },
      include: { subject: true, course: true, batch: true },
    });
    if (!exam) return res.status(404).json({ error: 'Exam not found' });
    res.json(exam);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// PUT /exams/:id
router.put('/exams/:id', authenticate, authorize('FACULTY', 'ADMIN'), async (req, res) => {
  try {
    const exam = await prisma.exam.update({
      where: { id: req.params.id },
      data: req.body,
      include: { subject: true, course: true, batch: true },
    });
    res.json(exam);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// DELETE /exams/:id
router.delete('/exams/:id', authenticate, authorize('FACULTY', 'ADMIN'), async (req, res) => {
  try {
    await prisma.exam.delete({ where: { id: req.params.id } });
    res.json({ message: 'Exam deleted' });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// POST /exams/:id/marks — FACULTY: upload marks
router.post('/exams/:id/marks', authenticate, authorize('FACULTY'), async (req, res) => {
  try {
    const exam = await prisma.exam.findUnique({ where: { id: req.params.id } });
    if (!exam) return res.status(404).json({ error: 'Exam not found' });

    const { marks } = req.body;
    const data = marks.map((m) => ({
      examId: exam.id,
      subjectId: exam.subjectId,
      studentId: m.studentId,
      marksObtained: m.marksObtained,
      grade: m.grade || null,
      remarks: m.remarks || null,
    }));

    const result = await prisma.mark.createMany({ data });
    res.status(201).json({ count: result.count });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// PUT /marks/:id — FACULTY: update single mark
router.put('/marks/:id', authenticate, authorize('FACULTY'), async (req, res) => {
  try {
    const mark = await prisma.mark.update({
      where: { id: req.params.id },
      data: req.body,
    });
    res.json(mark);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// GET /exams/:id/results — Authenticated: get results for an exam
router.get('/exams/:id/results', authenticate, async (req, res) => {
  try {
    const where = { examId: req.params.id };

    if (req.user.role === 'STUDENT') {
      const student = await prisma.student.findUnique({ where: { userId: req.user.id } });
      if (!student) return res.status(404).json({ error: 'Student profile not found' });
      where.studentId = student.id;
    }

    const results = await prisma.mark.findMany({
      where,
      include: { student: { include: { user: true } }, exam: true },
    });
    res.json(results);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// GET /results/student/:studentId — FACULTY/ADMIN: get all results for a student
router.get('/results/student/:studentId', authenticate, authorize('FACULTY', 'ADMIN'), async (req, res) => {
  try {
    const results = await prisma.mark.findMany({
      where: { studentId: req.params.studentId },
      include: { exam: { include: { subject: true } } },
      orderBy: { exam: { date: 'desc' } },
    });
    res.json(results);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

module.exports = router;
