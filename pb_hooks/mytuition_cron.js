/// <reference path="../pb_data/types.d.ts" />

// Auto-mark overdue homework daily at 9:00 AM
cronAdd("mark_overdue_homework", "0 9 * * *", () => {
    const today = new Date().toISOString().split("T")[0]
    try {
        const overdueHW = $app.db().select("id").from("homework")
            .where($app.db().raw("status = 'ACTIVE' AND dueDate < ?", [today])).all()
        for (const hw of overdueHW) {
            const record = $app.findRecordById("homework", hw.id)
            record.set("status", "OVERDUE")
            $app.save(record)
        }
        console.log(`[Cron] Marked ${overdueHW.length} homework items as overdue`)
    } catch (e) {
        console.log("[Cron Error] mark_overdue_homework:", e.message)
    }
})

// Auto-update class sessions status every 15 minutes
cronAdd("update_session_status", "*/15 * * * *", () => {
    const now = new Date().toISOString()
    try {
        // SCHEDULED -> ONGOING
        const starting = $app.db().select("id").from("class_sessions")
            .where($app.db().raw("status = 'SCHEDULED' AND startTime <= ? AND endTime > ?", [now, now])).all()
        for (const s of starting) {
            const record = $app.findRecordById("class_sessions", s.id)
            record.set("status", "ONGOING")
            $app.save(record)
        }
        
        // ONGOING -> COMPLETED
        const ending = $app.db().select("id").from("class_sessions")
            .where($app.db().raw("status = 'ONGOING' AND endTime <= ?", [now])).all()
        for (const s of ending) {
            const record = $app.findRecordById("class_sessions", s.id)
            record.set("status", "COMPLETED")
            $app.save(record)
        }
    } catch (e) {
        console.log("[Cron Error] update_session_status:", e.message)
    }
})

// Daily fee overdue check & smart offline reminders at 10:00 AM
cronAdd("check_fee_overdue", "0 10 * * *", () => {
    const today = new Date().toISOString().split("T")[0]
    
    // Calculate date for 3 days before due date (today + 3 days)
    const d3 = new Date()
    d3.setDate(d3.getDate() + 3)
    const threeDaysFromNow = d3.toISOString().split("T")[0]

    // 1. Check & notify: 3 days BEFORE due date
    try {
        const upcomingFees = $app.db().select("id, student, amount, dueDate").from("fees")
            .where($app.db().raw("status = 'PENDING' AND dueDate = ?", [threeDaysFromNow])).all()
        for (const fee of upcomingFees) {
            if (fee.student) {
                sendCronOneSignalNotification(
                    [fee.student],
                    "Tuition Fee Reminder 📅",
                    `Friendly reminder: ₹${fee.amount} fee due on ${fee.dueDate}. Please pay your teacher.`,
                    { type: "FEE_REMINDER_UPCOMING", feeId: fee.id }
                )
            }
        }
    } catch (e) {
        console.log("[Cron Error] fee 3-day reminder:", e.message)
    }

    // 2. Check & notify: ON due date
    try {
        const dueTodayFees = $app.db().select("id, student, amount").from("fees")
            .where($app.db().raw("status = 'PENDING' AND dueDate = ?", [today])).all()
        for (const fee of dueTodayFees) {
            if (fee.student) {
                sendCronOneSignalNotification(
                    [fee.student],
                    "Tuition Fee Due Today ⚠️",
                    `Fee due today: ₹${fee.amount}. Please pay your teacher directly.`,
                    { type: "FEE_REMINDER_TODAY", feeId: fee.id }
                )
            }
        }
    } catch (e) {
        console.log("[Cron Error] fee due today reminder:", e.message)
    }

    // 3. Mark newly overdue fees (< today) and notify
    try {
        const overdueFees = $app.db().select("id, student, amount, dueDate").from("fees")
            .where($app.db().raw("status = 'PENDING' AND dueDate < ?", [today])).all()
        for (const fee of overdueFees) {
            const record = $app.findRecordById("fees", fee.id)
            record.set("status", "OVERDUE")
            $app.save(record)
        }
        console.log(`[Cron] Marked ${overdueFees.length} fees as overdue`)
    } catch (e) {
        console.log("[Cron Error] check_fee_overdue mark:", e.message)
    }

    // 4. Overdue fees recurring reminder (every 3 days after dueDate)
    try {
        const allOverdue = $app.db().select("id, student, amount, dueDate").from("fees")
            .where($app.db().raw("status = 'OVERDUE'")).all()
        const todayMs = new Date(today + "T00:00:00Z").getTime()
        for (const fee of allOverdue) {
            if (!fee.student || !fee.dueDate) continue
            const dueMs = new Date(fee.dueDate + "T00:00:00Z").getTime()
            const diffDays = Math.round((todayMs - dueMs) / (1000 * 3600 * 24))
            // Send every 3 days overdue (day 3, day 6, day 9, etc.)
            if (diffDays > 0 && diffDays % 3 === 0) {
                sendCronOneSignalNotification(
                    [fee.student],
                    "Tuition Fee Overdue 🚨",
                    `Fee overdue: ₹${fee.amount}. Please clear it with your teacher soon.`,
                    { type: "FEE_REMINDER_OVERDUE", feeId: fee.id }
                )
            }
        }
    } catch (e) {
        console.log("[Cron Error] overdue reminders:", e.message)
    }
})

// Daily homework due-reminder push at 6:00 PM IST (12:30 UTC or 18:00 server time)
cronAdd("remind_due_tomorrow_homework", "30 12 * * *", () => {
    const d = new Date()
    d.setDate(d.getDate() + 1)
    const tomorrowStr = d.toISOString().split("T")[0]

    try {
        const activeHomeworks = $app.db().select("id", "title", "batch").from("homework")
            .where($app.db().raw("status = 'ACTIVE' AND dueDate = ?", [tomorrowStr])).all()

        for (const hw of activeHomeworks) {
            // Find students with PENDING submissions for this homework
            const pendingSubs = $app.db().select("student").from("homework_submissions")
                .where($app.db().raw("homework = ? AND status = 'PENDING'", [hw.id])).all()

            const studentIds = pendingSubs.map(s => s.student).filter(Boolean)
            if (studentIds.length > 0) {
                sendCronOneSignalNotification(
                    studentIds,
                    "Homework Due Tomorrow",
                    `Finish up: ${hw.title} is due tomorrow! 📚`,
                    { type: "HOMEWORK_DUE_REMINDER", homeworkId: hw.id }
                )
            }
        }
        console.log(`[Cron] Sent due reminders for ${activeHomeworks.length} homework items`)
    } catch (e) {
        console.log("[Cron Error] remind_due_tomorrow_homework:", e.message)
    }
})

function getCronSettingValue(key, defaultValue = "") {
    try {
        const row = $app.db().select("value").from("settings")
            .where($app.db().raw("key = ?", [key])).one()
        if (row && row.value !== undefined && row.value !== null) {
            return String(row.value)
        }
    } catch (_) {}
    return defaultValue
}

function sendCronOneSignalNotification(userIds, title, message, data = {}) {
    if (!userIds || userIds.length === 0) return
    const appId = getCronSettingValue("onesignal_app_id", "")
    const apiKey = getCronSettingValue("onesignal_rest_api_key", "")
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
