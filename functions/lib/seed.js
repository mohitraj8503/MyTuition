"use strict";
var __createBinding = (this && this.__createBinding) || (Object.create ? (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    var desc = Object.getOwnPropertyDescriptor(m, k);
    if (!desc || ("get" in desc ? !m.__esModule : desc.writable || desc.configurable)) {
      desc = { enumerable: true, get: function() { return m[k]; } };
    }
    Object.defineProperty(o, k2, desc);
}) : (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    o[k2] = m[k];
}));
var __setModuleDefault = (this && this.__setModuleDefault) || (Object.create ? (function(o, v) {
    Object.defineProperty(o, "default", { enumerable: true, value: v });
}) : function(o, v) {
    o["default"] = v;
});
var __importStar = (this && this.__importStar) || (function () {
    var ownKeys = function(o) {
        ownKeys = Object.getOwnPropertyNames || function (o) {
            var ar = [];
            for (var k in o) if (Object.prototype.hasOwnProperty.call(o, k)) ar[ar.length] = k;
            return ar;
        };
        return ownKeys(o);
    };
    return function (mod) {
        if (mod && mod.__esModule) return mod;
        var result = {};
        if (mod != null) for (var k = ownKeys(mod), i = 0; i < k.length; i++) if (k[i] !== "default") __createBinding(result, mod, k[i]);
        __setModuleDefault(result, mod);
        return result;
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
exports.runSeed = runSeed;
const admin = __importStar(require("firebase-admin"));
if (!admin.apps.length) {
    admin.initializeApp();
}
const db = admin.firestore();
async function runSeed() {
    console.log("Seeding MyTuition Firestore database...");
    // 1. Institute
    await db.collection("institutes").doc("inst_456").set({
        instituteId: "inst_456",
        name: "Chanakya Classes",
        address: "12, MG Road, Indiranagar, Bengaluru, KA 560038",
        phone: "+918012345678",
        email: "contact@chanakyaclasses.in",
        logoUrl: "",
        floors: [
            { floorId: "floor_g", name: "Ground Floor" },
            { floorId: "floor_1", name: "1st Floor" },
            { floorId: "floor_2", name: "2nd Floor" }
        ],
        createdAt: new Date().toISOString()
    });
    // 2. Student User (Mohit Raj)
    await db.collection("users").doc("stu_789").set({
        uid: "stu_789",
        instituteId: "inst_456",
        role: "STUDENT",
        name: "Mohit Raj",
        email: "mohitraj8503@gmail.com",
        phone: "+919876543210",
        photoUrl: "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=200&q=80",
        studentInfo: {
            studentId: "stu_789",
            classGrade: "Class 10",
            section: "A",
            rollNumber: "27",
            schoolName: "St. Xavier's High School",
            batches: ["batch_10a_maths", "batch_10a_science", "batch_10a_english"],
            parentUid: "parent_001",
            dateOfBirth: "2009-05-15",
            address: "123, Indiranagar, Bengaluru, KA 560038"
        },
        isActive: true,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString()
    });
    // 3. Parent User
    await db.collection("users").doc("parent_001").set({
        uid: "parent_001",
        instituteId: "inst_456",
        role: "PARENT",
        name: "Rajesh Kumar",
        email: "parent.rajesh@gmail.com",
        phone: "+919811223344",
        parentInfo: {
            childUids: ["stu_789"],
            childStudentIds: ["stu_789"],
            relationship: "Father"
        },
        isActive: true,
        createdAt: new Date().toISOString()
    });
    // 4. Teachers
    const teachers = [
        {
            teacherId: "teacher_001",
            name: "Mr. Rakesh Sharma",
            email: "rakesh@chanakya.in",
            phone: "+919812345678",
            qualification: "M.Sc. Mathematics, B.Ed.",
            experienceYears: 12,
            subjects: ["subj_maths"],
            batches: ["batch_10a_maths"]
        },
        {
            teacherId: "teacher_002",
            name: "Dr. Aalvina Fatehi",
            email: "aalvina@chanakya.in",
            phone: "+919823456789",
            qualification: "Ph.D. Fine Arts & Science",
            experienceYears: 9,
            subjects: ["subj_sketching", "subj_physics"],
            batches: ["batch_10a_sketching"]
        },
        {
            teacherId: "teacher_003",
            name: "Ms. Sara Khan",
            email: "sara@chanakya.in",
            phone: "+919834567890",
            qualification: "M.Sc. Chemistry",
            experienceYears: 8,
            subjects: ["subj_chem"],
            batches: ["batch_10a_science"]
        },
        {
            teacherId: "teacher_004",
            name: "Mrs. Anjali Das",
            email: "anjali@chanakya.in",
            phone: "+919845678901",
            qualification: "M.A. English Literature",
            experienceYears: 10,
            subjects: ["subj_english"],
            batches: ["batch_10a_english"]
        }
    ];
    for (const t of teachers) {
        await db.collection("teachers").doc(t.teacherId).set({
            ...t,
            instituteId: "inst_456",
            createdAt: new Date().toISOString()
        });
    }
    // 5. Subjects
    const subjects = [
        {
            subjectId: "subj_sketching",
            name: "Creative Sketching",
            code: "SKETCH",
            iconColor: "#6C48FF",
            iconName: "palette",
            description: "Art, Perspective, and Illustration",
            syllabus: [
                { chapter: 1, title: "Basics of Drawing", status: "COMPLETED" },
                { chapter: 2, title: "Light & Shadows", status: "COMPLETED" },
                { chapter: 3, title: "Perspective Theory", status: "IN_PROGRESS" },
                { chapter: 4, title: "Human Figure Anatomy", status: "UPCOMING" }
            ]
        },
        {
            subjectId: "subj_maths",
            name: "Maths",
            code: "MATH",
            iconColor: "#34C759",
            iconName: "calculate",
            description: "Class 10 Mathematics CBSE/ICSE",
            syllabus: [
                { chapter: 1, title: "Real Numbers", status: "COMPLETED" },
                { chapter: 2, title: "Polynomials", status: "COMPLETED" },
                { chapter: 3, title: "Linear Equations", status: "COMPLETED" },
                { chapter: 4, title: "Quadratic Equations", status: "IN_PROGRESS" },
                { chapter: 5, title: "Arithmetic Progressions", status: "UPCOMING" }
            ]
        },
        {
            subjectId: "subj_chem",
            name: "Chemistry",
            code: "CHEM",
            iconColor: "#FF9500",
            iconName: "science",
            description: "Class 10 Chemistry",
            syllabus: [
                { chapter: 1, title: "Chemical Reactions", status: "COMPLETED" },
                { chapter: 2, title: "Acids, Bases & Salts", status: "COMPLETED" },
                { chapter: 3, title: "Metals & Non-metals", status: "IN_PROGRESS" }
            ]
        },
        {
            subjectId: "subj_english",
            name: "English",
            code: "ENG",
            iconColor: "#007AFF",
            iconName: "book",
            description: "English Language and Literature",
            syllabus: [
                { chapter: 1, title: "First Flight", status: "COMPLETED" },
                { chapter: 2, title: "Footprints without Feet", status: "IN_PROGRESS" },
                { chapter: 3, title: "Essay & Writing Skills", status: "IN_PROGRESS" }
            ]
        }
    ];
    for (const s of subjects) {
        await db.collection("subjects").doc(s.subjectId).set({
            ...s,
            instituteId: "inst_456",
            createdAt: new Date().toISOString()
        });
    }
    // 6. Batches
    const batches = [
        {
            batchId: "batch_10a_maths",
            subjectId: "subj_maths",
            subjectName: "Maths",
            name: "Class 10-A • Maths Batch",
            classGrade: "Class 10",
            section: "A",
            teacherId: "teacher_001",
            teacherName: "Mr. Rakesh Sharma",
            studentIds: ["stu_789"],
            defaultRoomId: "room_4b",
            defaultRoomName: "4B",
            defaultFloor: "2nd Floor"
        },
        {
            batchId: "batch_10a_science",
            subjectId: "subj_chem",
            subjectName: "Chemistry",
            name: "Class 10-A • Science Batch",
            classGrade: "Class 10",
            section: "A",
            teacherId: "teacher_003",
            teacherName: "Ms. Sara Khan",
            studentIds: ["stu_789"],
            defaultRoomId: "room_2a",
            defaultRoomName: "2A",
            defaultFloor: "1st Floor"
        },
        {
            batchId: "batch_10a_english",
            subjectId: "subj_english",
            subjectName: "English",
            name: "Class 10-A • English Batch",
            classGrade: "Class 10",
            section: "A",
            teacherId: "teacher_004",
            teacherName: "Mrs. Anjali Das",
            studentIds: ["stu_789"],
            defaultRoomId: "room_3c",
            defaultRoomName: "3C",
            defaultFloor: "Ground Floor"
        }
    ];
    for (const b of batches) {
        await db.collection("batches").doc(b.batchId).set({
            ...b,
            instituteId: "inst_456",
            academicYear: "2025-2026",
            createdAt: new Date().toISOString()
        });
    }
    // 7. Class Sessions (The timetable matching Home screen design)
    const sessions = [
        // Next Class Card: Creative Sketching at 5:00 PM
        {
            sessionId: "session_sketching",
            batchId: "batch_10a_sketching",
            batchName: "Creative Sketching Batch",
            subjectId: "subj_sketching",
            subjectName: "Creative Sketching",
            subjectIconColor: "#6C48FF",
            subjectIconName: "palette",
            teacherId: "teacher_002",
            teacherName: "Dr. Aalvina Fatehi",
            roomId: "room_4b",
            roomName: "4B",
            floorName: "2nd Floor",
            directionsNote: "Opposite Physics Lab • Next to Staircase B",
            date: "2025-08-17",
            dayOfWeek: "MONDAY",
            startTime: "2025-08-17T17:00:00+05:30",
            endTime: "2025-08-17T18:30:00+05:30",
            startTimeDisplay: "5:00 PM",
            endTimeDisplay: "6:30 PM",
            topic: "Perspective and 3D Shadows",
            chapter: 3,
            status: "UPCOMING",
            sessionType: "CLASS"
        },
        // Timeline items for 2025-08-17
        {
            sessionId: "session_001",
            batchId: "batch_10a_maths",
            batchName: "Class 10-A • Maths Batch",
            subjectId: "subj_maths",
            subjectName: "Maths",
            subjectIconColor: "#34C759",
            subjectIconName: "calculate",
            teacherId: "teacher_001",
            teacherName: "Mr. Rakesh Sharma",
            roomId: "room_4b",
            roomName: "4B",
            floorName: "2nd Floor",
            date: "2025-08-17",
            dayOfWeek: "MONDAY",
            startTime: "2025-08-17T08:00:00+05:30",
            endTime: "2025-08-17T09:30:00+05:30",
            startTimeDisplay: "8:00 AM",
            endTimeDisplay: "9:30 AM",
            topic: "Chapter 4: Quadratic Equations",
            chapter: 4,
            status: "COMPLETED",
            sessionType: "LECTURE"
        },
        {
            sessionId: "session_002",
            batchId: "batch_10a_science",
            batchName: "Class 10-A • Science Batch",
            subjectId: "subj_chem",
            subjectName: "Chemistry",
            subjectIconColor: "#FF9500",
            subjectIconName: "science",
            teacherId: "teacher_003",
            teacherName: "Ms. Sara Khan",
            roomId: "room_2a",
            roomName: "2A",
            floorName: "1st Floor",
            date: "2025-08-17",
            dayOfWeek: "MONDAY",
            startTime: "2025-08-17T10:00:00+05:30",
            endTime: "2025-08-17T11:00:00+05:30",
            startTimeDisplay: "10:00 AM",
            endTimeDisplay: "11:00 AM",
            topic: "Atoms & Molecules",
            chapter: 2,
            status: "UPCOMING",
            sessionType: "LECTURE"
        },
        {
            sessionId: "session_003",
            batchId: "batch_10a_english",
            batchName: "Class 10-A • English Batch",
            subjectId: "subj_english",
            subjectName: "English",
            subjectIconColor: "#007AFF",
            subjectIconName: "book",
            teacherId: "teacher_004",
            teacherName: "Mrs. Anjali Das",
            roomId: "room_3c",
            roomName: "3C",
            floorName: "Ground Floor",
            date: "2025-08-17",
            dayOfWeek: "MONDAY",
            startTime: "2025-08-17T12:00:00+05:30",
            endTime: "2025-08-17T13:00:00+05:30",
            startTimeDisplay: "12:00 PM",
            endTimeDisplay: "1:00 PM",
            topic: "Essay Writing",
            chapter: 3,
            status: "UPCOMING",
            sessionType: "LECTURE"
        },
        // Sessions on other dates for week
        {
            sessionId: "session_004",
            batchId: "batch_10a_maths",
            batchName: "Class 10-A • Maths Batch",
            subjectId: "subj_maths",
            subjectName: "Maths",
            subjectIconColor: "#34C759",
            subjectIconName: "calculate",
            teacherId: "teacher_001",
            teacherName: "Mr. Rakesh Sharma",
            roomId: "room_4b",
            roomName: "4B",
            floorName: "2nd Floor",
            date: "2025-08-18",
            dayOfWeek: "TUESDAY",
            startTime: "2025-08-18T09:00:00+05:30",
            endTime: "2025-08-18T10:30:00+05:30",
            startTimeDisplay: "9:00 AM",
            endTimeDisplay: "10:30 AM",
            topic: "Quadratic Equations Problem Solving",
            chapter: 4,
            status: "SCHEDULED",
            sessionType: "LECTURE"
        },
        {
            sessionId: "session_005",
            batchId: "batch_10a_science",
            batchName: "Class 10-A • Science Batch",
            subjectId: "subj_chem",
            subjectName: "Chemistry",
            subjectIconColor: "#FF9500",
            subjectIconName: "science",
            teacherId: "teacher_003",
            teacherName: "Ms. Sara Khan",
            roomId: "room_2a",
            roomName: "2A",
            floorName: "1st Floor",
            date: "2025-08-19",
            dayOfWeek: "WEDNESDAY",
            startTime: "2025-08-19T11:00:00+05:30",
            endTime: "2025-08-19T12:30:00+05:30",
            startTimeDisplay: "11:00 AM",
            endTimeDisplay: "12:30 PM",
            topic: "Periodic Table Classification",
            chapter: 3,
            status: "SCHEDULED",
            sessionType: "LECTURE"
        }
    ];
    for (const s of sessions) {
        await db.collection("class_sessions").doc(s.sessionId).set({
            ...s,
            instituteId: "inst_456",
            createdAt: new Date().toISOString()
        });
    }
    // 8. Homework Items
    const homeworkItems = [
        {
            homeworkId: "hw_001",
            batchId: "batch_10a_maths",
            subjectId: "subj_maths",
            subjectName: "Maths",
            subjectIconColor: "#34C759",
            teacherId: "teacher_001",
            teacherName: "Mr. Rakesh Sharma",
            title: "Trigonometry Worksheet",
            description: "Solve all questions from Exercise 8.1 in NCERT textbook. Show all working steps. Submit on Monday.",
            assignedDate: "2025-08-15",
            dueDate: "2025-08-19",
            dueDateTime: "2025-08-19T09:00:00+05:30",
            chapter: 8,
            topic: "Introduction to Trigonometry",
            studentIds: ["stu_789"],
            attachments: [
                {
                    name: "trig_worksheet.pdf",
                    url: "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf",
                    type: "PDF",
                    sizeBytes: 245678
                }
            ],
            status: "ACTIVE"
        },
        {
            homeworkId: "hw_002",
            batchId: "batch_10a_science",
            subjectId: "subj_chem",
            subjectName: "Chemistry",
            subjectIconColor: "#FF9500",
            teacherId: "teacher_003",
            teacherName: "Ms. Sara Khan",
            title: "Atoms Lab Report",
            description: "Complete numerical questions from Chapter 2 and prepare laboratory observations summary.",
            assignedDate: "2025-08-16",
            dueDate: "2025-08-20",
            dueDateTime: "2025-08-20T10:00:00+05:30",
            chapter: 2,
            topic: "Atoms & Molecules",
            studentIds: ["stu_789"],
            attachments: [],
            status: "ACTIVE"
        },
        {
            homeworkId: "hw_003",
            batchId: "batch_10a_english",
            subjectId: "subj_english",
            subjectName: "English",
            subjectIconColor: "#007AFF",
            teacherId: "teacher_004",
            teacherName: "Mrs. Anjali Das",
            title: "Essay: Impact of Technology",
            description: "Write a 500-word essay on the impact of technology on modern education. Must be typed.",
            assignedDate: "2025-08-10",
            dueDate: "2025-08-14",
            dueDateTime: "2025-08-14T23:59:00+05:30",
            chapter: 3,
            topic: "Essay Writing",
            studentIds: ["stu_789"],
            attachments: [],
            status: "OVERDUE"
        },
        {
            homeworkId: "hw_004",
            batchId: "batch_10a_science",
            subjectId: "subj_sketching",
            subjectName: "Physics / Sketching",
            subjectIconColor: "#6C48FF",
            teacherId: "teacher_002",
            teacherName: "Dr. Aalvina Fatehi",
            title: "Newton's Laws Diagram",
            description: "Draw free body diagrams demonstrating all three laws of motion.",
            assignedDate: "2025-08-05",
            dueDate: "2025-08-10",
            dueDateTime: "2025-08-10T17:00:00+05:30",
            chapter: 2,
            topic: "Laws of Motion",
            studentIds: ["stu_789"],
            attachments: [],
            status: "COMPLETED"
        }
    ];
    for (const hw of homeworkItems) {
        await db.collection("homework").doc(hw.homeworkId).set({
            ...hw,
            instituteId: "inst_456",
            createdAt: new Date().toISOString()
        });
    }
    // 9. Homework Submission for Completed Item
    await db.collection("homework_submissions").doc("sub_hw004_stu789").set({
        submissionId: "sub_hw004_stu789",
        homeworkId: "hw_004",
        studentId: "stu_789",
        studentUid: "stu_789",
        status: "SUBMITTED",
        submittedAt: "2025-08-09T14:30:00+05:30",
        grade: "A",
        teacherRemarks: "Excellent diagrams with clear force labels."
    });
    // 10. Attendance Records (18 attended out of 20)
    for (let i = 1; i <= 20; i++) {
        const isPresent = i <= 18;
        await db.collection("attendance").doc(`att_rec_${i}`).set({
            attendanceId: `att_rec_${i}`,
            instituteId: "inst_456",
            sessionId: `session_sample_${i}`,
            studentId: "stu_789",
            studentUid: "stu_789",
            date: `2025-08-${String(i).padStart(2, "0")}`,
            status: isPresent ? "PRESENT" : "ABSENT",
            markedBy: "teacher_001",
            markedAt: `2025-08-${String(i).padStart(2, "0")}T09:05:00+05:30`
        });
    }
    // 11. Fees
    await db.collection("fees").doc("fee_aug2025_stu789").set({
        feeId: "fee_aug2025_stu789",
        instituteId: "inst_456",
        studentId: "stu_789",
        studentUid: "stu_789",
        batchIds: ["batch_10a_maths", "batch_10a_science", "batch_10a_english"],
        amount: 2500.0,
        amountDisplay: "₹2,500",
        period: "MONTHLY",
        month: "AUGUST",
        academicYear: "2025-2026",
        dueDate: "2025-08-10",
        status: "PAID",
        paidDate: "2025-08-08",
        paymentMethod: "RAZORPAY_UPI",
        razorpayPaymentId: "pay_Nabc123XYZ",
        razorpayOrderId: "order_Nabc456XYZ"
    });
    await db.collection("fees").doc("fee_sep2025_stu789").set({
        feeId: "fee_sep2025_stu789",
        instituteId: "inst_456",
        studentId: "stu_789",
        studentUid: "stu_789",
        batchIds: ["batch_10a_maths", "batch_10a_science", "batch_10a_english"],
        amount: 2500.0,
        amountDisplay: "₹2,500",
        period: "MONTHLY",
        month: "SEPTEMBER",
        academicYear: "2025-2026",
        dueDate: "2025-09-10",
        status: "PENDING"
    });
    // 12. Announcements
    await db.collection("announcements").doc("ann_001").set({
        announcementId: "ann_001",
        instituteId: "inst_456",
        title: "Independence Day Holiday",
        message: "The institute will remain closed on August 15th for Independence Day. Classes resume on August 16th.",
        type: "HOLIDAY",
        date: "2025-08-13"
    });
    await db.collection("announcements").doc("ann_002").set({
        announcementId: "ann_002",
        instituteId: "inst_456",
        title: "Class 10 Monthly Test Series",
        message: "Mathematics and Science monthly assessments will be conducted on August 25th.",
        type: "EVENT",
        date: "2025-08-16"
    });
    console.log("Seeding complete! 12 collections populated with real tuition data.");
}
if (require.main === module) {
    runSeed().catch(console.error);
}
//# sourceMappingURL=seed.js.map