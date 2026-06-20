const { PrismaClient } = require('@prisma/client');

// Shared PrismaClient singleton — prevents multiple instances
const prisma = new PrismaClient();

module.exports = prisma;
