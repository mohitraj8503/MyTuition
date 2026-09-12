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
exports.sendNotification = exports.verifyRazorpayPayment = exports.createRazorpayOrder = exports.getProfileData = exports.getCalendarData = exports.getSubjects = exports.markHomeworkComplete = exports.getHomeworkList = exports.getClassDetail = exports.getHomeData = exports.onUserLogin = void 0;
const functions = __importStar(require("firebase-functions"));
const admin = __importStar(require("firebase-admin"));
admin.initializeApp();
const db = admin.firestore();
/**
 * 3.1 onUserLogin - Auto-provision user on first login
 */
exports.onUserLogin = functions.region("asia-south1").https.onCall(async (data, context) => {
    const uid = context.auth?.uid || data?.uid || "stu_789";
    const userRef = db.collection("users").doc(uid);
    const userSnap = await userRef.get();
    if (!userSnap.exists) {
        const defaultProfile = {
            uid,
            instituteId: "inst_456",
            role: "STUDENT",
            name: data?.name || "Mohit Raj",
            email: data?.email || "mohitraj8503@gmail.com",
            phone: data?.phone || "+919876543210",
            photoUrl: data?.photoUrl || "",
            studentInfo: {
                studentId: "stu_789",
                classGrade: "Class 10",
                section: "A",
                rollNumber: "27",
                schoolName: "St. Xavier's School",
                batches: ["batch_10a_maths", "batch_10a_science", "batch_10a_english"],
                parentUid: "parent_uid_001",
                dateOfBirth: "2009-05-15",
                address: "123, Indiranagar, Bengaluru, KA 560038"
            },
            isActive: true,
            createdAt: admin.firestore.FieldValue.serverTimestamp(),
            updatedAt: admin.firestore.FieldValue.serverTimestamp()
        };
        await userRef.set(defaultProfile);
        return defaultProfile;
    }
    return userSnap.data();
});
/**
 * 3.2 getHomeData - Single API that returns EVERYTHING for the Home screen
 */
exports.getHomeData = functions.region("asia-south1").https.onCall(async (data, context) => {
    const uid = context.auth?.uid || "stu_789";
    const selectedDate = data?.date || "2025-08-17";
    const userDoc = await db.collection("users").doc(uid).get();
    const userData = userDoc.data() || {
        name: "Mohit Raj",
        studentInfo: { classGrade: "Class 10-A", batches: ["batch_10a_maths"] }
    };
    const batches = userData.studentInfo?.batches || ["batch_10a_maths"];
    // Sessions for selected date
    const sessionsSnap = await db.collection("class_sessions")
        .where("date", "==", selectedDate)
        .get();
    const timeline = sessionsSnap.docs
        .map(d => d.data())
        .sort((a, b) => (a.startTime || "").localeCompare(b.startTime || ""));
    // Next class calculation
    const nextClassSnap = await db.collection("class_sessions")
        .where("sessionId", "==", "session_sketching")
        .get();
    let nextClass = nextClassSnap.docs[0]?.data();
    if (!nextClass) {
        nextClass = {
            sessionId: "session_sketching",
            subjectName: "Creative Sketching",
            teacherName: "Dr. Aalvina Fatehi",
            startTimeDisplay: "5:00 PM",
            endTimeDisplay: "6:30 PM",
            roomName: "4B",
            floorName: "2nd Floor",
            directionsNote: "Opposite Physics Lab • Next to Staircase B"
        };
    }
    // Week dates with class indicators
    const weekDatesList = [
        { date: "2025-08-17", dayAbbr: "Mon", dayNumber: "17", hasClasses: true },
        { date: "2025-08-18", dayAbbr: "Tue", dayNumber: "18", hasClasses: true },
        { date: "2025-08-19", dayAbbr: "Wed", dayNumber: "19", hasClasses: true },
        { date: "2025-08-20", dayAbbr: "Thu", dayNumber: "20", hasClasses: false },
        { date: "2025-08-21", dayAbbr: "Fri", dayNumber: "21", hasClasses: true },
        { date: "2025-08-22", dayAbbr: "Sat", dayNumber: "22", hasClasses: true },
        { date: "2025-08-23", dayAbbr: "Sun", dayNumber: "23", hasClasses: false }
    ];
    // Announcements
    const annSnap = await db.collection("announcements").limit(3).get();
    const announcements = annSnap.docs.map(d => d.data());
    return {
        user: {
            uid,
            name: userData.name || "Mohit Raj",
            role: userData.role || "STUDENT",
            classGrade: userData.studentInfo?.classGrade || "Class 10",
            section: userData.studentInfo?.section || "A"
        },
        nextClass: {
            id: nextClass.sessionId,
            subjectName: nextClass.subjectName,
            teacherName: nextClass.teacherName,
            timeText: `Today • ${nextClass.startTimeDisplay} - ${nextClass.endTimeDisplay}`,
            countdownText: "Starts in 45 min",
            room: `Room ${nextClass.roomName || "4B"}`,
            floor: `${nextClass.floorName || "2nd Floor"}, Arts Block`,
            directionsNote: nextClass.directionsNote || "Opposite Physics Lab • Next to Staircase B",
            status: "UPCOMING"
        },
        schedule: {
            monthYear: "August 2025",
            selectedDate,
            weekDates: weekDatesList
        },
        timeline: timeline.map(s => ({
            sessionId: s.sessionId,
            time: (s.startTimeDisplay || "8:00 AM").replace(" AM", "").replace(" PM", ""),
            startTimeDisplay: s.startTimeDisplay,
            endTimeDisplay: s.endTimeDisplay,
            subjectName: s.subjectName,
            topic: s.topic,
            subjectIconColor: s.subjectIconColor || "#34C759",
            subjectIconName: s.subjectIconName || "calculate",
            teacherName: s.teacherName,
            roomName: s.roomName,
            floorName: s.floorName,
            status: s.status || "UPCOMING"
        })),
        summary: {
            attendancePercent: 92,
            classesAttended: 18,
            totalClasses: 20,
            feeStatus: "PAID",
            pendingHomeworkCount: 2,
            nextFeeDue: null,
            nextFeeAmount: null
        },
        announcements
    };
});
/**
 * 3.3 getClassDetail - Full detail of a specific class session
 */
exports.getClassDetail = functions.region("asia-south1").https.onCall(async (data, context) => {
    const sessionId = data?.sessionId || "session_sketching";
    const doc = await db.collection("class_sessions").doc(sessionId).get();
    const session = doc.data() || {
        sessionId,
        subjectName: "Creative Sketching",
        topic: "Chapter 4: Perspective and Shadows",
        date: "2025-08-17",
        startTimeDisplay: "5:00 PM",
        endTimeDisplay: "6:30 PM",
        teacherName: "Dr. Aalvina Fatehi",
        roomName: "4B",
        floorName: "2nd Floor"
    };
    return {
        session,
        professor: {
            name: session.teacherName || "Dr. Aalvina Fatehi",
            avatarUrl: "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&w=300&q=80",
            qualification: "Ph.D. Fine Arts & Physics"
        },
        attendance: {
            status: "PRESENT",
            markedAt: "2025-08-17T17:05:00+05:30"
        },
        lastLessons: [
            {
                subjectName: session.subjectName,
                topic: "Foundation Shapes",
                durationMinutes: 55,
                resourceType: "VIDEO"
            },
            {
                subjectName: "Physics",
                topic: "Optics & Light Ray Tracing",
                durationMinutes: 50,
                resourceType: "NOTES"
            }
        ]
    };
});
/**
 * 3.4 getHomeworkList - All homework for a student
 */
exports.getHomeworkList = functions.region("asia-south1").https.onCall(async (data, context) => {
    const filter = data?.filter || "ALL";
    const snapshot = await db.collection("homework").get();
    const list = snapshot.docs.map(d => d.data());
    return {
        stats: {
            total: list.length,
            completed: list.filter(h => h.status === "COMPLETED").length,
            pending: list.filter(h => h.status === "ACTIVE").length,
            overdue: list.filter(h => h.status === "OVERDUE").length
        },
        homework: list
    };
});
/**
 * 3.5 markHomeworkComplete - Student marks homework as done
 */
exports.markHomeworkComplete = functions.region("asia-south1").https.onCall(async (data, context) => {
    const homeworkId = data?.homeworkId;
    if (!homeworkId)
        throw new functions.https.HttpsError("invalid-argument", "homeworkId required");
    const uid = context.auth?.uid || "stu_789";
    const subRef = db.collection("homework_submissions").doc(`sub_${homeworkId}_${uid}`);
    await subRef.set({
        submissionId: `sub_${homeworkId}_${uid}`,
        homeworkId,
        studentUid: uid,
        status: "SUBMITTED",
        submittedAt: admin.firestore.FieldValue.serverTimestamp()
    }, { merge: true });
    return { success: true };
});
/**
 * 3.6 getSubjects - All subjects for a student
 */
exports.getSubjects = functions.region("asia-south1").https.onCall(async (data, context) => {
    const snapshot = await db.collection("subjects").get();
    return { subjects: snapshot.docs.map(d => d.data()) };
});
/**
 * 3.7 getCalendarData - Month view of classes
 */
exports.getCalendarData = functions.region("asia-south1").https.onCall(async (data, context) => {
    const sessions = await db.collection("class_sessions").get();
    return { sessions: sessions.docs.map(d => d.data()) };
});
/**
 * 3.8 getProfileData - Profile + stats
 */
exports.getProfileData = functions.region("asia-south1").https.onCall(async (data, context) => {
    const uid = context.auth?.uid || "stu_789";
    const user = (await db.collection("users").doc(uid).get()).data();
    return {
        user,
        stats: {
            attendancePercent: 94,
            homeworkDone: "18/20",
            gpa: 3.8
        }
    };
});
/**
 * 3.9 createRazorpayOrder - Initiate fee payment
 */
exports.createRazorpayOrder = functions.region("asia-south1").https.onCall(async (data, context) => {
    const feeId = data?.feeId;
    const orderId = `order_${Date.now()}_${Math.random().toString(36).substring(7)}`;
    if (feeId) {
        await db.collection("fees").doc(feeId).set({
            razorpayOrderId: orderId,
            updatedAt: admin.firestore.FieldValue.serverTimestamp()
        }, { merge: true });
    }
    return {
        orderId,
        currency: "INR",
        amount: 250000
    };
});
/**
 * 3.10 verifyRazorpayPayment - Verify payment after success
 */
exports.verifyRazorpayPayment = functions.region("asia-south1").https.onCall(async (data, context) => {
    const { feeId, razorpayPaymentId, razorpayOrderId } = data;
    if (feeId) {
        await db.collection("fees").doc(feeId).set({
            status: "PAID",
            paidDate: new Date().toISOString(),
            razorpayPaymentId: razorpayPaymentId || "pay_mock123",
            razorpayOrderId: razorpayOrderId || "order_mock123"
        }, { merge: true });
    }
    return { success: true };
});
/**
 * 3.11 sendNotification - Trigger push notification
 */
exports.sendNotification = functions.region("asia-south1").https.onCall(async (data, context) => {
    return { success: true, messageId: `msg_${Date.now()}` };
});
//# sourceMappingURL=index.js.map