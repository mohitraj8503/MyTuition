package com.example.mytuition.core.di

import com.example.mytuition.core.data.repository.FirebaseAuthRepository
import com.example.mytuition.core.data.repository.FirebaseFeeRepository
import com.example.mytuition.core.data.repository.FirebaseHomeworkRepository
import com.example.mytuition.core.data.repository.FirebaseSubjectRepository
import com.example.mytuition.core.data.repository.HomeRepository
import com.example.mytuition.core.domain.repository.AuthRepository
import com.example.mytuition.core.domain.repository.FeeRepository
import com.example.mytuition.core.domain.repository.HomeworkRepository
import com.example.mytuition.core.domain.repository.SubjectRepository
import com.example.mytuition.core.domain.usecase.GetHomeworkDetailUseCase
import com.example.mytuition.core.domain.usecase.GetHomeworkListUseCase
import com.example.mytuition.core.domain.usecase.GetSubjectDetailUseCase
import com.example.mytuition.core.domain.usecase.GetSubjectsUseCase
import com.example.mytuition.core.domain.usecase.MarkHomeworkCompleteUseCase

object AppContainer {
    val authRepository: AuthRepository by lazy {
        FirebaseAuthRepository()
    }
    
    val homeworkRepository: HomeworkRepository by lazy {
        FirebaseHomeworkRepository()
    }

    val subjectRepository: SubjectRepository by lazy {
        FirebaseSubjectRepository()
    }

    val homeRepository: HomeRepository by lazy {
        HomeRepository()
    }

    val feeRepository: FeeRepository by lazy {
        FirebaseFeeRepository()
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
