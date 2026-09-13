/// <reference path="../pb_data/types.d.ts" />

// POST /api/mytuition/seed-demo
routerAdd("POST", "/api/mytuition/seed-demo", (c) => {
    try {
        const todayStr = new Date().toISOString().split("T")[0]
        const currentMonth = todayStr.substring(0, 7) // "YYYY-MM"

        // 1. Create Institute
        let inst = null
        try {
            inst = $app.findFirstRecordByFilter("institutes", "shortName = 'chanakya'")
        } catch (_) {}
        
        if (!inst) {
            const instCol = $app.findCollectionByNameOrId("institutes")
            inst = new Record(instCol)
            inst.set("name", "Chanakya Classes")
            inst.set("shortName", "chanakya")
            inst.set("address", "12, MG Road, Metro Pillar 44, Bengaluru")
            inst.set("phone", "+91 80 4123 4567")
            inst.set("email", "support@chanakyaclasses.in")
            inst.set("academicYear", "2026-2027")
            inst.set("timezone", "Asia/Kolkata")
            $app.save(inst)
        }

        // 2. Create Rooms
        let room4b = null
        try {
            room4b = $app.findFirstRecordByFilter("rooms", "name = '4B'")
        } catch (_) {}
        if (!room4b) {
            const roomCol = $app.findCollectionByNameOrId("rooms")
            room4b = new Record(roomCol)
            room4b.set("name", "4B")
            room4b.set("floor", "2nd Floor, Arts Block")
            room4b.set("institute", inst.getId())
            room4b.set("capacity", 30)
            $app.save(room4b)
        }

        let room302 = null
        try {
            room302 = $app.findFirstRecordByFilter("rooms", "name = '302'")
        } catch (_) {}
        if (!room302) {
            const roomCol = $app.findCollectionByNameOrId("rooms")
            room302 = new Record(roomCol)
            room302.set("name", "302")
            room302.set("floor", "3rd Floor, Science Block")
            room302.set("institute", inst.getId())
            room302.set("capacity", 35)
            $app.save(room302)
        }

        // 3. Create Subjects
        let maths = null
        try {
            maths = $app.findFirstRecordByFilter("subjects", "code = 'MATH'")
        } catch (_) {}
        if (!maths) {
            const subjCol = $app.findCollectionByNameOrId("subjects")
            maths = new Record(subjCol)
            maths.set("name", "Mathematics")
            maths.set("code", "MATH")
            maths.set("iconColor", "#34C759")
            maths.set("iconName", "calculate")
            maths.set("institute", inst.getId())
            $app.save(maths)
        }

        let physics = null
        try {
            physics = $app.findFirstRecordByFilter("subjects", "code = 'PHYS'")
        } catch (_) {}
        if (!physics) {
            const subjCol = $app.findCollectionByNameOrId("subjects")
            physics = new Record(subjCol)
            physics.set("name", "Physics")
            physics.set("code", "PHYS")
            physics.set("iconColor", "#007AFF")
            physics.set("iconName", "science")
            physics.set("institute", inst.getId())
            $app.save(physics)
        }

        // 4. Create Teacher User
        let teacher = null
        try {
            teacher = $app.findAuthRecordByUsername("users", "demo_teacher@mytuition")
        } catch (_) {}
        if (!teacher) {
            const usersCol = $app.findCollectionByNameOrId("users")
            teacher = new Record(usersCol)
            teacher.setUsername("demo_teacher@mytuition")
            teacher.setPassword("teacher@123")
            teacher.set("name", "Mr. Rakesh Sharma")
            teacher.set("role", "TEACHER")
            teacher.set("phone", "+919812345678")
            teacher.set("email", "rakesh@chanakya.in")
            teacher.set("institute", inst.getId())
            teacher.set("teacherId", "teacher_001")
            teacher.set("qualification", "M.Sc. Mathematics")
            teacher.set("experienceYears", 12)
            teacher.set("isActive", true)
            $app.save(teacher)
        }

        // 5. Create Batches
        let batch = null
        try {
            batch = $app.findFirstRecordByFilter("batches", "name = 'Class 10-A • Maths Batch'")
        } catch (_) {}
        if (!batch) {
            const batchCol = $app.findCollectionByNameOrId("batches")
            batch = new Record(batchCol)
            batch.set("name", "Class 10-A • Maths Batch")
            batch.set("institute", inst.getId())
            batch.set("subject", maths.getId())
            batch.set("teacher", teacher.getId())
            batch.set("classGrade", "Class 10")
            batch.set("section", "A")
            batch.set("defaultRoom", room4b.getId())
            batch.set("scheduleDays", ["MON", "TUE", "WED", "THU", "FRI", "SAT"])
            batch.set("startTime", "16:00")
            batch.set("endTime", "17:30")
            batch.set("defaultFee", 2500)
            batch.set("students", [])
            $app.save(batch)
        }

        let batchPhysics = null
        try {
            batchPhysics = $app.findFirstRecordByFilter("batches", "name = 'Class 10-A • Physics Batch'")
        } catch (_) {}
        if (!batchPhysics) {
            const batchCol = $app.findCollectionByNameOrId("batches")
            batchPhysics = new Record(batchCol)
            batchPhysics.set("name", "Class 10-A • Physics Batch")
            batchPhysics.set("institute", inst.getId())
            batchPhysics.set("subject", physics.getId())
            batchPhysics.set("teacher", teacher.getId())
            batchPhysics.set("classGrade", "Class 10")
            batchPhysics.set("section", "A")
            batchPhysics.set("defaultRoom", room302.getId())
            batchPhysics.set("scheduleDays", ["MON", "WED", "FRI"])
            batchPhysics.set("startTime", "10:00")
            batchPhysics.set("endTime", "11:30")
            batchPhysics.set("defaultFee", 2500)
            batchPhysics.set("students", [])
            $app.save(batchPhysics)
        }

        // 6. Create Demo Student
        let student = null
        try {
            student = $app.findAuthRecordByUsername("users", "demo_student@mytuition")
        } catch (_) {}
        if (!student) {
            const usersCol = $app.findCollectionByNameOrId("users")
            student = new Record(usersCol)
            student.setUsername("demo_student@mytuition")
            student.setPassword("demo@123")
            student.set("name", "Mohit Raj")
            student.set("role", "STUDENT")
            student.set("phone", "+919876543210")
            student.set("email", "mohitraj8503@gmail.com")
            student.set("institute", inst.getId())
            student.set("classGrade", "Class 10")
            student.set("section", "A")
            student.set("rollNumber", "27")
            student.set("schoolName", "St. Xavier's High School")
            student.set("parentName", "Rajesh Kumar")
            student.set("parentPhone", "+919876500000")
            student.set("batches", [batch.getId(), batchPhysics.getId()])
            student.set("primaryBatch", batch.getId())
            student.set("initialPassword", "demo@123")
            student.set("isActive", true)
            $app.save(student)

            batch.set("students", [student.getId()])
            $app.save(batch)

            batchPhysics.set("students", [student.getId()])
            $app.save(batchPhysics)
        } else {
            // Ensure student has batches properly set
            const currentBatches = student.get("batches") || []
            if (!currentBatches.includes(batch.getId()) || !currentBatches.includes(batchPhysics.getId())) {
                student.set("batches", [batch.getId(), batchPhysics.getId()])
                student.set("primaryBatch", batch.getId())
                $app.save(student)
            }
        }

        // 7. Seed Class Sessions (Today, Tomorrow, and subsequent days)
        const sessionsCol = $app.findCollectionByNameOrId("class_sessions")
        let todaySession = null
        try {
            todaySession = $app.findFirstRecordByFilter("class_sessions", `batch = '${batch.getId()}' && date = '${todayStr}'`)
        } catch (_) {}
        if (!todaySession) {
            todaySession = new Record(sessionsCol)
            todaySession.set("batch", batch.getId())
            todaySession.set("subject", maths.getId())
            todaySession.set("teacher", teacher.getId())
            todaySession.set("room", room4b.getId())
            todaySession.set("date", todayStr)
            todaySession.set("startTime", "16:00")
            todaySession.set("endTime", "17:30")
            todaySession.set("topic", "Quadratic Equations — Word Problems")
            todaySession.set("status", "SCHEDULED")
            $app.save(todaySession)
        }

        // Tomorrow's session
        const tomorrow = new Date()
        tomorrow.setDate(tomorrow.getDate() + 1)
        const tomorrowStr = tomorrow.toISOString().split("T")[0]
        let tomorrowSession = null
        try {
            tomorrowSession = $app.findFirstRecordByFilter("class_sessions", `batch = '${batchPhysics.getId()}' && date = '${tomorrowStr}'`)
        } catch (_) {}
        if (!tomorrowSession) {
            tomorrowSession = new Record(sessionsCol)
            tomorrowSession.set("batch", batchPhysics.getId())
            tomorrowSession.set("subject", physics.getId())
            tomorrowSession.set("teacher", teacher.getId())
            tomorrowSession.set("room", room302.getId())
            tomorrowSession.set("date", tomorrowStr)
            tomorrowSession.set("startTime", "10:00")
            tomorrowSession.set("endTime", "11:30")
            tomorrowSession.set("topic", "Electromagnetic Induction & Faraday's Laws")
            tomorrowSession.set("status", "SCHEDULED")
            $app.save(tomorrowSession)
        }

        // 8. Seed Attendance (student attended past classes)
        const attCol = $app.findCollectionByNameOrId("attendance")
        let existingAtt = null
        try {
            existingAtt = $app.findFirstRecordByFilter("attendance", `student = '${student.getId()}'`)
        } catch (_) {}
        if (!existingAtt) {
            const pastDates = [-1, -2, -3, -4]
            for (const offset of pastDates) {
                const pastD = new Date()
                pastD.setDate(pastD.getDate() + offset)
                const pDateStr = pastD.toISOString().split("T")[0]
                const attRec = new Record(attCol)
                attRec.set("student", student.getId())
                attRec.set("batch", batch.getId())
                attRec.set("session", todaySession.getId())
                attRec.set("status", offset === -3 ? "ABSENT" : "PRESENT")
                attRec.set("markedBy", teacher.getId())
                attRec.set("date", pDateStr)
                try { $app.save(attRec) } catch (_) {}
            }
        }

        // 9. Seed Homework & Submissions (1 pending, 1 completed)
        const hwCol = $app.findCollectionByNameOrId("homework")
        const subCol = $app.findCollectionByNameOrId("homework_submissions")

        let hw1 = null
        try {
            hw1 = $app.findFirstRecordByFilter("homework", `batch = '${batch.getId()}' && title = 'Exercise 4.3 — Quadratic Roots'`)
        } catch (_) {}
        if (!hw1) {
            hw1 = new Record(hwCol)
            hw1.set("batch", batch.getId())
            hw1.set("subject", maths.getId())
            hw1.set("teacher", teacher.getId())
            hw1.set("title", "Exercise 4.3 — Quadratic Roots")
            hw1.set("description", "Complete Questions 1 to 10 from NCERT Textbook Exercise 4.3 in your class notebook.")
            hw1.set("chapter", "Chapter 4: Quadratic Equations")
            hw1.set("dueDate", tomorrowStr)
            hw1.set("status", "ACTIVE")
            $app.save(hw1)

            const sub1 = new Record(subCol)
            sub1.set("homework", hw1.getId())
            sub1.set("student", student.getId())
            sub1.set("status", "PENDING")
            $app.save(sub1)
        }

        let hw2 = null
        try {
            hw2 = $app.findFirstRecordByFilter("homework", `batch = '${batchPhysics.getId()}' && title = 'Ohm\'s Law Numerical Practice'`)
        } catch (_) {}
        if (!hw2) {
            hw2 = new Record(hwCol)
            hw2.set("batch", batchPhysics.getId())
            hw2.set("subject", physics.getId())
            hw2.set("teacher", teacher.getId())
            hw2.set("title", "Ohm's Law Numerical Practice")
            hw2.set("description", "Solve all numericals on resistance in series and parallel combinations.")
            hw2.set("chapter", "Chapter 12: Electricity")
            hw2.set("dueDate", todayStr)
            hw2.set("status", "ACTIVE")
            $app.save(hw2)

            const sub2 = new Record(subCol)
            sub2.set("homework", hw2.getId())
            sub2.set("student", student.getId())
            sub2.set("status", "COMPLETED")
            sub2.set("submittedAt", todayStr + " 10:30:00")
            sub2.set("grade", "A")
            sub2.set("remarks", "Excellent work! Clear formulas and step-by-step calculations.")
            $app.save(sub2)
        }

        // 10. Seed Fees (One Paid, One Pending)
        const feesCol = $app.findCollectionByNameOrId("fees")
        let feePaid = null
        try {
            feePaid = $app.findFirstRecordByFilter("fees", `student = '${student.getId()}' && status = 'PAID'`)
        } catch (_) {}
        if (!feePaid) {
            feePaid = new Record(feesCol)
            feePaid.set("student", student.getId())
            feePaid.set("batch", batch.getId())
            feePaid.set("amount", 2500)
            feePaid.set("period", "MONTHLY")
            feePaid.set("month", "AUGUST")
            feePaid.set("dueDate", "2026-08-10")
            feePaid.set("paidDate", "2026-08-08")
            feePaid.set("paymentMethod", "DIRECT_UPI")
            feePaid.set("status", "PAID")
            $app.save(feePaid)
        }

        let feePending = null
        try {
            feePending = $app.findFirstRecordByFilter("fees", `student = '${student.getId()}' && status = 'PENDING'`)
        } catch (_) {}
        if (!feePending) {
            feePending = new Record(feesCol)
            feePending.set("student", student.getId())
            feePending.set("batch", batch.getId())
            feePending.set("amount", 2500)
            feePending.set("period", "MONTHLY")
            feePending.set("month", "SEPTEMBER")
            feePending.set("dueDate", `${currentMonth}-10`)
            feePending.set("status", "PENDING")
            $app.save(feePending)
        }

        // 11. Seed Announcement
        const annCol = $app.findCollectionByNameOrId("announcements")
        let ann = null
        try {
            ann = $app.findFirstRecordByFilter("announcements", "title = 'Monthly Mathematics Assessment'")
        } catch (_) {}
        if (!ann) {
            ann = new Record(annCol)
            ann.set("institute", inst.getId())
            ann.set("title", "Monthly Mathematics Assessment")
            ann.set("message", "The monthly test covering Quadratic Equations and Arithmetic Progressions will be conducted this Saturday. Revise all NCERT exercises.")
            ann.set("type", "IMPORTANT")
            ann.set("date", todayStr)
            $app.save(ann)
        }

        return c.json(200, {
            success: true,
            message: "PocketBase demo data seeded successfully with full dashboard coverage!",
            studentLogin: "demo_student@mytuition / demo@123",
            teacherLogin: "demo_teacher@mytuition / teacher@123"
        })
    } catch (e) {
        return c.json(500, { error: e.message })
    }
})
