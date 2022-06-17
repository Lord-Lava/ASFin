package com.lava.asfin.di

import android.app.Application
import com.lava.asfin.data.local.StudentDatabase
import com.lava.asfin.data.remote.services.AspireApi
import com.lava.asfin.data.repository.StudentRepositoryImpl
import com.lava.asfin.domain.repository.StudentRepository
import com.lava.asfin.helper.RetrofitRequestHelper
import com.lava.asfin.presentation.adapters.StudentAdapter
import com.lava.asfin.presentation.adapters.StudentListener
import com.lava.asfin.presentation.studentList.StudentListViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Provides
    @Singleton
    fun provideAspireApi(): AspireApi =
        RetrofitRequestHelper().getAspireClient().create(AspireApi::class.java)

    @Provides
    @Singleton
    fun provideStudentDatabase(app: Application): StudentDatabase = StudentDatabase.getInstance(app)

    @Provides
    @Singleton
    fun provideStudentRepository(api: AspireApi, db: StudentDatabase): StudentRepository =
        StudentRepositoryImpl(api, db)

}