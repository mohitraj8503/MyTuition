package com.example.mytuition.core.navigation

object Routes {
    const val Splash = "splash"
    const val Onboarding = "onboarding"
    const val Login = "login"
    const val Otp = "otp"
    const val Home = "home"
    const val ClassDetail = "class_detail"
    const val Homework = "homework"
    const val HomeworkDetail = "homework_detail"
    const val Subjects = "subjects"
    const val SubjectDetail = "subject_detail"
    const val Calendar = "calendar"
    const val Profile = "profile"

    // === TEACHER ROUTES ===
    const val TeacherHome = "teacher_home"
    const val TeacherBatchDetail = "teacher_batch_detail"
    const val TeacherAttendance = "teacher_attendance"
    const val TeacherHomeworkCreate = "teacher_homework_create"
    const val TeacherHomeworkReview = "teacher_homework_review"
    const val TeacherAnnouncements = "teacher_announcements"
    const val TeacherStudents = "teacher_students"
    const val TeacherStudentProfile = "teacher_student_profile"
    const val TeacherCalendar = "teacher_calendar"
    const val TeacherProfile = "teacher_profile"
    const val TeacherEarnings = "teacher_earnings"
    const val TeacherRegisterStudent = "teacher_register_student"

    fun classDetailRoute(classId: String) = "class_detail/$classId"
    fun homeworkDetailRoute(homeworkId: String) = "$HomeworkDetail/$homeworkId"
    fun subjectDetailRoute(subjectId: String) = "$SubjectDetail/$subjectId"

    fun teacherBatchDetailRoute(batchId: String) = "$TeacherBatchDetail/$batchId"
    fun teacherAttendanceRoute(sessionId: String) = "$TeacherAttendance/$sessionId"
    fun teacherHomeworkCreateRoute(batchId: String) = "$TeacherHomeworkCreate/$batchId"
    fun teacherHomeworkReviewRoute(homeworkId: String) = "$TeacherHomeworkReview/$homeworkId"
    fun teacherStudentsRoute(batchId: String = "batch_10a_maths") = "$TeacherStudents/$batchId"
    fun teacherStudentProfileRoute(studentId: String) = "$TeacherStudentProfile/$studentId"
    fun teacherRegisterStudentRoute(batchId: String? = null) = if (batchId != null) "$TeacherRegisterStudent/$batchId" else TeacherRegisterStudent
}
