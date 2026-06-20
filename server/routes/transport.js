const express = require('express');
const router = express.Router();
const prisma = require('../prisma');
const { authenticate, authorize } = require('../middleware/auth');

router.get('/', authenticate, async (req, res) => {
  try { res.json(await prisma.transportRoute.findMany()); } catch (err) { res.status(500).json({ error: err.message }); }
});

router.post('/', authenticate, authorize('ADMIN'), async (req, res) => {
  try { res.status(201).json(await prisma.transportRoute.create({ data: req.body })); } catch (err) { res.status(500).json({ error: err.message }); }
});

router.get('/:id', authenticate, async (req, res) => {
  try { res.json(await prisma.transportRoute.findUnique({ where: { id: req.params.id } })); } catch (err) { res.status(500).json({ error: err.message }); }
});

router.put('/:id', authenticate, authorize('ADMIN'), async (req, res) => {
  try { res.json(await prisma.transportRoute.update({ where: { id: req.params.id }, data: req.body })); } catch (err) { res.status(500).json({ error: err.message }); }
});

router.delete('/:id', authenticate, authorize('ADMIN'), async (req, res) => {
  try { await prisma.transportRoute.delete({ where: { id: req.params.id } }); res.json({ success: true }); } catch (err) { res.status(500).json({ error: err.message }); }
});

module.exports = router;
