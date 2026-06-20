const express = require('express');
const { PrismaClient } = require('@prisma/client');

const router = express.Router();
const prisma = new PrismaClient();

router.get('/seed', async (req, res) => {
  try {
    console.log('Clearing old data...');
    await prisma.student.deleteMany({});
    await prisma.course.deleteMany({});
    await prisma.batch.deleteMany({});
    await prisma.exam.deleteMany({});

    console.log('Seeding courses...');
    const cse = await prisma.course.create({ data: { name: 'Computer Science and Engineering', code: 'CSE', duration: '4 Years', department: 'Engineering' }});
    const ece = await prisma.course.create({ data: { name: 'Electronics and Communication', code: 'ECE', duration: '4 Years', department: 'Engineering' }});
    const mba = await prisma.course.create({ data: { name: 'Master of Business Administration', code: 'MBA', duration: '2 Years', department: 'Management' }});

    console.log('Seeding batches...');
    const batch1 = await prisma.batch.create({ data: { courseId: cse.id, name: 'CSE Batch 2024', startYear: '2024', endYear: '2028', currentSemester: 1, capacity: 60 }});
    const batch2 = await prisma.batch.create({ data: { courseId: ece.id, name: 'ECE Batch 2024', startYear: '2024', endYear: '2028', currentSemester: 1, capacity: 40 }});

    console.log('Seeding students...');
    await prisma.student.createMany({
      data: [
        { studentId: 'STU-2024-0001', firstName: 'Aarav', lastName: 'Sharma', email: 'aarav.sharma@example.com', phone: '9876543210', gender: 'Male', courseId: cse.id, batchId: batch1.id, status: 'Active' },
        { studentId: 'STU-2024-0002', firstName: 'Diya', lastName: 'Patel', email: 'diya.patel@example.com', phone: '9876543211', gender: 'Female', courseId: cse.id, batchId: batch1.id, status: 'Active' },
        { studentId: 'STU-2024-0003', firstName: 'Vivaan', lastName: 'Singh', email: 'vivaan.singh@example.com', phone: '9876543212', gender: 'Male', courseId: ece.id, batchId: batch2.id, status: 'Active' }
      ]
    });

    console.log('Seeding exams...');
    await prisma.exam.create({ data: { name: 'Mid-Term Examination 2024', type: 'Marks', courseId: cse.id, batchId: batch1.id, date: new Date('2024-10-15'), totalMarks: 100, passingMarks: 40, status: 'Scheduled' }});

    res.json({ message: 'Seeding completed successfully!' });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

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
