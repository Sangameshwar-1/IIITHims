const express = require('express');
const router = express.Router();
const prisma = require('../prisma');
const { authenticate, authorize } = require('../middleware/auth');

// GET /courses
router.get('/courses', authenticate, async (req, res) => {
  try {
    const courses = await prisma.course.findMany({
      include: { department: true },
    });
    res.json(courses);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// POST /courses
router.post('/courses', authenticate, authorize('ADMIN'), async (req, res) => {
  try {
    const { name, code, departmentId, duration, totalSemesters, description } = req.body;
    const course = await prisma.course.create({
      data: { name, code, departmentId, duration, totalSemesters, description },
      include: { department: true },
    });
    res.status(201).json(course);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// GET /courses/:id
router.get('/courses/:id', authenticate, async (req, res) => {
  try {
    const course = await prisma.course.findUnique({
      where: { id: req.params.id },
      include: { department: true },
    });
    if (!course) return res.status(404).json({ error: 'Course not found' });
    res.json(course);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// PUT /courses/:id
router.put('/courses/:id', authenticate, authorize('ADMIN'), async (req, res) => {
  try {
    const course = await prisma.course.update({
      where: { id: req.params.id },
      data: req.body,
      include: { department: true },
    });
    res.json(course);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// DELETE /courses/:id
router.delete('/courses/:id', authenticate, authorize('ADMIN'), async (req, res) => {
  try {
    await prisma.course.delete({ where: { id: req.params.id } });
    res.json({ message: 'Course deleted' });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// GET /subjects
router.get('/subjects', authenticate, async (req, res) => {
  try {
    const subjects = await prisma.subject.findMany({
      include: { course: true },
    });
    res.json(subjects);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// POST /subjects
router.post('/subjects', authenticate, authorize('ADMIN'), async (req, res) => {
  try {
    const { name, code, courseId, semester, credits, type } = req.body;
    const subject = await prisma.subject.create({
      data: { name, code, courseId, semester, credits, type },
      include: { course: true },
    });
    res.status(201).json(subject);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// GET /subjects/:id
router.get('/subjects/:id', authenticate, async (req, res) => {
  try {
    const subject = await prisma.subject.findUnique({
      where: { id: req.params.id },
      include: { course: true },
    });
    if (!subject) return res.status(404).json({ error: 'Subject not found' });
    res.json(subject);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// PUT /subjects/:id
router.put('/subjects/:id', authenticate, authorize('ADMIN'), async (req, res) => {
  try {
    const subject = await prisma.subject.update({
      where: { id: req.params.id },
      data: req.body,
      include: { course: true },
    });
    res.json(subject);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// DELETE /subjects/:id
router.delete('/subjects/:id', authenticate, authorize('ADMIN'), async (req, res) => {
  try {
    await prisma.subject.delete({ where: { id: req.params.id } });
    res.json({ message: 'Subject deleted' });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// POST /subjects/:id/assign-faculty
router.post('/subjects/:id/assign-faculty', authenticate, authorize('ADMIN'), async (req, res) => {
  try {
    const { facultyId, batchId, sectionId } = req.body;
    const record = await prisma.facultySubject.create({
      data: {
        subjectId: req.params.id,
        facultyId,
        batchId: batchId || undefined,
        sectionId: sectionId || undefined,
      },
    });
    res.status(201).json(record);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// DELETE /subjects/:id/remove-faculty/:facultyId
router.delete('/subjects/:id/remove-faculty/:facultyId', authenticate, authorize('ADMIN'), async (req, res) => {
  try {
    await prisma.facultySubject.deleteMany({
      where: {
        subjectId: req.params.id,
        facultyId: req.params.facultyId,
      },
    });
    res.json({ message: 'Faculty assignment removed' });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

module.exports = router;
