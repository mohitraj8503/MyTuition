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

    fun classDetailRoute(classId: String) = "class_detail/$classId"
    fun homeworkDetailRoute(homeworkId: String) = "$HomeworkDetail/$homeworkId"
    fun subjectDetailRoute(subjectId: String) = "$SubjectDetail/$subjectId"
}
