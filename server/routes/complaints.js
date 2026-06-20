const express = require('express');
const router = express.Router();
const prisma = require('../prisma');
const { authenticate, authorize } = require('../middleware/auth');

// POST / - STUDENT
router.post('/', authenticate, authorize('STUDENT'), async (req, res) => {
  try {
    const student = await prisma.student.findUnique({ where: { userId: req.user.userId } });
    const { title, description, category } = req.body;
    res.status(201).json(await prisma.complaint.create({ data: { studentId: student.id, title, description, category } }));
  } catch (err) { res.status(500).json({ error: err.message }); }
});

// GET /me - STUDENT
router.get('/me', authenticate, authorize('STUDENT'), async (req, res) => {
  try {
    const student = await prisma.student.findUnique({ where: { userId: req.user.userId } });
    res.json(await prisma.complaint.findMany({ where: { studentId: student.id }, orderBy: { createdAt: 'desc' } }));
  } catch (err) { res.status(500).json({ error: err.message }); }
});

// GET / - ADMIN
router.get('/', authenticate, authorize('ADMIN'), async (req, res) => {
  try {
    const { status, category } = req.query;
    const where = {};
    if (status) where.status = status;
    if (category) where.category = category;
    res.json(await prisma.complaint.findMany({ where, include: { student: true }, orderBy: { createdAt: 'desc' } }));
  } catch (err) { res.status(500).json({ error: err.message }); }
});

// GET /:id
router.get('/:id', authenticate, async (req, res) => {
  try {
    const complaint = await prisma.complaint.findUnique({ where: { id: req.params.id }, include: { student: true } });
    if (req.user.role === 'STUDENT') {
      const student = await prisma.student.findUnique({ where: { userId: req.user.userId } });
      if (complaint.studentId !== student.id) return res.status(403).json({ error: 'Access denied' });
    }
    res.json(complaint);
  } catch (err) { res.status(500).json({ error: err.message }); }
});

// PUT /:id/assign - ADMIN
router.put('/:id/assign', authenticate, authorize('ADMIN'), async (req, res) => {
  try { res.json(await prisma.complaint.update({ where: { id: req.params.id }, data: { assignedTo: req.body.assignedTo, status: 'InProgress' } })); } catch (err) { res.status(500).json({ error: err.message }); }
});

// PUT /:id/resolve - ADMIN
router.put('/:id/resolve', authenticate, authorize('ADMIN'), async (req, res) => {
  try { res.json(await prisma.complaint.update({ where: { id: req.params.id }, data: { resolution: req.body.resolution, status: 'Resolved' } })); } catch (err) { res.status(500).json({ error: err.message }); }
});

// PUT /:id/status - ADMIN
router.put('/:id/status', authenticate, authorize('ADMIN'), async (req, res) => {
  try { res.json(await prisma.complaint.update({ where: { id: req.params.id }, data: { status: req.body.status } })); } catch (err) { res.status(500).json({ error: err.message }); }
});

module.exports = router;
