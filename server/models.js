const mongoose = require('mongoose');

// --- Student ---
const studentSchema = new mongoose.Schema({
  id: { type: Number, default: () => Date.now(), unique: true },
  studentId: { type: String, required: true, unique: true },
  firstName: { type: String, required: true },
  lastName: { type: String, required: true },
  email: { type: String, required: true },
  phone: { type: String, required: true },
  dateOfBirth: String,
  gender: String,
  address: String,
  city: String,
  state: String,
  pinCode: String,
  bloodGroup: String,
  nationality: String,
  religion: String,
  category: String,
  photoUrl: String,
  batchId: Number,
  courseId: Number, 
  admissionDate: String,
  previousSchool: String,
  previousGrade: String,
  previousPercentage: String,
  status: { type: String, default: 'Active' },
  createdAt: { type: Number, default: Date.now }
}, { toJSON: { virtuals: true }, id: false });

const Student = mongoose.model('Student', studentSchema);

// --- Course ---
const courseSchema = new mongoose.Schema({
  id: { type: Number, default: () => Date.now(), unique: true },
  name: { type: String, required: true },
  code: { type: String, required: true },
  duration: String,
  description: String,
  department: String,
  status: { type: String, default: 'Active' },
  createdAt: { type: Number, default: Date.now }
}, { toJSON: { virtuals: true }, id: false });
const Course = mongoose.model('Course', courseSchema);

// --- Batch ---
const batchSchema = new mongoose.Schema({
  id: { type: Number, default: () => Date.now(), unique: true },
  courseId: Number,
  name: { type: String, required: true },
  startYear: String,
  endYear: String,
  currentSemester: Number,
  capacity: Number,
  status: { type: String, default: 'Active' },
  createdAt: { type: Number, default: Date.now }
}, { toJSON: { virtuals: true }, id: false });
const Batch = mongoose.model('Batch', batchSchema);

// --- Subject ---
const subjectSchema = new mongoose.Schema({
  id: { type: Number, default: () => Date.now(), unique: true },
  courseId: Number,
  name: { type: String, required: true },
  code: { type: String, required: true },
  credits: Number,
  semester: Number,
  type: String,
  status: { type: String, default: 'Active' },
  createdAt: { type: Number, default: Date.now }
}, { toJSON: { virtuals: true }, id: false });
const Subject = mongoose.model('Subject', subjectSchema);

// --- Exam ---
const examSchema = new mongoose.Schema({
  id: { type: Number, default: () => Date.now(), unique: true },
  name: { type: String, required: true },
  type: String,
  subjectId: Number,
  courseId: Number,
  batchId: Number,
  date: String,
  startTime: String,
  endTime: String,
  totalMarks: Number,
  passingMarks: Number,
  description: String,
  venue: String,
  status: { type: String, default: 'Scheduled' },
  createdAt: { type: Number, default: Date.now }
}, { toJSON: { virtuals: true }, id: false });
const Exam = mongoose.model('Exam', examSchema);

// --- Exam Result ---
const examResultSchema = new mongoose.Schema({
  id: { type: Number, default: () => Date.now(), unique: true },
  examId: Number,
  studentId: Number,
  marksObtained: Number,
  grade: String,
  status: String,
  remarks: String,
  createdAt: { type: Number, default: Date.now }
}, { toJSON: { virtuals: true }, id: false });
const ExamResult = mongoose.model('ExamResult', examResultSchema);

// --- News ---
const newsSchema = new mongoose.Schema({
  id: { type: Number, default: () => Date.now(), unique: true },
  title: { type: String, required: true },
  content: { type: String, required: true },
  author: String,
  category: String,
  isPublished: Boolean,
  publishedAt: String,
  createdAt: { type: Number, default: Date.now }
}, { toJSON: { virtuals: true }, id: false });
const News = mongoose.model('News', newsSchema);

module.exports = {
  Student, Course, Batch, Subject, Exam, ExamResult, News
};
