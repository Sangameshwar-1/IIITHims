const express = require('express');
const { PrismaClient } = require('@prisma/client');

const router = express.Router();
const prisma = new PrismaClient();

// Helper function to create generic CRUD routes for a Prisma model
const createCrudRoutes = (model, basePath) => {
  // GET all
  router.get(`/${basePath}`, async (req, res) => {
    try {
      const items = await model.findMany({
        orderBy: { createdAt: 'desc' }
      });
      res.json(items);
    } catch (err) {
      res.status(500).json({ error: err.message });
    }
  });

  // GET by ID
  router.get(`/${basePath}/:id`, async (req, res) => {
    try {
      const item = await model.findUnique({
        where: { id: req.params.id }
      });
      if (!item) return res.status(404).json({ error: 'Not found' });
      res.json(item);
    } catch (err) {
      res.status(500).json({ error: err.message });
    }
  });

  // POST create
  router.post(`/${basePath}`, async (req, res) => {
    try {
      const data = { ...req.body, createdAt: req.body.createdAt || Date.now() };
      const saved = await model.create({ data });
      res.status(201).json(saved);
    } catch (err) {
      res.status(400).json({ error: err.message });
    }
  });

  // PUT update
  router.put(`/${basePath}/:id`, async (req, res) => {
    try {
      const updated = await model.update({
        where: { id: req.params.id },
        data: req.body
      });
      res.json(updated);
    } catch (err) {
      if (err.code === 'P2025') return res.status(404).json({ error: 'Not found' });
      res.status(400).json({ error: err.message });
    }
  });

  // DELETE
  router.delete(`/${basePath}/:id`, async (req, res) => {
    try {
      await model.delete({
        where: { id: req.params.id }
      });
      res.json({ message: 'Deleted successfully' });
    } catch (err) {
      if (err.code === 'P2025') return res.status(404).json({ error: 'Not found' });
      res.status(500).json({ error: err.message });
    }
  });
};

// Create routes for all models
createCrudRoutes(prisma.student, 'students');
createCrudRoutes(prisma.course, 'courses');
createCrudRoutes(prisma.batch, 'batches');
createCrudRoutes(prisma.subject, 'subjects');
createCrudRoutes(prisma.exam, 'exams');
createCrudRoutes(prisma.examResult, 'results');
createCrudRoutes(prisma.news, 'news');

// Specific Dashboard Stats Route
router.get('/dashboard/stats', async (req, res) => {
  try {
    const totalStudents = await prisma.student.count({ where: { status: 'Active' } });
    const activeCourses = await prisma.course.count({ where: { status: 'Active' } });
    const upcomingExams = await prisma.exam.count({ where: { status: 'Scheduled' } });
    
    const pendingAdmissions = 0; 

    res.json({
      totalStudents,
      activeCourses,
      upcomingExams,
      pendingAdmissions
    });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

module.exports = router;
