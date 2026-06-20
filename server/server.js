const express = require('express');
const cors = require('cors');
require('dotenv').config();

const app = express();
const PORT = process.env.PORT || 3000;

// Middleware
app.use(cors());
app.use(express.json());

// Mount Routes
const authRoutes = require('./routes/auth');
const adminRoutes = require('./routes/admin');
const dashboardRoutes = require('./routes/dashboard');
const studentsRoutes = require('./routes/students');
const facultyRoutes = require('./routes/faculty');
const parentsRoutes = require('./routes/parents');
const academicsRoutes = require('./routes/academics');
const migrateRoutes = require('./routes/migrate');

app.use('/api/auth', authRoutes);
app.use('/api/admin', adminRoutes);
app.use('/api/dashboard', dashboardRoutes);
app.use('/api/students', studentsRoutes);
app.use('/api/faculty', facultyRoutes);
app.use('/api/parents', parentsRoutes);
app.use('/api/academics', academicsRoutes);
app.use('/api/migrate', migrateRoutes);

app.use('/api/courses', require('./routes/courses'));
app.use('/api/attendance', require('./routes/attendance'));
app.use('/api/exams', require('./routes/exams'));
app.use('/api/timetable', require('./routes/timetable'));

app.use('/api/notices', require('./routes/notices'));
app.use('/api/complaints', require('./routes/complaints'));
app.use('/api/library', require('./routes/library'));
app.use('/api/hostel', require('./routes/hostel'));
app.use('/api/transport', require('./routes/transport'));
app.use('/api/placement', require('./routes/placement'));
app.use('/api/leave', require('./routes/leave'));

app.use('/api/seed', require('./routes/seed'));

// Health check endpoint
app.get('/', (req, res) => {
  res.send('IMS API Server is running on Vercel with Supabase! (Expanded v2)');
});

// Start the server (for local development)
if (process.env.NODE_ENV !== 'production') {
  app.listen(PORT, () => {
    console.log(`🚀 Server running locally on port ${PORT}`);
  });
}

// Export for Vercel Serverless Functions
module.exports = app;
