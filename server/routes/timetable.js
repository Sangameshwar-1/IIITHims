const express = require('express');
const router = express.Router();
const prisma = require('../prisma');
const { authenticate, authorize } = require('../middleware/auth');

// GET / — Authenticated: list timetable entries with filters
router.get('/', authenticate, async (req, res) => {
  try {
    const { courseId, batchId, semester, day } = req.query;
    const where = {};
    if (courseId) where.courseId = courseId;
    if (batchId) where.batchId = batchId;
    if (semester) where.semester = parseInt(semester);
    if (day) where.day = day;

    const entries = await prisma.timetable.findMany({
      where,
      include: { course: true, batch: true, subject: true },
      orderBy: [{ day: 'asc' }, { startTime: 'asc' }],
    });
    res.json(entries);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// POST / — ADMIN: create timetable entry
router.post('/', authenticate, authorize('ADMIN'), async (req, res) => {
  try {
    const { courseId, batchId, subjectId, day, startTime, endTime, room, facultyName, semester } = req.body;
    const entry = await prisma.timetable.create({
      data: { courseId, batchId, subjectId, day, startTime, endTime, room, facultyName, semester },
      include: { course: true, batch: true, subject: true },
    });
    res.status(201).json(entry);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// POST /bulk — ADMIN: create multiple timetable entries
router.post('/bulk', authenticate, authorize('ADMIN'), async (req, res) => {
  try {
    const { entries } = req.body;
    const result = await prisma.timetable.createMany({ data: entries });
    res.status(201).json({ count: result.count });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// PUT /:id — ADMIN: update timetable entry
router.put('/:id', authenticate, authorize('ADMIN'), async (req, res) => {
  try {
    const entry = await prisma.timetable.update({
      where: { id: req.params.id },
      data: req.body,
      include: { course: true, batch: true, subject: true },
    });
    res.json(entry);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// DELETE /:id — ADMIN: delete timetable entry
router.delete('/:id', authenticate, authorize('ADMIN'), async (req, res) => {
  try {
    await prisma.timetable.delete({ where: { id: req.params.id } });
    res.json({ message: 'Timetable entry deleted' });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

module.exports = router;
