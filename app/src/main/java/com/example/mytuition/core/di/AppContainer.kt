package com.example.mytuition.core.di

import com.example.mytuition.core.data.repository.MockAuthRepository
import com.example.mytuition.core.data.repository.MockHomeworkRepository
import com.example.mytuition.core.data.repository.MockSubjectRepository
import com.example.mytuition.core.domain.repository.AuthRepository
import com.example.mytuition.core.domain.repository.HomeworkRepository
import com.example.mytuition.core.domain.repository.SubjectRepository
import com.example.mytuition.core.domain.usecase.GetHomeworkListUseCase
import com.example.mytuition.core.domain.usecase.GetHomeworkDetailUseCase
import com.example.mytuition.core.domain.usecase.MarkHomeworkCompleteUseCase
import com.example.mytuition.core.domain.usecase.GetSubjectsUseCase
import com.example.mytuition.core.domain.usecase.GetSubjectDetailUseCase

object AppContainer {
    val authRepository: AuthRepository by lazy {
        MockAuthRepository()
    }
    
    val homeworkRepository: HomeworkRepository by lazy {
        MockHomeworkRepository()
    }

    val subjectRepository: SubjectRepository by lazy {
        MockSubjectRepository()
    }

    val getHomeworkListUseCase: GetHomeworkListUseCase by lazy {
        GetHomeworkListUseCase(homeworkRepository)
    }

    val getHomeworkDetailUseCase: GetHomeworkDetailUseCase by lazy {
        GetHomeworkDetailUseCase(homeworkRepository)
    }

    val markHomeworkCompleteUseCase: MarkHomeworkCompleteUseCase by lazy {
        MarkHomeworkCompleteUseCase(homeworkRepository)
    }

    val getSubjectsUseCase: GetSubjectsUseCase by lazy {
        GetSubjectsUseCase(subjectRepository)
    }

    val getSubjectDetailUseCase: GetSubjectDetailUseCase by lazy {
        GetSubjectDetailUseCase(subjectRepository)
    }
}
