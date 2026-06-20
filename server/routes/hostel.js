const express = require('express');
const router = express.Router();
const prisma = require('../prisma');
const { authenticate, authorize } = require('../middleware/auth');

// HOSTEL CRUD
router.get('/', authenticate, async (req, res) => {
  try { res.json(await prisma.hostel.findMany({ include: { rooms: true } })); } catch (err) { res.status(500).json({ error: err.message }); }
});

router.post('/', authenticate, authorize('ADMIN'), async (req, res) => {
  try { res.status(201).json(await prisma.hostel.create({ data: req.body })); } catch (err) { res.status(500).json({ error: err.message }); }
});

router.get('/:id', authenticate, async (req, res) => {
  try { res.json(await prisma.hostel.findUnique({ where: { id: req.params.id }, include: { rooms: { include: { students: true } } } })); } catch (err) { res.status(500).json({ error: err.message }); }
});

router.put('/:id', authenticate, authorize('ADMIN'), async (req, res) => {
  try { res.json(await prisma.hostel.update({ where: { id: req.params.id }, data: req.body })); } catch (err) { res.status(500).json({ error: err.message }); }
});

router.delete('/:id', authenticate, authorize('ADMIN'), async (req, res) => {
  try { await prisma.hostel.delete({ where: { id: req.params.id } }); res.json({ success: true }); } catch (err) { res.status(500).json({ error: err.message }); }
});

// ROOMS
router.get('/rooms', authenticate, authorize('ADMIN'), async (req, res) => {
  try { res.json(await prisma.room.findMany({ include: { hostel: true } })); } catch (err) { res.status(500).json({ error: err.message }); }
});

router.post('/rooms', authenticate, authorize('ADMIN'), async (req, res) => {
  try { res.status(201).json(await prisma.room.create({ data: req.body })); } catch (err) { res.status(500).json({ error: err.message }); }
});

router.get('/rooms/:id', authenticate, authorize('ADMIN'), async (req, res) => {
  try { res.json(await prisma.room.findUnique({ where: { id: req.params.id }, include: { hostel: true, students: true } })); } catch (err) { res.status(500).json({ error: err.message }); }
});

router.put('/rooms/:id', authenticate, authorize('ADMIN'), async (req, res) => {
  try { res.json(await prisma.room.update({ where: { id: req.params.id }, data: req.body })); } catch (err) { res.status(500).json({ error: err.message }); }
});

router.delete('/rooms/:id', authenticate, authorize('ADMIN'), async (req, res) => {
  try { await prisma.room.delete({ where: { id: req.params.id } }); res.json({ success: true }); } catch (err) { res.status(500).json({ error: err.message }); }
});

// ROOM ALLOCATION
router.post('/rooms/:id/allocate', authenticate, authorize('ADMIN'), async (req, res) => {
  try {
    const result = await prisma.$transaction(async (tx) => {
      const room = await tx.room.findUnique({ where: { id: req.params.id } });
      if (room.occupancy >= room.capacity) throw new Error('Room is full');
      
      const student = await tx.student.update({ where: { id: req.body.studentId }, data: { roomId: req.params.id } });
      await tx.room.update({ where: { id: req.params.id }, data: { occupancy: { increment: 1 } } });
      return student;
    });
    res.json(result);
  } catch (err) { res.status(500).json({ error: err.message }); }
});

router.post('/rooms/:id/deallocate', authenticate, authorize('ADMIN'), async (req, res) => {
  try {
    const result = await prisma.$transaction(async (tx) => {
      const student = await tx.student.update({ where: { id: req.body.studentId }, data: { roomId: null } });
      await tx.room.update({ where: { id: req.params.id }, data: { occupancy: { decrement: 1 } } });
      return student;
    });
    res.json(result);
  } catch (err) { res.status(500).json({ error: err.message }); }
});

// STUDENT ME
router.get('/me', authenticate, authorize('STUDENT'), async (req, res) => {
  try {
    const student = await prisma.student.findUnique({ where: { userId: req.user.userId } });
    if (!student.roomId) return res.json(null);
    res.json(await prisma.room.findUnique({ where: { id: student.roomId }, include: { hostel: true } }));
  } catch (err) { res.status(500).json({ error: err.message }); }
});

// STUDENT COMPLAINTS
router.post('/complaints', authenticate, authorize('STUDENT'), async (req, res) => {
  try {
    const student = await prisma.student.findUnique({ where: { userId: req.user.userId } });
    res.status(201).json(await prisma.complaint.create({ data: { ...req.body, category: 'Hostel', studentId: student.id } }));
  } catch (err) { res.status(500).json({ error: err.message }); }
});

module.exports = router;
