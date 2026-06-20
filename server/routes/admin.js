const express = require('express');
const router = express.Router();
const prisma = require('../prisma');
const bcrypt = require('bcryptjs');
const { authenticate, authorize } = require('../middleware/auth');

router.use(authenticate, authorize('ADMIN'));

// Departments
router.get('/departments', async (req, res) => {
  try { res.json(await prisma.department.findMany()); } catch (err) { res.status(500).json({ error: err.message }); }
});

router.post('/departments', async (req, res) => {
  try { res.status(201).json(await prisma.department.create({ data: req.body })); } catch (err) { res.status(500).json({ error: err.message }); }
});

router.get('/departments/:id', async (req, res) => {
  try { res.json(await prisma.department.findUnique({ where: { id: req.params.id } })); } catch (err) { res.status(500).json({ error: err.message }); }
});

router.put('/departments/:id', async (req, res) => {
  try { res.json(await prisma.department.update({ where: { id: req.params.id }, data: req.body })); } catch (err) { res.status(500).json({ error: err.message }); }
});

router.delete('/departments/:id', async (req, res) => {
  try { await prisma.department.delete({ where: { id: req.params.id } }); res.json({ success: true }); } catch (err) { res.status(500).json({ error: err.message }); }
});

// Sections
router.get('/sections', async (req, res) => {
  try { res.json(await prisma.section.findMany()); } catch (err) { res.status(500).json({ error: err.message }); }
});

router.post('/sections', async (req, res) => {
  try { res.status(201).json(await prisma.section.create({ data: req.body })); } catch (err) { res.status(500).json({ error: err.message }); }
});

router.get('/sections/:id', async (req, res) => {
  try { res.json(await prisma.section.findUnique({ where: { id: req.params.id } })); } catch (err) { res.status(500).json({ error: err.message }); }
});

router.put('/sections/:id', async (req, res) => {
  try { res.json(await prisma.section.update({ where: { id: req.params.id }, data: req.body })); } catch (err) { res.status(500).json({ error: err.message }); }
});

router.delete('/sections/:id', async (req, res) => {
  try { await prisma.section.delete({ where: { id: req.params.id } }); res.json({ success: true }); } catch (err) { res.status(500).json({ error: err.message }); }
});

// Users
router.get('/users', async (req, res) => {
  try {
    const users = await prisma.user.findMany({
      select: { id: true, email: true, role: true, isActive: true, createdAt: true, student: true, faculty: true, parent: true }
    });
    res.json(users);
  } catch (err) { res.status(500).json({ error: err.message }); }
});

router.delete('/users/:id', async (req, res) => {
  try { await prisma.user.delete({ where: { id: req.params.id } }); res.json({ success: true }); } catch (err) { res.status(500).json({ error: err.message }); }
});

router.put('/users/:id/reset-password', async (req, res) => {
  try {
    const { newPassword } = req.body;
    const passwordHash = await bcrypt.hash(newPassword, 10);
    await prisma.user.update({ where: { id: req.params.id }, data: { passwordHash } });
    res.json({ success: true });
  } catch (err) { res.status(500).json({ error: err.message }); }
});

router.put('/users/:id/toggle-active', async (req, res) => {
  try {
    const user = await prisma.user.findUnique({ where: { id: req.params.id } });
    await prisma.user.update({ where: { id: req.params.id }, data: { isActive: !user.isActive } });
    res.json({ success: true, isActive: !user.isActive });
  } catch (err) { res.status(500).json({ error: err.message }); }
});

// Analytics
router.get('/analytics', async (req, res) => {
  try {
    const [totalStudents, totalFaculty, totalParents, totalCourses, totalDepartments, complaints, leaveRequests] = await Promise.all([
      prisma.student.count(),
      prisma.faculty.count(),
      prisma.parent.count(),
      prisma.course.count(),
      prisma.department.count(),
      prisma.complaint.groupBy({ by: ['status'], _count: true }),
      prisma.leaveRequest.groupBy({ by: ['status'], _count: true })
    ]);
    res.json({ totalStudents, totalFaculty, totalParents, totalCourses, totalDepartments, complaints, leaveRequests });
  } catch (err) { res.status(500).json({ error: err.message }); }
});

// Audit Logs
router.get('/audit-logs', async (req, res) => {
  try {
    res.json(await prisma.auditLog.findMany({ take: 100, orderBy: { createdAt: 'desc' }, include: { user: { select: { email: true } } } }));
  } catch (err) { res.status(500).json({ error: err.message }); }
});

module.exports = router;
