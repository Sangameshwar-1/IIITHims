require('dotenv').config();
const express = require('express');
const cors = require('cors');
const routes = require('./routes');
const { PrismaClient } = require('@prisma/client');

const app = express();
const PORT = process.env.PORT || 3000;
const prisma = new PrismaClient();

// Middleware
app.use(cors());
app.use(express.json());

// Routes
app.use('/api', routes);

// Database Connection check
prisma.$connect()
  .then(() => {
    console.log('Connected to PostgreSQL database via Prisma');
    
    // Start server locally (Vercel will ignore this if we export app)
    if (process.env.NODE_ENV !== 'production') {
      app.listen(PORT, () => {
        console.log(`IMS API Server running on port ${PORT}`);
      });
    }
  })
  .catch(err => {
    console.error('Failed to connect to the database', err);
    if (process.env.NODE_ENV !== 'production') {
      process.exit(1);
    }
  });

// Basic health check route
app.get('/', (req, res) => {
  res.send('IMS API Server is running on Vercel with Supabase!');
});

// Export the app for Vercel serverless functions
module.exports = app;
