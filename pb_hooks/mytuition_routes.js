/// <reference path="../pb_data/types.d.ts" />

// ==========================================
// 1. REGISTER STUDENT (Teacher Gatekeeper)
// POST /api/mytuition/register-student
// ==========================================
routerAdd("POST", "/api/mytuition/register-student", (c) => {
    const authRecord = c.get("authRecord")
    if (!authRecord || (authRecord.get("role") !== "TEACHER" && authRecord.get("role") !== "ADMIN")) {
        return c.json(403, { error: "Only teachers and admins can register students" })
    }
    
    const body = $apis.requestInfo(c).data || {}
    const name = (body.name || "").trim()
    const phone = (body.phone || "").trim()
    const email = (body.email || "").trim()
    const batchId = (body.batchId || "").trim()
    const monthlyFee = Number(body.monthlyFee || 0)
    const joinDate = body.joinDate || new Date().toISOString().split("T")[0]
    const customPassword = body.customPassword ? body.customPassword.trim() : null
    const rollNumber = body.rollNumber ? body.rollNumber.trim() : ""
    const notifyParent = body.notifyParent !== false
    
    if (!name || name.length < 2) {
        return c.json(400, { error: "Student name is required (at least 2 characters)" })
    }
    if (!batchId) {
        return c.json(400, { error: "Batch selection is required" })
    }
    
    // Retrieve institute
    let instituteRecord = null
    try {
        const batch = $app.findRecordById("batches", batchId)
        const instId = batch.get("institute") || authRecord.get("institute")
        if (instId) {
            instituteRecord = $app.findRecordById("institutes", instId)
        }
    } catch (_) {}
    
    const shortName = instituteRecord?.get("shortName") || "chanakya"
    
    // Auto-generate username with collision detection
    const baseName = name.toLowerCase().replace(/[^a-z0-9]/g, "").substring(0, 20)
    let candidateUsername = `${baseName}@${shortName}`
    let suffix = 2
    
    while (true) {
        try {
            const existing = $app.findAuthRecordByUsername("users", candidateUsername)
            if (existing) {
                candidateUsername = `${baseName}${suffix}@${shortName}`
                suffix++
            } else {
                break
            }
        } catch (_) {
            break
        }
    }
    
    // Generate simple memorable password
    const firstName = name.split(" ")[0].toLowerCase().replace(/[^a-z0-9]/g, "").substring(0, 8)
    const password = customPassword || `${firstName}@123`
    
    // Create student user record in users auth collection
    const usersCol = $app.findCollectionByNameOrId("users")
    const student = new Record(usersCol)
    student.setUsername(candidateUsername)
    student.setPassword(password)
    student.set("name", name)
    student.set("role", "STUDENT")
    student.set("phone", phone)
    student.set("email", email || `${candidateUsername.replace("@", ".")}@mytuition.app`)
    student.set("institute", instituteRecord?.id || authRecord.get("institute"))
    student.set("batches", [batchId])
    student.set("primaryBatch", batchId)
    student.set("registeredBy", authRecord.getId())
    student.set("initialPassword", password) // Teacher vault entry
    student.set("rollNumber", rollNumber)
    student.set("isActive", true)
    
    try {
        $app.save(student)
    } catch (err) {
        return c.json(500, { error: "Failed to create student record: " + err.message })
    }
    
    // Create initial monthly fee record
    try {
        const feesCol = $app.findCollectionByNameOrId("fees")
        const fee = new Record(feesCol)
        fee.set("student", student.getId())
        fee.set("batch", batchId)
        fee.set("amount", monthlyFee > 0 ? monthlyFee : 2500)
        fee.set("period", "MONTHLY")
        fee.set("month", new Date().toLocaleString("en-US", { month: "long" }).toUpperCase())
        fee.set("dueDate", getNextMonthTenth())
        fee.set("status", "PENDING")
        $app.save(fee)
    } catch (_) {}
    
    // Update batch's enrolled student list
    try {
        const batch = $app.findRecordById("batches", batchId)
        const currentStudents = batch.get("students") || []
        if (!currentStudents.includes(student.getId())) {
            currentStudents.push(student.getId())
            batch.set("students", currentStudents)
            $app.save(batch)
        }
    } catch (_) {}
    
    let batchName = "Maths Batch"
    try {
        batchName = $app.findRecordById("batches", batchId).get("name")
    } catch (_) {}

    // Send OneSignal push notification if parent notification requested
    if (notifyParent) {
        sendOneSignalNotification(
            [student.getId()],
            `Welcome to ${instituteRecord?.get("name") || "MyTuition"}!`,
            `Your student account is created. Username: ${candidateUsername}, Password: ${password}`,
            { type: "STUDENT_CREDENTIALS", username: candidateUsername, password: password }
        )
    }
    
    return c.json(200, {
        studentUid: student.getId(),
        username: candidateUsername,
        password: password,
        batchName: batchName,
        name: name
    })
})

// ==========================================
// 2. HOME DATA (Consolidated for Student & Teacher)
// GET /api/mytuition/home-data?date=YYYY-MM-DD
// ==========================================
routerAdd("GET", "/api/mytuition/home-data", (c) => {
    const authRecord = c.get("authRecord")
    if (!authRecord) return c.json(401, { error: "Unauthorized" })
    
    const date = c.queryParam("date") || new Date().toISOString().split("T")[0]
    const role = (authRecord.get("role") || "STUDENT").toUpperCase()
    
    if (role === "STUDENT") {
        return c.json(200, buildStudentHomeData(authRecord, date))
    } else {
        return c.json(200, buildTeacherHomeData(authRecord, date))
    }
})

function buildStudentHomeData(student, date) {
    const studentId = student.getId()
    const batchIds = student.get("batches") || []
    const currentMonth = date.substring(0, 7) // "YYYY-MM"

    // 1. Sessions for today
    let studentSessions = []
    try {
        if (batchIds.length > 0) {
            const placeholders = batchIds.map(() => "?").join(",")
            studentSessions = $app.db().select(
                "class_sessions.*",
                "subjects.name as subjectName",
                "subjects.iconColor as subjectColor",
                "subjects.iconName as subjectIcon",
                "rooms.name as roomName",
                "rooms.floor as roomFloor",
                "users.name as teacherName"
            )
            .from("class_sessions")
            .leftJoin("subjects", $app.db().raw("subjects.id = class_sessions.subject"))
            .leftJoin("rooms", $app.db().raw("rooms.id = class_sessions.room"))
            .leftJoin("users", $app.db().raw("users.id = class_sessions.teacher"))
            .where($app.db().raw(`class_sessions.date = ? AND class_sessions.batch IN (${placeholders})`, [date, ...batchIds]))
            .orderBy("class_sessions.startTime ASC")
            .all()
        }
    } catch (_) {}

    // 2. Next class: first check today's upcoming sessions, else query next future session
    let nextClass = null
    const upcomingToday = studentSessions.find(s => s.status === "UPCOMING" || s.status === "SCHEDULED" || s.status === "IN_PROGRESS")
    
    let targetNextSession = upcomingToday || null
    let isTodaySession = !!upcomingToday

    if (!targetNextSession && batchIds.length > 0) {
        try {
            const placeholders = batchIds.map(() => "?").join(",")
            const futureRow = $app.db().select(
                "class_sessions.*",
                "subjects.name as subjectName",
                "subjects.iconColor as subjectColor",
                "subjects.iconName as subjectIcon",
                "rooms.name as roomName",
                "rooms.floor as roomFloor",
                "users.name as teacherName"
            )
            .from("class_sessions")
            .leftJoin("subjects", $app.db().raw("subjects.id = class_sessions.subject"))
            .leftJoin("rooms", $app.db().raw("rooms.id = class_sessions.room"))
            .leftJoin("users", $app.db().raw("users.id = class_sessions.teacher"))
            .where($app.db().raw(`class_sessions.date > ? AND class_sessions.batch IN (${placeholders})`, [date, ...batchIds]))
            .orderBy("class_sessions.date ASC", "class_sessions.startTime ASC")
            .limit(1)
            .one()

            if (futureRow) {
                targetNextSession = futureRow
                isTodaySession = false
            }
        } catch (_) {}
    }

    if (targetNextSession) {
        let countdownText = ""
        let timeText = ""
        const sessionDate = targetNextSession.date || date
        const timePart = targetNextSession.startTime || "00:00"
        
        if (isTodaySession) {
            timeText = `Today • ${targetNextSession.startTime || ''} - ${targetNextSession.endTime || ''}`
            try {
                const sessionStartMs = new Date(`${sessionDate}T${timePart.length === 5 ? timePart + ':00' : timePart}`).getTime()
                const diffMin = Math.round((sessionStartMs - Date.now()) / (1000 * 60))
                if (diffMin > 60) {
                    const hours = Math.floor(diffMin / 60)
                    countdownText = `Starts in ${hours} hr${hours > 1 ? 's' : ''}`
                } else if (diffMin > 0) {
                    countdownText = `Starts in ${diffMin} min`
                } else if (diffMin >= -90) {
                    countdownText = "In progress"
                } else {
                    countdownText = "Ended"
                }
            } catch (_) {
                countdownText = ""
            }
        } else {
            // Future day
            const targetD = new Date(sessionDate + "T12:00:00Z")
            const todayD = new Date(date + "T12:00:00Z")
            const dayDiff = Math.round((targetD.getTime() - todayD.getTime()) / (1000 * 3600 * 24))
            const daysShort = ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"]
            const dayPrefix = dayDiff === 1 ? "Tomorrow" : `${daysShort[targetD.getUTCDay()]} ${targetD.getUTCDate()}`
            timeText = `${dayPrefix} • ${targetNextSession.startTime || ''}`
            countdownText = `In ${dayDiff} day${dayDiff > 1 ? 's' : ''}`
        }

        nextClass = {
            id: targetNextSession.id,
            subjectName: targetNextSession.subjectName || "",
            teacherName: targetNextSession.teacherName || "",
            timeText: timeText,
            countdownText: countdownText,
            room: targetNextSession.roomName || "",
            floor: targetNextSession.roomFloor || "",
            directionsNote: targetNextSession.roomFloor ? `Floor: ${targetNextSession.roomFloor}` : "",
            status: targetNextSession.status || "SCHEDULED"
        }
    }

    // 3. Pending homework count
    let pendingHwCount = 0
    try {
        const hwRow = $app.db().select("COUNT(*) as cnt").from("homework_submissions")
            .where($app.db().raw("student = ? AND status = 'PENDING'", [studentId])).one()
        pendingHwCount = hwRow ? Number(hwRow.cnt || 0) : 0
    } catch (_) {}

    // 4. Real fee status (using dueDate DESC)
    let feeStatus = "PAID"
    let feeAmount = 0.0
    try {
        const latestFee = $app.db().select("*").from("fees")
            .where($app.db().raw("student = ?", [studentId]))
            .orderBy("dueDate DESC").limit(1).one()
        if (latestFee) {
            feeStatus = latestFee.status || "PENDING"
            feeAmount = Number(latestFee.amount || 0)
        }
    } catch (_) {}

    // 5. Attendance calculations for current month (EXCUSED counted as attended)
    let attendedCount = 0
    let totalClassesCount = 0
    try {
        const attRows = $app.db().select("status").from("attendance")
            .where($app.db().raw("student = ? AND date LIKE ?", [studentId, `${currentMonth}%`])).all()
        totalClassesCount = attRows.length
        attendedCount = attRows.filter(r => r.status === "PRESENT" || r.status === "LATE" || r.status === "EXCUSED").length
    } catch (_) {}

    const attendancePercent = totalClassesCount > 0 ? Math.round((attendedCount / totalClassesCount) * 100) : 0

    // 6. Institute name & announcements
    let instituteName = "MyTuition Academy"
    let announcements = []
    try {
        const instId = student.get("institute")
        if (instId) {
            const inst = $app.findRecordById("institutes", instId)
            instituteName = inst.get("name") || instituteName
        }
        announcements = $app.db().select("id", "title", "message", "type", "date")
            .from("announcements")
            .where(instId ? $app.db().raw("institute = ?", [instId]) : $app.db().raw("1=1"))
            .orderBy("date DESC").limit(5).all()
    } catch (_) {}

    return {
        studentName: student.get("name") || "",
        className: student.get("classGrade") || "",
        tuitionName: instituteName,
        avatarUrl: student.get("avatarFile") ? `/api/files/users/${student.getId()}/${student.get("avatarFile")}` : null,
        nextClass: nextClass,
        weekDates: buildWeekDates(date, "STUDENT", studentId),
        selectedDate: date,
        timeline: studentSessions.map(s => ({
            sessionId: s.id,
            time: s.startTime ? s.startTime.substring(0, 5) : "",
            startTimeDisplay: s.startTime || "",
            endTimeDisplay: s.endTime || "",
            subjectName: s.subjectName || "",
            topic: s.topic || "",
            subjectIconColorHex: s.subjectColor || "#34C759",
            subjectIconName: s.subjectIcon || "calculate",
            teacherName: s.teacherName || "",
            roomName: s.roomName || "",
            floorName: s.roomFloor || "",
            status: s.status || "SCHEDULED"
        })),
        summary: {
            attendancePercent: attendancePercent,
            classesAttended: attendedCount,
            totalClasses: totalClassesCount,
            feeStatus: feeStatus,
            feeAmount: feeAmount,
            pendingHomeworkCount: pendingHwCount,
            outstandingFeeText: feeStatus === "PAID" ? "All fees cleared" : `₹${feeAmount} pending`
        },
        announcements: announcements
    }
}

function buildTeacherHomeData(teacher, date) {
    const teacherId = teacher.getId()
    const currentMonth = date.substring(0, 7) // "YYYY-MM"

    // 1. Active batches
    let activeBatchesCount = 0
    let teacherBatchIds = []
    try {
        const batchRows = $app.db().select("id").from("batches")
            .where($app.db().raw("teacher = ?", [teacherId])).all()
        teacherBatchIds = batchRows.map(b => b.id)
        activeBatchesCount = teacherBatchIds.length
    } catch (_) {}

    // 2. Total distinct students enrolled in teacher's batches
    let totalStudents = 0
    try {
        if (teacherBatchIds.length > 0) {
            const placeholders = teacherBatchIds.map(() => "?").join(",")
            const row = $app.db().select("COUNT(DISTINCT users.id) as cnt")
                .from("users")
                .innerJoin("batches", $app.db().raw(`batches.id IN (${placeholders}) AND batches.students LIKE ('%' || users.id || '%')`, teacherBatchIds))
                .where($app.db().raw("users.role = 'STUDENT'"))
                .one()
            totalStudents = row ? Number(row.cnt || 0) : 0
        }
    } catch (_) {}

    // 3. Today's classes count and real sessions timeline
    let todaySessions = []
    try {
        todaySessions = $app.db().select(
            "class_sessions.*",
            "subjects.name as subjectName",
            "subjects.iconColor as subjectColor",
            "subjects.iconName as subjectIcon",
            "rooms.name as roomName",
            "rooms.floor as roomFloor",
            "batches.students as batchStudents"
        )
        .from("class_sessions")
        .leftJoin("subjects", $app.db().raw("subjects.id = class_sessions.subject"))
        .leftJoin("rooms", $app.db().raw("rooms.id = class_sessions.room"))
        .leftJoin("batches", $app.db().raw("batches.id = class_sessions.batch"))
        .where($app.db().raw("class_sessions.teacher = ? AND class_sessions.date = ?", [teacherId, date]))
        .orderBy("class_sessions.startTime ASC")
        .all()
    } catch (_) {}

    // Check attendance records for today's sessions
    let pendingAttendanceCount = 0
    const timelineWithDetails = todaySessions.map(s => {
        let studentCount = 0
        try {
            if (s.batchStudents) {
                const parsed = JSON.parse(s.batchStudents)
                studentCount = Array.isArray(parsed) ? parsed.length : 0
            }
        } catch (_) {}

        let attendanceTaken = false
        try {
            const attRow = $app.db().select("COUNT(*) as cnt")
                .from("attendance")
                .where($app.db().raw("session = ?", [s.id]))
                .one()
            attendanceTaken = (attRow ? Number(attRow.cnt || 0) : 0) > 0
        } catch (_) {}

        if (!attendanceTaken && s.status !== "CANCELLED") {
            pendingAttendanceCount++
        }

        return {
            sessionId: s.id,
            batchId: s.batch || "",
            batchName: s.subjectName || "",
            time: s.startTime ? s.startTime.substring(0, 5) : "",
            startTimeDisplay: s.startTime || "",
            endTimeDisplay: s.endTime || "",
            subjectName: s.subjectName || "",
            topic: s.topic || "",
            subjectIconColorHex: s.subjectColor || "#34C759",
            subjectIconName: s.subjectIcon || "calculate",
            roomName: s.roomName || "",
            floorName: s.roomFloor || "",
            studentCount: studentCount,
            status: s.status || "SCHEDULED",
            attendanceTaken: attendanceTaken
        }
    })

    // 4. Pending homework submissions review count
    let pendingReviewCount = 0
    try {
        const pRow = $app.db().select("COUNT(homework_submissions.id) as cnt")
            .from("homework_submissions")
            .innerJoin("homework", $app.db().raw("homework.id = homework_submissions.homework"))
            .where($app.db().raw("homework.teacher = ? AND homework_submissions.status = 'PENDING'", [teacherId]))
            .one()
        pendingReviewCount = pRow ? Number(pRow.cnt || 0) : 0
    } catch (_) {}

    // 5. Total fee collected this month
    let totalCollectedMonth = 0.0
    try {
        if (teacherBatchIds.length > 0) {
            const placeholders = teacherBatchIds.map(() => "?").join(",")
            const feeRow = $app.db().select("COALESCE(SUM(fees.amount), 0) as total")
                .from("fees")
                .where($app.db().raw(`fees.batch IN (${placeholders}) AND fees.status = 'PAID' AND fees.paidDate LIKE ?`, [...teacherBatchIds, `${currentMonth}%`]))
                .one()
            totalCollectedMonth = feeRow ? Number(feeRow.total || 0) : 0.0
        }
    } catch (_) {}

    // 6. Real announcements
    let announcements = []
    try {
        const instId = teacher.get("institute")
        announcements = $app.db().select("id", "title", "message", "type", "date")
            .from("announcements")
            .where(instId ? $app.db().raw("institute = ?", [instId]) : $app.db().raw("1=1"))
            .orderBy("date DESC").limit(5).all()
    } catch (_) {}

    let instituteName = "MyTuition Academy"
    try {
        const instId = teacher.get("institute")
        if (instId) {
            const inst = $app.findRecordById("institutes", instId)
            instituteName = inst.get("name") || instituteName
        }
    } catch (_) {}

    return {
        teacherName: teacher.get("name") || "Teacher",
        teacherId: teacher.get("teacherId") || teacher.getId(),
        instituteName: instituteName,
        totalStudents: totalStudents,
        activeBatches: activeBatchesCount,
        todayClassesCount: todaySessions.length,
        pendingReviewCount: pendingReviewCount,
        pendingAttendanceCount: pendingAttendanceCount,
        totalCollectedMonth: totalCollectedMonth,
        weekDates: buildWeekDates(date, "TEACHER", teacherId),
        timeline: timelineWithDetails,
        announcements: announcements
    }
}

function buildWeekDates(dateStr, role, recordId) {
    const daysAbbr = ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"]
    let baseDate = new Date(dateStr + "T12:00:00Z")
    if (isNaN(baseDate.getTime())) {
        baseDate = new Date()
    }
    
    // Determine Monday of current week
    const currentDayOfWeek = baseDate.getUTCDay() // 0 = Sun, 1 = Mon, ...
    const diffToMonday = currentDayOfWeek === 0 ? -6 : 1 - currentDayOfWeek
    const mondayDate = new Date(baseDate)
    mondayDate.setUTCDate(baseDate.getUTCDate() + diffToMonday)
    
    const weekDates = []
    for (let i = 0; i < 7; i++) {
        const d = new Date(mondayDate)
        d.setUTCDate(mondayDate.getUTCDate() + i)
        const dStr = d.toISOString().split("T")[0]
        const dayAbbr = daysAbbr[d.getUTCDay()]
        const dayNumber = String(d.getUTCDate())
        
        let hasClasses = false
        try {
            if (role === "TEACHER") {
                const countRow = $app.db().select("COUNT(*) as cnt")
                    .from("class_sessions")
                    .where($app.db().raw("teacher = ? AND date = ?", [recordId, dStr]))
                    .one()
                hasClasses = (countRow ? Number(countRow.cnt || 0) : 0) > 0
            } else {
                const countRow = $app.db().select("COUNT(class_sessions.id) as cnt")
                    .from("class_sessions")
                    .innerJoin("batches", $app.db().raw("batches.id = class_sessions.batch"))
                    .where($app.db().raw("class_sessions.date = ? AND batches.students LIKE ?", [dStr, `%${recordId}%`]))
                    .one()
                hasClasses = (countRow ? Number(countRow.cnt || 0) : 0) > 0
            }
        } catch (_) {}

        weekDates.push({
            date: dStr,
            dayAbbr: dayAbbr,
            dayNumber: dayNumber,
            hasClasses: hasClasses
        })
    }
    return weekDates
}

function getNextMonthTenth() {
    const d = new Date()
    d.setMonth(d.getMonth() + 1)
    d.setDate(10)
    return d.toISOString().split("T")[0]
}

// ==========================================
// 3. MARK ATTENDANCE (Batched)
// POST /api/mytuition/mark-attendance
// ==========================================
routerAdd("POST", "/api/mytuition/mark-attendance", (c) => {
    const authRecord = c.get("authRecord")
    if (!authRecord || (authRecord.get("role") !== "TEACHER" && authRecord.get("role") !== "ADMIN")) {
        return c.json(403, { error: "Unauthorized" })
    }
    
    const body = $apis.requestInfo(c).data || {}
    const sessionId = body.sessionId
    const attendanceMap = body.attendanceMap || {} // { [studentId]: "PRESENT"|"ABSENT"|"LATE" }
    
    if (!sessionId) return c.json(400, { error: "sessionId is required" })
    
    const attCol = $app.findCollectionByNameOrId("attendance")
    let markedCount = 0
    
    for (const [studentId, status] of Object.entries(attendanceMap)) {
        try {
            let record = null
            try {
                record = $app.findFirstRecordByFilter("attendance", `session = '${sessionId}' && student = '${studentId}'`)
            } catch (_) {}
            
            if (!record) {
                record = new Record(attCol)
                record.set("session", sessionId)
                record.set("student", studentId)
            }
            record.set("status", status)
            record.set("markedBy", authRecord.getId())
            record.set("date", new Date().toISOString().split("T")[0])
            $app.save(record)
            markedCount++
        } catch (_) {}
    }

    // Send OneSignal push to absent students' parents
    const absentStudentIds = Object.entries(attendanceMap)
        .filter(([_, status]) => status === "ABSENT")
        .map(([studentId, _]) => studentId)
    
    if (absentStudentIds.length > 0) {
        sendOneSignalNotification(
            absentStudentIds,
            "Attendance Alert",
            "Your child was marked ABSENT today. Please contact the tuition teacher if you have any questions.",
            { type: "ATTENDANCE_ABSENT", sessionId: sessionId }
        )
    }
    
    return c.json(200, { success: true, markedCount: markedCount })
})

// ==========================================
// 4. CREATE HOMEWORK WITH SUBMISSIONS
// POST /api/mytuition/create-homework
// ==========================================
routerAdd("POST", "/api/mytuition/create-homework", (c) => {
    const authRecord = c.get("authRecord")
    if (!authRecord || (authRecord.get("role") !== "TEACHER" && authRecord.get("role") !== "ADMIN")) {
        return c.json(403, { error: "Unauthorized" })
    }
    
    const body = $apis.requestInfo(c).data || {}
    const batchId = body.batchId
    const title = body.title
    const description = body.description || ""
    const dueDate = body.dueDate || new Date().toISOString().split("T")[0]
    const subjectId = body.subjectId || null
    const notifyStudents = body.notifyStudents !== false
    
    if (!batchId || !title) return c.json(400, { error: "batchId and title are required" })
    
    const hwCol = $app.findCollectionByNameOrId("homework")
    const homework = new Record(hwCol)
    homework.set("batch", batchId)
    homework.set("teacher", authRecord.getId())
    if (subjectId) homework.set("subject", subjectId)
    homework.set("title", title)
    homework.set("description", description)
    homework.set("dueDate", dueDate)
    homework.set("status", "ACTIVE")
    $app.save(homework)
    
    // Auto-create submission records for all students in the batch
    let studentIds = []
    try {
        const batch = $app.findRecordById("batches", batchId)
        studentIds = batch.get("students") || []
    } catch (_) {}
    
    const subCol = $app.findCollectionByNameOrId("homework_submissions")
    for (const sid of studentIds) {
        try {
            const sub = new Record(subCol)
            sub.set("homework", homework.getId())
            sub.set("student", sid)
            sub.set("status", "PENDING")
            $app.save(sub)
        } catch (_) {}
    }

    // Broadcast push notification to students in batch
    if (notifyStudents && studentIds.length > 0) {
        sendOneSignalNotification(
            studentIds,
            "New Homework Assigned",
            `${title} • Due: ${dueDate}`,
            { type: "HOMEWORK_ASSIGNED", homeworkId: homework.getId() }
        )
    }
    
    return c.json(200, { homeworkId: homework.getId(), studentCount: studentIds.length })
})

// ==========================================
// 5. STUDENT CREDENTIALS VAULT (Teacher Only)
// GET /api/mytuition/student-credentials/:studentId
// ==========================================
routerAdd("GET", "/api/mytuition/student-credentials/:studentId", (c) => {
    const authRecord = c.get("authRecord")
    if (!authRecord || (authRecord.get("role") !== "TEACHER" && authRecord.get("role") !== "ADMIN")) {
        return c.json(403, { error: "Unauthorized" })
    }
    
    const studentId = c.pathParam("studentId")
    const student = $app.findRecordById("users", studentId)
    
    return c.json(200, {
        studentId: student.getId(),
        username: student.get("username"),
        password: student.get("initialPassword") || "ayush@123",
        name: student.get("name")
    })
})

// ==========================================
// 6. RESET PASSWORD (Teacher Only)
// POST /api/mytuition/reset-password/:studentId
// ==========================================
routerAdd("POST", "/api/mytuition/reset-password/:studentId", (c) => {
    const authRecord = c.get("authRecord")
    if (!authRecord || (authRecord.get("role") !== "TEACHER" && authRecord.get("role") !== "ADMIN")) {
        return c.json(403, { error: "Unauthorized" })
    }
    
    const studentId = c.pathParam("studentId")
    const body = $apis.requestInfo(c).data || {}
    const student = $app.findRecordById("users", studentId)
    
    const firstName = (student.get("name") || "student").split(" ")[0].toLowerCase().substring(0, 8)
    const newPass = body.newPassword || `${firstName}@123`
    
    student.setPassword(newPass)
    student.set("initialPassword", newPass)
    $app.save(student)
    
    return c.json(200, {
        studentId: studentId,
        username: student.get("username"),
        password: newPass
    })
})

// ==========================================
// 7. OFFLINE FEE PAYMENT MANAGEMENT (Teacher / Admin Only)
// POST /api/mytuition/mark-fee-paid
// POST /api/mytuition/mark-fee-pending
// ==========================================
routerAdd("POST", "/api/mytuition/mark-fee-paid", (c) => {
    const authRecord = c.get("authRecord")
    if (!authRecord || (authRecord.get("role") !== "TEACHER" && authRecord.get("role") !== "ADMIN")) {
        return c.json(403, { error: "Only teachers and admins can mark fees as paid" })
    }

    const body = $apis.requestInfo(c).data || {}
    const feeId = body.feeId
    const paymentMethod = (body.paymentMethod || "CASH").toUpperCase() // CASH, DIRECT_UPI, OTHER
    const paymentNote = body.paymentNote || ""

    if (!feeId) return c.json(400, { error: "feeId is required" })

    let fee = null
    try {
        fee = $app.findRecordById("fees", feeId)
    } catch (_) {
        return c.json(404, { error: "Fee record not found" })
    }

    // Ownership check: if teacher, verify batch belongs to teacher
    if (authRecord.get("role") === "TEACHER") {
        const batchId = fee.get("batch")
        try {
            const batch = $app.findRecordById("batches", batchId)
            if (batch.get("teacher") !== authRecord.getId()) {
                return c.json(403, { error: "You are not authorized to manage fees for this batch" })
            }
        } catch (_) {}
    }

    const todayStr = new Date().toISOString().split("T")[0]
    fee.set("status", "PAID")
    fee.set("paidDate", todayStr)
    fee.set("paymentMethod", paymentMethod)
    if (paymentNote) {
        fee.set("paymentNote", paymentNote)
    }
    $app.save(fee)

    // Notify student/parent via OneSignal push
    const studentId = fee.get("student")
    const feeAmount = fee.get("amount") || 0
    const feeMonth = fee.get("month") || "this month"
    if (studentId) {
        sendOneSignalNotification(
            [studentId],
            "Fee Payment Received ✓",
            `Fee received ✓ ₹${feeAmount} for ${feeMonth}. Thank you!`,
            { type: "FEE_PAID", feeId: fee.getId() }
        )
    }

    return c.json(200, {
        success: true,
        fee: {
            id: fee.getId(),
            student: fee.get("student"),
            batch: fee.get("batch"),
            amount: fee.get("amount"),
            status: "PAID",
            paidDate: todayStr,
            paymentMethod: paymentMethod,
            month: fee.get("month"),
            dueDate: fee.get("dueDate")
        }
    })
})

routerAdd("POST", "/api/mytuition/mark-fee-pending", (c) => {
    const authRecord = c.get("authRecord")
    if (!authRecord || (authRecord.get("role") !== "TEACHER" && authRecord.get("role") !== "ADMIN")) {
        return c.json(403, { error: "Only teachers and admins can modify fee status" })
    }

    const body = $apis.requestInfo(c).data || {}
    const feeId = body.feeId
    if (!feeId) return c.json(400, { error: "feeId is required" })

    let fee = null
    try {
        fee = $app.findRecordById("fees", feeId)
    } catch (_) {
        return c.json(404, { error: "Fee record not found" })
    }

    if (authRecord.get("role") === "TEACHER") {
        const batchId = fee.get("batch")
        try {
            const batch = $app.findRecordById("batches", batchId)
            if (batch.get("teacher") !== authRecord.getId()) {
                return c.json(403, { error: "You are not authorized to manage fees for this batch" })
            }
        } catch (_) {}
    }

    fee.set("status", "PENDING")
    fee.set("paidDate", "")
    fee.set("paymentMethod", "")
    $app.save(fee)

    return c.json(200, {
        success: true,
        fee: {
            id: fee.getId(),
            status: "PENDING",
            paidDate: null
        }
    })
})

// ==========================================
// 8. TEACHER EARNINGS BREAKDOWN
// GET /api/mytuition/teacher-earnings?month=MM&year=YYYY
// ==========================================
routerAdd("GET", "/api/mytuition/teacher-earnings", (c) => {
    const authRecord = c.get("authRecord")
    if (!authRecord || (authRecord.get("role") !== "TEACHER" && authRecord.get("role") !== "ADMIN")) {
        return c.json(403, { error: "Unauthorized" })
    }

    const teacherId = authRecord.getId()
    const now = new Date()
    const year = c.queryParam("year") || String(now.getUTCFullYear())
    const month = c.queryParam("month") || String(now.getUTCMonth() + 1).padStart(2, "0")
    const periodPrefix = `${year}-${month}`

    // 1. Get all batches taught by this teacher
    let batches = []
    try {
        batches = $app.db().select("id", "name", "defaultFee").from("batches")
            .where($app.db().raw("teacher = ?", [teacherId])).all()
    } catch (_) {}

    const batchIds = batches.map(b => b.id)
    if (batchIds.length === 0) {
        return c.json(200, {
            period: periodPrefix,
            totalCollected: 0.0,
            totalPending: 0.0,
            paidCount: 0,
            pendingCount: 0,
            batches: []
        })
    }

    const placeholders = batchIds.map(() => "?").join(",")
    let feeRecords = []
    try {
        feeRecords = $app.db().select("batch", "amount", "status", "dueDate", "paidDate")
            .from("fees")
            .where($app.db().raw(`batch IN (${placeholders}) AND (dueDate LIKE ? OR paidDate LIKE ?)`, [...batchIds, `${periodPrefix}%`, `${periodPrefix}%`]))
            .all()
    } catch (_) {}

    let totalCollected = 0.0
    let totalPending = 0.0
    let paidCount = 0
    let pendingCount = 0

    const batchBreakdown = batches.map(b => {
        const bFees = feeRecords.filter(f => f.batch === b.id)
        let bCollected = 0.0
        let bPending = 0.0
        let bPaidCount = 0
        let bPendingCount = 0

        for (const f of bFees) {
            const amt = Number(f.amount || 0)
            if (f.status === "PAID") {
                bCollected += amt
                bPaidCount++
            } else {
                bPending += amt
                bPendingCount++
            }
        }

        totalCollected += bCollected
        totalPending += bPending
        paidCount += bPaidCount
        pendingCount += bPendingCount

        return {
            batchId: b.id,
            batchName: b.name,
            defaultFee: Number(b.defaultFee || 0),
            collectedAmount: bCollected,
            pendingAmount: bPending,
            paidCount: bPaidCount,
            pendingCount: bPendingCount,
            totalStudents: bPaidCount + bPendingCount
        }
    })

    return c.json(200, {
        period: periodPrefix,
        totalCollected: totalCollected,
        totalPending: totalPending,
        paidCount: paidCount,
        pendingCount: pendingCount,
        batches: batchBreakdown
    })
})

// ==========================================
// 9. CHECK INSTITUTE SHORTNAME AVAILABILITY
// GET /api/mytuition/check-shortname/:name
// ==========================================
routerAdd("GET", "/api/mytuition/check-shortname/:name", (c) => {
    const rawName = (c.pathParam("name") || "").toLowerCase().replace(/[^a-z0-9]/g, "")
    if (!rawName) {
        return c.json(400, { error: "Shortname must contain alphanumeric characters" })
    }

    let existing = null
    try {
        existing = $app.findFirstRecordByFilter("institutes", `shortName = '${rawName}'`)
    } catch (_) {}

    if (!existing) {
        return c.json(200, {
            available: true,
            shortName: rawName,
            suggestions: []
        })
    }

    // Generate suggestions
    const suggestions = [
        `${rawName}classes`,
        `${rawName}academy`,
        `${rawName}edu`,
        `${rawName}1`
    ].filter(sug => {
        try {
            return !$app.findFirstRecordByFilter("institutes", `shortName = '${sug}'`)
        } catch (_) {
            return true
        }
    })

    return c.json(200, {
        available: false,
        shortName: rawName,
        suggestions: suggestions
    })
})

// ==========================================
// 10. REMIND PENDING HOMEWORK STUDENTS
// POST /api/mytuition/remind-pending-homework
// ==========================================
routerAdd("POST", "/api/mytuition/remind-pending-homework", (c) => {
    const authRecord = c.get("authRecord")
    if (!authRecord || (authRecord.get("role") !== "TEACHER" && authRecord.get("role") !== "ADMIN")) {
        return c.json(403, { error: "Unauthorized" })
    }

    const body = $apis.requestInfo(c).data || {}
    const homeworkId = body.homeworkId
    if (!homeworkId) return c.json(400, { error: "homeworkId is required" })

    let hwTitle = "Pending Homework"
    try {
        const hw = $app.findRecordById("homework", homeworkId)
        hwTitle = hw.get("title") || hwTitle
    } catch (_) {}

    let pendingStudentIds = []
    try {
        const rows = $app.db().select("student").from("homework_submissions")
            .where($app.db().raw("homework = ? AND status != 'COMPLETED'", [homeworkId])).all()
        pendingStudentIds = rows.map(r => r.student).filter(Boolean)
    } catch (_) {}

    if (pendingStudentIds.length > 0) {
        sendOneSignalNotification(
            pendingStudentIds,
            "Homework Reminder ⏰",
            `Please submit: "${hwTitle}". Your teacher is waiting for your submission!`,
            { type: "HOMEWORK_REMINDER", homeworkId: homeworkId }
        )
    }

    return c.json(200, { success: true, remindedCount: pendingStudentIds.length })
})

// ==========================================
// HELPER FUNCTIONS: SETTINGS & ONESIGNAL
// ==========================================
function getSettingValue(key, defaultValue = "") {
    try {
        const row = $app.db().select("value").from("settings")
            .where($app.db().raw("key = ?", [key])).one()
        if (row && row.value !== undefined && row.value !== null) {
            return String(row.value)
        }
    } catch (_) {}
    return defaultValue
}

function sendOneSignalNotification(userIds, title, message, data = {}) {
    if (!userIds || userIds.length === 0) return
    const appId = getSettingValue("onesignal_app_id", "")
    const apiKey = getSettingValue("onesignal_rest_api_key", "")
    if (!appId || !apiKey) return

    try {
        $http.send({
            url: "https://onesignal.com/api/v1/notifications",
            method: "POST",
            headers: {
                "Authorization": `Basic ${apiKey}`,
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                app_id: appId,
                include_aliases: {
                    external_id: userIds
                },
                target_channel: "push",
                headings: { en: title },
                contents: { en: message },
                data: data
            })
        })
    } catch (_) {}
}

