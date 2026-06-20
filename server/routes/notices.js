const express = require('express');
const router = express.Router();
const prisma = require('../prisma');
const { authenticate, authorize } = require('../middleware/auth');

// GET / - Authenticated
router.get('/', authenticate, async (req, res) => {
  try {
    const { category, targetRole } = req.query;
    const where = {};
    if (category) where.category = category;
    if (targetRole) where.targetRole = targetRole;

    const notices = await prisma.notice.findMany({ where, orderBy: { publishedAt: 'desc' } });
    res.json(notices);
  } catch (err) { res.status(500).json({ error: err.message }); }
});

// GET /:id
router.get('/:id', authenticate, async (req, res) => {
  try { res.json(await prisma.notice.findUnique({ where: { id: req.params.id } })); } catch (err) { res.status(500).json({ error: err.message }); }
});

// POST /
router.post('/', authenticate, authorize('ADMIN', 'FACULTY'), async (req, res) => {
  try {
    let authorId = null;
    if (req.user.role === 'FACULTY') {
      const fac = await prisma.faculty.findUnique({ where: { userId: req.user.userId } });
      authorId = fac.id;
    }
    const notice = await prisma.notice.create({ data: { ...req.body, authorId } });
    res.status(201).json(notice);
  } catch (err) { res.status(500).json({ error: err.message }); }
});

// PUT /:id
router.put('/:id', authenticate, authorize('ADMIN', 'FACULTY'), async (req, res) => {
  try {
    const notice = await prisma.notice.findUnique({ where: { id: req.params.id } });
    if (req.user.role === 'FACULTY') {
      const fac = await prisma.faculty.findUnique({ where: { userId: req.user.userId } });
      if (notice.authorId !== fac.id) return res.status(403).json({ error: 'Not authorized to edit this notice' });
    }
    res.json(await prisma.notice.update({ where: { id: req.params.id }, data: req.body }));
  } catch (err) { res.status(500).json({ error: err.message }); }
});

// DELETE /:id
router.delete('/:id', authenticate, authorize('ADMIN'), async (req, res) => {
  try { await prisma.notice.delete({ where: { id: req.params.id } }); res.json({ success: true }); } catch (err) { res.status(500).json({ error: err.message }); }
});

// GET /notifications/me
router.get('/notifications/me', authenticate, async (req, res) => {
  try {
    const { unreadOnly } = req.query;
    const where = { userId: req.user.userId };
    if (unreadOnly === 'true') where.isRead = false;
    res.json(await prisma.notification.findMany({ where, orderBy: { createdAt: 'desc' } }));
  } catch (err) { res.status(500).json({ error: err.message }); }
});

// PUT /notifications/:id/read
router.put('/notifications/:id/read', authenticate, async (req, res) => {
  try {
    res.json(await prisma.notification.update({ where: { id: req.params.id, userId: req.user.userId }, data: { isRead: true } }));
  } catch (err) { res.status(500).json({ error: err.message }); }
});

module.exports = router;
