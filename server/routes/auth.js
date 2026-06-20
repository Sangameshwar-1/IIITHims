const express = require('express');
const router = express.Router();
const bcrypt = require('bcryptjs');
const prisma = require('../prisma');
const { generateToken, authenticate, authorize } = require('../middleware/auth');

// POST /login - Public
router.post('/login', async (req, res) => {
  try {
    const { email, password } = req.body;
    if (!email || !password) return res.status(400).json({ error: 'Email and password required' });

    const user = await prisma.user.findUnique({ where: { email } });
    if (!user || !user.isActive) return res.status(401).json({ error: 'Invalid credentials or inactive account' });

    const isValid = await bcrypt.compare(password, user.passwordHash);
    if (!isValid) return res.status(401).json({ error: 'Invalid credentials' });

    const token = generateToken(user);
    res.json({ token, user: { id: user.id, email: user.email, role: user.role } });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// POST /register - Admin Only
router.post('/register', authenticate, authorize('ADMIN'), async (req, res) => {
  try {
    const { email, password, role, firstName, lastName, ...profileFields } = req.body;
    if (!email || !password || !role) return res.status(400).json({ error: 'Email, password, and role required' });

    const existing = await prisma.user.findUnique({ where: { email } });
    if (existing) return res.status(400).json({ error: 'Email already in use' });

    const passwordHash = await bcrypt.hash(password, 10);

    const result = await prisma.$transaction(async (tx) => {
      const user = await tx.user.create({
        data: { email, passwordHash, role }
      });

      if (role === 'STUDENT') {
        const { studentId, phone, courseId, batchId, departmentId } = profileFields;
        await tx.student.create({
          data: {
            userId: user.id,
            studentId, firstName, lastName, email, phone, courseId, batchId, departmentId
          }
        });
      } else if (role === 'FACULTY') {
        const { facultyId, phone, departmentId, designation } = profileFields;
        await tx.faculty.create({
          data: {
            userId: user.id,
            facultyId, firstName, lastName, email, phone, departmentId, designation
          }
        });
      } else if (role === 'PARENT') {
        const { studentId, phone, relation } = profileFields;
        await tx.parent.create({
          data: {
            userId: user.id,
            studentId, firstName, lastName, email, phone, relation
          }
        });
      }
      return user;
    });

    res.status(201).json({ message: 'User created successfully', userId: result.id });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// PUT /change-password - Authenticated
router.put('/change-password', authenticate, async (req, res) => {
  try {
    const { currentPassword, newPassword } = req.body;
    const user = await prisma.user.findUnique({ where: { id: req.user.userId } });

    const isValid = await bcrypt.compare(currentPassword, user.passwordHash);
    if (!isValid) return res.status(400).json({ error: 'Incorrect current password' });

    const passwordHash = await bcrypt.hash(newPassword, 10);
    await prisma.user.update({
      where: { id: user.id },
      data: { passwordHash }
    });

    res.json({ message: 'Password updated successfully' });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// GET /me - Authenticated
router.get('/me', authenticate, async (req, res) => {
  try {
    const user = await prisma.user.findUnique({
      where: { id: req.user.userId },
      include: {
        student: true,
        faculty: true,
        parent: { include: { student: true } }
      }
    });
    
    // Remove password hash before sending
    const { passwordHash, ...safeUser } = user;
    res.json(safeUser);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

module.exports = router;
