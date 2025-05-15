package com.example.onlinecourse.network

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    //1.	авторизация
    @POST("rpc/authorize_user")
    suspend fun authorizeUser(
        @Body request: AuthorizeRequest
    ): List<AuthorizeResponse>

    //2.   регистрация
    @POST("rpc/register_user")
    suspend fun registerUser(
        @Body request: RegisterRequest
    ): Long

    //3.	просмотр своих обращений
    @GET("rpc/get_user_appeals")
    suspend fun getUserAppeals(
        @Query("p_user_id") userId: Long
    ): List<UserAppealResponse>

    //4.	просмотр конкретного обращения
    @GET("rpc/get_appeal_detail")
    suspend fun getAppealDetail(
        @Query("p_appeal_id") appealId: Long
    ): List<AppealDetailResponse>

    //5.	добавить обращение
    @POST("rpc/add_appeal_with_file")
    suspend fun addAppeal(
        @Body request: AddAppealRequest
    ): Boolean

    //6.	добавить ответ на обращение
    @POST("rpc/add_answer_to_appeal")
    suspend fun addAnswerToAppeal(
        @Body request: AddAnswerRequest
    ): Boolean

    //7.	просмотр тем обращений
    @GET("rpc/get_topics_appeals")
    suspend fun getAppealTopics(): List<AppealTopicResponse>

    //8.	просмотр всех обращений
    @GET("rpc/get_all_appeals")
    suspend fun getAllAppeals(): List<AllAppealResponse>

    //9.	просмотр данных пользователя
    @GET("rpc/get_user_profile")
    suspend fun getUserProfile(
        @Query("p_user_id") userId: Long
    ): List<UserProfileResponse>

    //10.	изменение данных пользователя
    @POST("rpc/update_user_profile")
    suspend fun updateUserProfile(
        @Body request: UpdateUserProfileRequest
    ): Boolean

    //11.	изменение пароля
    @POST("rpc/update_user_password")
    suspend fun updateUserPassword(
        @Body request: UpdateUserPasswordRequest
    ): Boolean

    //12.	просмотр уведомлений
    @GET("rpc/get_user_notifications")
    suspend fun getUserNotifications(
        @Query("p_user_id") userId: Long
    ): List<NotificationResponse>

    //13.	просмотр конкретного уведомления
    @GET("rpc/get_notification_details")
    suspend fun getNotificationDetails(
        @Query("p_notification_id") notificationId: Long
    ): NotificationDetailResponse

    //14.	просмотр всех курсов
    @GET("rpc/get_all_courses")
    suspend fun getAllCourses(): List<CourseResponse>

    //15.	просмотр конкретного курса
    @GET("rpc/get_course_details")
    suspend fun getCourseDetails(
        @Query("p_course_id") courseId: Long
    ): CourseDetailResponse

    //16.	просмотр курсов
    @POST("rpc/get_courses_by_user_and_status")
    suspend fun getCoursesByUserAndStatus(
        @Body request: UserCourseRequest
    ): List<UserCourseResponse>

    //17.	просмотр уроков курса
    @GET("rpc/get_lessons_by_course")
    suspend fun getLessonsByCourse(
        @Query("p_course_id") courseId: Long
    ): List<LessonResponse>

    //18.	просмотр урока курса
    @GET("rpc/get_lesson_details")
    suspend fun getLessonDetails(
        @Query("p_lesson_id") lessonId: Long
    ): LessonDetailResponse

    //19.	просмотр шагов урока
    @GET("rpc/get_steps_by_lesson")
    suspend fun getStepsByLesson(
        @Query("p_lesson_id") lessonId: Long
    ): List<StepResponse>

    //20.	просмотр шага урока
    @GET("rpc/get_step_details")
    suspend fun getStepDetails(
        @Query("p_step_id") stepId: Long
    ): StepDetailResponse

    //21.	просмотр статистики по дням
    @GET("rpc/get_daily_statistics")
    suspend fun getDailyStatistics(
        @Query("p_user_id") userId: Long
    ): List<DailyStatisticsResponse>

    //22.	просмотр курсов, которые создал учитель
    @GET("rpc/get_courses_by_teacher")
    suspend fun getCoursesByTeacher(
        @Query("p_teacher_id") teacherId: Long
    ): List<CourseByTeacherResponse>

    //23.	создание курса
    @POST("rpc/create_course")
    suspend fun createCourse(
        @Body courseData: CreateCourseRequest
    ): Long

    //24.	просмотр категорий курсов
    @GET("rpc/get_course_categories")
    suspend fun getCourseCategories(): List<CourseCategory>

    //25.	создание урока
    @POST("rpc/create_lesson")
    suspend fun createLesson(
        @Body request: CreateLessonRequest
    ): Long

    //26.	изменение курса
    @POST("rpc/update_course")
    suspend fun updateCourse(
        @Body request: UpdateCourseRequest
    ): Boolean

    //27.	изменение урока
    @POST("rpc/update_lesson")
    suspend fun updateLesson(
        @Body request: UpdateLessonRequest
    ): Boolean

    //28.	удаление курса
    @POST("rpc/delete_course")
    suspend fun deleteCourse(
        @Body request: DeleteCourseRequest
    ): Boolean

    //29.	удаление урока
    @POST("rpc/delete_lesson")
    suspend fun deleteLesson(
        @Body request: DeleteLessonRequest
    ): Boolean

    //30.	удаление шага
    @POST("rpc/delete_step")
    suspend fun deleteStep(
        @Body request: DeleteStepRequest
    ): Boolean

    //31.	просмотр ответов на шаг
    @GET("rpc/get_answers_for_step")
    suspend fun getAnswersForStep(
        @Query("step_id") stepId: Long
    ): List<AnswersForStepResponse>

    //32.	просмотр ответа на шаг
    @GET("rpc/get_answer_for_step")
    suspend fun getAnswerForStep(
        @Query("answer_id") answerId: Long
    ): AnswerForStepResponse

    //33.	оценить ответ на шаг
    @POST("rpc/evaluate_answer_on_step")
    suspend fun evaluateAnswerOnStep(
        @Body request: EvaluateAnswerOnStepRequest
    ): Boolean

    //34.	просмотр статистики на курсе
    @GET("rpc/view_course_statistics")
    suspend fun viewCourseStatistics(
        @Query("course_id") courseId: Long
    ): List<ViewCourseStatisticsResponse>

    //35.	новые пользователи
    @GET("rpc/get_new_users")
    suspend fun getNewUsers(): List<NewUserResponse>

    //36.	просмотр всех пользователей
    @GET("rpc/get_all_users")
    suspend fun getAllUsers(): List<AllUserResponse>

    //37.	выдать предупреждение пользователю
    @POST("rpc/issue_warning")
    suspend fun issueWarning(@Query("user_id") userId: Long): Boolean

    //38.	удалить страницу пользователя
    @POST("rpc/delete_user")
    suspend fun deleteUser(@Query("p_user_id") userId: Long): Boolean

    //39.	выдать предупреждение на курс
    @POST("rpc/warn_on_course")
    suspend fun warnOnCourse(@Query("course_id") courseId: Long): Boolean

    //40.	выдать предупреждение на шаг урока
    @POST("rpc/warn_on_step")
    suspend fun warnOnStep(@Query("step_id") stepId: Long): Boolean

    //41.	поступление/отложить курс
    @POST("rpc/enroll_or_defer_course")
    suspend fun enrollOrDeferCourse(
        @Body request: EnrollOrDeferCourseRequest
    ): Boolean

    //42.	просмотр своих сертификатов
    @GET("rpc/get_user_sertificates")
    suspend fun getUserSertificates(
        @Query("user_id") userId: Long
    ): List<UserCertificatesResponse>

    //43.	просмотр конкретного сертификата	???

    //44.	ответ на шаг урока
    @POST("rpc/answer_lesson_step")
    suspend fun answerLessonStep(
        @Body request: AnswerLessonStepRequest
    ): Boolean

    //45.	изменение ответа на шаг урока
    @POST("rpc/update_lesson_step_answer")
    suspend fun updateLessonStepAnswer(
        @Body request: UpdateLessonStepAnswerRequest
    ): Boolean

    //46.1.	создание шага «Лекция»
    @POST("rpc/create_lecture_step")
    suspend fun createLectureStep(
        @Body request: CreateLectureStepRequest
    ): Boolean

    //46.2.	создание шага «Вопрос без вариантов ответа»
    @POST("rpc/create_open_question_step")
    suspend fun createOpenQuestionStep(
        @Body request: CreateOpenQuestionStepRequest
    ): Boolean

    //46.3.	создание шага «Вопрос с вариантами ответа»
    @POST("rpc/create_multiple_choice_question_step")
    suspend fun createMultipleChoiceQuestionStep(
        @Body request: CreateMultipleChoiceQuestionStepRequest
    ): Boolean

    //46.4.	создание шага «Вопрос с приложением»
    @POST("rpc/create_file_upload_question_step")
    suspend fun createFileUploadQuestionStep(
        @Body request: CreateFileUploadQuestionStepRequest
    ): Boolean

    //47.1.	изменение шага «Лекция»
    @POST("rpc/update_lecture_step")
    suspend fun updateLectureStep(
        @Body request: UpdateLectureStepRequest
    ): Boolean

    //47.2.	изменение шага «Вопрос без вариантов ответа»
    @POST("rpc/update_open_question_step")
    suspend fun updateOpenQuestionStep(
        @Body request: UpdateOpenQuestionStepRequest
    ): Boolean

    //47.3.	изменение шага «Вопрос с вариантами ответа»
    @POST("rpc/update_multiple_choice_question_step")
    suspend fun updateMultipleChoiceQuestionStep(
        @Body request: UpdateMultipleChoiceQuestionStepRequest
    ): Boolean

    //47.4.	изменение шага «Вопрос с приложением»
    @POST("rpc/update_file_upload_question_step")
    suspend fun updateFileUploadQuestionStep(
        @Body request: UpdateFileUploadQuestionStepRequest
    ): Boolean

    //48.	добавить категории курсов
    @POST("rpc/add_course_category")
    suspend fun addCourseCategory(
        @Body request: AddCourseCategoryRequest
    ): Boolean

    //49.	изменить категорию курсов
    @POST("rpc/update_course_category")
    suspend fun updateCourseCategory(
        @Body request: UpdateCourseCategoryRequest
    ): Boolean

    //50.	удалить категорию курсов
    @POST("rpc/delete_course_category")
    suspend fun deleteCourseCategory(
        @Query("id") id: Long
    ): Boolean

    //51.	добавить темы обращений
    @POST("rpc/add_topic_appeal")
    suspend fun addTopicAppeal(
        @Body request: AddTopicAppealRequest
    ): Boolean

    //52.	изменить тему обращений
    @POST("rpc/update_topic_appeal")
    suspend fun updateTopicAppeal(
        @Body request: UpdateTopicAppealRequest
    ): Boolean

    //53.	удалить тему обращения
    @POST("rpc/delete_topic_appeal")
    suspend fun deleteTopicAppeal(
        @Query("id") id: Long
    ): Boolean

    //54.	зарегистрировать преподавателя
    @POST("rpc/register_teacher")
    suspend fun registerTeacher(
        @Body request: RegisterTeacherRequest
    ): Long

    //55.	Общая статистика платформы
    @GET("rpc/get_platform_statistics")
    suspend fun getPlatformStatistics(): GetPlatformStatisticsResponse

    //56.	Активность пользователей
    @GET("rpc/get_user_activity_stats")
    suspend fun getUserActivityStats(
        @Query("days_back") daysBack: Int = 14
    ): List<UserActivityStatsItemResponse>

    //57.	Статистика по обращениям
    @GET("rpc/get_appeals_statistics")
    suspend fun getAppealsStatistics(): List<AppealStatisticsItemResponse>

    //58.	выдать сертификат студенту	???
    //59.	скачать сертификат	???
}