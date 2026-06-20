const express = require('express');
const router = express.Router();
const prisma = require('../prisma');
const { authenticate, authorize } = require('../middleware/auth');

// GET /books
router.get('/books', authenticate, async (req, res) => {
  try {
    const { search, category, available } = req.query;
    const where = {};
    if (category) where.category = category;
    if (available === 'true') where.availableCopies = { gt: 0 };
    if (search) {
      where.OR = [
        { title: { contains: search, mode: 'insensitive' } },
        { author: { contains: search, mode: 'insensitive' } }
      ];
    }
    res.json(await prisma.libraryBook.findMany({ where }));
  } catch (err) { res.status(500).json({ error: err.message }); }
});

// GET /books/:id
router.get('/books/:id', authenticate, async (req, res) => {
  try { res.json(await prisma.libraryBook.findUnique({ where: { id: req.params.id } })); } catch (err) { res.status(500).json({ error: err.message }); }
});

// POST /books - ADMIN
router.post('/books', authenticate, authorize('ADMIN'), async (req, res) => {
  try { 
    const book = await prisma.libraryBook.create({ data: { ...req.body, availableCopies: req.body.totalCopies } });
    res.status(201).json(book); 
  } catch (err) { res.status(500).json({ error: err.message }); }
});

// PUT /books/:id - ADMIN
router.put('/books/:id', authenticate, authorize('ADMIN'), async (req, res) => {
  try { res.json(await prisma.libraryBook.update({ where: { id: req.params.id }, data: req.body })); } catch (err) { res.status(500).json({ error: err.message }); }
});

// DELETE /books/:id - ADMIN
router.delete('/books/:id', authenticate, authorize('ADMIN'), async (req, res) => {
  try { await prisma.libraryBook.delete({ where: { id: req.params.id } }); res.json({ success: true }); } catch (err) { res.status(500).json({ error: err.message }); }
});

// POST /books/:id/issue - ADMIN
router.post('/books/:id/issue', authenticate, authorize('ADMIN'), async (req, res) => {
  try {
    const result = await prisma.$transaction(async (tx) => {
      const book = await tx.libraryBook.findUnique({ where: { id: req.params.id } });
      if (book.availableCopies <= 0) throw new Error('No copies available');
      
      const issue = await tx.issuedBook.create({
        data: { bookId: req.params.id, studentId: req.body.studentId, issueDate: new Date().toISOString().split('T')[0], dueDate: req.body.dueDate }
      });
      await tx.libraryBook.update({ where: { id: req.params.id }, data: { availableCopies: book.availableCopies - 1 } });
      return issue;
    });
    res.status(201).json(result);
  } catch (err) { res.status(500).json({ error: err.message }); }
});

// PUT /issued/:id/return - ADMIN
router.put('/issued/:id/return', authenticate, authorize('ADMIN'), async (req, res) => {
  try {
    const result = await prisma.$transaction(async (tx) => {
      const issue = await tx.issuedBook.findUnique({ where: { id: req.params.id } });
      const returnDate = new Date().toISOString().split('T')[0];
      
      // Calculate fine simplistic (10 per day overdue)
      let fine = 0;
      if (new Date(returnDate) > new Date(issue.dueDate)) {
        const diffTime = Math.abs(new Date(returnDate) - new Date(issue.dueDate));
        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
        fine = diffDays * 10;
      }

      const updated = await tx.issuedBook.update({ where: { id: req.params.id }, data: { returnDate, status: 'Returned', fine } });
      await tx.libraryBook.update({ where: { id: issue.bookId }, data: { availableCopies: { increment: 1 } } });
      return updated;
    });
    res.json(result);
  } catch (err) { res.status(500).json({ error: err.message }); }
});

// PUT /issued/:id/renew - STUDENT
router.put('/issued/:id/renew', authenticate, authorize('STUDENT'), async (req, res) => {
  try {
    const student = await prisma.student.findUnique({ where: { userId: req.user.userId } });
    const issue = await prisma.issuedBook.findUnique({ where: { id: req.params.id } });
    if (issue.studentId !== student.id) return res.status(403).json({ error: 'Not your book' });
    
    const newDueDate = new Date(new Date(issue.dueDate).getTime() + 14 * 24 * 60 * 60 * 1000).toISOString().split('T')[0];
    res.json(await prisma.issuedBook.update({ where: { id: req.params.id }, data: { dueDate: newDueDate } }));
  } catch (err) { res.status(500).json({ error: err.message }); }
});

// GET /me/issued - STUDENT
router.get('/me/issued', authenticate, authorize('STUDENT'), async (req, res) => {
  try {
    const student = await prisma.student.findUnique({ where: { userId: req.user.userId } });
    res.json(await prisma.issuedBook.findMany({ where: { studentId: student.id }, include: { book: true } }));
  } catch (err) { res.status(500).json({ error: err.message }); }
});

module.exports = router;
