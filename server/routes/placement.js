const express = require('express');
const router = express.Router();
const prisma = require('../prisma');
const { authenticate, authorize } = require('../middleware/auth');

// GET /companies
router.get('/companies', authenticate, async (req, res) => {
  try { res.json(await prisma.company.findMany()); } catch (err) { res.status(500).json({ error: err.message }); }
});

// POST /companies - ADMIN
router.post('/companies', authenticate, authorize('ADMIN'), async (req, res) => {
  try { res.status(201).json(await prisma.company.create({ data: req.body })); } catch (err) { res.status(500).json({ error: err.message }); }
});

// PUT /companies/:id - ADMIN
router.put('/companies/:id', authenticate, authorize('ADMIN'), async (req, res) => {
  try { res.json(await prisma.company.update({ where: { id: req.params.id }, data: req.body })); } catch (err) { res.status(500).json({ error: err.message }); }
});

// DELETE /companies/:id - ADMIN
router.delete('/companies/:id', authenticate, authorize('ADMIN'), async (req, res) => {
  try { await prisma.company.delete({ where: { id: req.params.id } }); res.json({ success: true }); } catch (err) { res.status(500).json({ error: err.message }); }
});

// POST /apply - STUDENT
router.post('/apply', authenticate, authorize('STUDENT'), async (req, res) => {
  try {
    const student = await prisma.student.findUnique({ where: { userId: req.user.userId } });
    const { companyId, position, resumeUrl } = req.body;
    res.status(201).json(await prisma.placement.create({ data: { studentId: student.id, companyId, position, resumeUrl } }));
  } catch (err) { res.status(500).json({ error: err.message }); }
});

// GET /me - STUDENT
router.get('/me', authenticate, authorize('STUDENT'), async (req, res) => {
  try {
    const student = await prisma.student.findUnique({ where: { userId: req.user.userId } });
    res.json(await prisma.placement.findMany({ where: { studentId: student.id }, include: { company: true } }));
  } catch (err) { res.status(500).json({ error: err.message }); }
});

// GET / - ADMIN
router.get('/', authenticate, authorize('ADMIN'), async (req, res) => {
  try {
    const { status, companyId } = req.query;
    const where = {};
    if (status) where.status = status;
    if (companyId) where.companyId = companyId;
    res.json(await prisma.placement.findMany({ where, include: { company: true, student: true } }));
  } catch (err) { res.status(500).json({ error: err.message }); }
});

// PUT /:id/status - ADMIN
router.put('/:id/status', authenticate, authorize('ADMIN'), async (req, res) => {
  try {
    const { status, remarks } = req.body;
    res.json(await prisma.placement.update({ where: { id: req.params.id }, data: { status, remarks } }));
  } catch (err) { res.status(500).json({ error: err.message }); }
});

module.exports = router;
