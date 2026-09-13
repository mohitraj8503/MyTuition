/// <reference path="../pb_data/types.d.ts" />

// Notify teacher when homework submission is made
onRecordUpdate((e) => {
    try {
        if (e.record.get("status") === "SUBMITTED") {
            const hwId = e.record.get("homework")
            if (hwId) {
                const homework = $app.findRecordById("homework", hwId)
                console.log(`[Notification] Submission received for homework: ${homework?.get("title")}`)
            }
        }
    } catch (err) {
        console.log("[Hook Error] onRecordUpdate homework_submissions:", err.message)
    }
}, "homework_submissions")

// Alert when student marked absent
onRecordCreate((e) => {
    try {
        if (e.record.get("status") === "ABSENT") {
            const studentId = e.record.get("student")
            const student = $app.findRecordById("users", studentId)
            console.log(`[Notification] Absent notification logged for student: ${student?.get("name")}`)
        }
    } catch (err) {
        console.log("[Hook Error] onRecordCreate attendance:", err.message)
    }
}, "attendance")
