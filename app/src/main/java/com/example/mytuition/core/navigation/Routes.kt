package com.example.mytuition.core.navigation

object Routes {
    const val Splash = "splash"
    const val Login = "login"
    const val Otp = "otp"
    const val Home = "home"
    const val HomeworkDetail = "homework_detail"
    const val Subjects = "subjects"
    const val SubjectDetail = "subject_detail"
    
    fun homeworkDetailRoute(homeworkId: String) = "$HomeworkDetail/$homeworkId"
    fun subjectDetailRoute(subjectId: String) = "$SubjectDetail/$subjectId"
}
