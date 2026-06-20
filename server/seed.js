require('dotenv').config();
const mongoose = require('mongoose');
const { Student, Course, Batch, Subject, Exam, ExamResult, News } = require('./models');

const MONGO_URI = process.env.MONGO_URI || 'mongodb://127.0.0.1:27017/ims';

const seedData = async () => {
  try {
    await mongoose.connect(MONGO_URI);
    console.log('Connected to MongoDB. Clearing old data...');

    // Clear existing data
    await Promise.all([
      Student.deleteMany({}),
      Course.deleteMany({}),
      Batch.deleteMany({}),
      Subject.deleteMany({}),
      Exam.deleteMany({}),
      ExamResult.deleteMany({}),
      News.deleteMany({})
    ]);

    console.log('Seeding new data...');

    // Seed Courses
    const courses = await Course.insertMany([
      { name: 'Computer Science and Engineering', code: 'CSE', duration: '4 Years', department: 'Engineering' },
      { name: 'Electronics and Communication', code: 'ECE', duration: '4 Years', department: 'Engineering' },
      { name: 'Mechanical Engineering', code: 'ME', duration: '4 Years', department: 'Engineering' },
      { name: 'Master of Business Administration', code: 'MBA', duration: '2 Years', department: 'Management' }
    ]);

    // Seed Batches
    const batches = await Batch.insertMany([
      { courseId: courses[0]._id, name: 'CSE Batch 2024', startYear: '2024', endYear: '2028', currentSemester: 1, capacity: 60 },
      { courseId: courses[0]._id, name: 'CSE Batch 2023', startYear: '2023', endYear: '2027', currentSemester: 3, capacity: 60 },
      { courseId: courses[1]._id, name: 'ECE Batch 2024', startYear: '2024', endYear: '2028', currentSemester: 1, capacity: 40 },
      { courseId: courses[3]._id, name: 'MBA Batch 2024', startYear: '2024', endYear: '2026', currentSemester: 1, capacity: 30 }
    ]);

    // Seed Students
    const students = await Student.insertMany([
      {
        studentId: 'STU-2024-0001', firstName: 'Aarav', lastName: 'Sharma', email: 'aarav.sharma@example.com',
        phone: '9876543210', dateOfBirth: '2005-04-12', gender: 'Male', address: '123 Main St', city: 'Delhi',
        state: 'Delhi', pinCode: '110001', bloodGroup: 'O+', nationality: 'Indian', religion: 'Hindu',
        category: 'General', courseId: courses[0]._id, batchId: batches[0]._id, admissionDate: '2024-08-01',
        previousSchool: 'DPS RK Puram', previousGrade: '12th', previousPercentage: '92.5', status: 'Active'
      },
      {
        studentId: 'STU-2024-0002', firstName: 'Diya', lastName: 'Patel', email: 'diya.patel@example.com',
        phone: '9876543211', dateOfBirth: '2005-08-22', gender: 'Female', address: '456 Park Ave', city: 'Mumbai',
        state: 'Maharashtra', pinCode: '400001', bloodGroup: 'B+', nationality: 'Indian', religion: 'Hindu',
        category: 'General', courseId: courses[0]._id, batchId: batches[0]._id, admissionDate: '2024-08-02',
        previousSchool: 'Bombay Scottish', previousGrade: '12th', previousPercentage: '95.0', status: 'Active'
      },
      {
        studentId: 'STU-2024-0003', firstName: 'Vivaan', lastName: 'Singh', email: 'vivaan.singh@example.com',
        phone: '9876543212', dateOfBirth: '2004-11-05', gender: 'Male', address: '789 Lake Rd', city: 'Bangalore',
        state: 'Karnataka', pinCode: '560001', bloodGroup: 'A-', nationality: 'Indian', religion: 'Hindu',
        category: 'General', courseId: courses[1]._id, batchId: batches[2]._id, admissionDate: '2024-08-03',
        previousSchool: 'Bishop Cotton', previousGrade: '12th', previousPercentage: '88.0', status: 'Active'
      }
    ]);

    // Seed Exams
    const exams = await Exam.insertMany([
      {
        name: 'Mid-Term Examination 2024', type: 'Marks', courseId: courses[0]._id, batchId: batches[0]._id,
        date: '2024-10-15', startTime: '09:00', endTime: '12:00', totalMarks: 100, passingMarks: 40,
        description: 'First semester mid-term exams', venue: 'Main Hall A', status: 'Scheduled'
      },
      {
        name: 'Final Semester Examination 2023', type: 'Grade', courseId: courses[0]._id, batchId: batches[1]._id,
        date: '2023-12-10', startTime: '10:00', endTime: '13:00', totalMarks: 100, passingMarks: 40,
        description: 'Second semester finals', venue: 'Main Hall B', status: 'Completed'
      }
    ]);

    // Seed News
    await News.insertMany([
      { title: 'Welcome to the New Academic Year', content: 'We are excited to welcome all incoming students to the 2024-2025 academic year. Orientation begins on August 15th.', author: 'Admin', category: 'General', isPublished: true, publishedAt: '2024-08-01' },
      { title: 'Campus Placement Drive 2024', content: 'Top tier companies including Google, Microsoft, and Amazon will be visiting the campus starting next week.', author: 'Placement Cell', category: 'Event', isPublished: true, publishedAt: '2024-09-05' },
      { title: 'Diwali Holidays Announced', content: 'The institute will remain closed from Oct 24th to Oct 28th on account of Diwali.', author: 'Registrar', category: 'Holiday', isPublished: true, publishedAt: '2024-10-10' }
    ]);

    console.log('Seeding completed successfully!');
    process.exit(0);
  } catch (err) {
    console.error('Error seeding database:', err);
    process.exit(1);
  }
};

seedData();
