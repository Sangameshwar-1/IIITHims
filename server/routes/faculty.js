const express = require('express');
const router = express.Router();
const prisma = require('../prisma');
const bcrypt = require('bcryptjs');
const { authenticate, authorize } = require('../middleware/auth');

// GET / - ADMIN
router.get('/', authenticate, authorize('ADMIN'), async (req, res) => {
  try { res.json(await prisma.faculty.findMany({ include: { department: true } })); } 
  catch (error) { res.status(500).json({ error: error.message }); }
});

// GET /:id - ADMIN
router.get('/:id', authenticate, authorize('ADMIN'), async (req, res) => {
  try { 
    if (req.params.id === 'me') return next();
    res.json(await prisma.faculty.findUnique({ where: { id: req.params.id }, include: { department: true } })); 
  } catch (error) { res.status(500).json({ error: error.message }); }
});

// POST / - ADMIN
router.post('/', authenticate, authorize('ADMIN'), async (req, res) => {
  try {
    const { facultyId, firstName, lastName, email, phone, departmentId, designation, ...rest } = req.body;
    const passwordHash = await bcrypt.hash('faculty123', 10);
    const result = await prisma.$transaction(async (tx) => {
      const user = await tx.user.create({ data: { email, passwordHash, role: 'FACULTY' } });
      return tx.faculty.create({ data: { userId: user.id, facultyId, firstName, lastName, email, phone, departmentId, designation, ...rest } });
    });
    res.status(201).json(result);
  } catch (error) { res.status(500).json({ error: error.message }); }
});

// PUT /:id - ADMIN
router.put('/:id', authenticate, authorize('ADMIN'), async (req, res) => {
  try { 
    if (req.params.id === 'me') return next();
    res.json(await prisma.faculty.update({ where: { id: req.params.id }, data: req.body })); 
  } catch (error) { res.status(500).json({ error: error.message }); }
});

// DELETE /:id - ADMIN
router.delete('/:id', authenticate, authorize('ADMIN'), async (req, res) => {
  try {
    const fac = await prisma.faculty.findUnique({ where: { id: req.params.id } });
    if (fac) await prisma.user.delete({ where: { id: fac.userId } });
    res.json({ success: true });
  } catch (error) { res.status(500).json({ error: error.message }); }
});

// --- FACULTY ME ROUTES ---

// GET /me/profile
router.get('/me/profile', authenticate, authorize('FACULTY'), async (req, res) => {
  try {
    res.json(await prisma.faculty.findUnique({ 
      where: { userId: req.user.userId }, 
      include: { department: true, subjectAssignments: { include: { subject: true } } } 
    }));
  } catch (error) { res.status(500).json({ error: error.message }); }
});

// PUT /me/profile
router.put('/me/profile', authenticate, authorize('FACULTY'), async (req, res) => {
  try {
    const { phone, photoUrl, qualification } = req.body;
    res.json(await prisma.faculty.update({ where: { userId: req.user.userId }, data: { phone, photoUrl, qualification } }));
  } catch (error) { res.status(500).json({ error: error.message }); }
});

// GET /me/subjects
router.get('/me/subjects', authenticate, authorize('FACULTY'), async (req, res) => {
  try {
    const fac = await prisma.faculty.findUnique({ where: { userId: req.user.userId } });
    const assignments = await prisma.facultySubject.findMany({ where: { facultyId: fac.id }, include: { subject: { include: { course: true } } } });
    res.json(assignments);
  } catch (error) { res.status(500).json({ error: error.message }); }
});

// GET /me/students
router.get('/me/students', authenticate, authorize('FACULTY'), async (req, res) => {
  try {
    const fac = await prisma.faculty.findUnique({ where: { userId: req.user.userId } });
    const assignments = await prisma.facultySubject.findMany({ where: { facultyId: fac.id } });
    
    // Simplistic: grab all students in the batches/courses the faculty teaches
    const courseIds = assignments.map(a => a.subject.courseId).filter(Boolean);
    const students = await prisma.student.findMany({ where: { courseId: { in: courseIds } } });
    res.json(students);
  } catch (error) { res.status(500).json({ error: error.message }); }
});

// GET /me/timetable
router.get('/me/timetable', authenticate, authorize('FACULTY'), async (req, res) => {
  try {
    const fac = await prisma.faculty.findUnique({ where: { userId: req.user.userId } });
    const assignments = await prisma.facultySubject.findMany({ where: { facultyId: fac.id } });
    const subjectIds = assignments.map(a => a.subjectId);
    res.json(await prisma.timetable.findMany({ where: { subjectId: { in: subjectIds } }, include: { course: true, batch: true } }));
  } catch (error) { res.status(500).json({ error: error.message }); }
});

module.exports = router;
