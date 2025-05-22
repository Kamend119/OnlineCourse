package com.example.onlinecourse.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
import retrofit2.http.Streaming

interface ApiService {
    @GET("get_file")
    @Streaming
    suspend fun getFile(@Query("file_path") filePath: String): Response<ResponseBody>

    //1.	авторизация
    @GET("login")
    suspend fun login(
        @Query("login_") login: String,
        @Query("password") password: String
    ): AuthorizeResponse

    //2.   регистрация
    @Multipart
    @POST("register_user")
    suspend fun registerUser(
        @Part("login") login: RequestBody,
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody,
        @Part("last_name") lastName: RequestBody,
        @Part("first_name") firstName: RequestBody,
        @Part("patronymic") patronymic: RequestBody,
        @Part("file_type") fileType: RequestBody? = null,
        @Part("original_name") originalName: RequestBody? = null,
        @Part("mime_type") mimeType: RequestBody? = null,
        @Part("size_bytes") sizeBytes: RequestBody? = null,
        @Part file: MultipartBody.Part? = null
    ): LongResponse

    //3.	просмотр своих обращений
    @GET("get_user_appeals")
    suspend fun getUserAppeals(
        @Query("user_id") userId: Long
    ): List<UserAppealsResponse>

    //4.	просмотр конкретного обращения
    @GET("get_appeal_detail")
    suspend fun getAppealDetail(
        @Query("appeal_id") appealId: Long
    ): List<AppealDetailResponse>

    //5.	добавить обращение
    @Multipart
    @POST("add_appeal_with_file")
    suspend fun addAppealWithFile(
        @Part("user_id") userId: RequestBody,
        @Part("topic_appeal_id") topicAppealId: RequestBody,
        @Part("heading_appeal") headingAppeal: RequestBody,
        @Part("text_appeal") textAppeal: RequestBody,
        @Part("file_type") fileType: RequestBody? = null,
        @Part("original_name") originalName: RequestBody? = null,
        @Part("mime_type") mimeType: RequestBody? = null,
        @Part("size_bytes") sizeBytes: RequestBody? = null,
        @Part file: MultipartBody.Part? = null
    ): Response<Boolean>

    //6.	добавить ответ на обращение
    @POST("add_answer_to_appeal")
    suspend fun addAnswerToAppeal(
        @Query("appeal_id") appealId: Long,
        @Query("user_id") userId: Long,
        @Query("text_answer") textAnswer: String
    ): Response<Boolean>

    //7.	просмотр тем обращений
    @GET("get_topics_appeals")
    suspend fun getTopicsAppeals(): List<AppealTopicResponse>

    //8.	просмотр всех обращений
    @GET("get_all_appeals")
    suspend fun getAllAppeals(): List<AllAppealResponse>

    //9.	просмотр данных пользователя
    @GET("get_user_profile")
    suspend fun getUserProfile(
        @Query("user_id") userId: Long)
    : List<UserProfileResponse>

    //10.	изменение данных пользователя
    @Multipart
    @POST("update_user_profile")
    suspend fun updateUserProfile(
        @Part("user_id") userId: RequestBody,
        @Part("email") email: RequestBody,
        @Part("last_name") lastName: RequestBody,
        @Part("first_name") firstName: RequestBody,
        @Part("patronymic") patronymic: RequestBody?,
        @Part("file_type") fileType: RequestBody?,
        @Part("original_name") originalName: RequestBody?,
        @Part("mime_type") mimeType: RequestBody?,
        @Part("size_bytes") sizeBytes: RequestBody?,
        @Part file: MultipartBody.Part?
    ): Response<Boolean>

    //11.	изменение пароля
    @POST("update_user_password")
    suspend fun updateUserPassword(
        @Query("user_id") userId: Long,
        @Query("password") currentPassword: String,
        @Query("new_password") newPassword: String
    ): Response<Boolean>

    //12.	просмотр уведомлений
    @GET("get_user_notifications")
    suspend fun getUserNotifications(
        @Query("user_id") userId: Long
    ): List<NotificationResponse>

    //13.	просмотр конкретного уведомления
    @GET("get_notification_details")
    suspend fun getNotificationDetails(
        @Query("notif_id") notifId: Long
    ): List<NotificationDetailResponse>

    //14.	просмотр всех курсов
    @GET("get_all_courses")
    suspend fun getAllCourses(): List<CourseResponse>

    //15.	просмотр конкретного курса
    @GET("get_course_details")
    suspend fun getCourseDetails(
        @Query("course_id") courseId: Long
    ): List<CourseDetailResponse>

    //16.	просмотр курсов
    @GET("get_courses_by_user_and_status")
    suspend fun getCoursesByUserAndStatus(
        @Query("user_id") userId: Long,
        @Query("status_name") statusName: String
    ): List<UserCourseResponse>

    //17.	просмотр уроков курса
    @GET("get_lessons_by_course")
    suspend fun getLessonsByCourse(
        @Query("course_id") courseId: Long
    ): List<LessonResponse>

    //18.	просмотр урока курса
    @GET("get_lesson_details")
    suspend fun getLessonDetails(
        @Query("lesson_id") lessonId: Long
    ): List<LessonDetailResponse>

    //19.	просмотр шагов урока
    @GET("get_steps_by_lesson")
    suspend fun getStepsByLesson(
        @Query("lesson_id") lessonId: Long
    ): List<StepsByLessons>

    //20.	просмотр шага урока
    @GET("get_step_details")
    suspend fun getStepDetails(
        @Query("step_id") stepId: Long,
        @Query("user_id") userId: Long
    ): List<StepDetailResponse>

    //21.	просмотр статистики по дням
    @GET("get_daily_statistics")
    suspend fun getDailyStatistics(
        @Query("user_id") userId: Long
    ): List<DailyStatisticsResponse>

    //22.	просмотр курсов, которые создал учитель
    @GET("get_courses_by_teacher")
    suspend fun getCoursesByTeacher(
        @Query("teacher_id") teacherId: Long
    ): List<CourseByTeacherResponse>

    //23.	создание курса
    @POST("create_course")
    suspend fun createCourse(
        @Query("course_category_id") courseCategoryId: Long,
        @Query("user_id") userId: Long,
        @Query("name") name: String,
        @Query("description") description: String
    ): LongResponse

    //24.	просмотр категорий курсов
    @GET("get_course_categories")
    suspend fun getCourseCategories(): List<CourseCategory>

    //25.	создание урока
    @POST("create_lesson")
    suspend fun createLesson(
        @Query("course_id") courseId: Long,
        @Query("name") name: String,
        @Query("description") description: String,
        @Query("sequence_number") sequenceNumber: Long
    ): LongResponse

    //26.	изменение курса
    @POST("update_course")
    suspend fun updateCourse(
        @Query("course_id") courseId: Long,
        @Query("course_category_id") courseCategoryId: Long,
        @Query("user_id") userId: Long,
        @Query("name") name: String,
        @Query("description") description: String
    ): Response<Boolean>

    //27.	изменение урока
    @POST("update_lesson")
    suspend fun updateLesson(
        @Query("lesson_id") lessonId: Long,
        @Query("course_id") courseId: Long,
        @Query("name") name: String,
        @Query("description") description: String,
        @Query("sequence_number") sequenceNumber: Long
    ): Response<Boolean>

    //28.	удаление курса
    @POST("delete_course")
    suspend fun deleteCourse(
        @Query("course_id") courseId: Long,
        @Query("user_id") userId: Long
    ): Response<Boolean>

    //29.	удаление урока
    @POST("delete_lesson")
    suspend fun deleteLesson(
        @Query("lesson_id") lessonId: Long,
        @Query("user_id") userId: Long
    ): Response<Boolean>

    //30.	удаление шага
    @POST("delete_step")
    suspend fun deleteStep(
        @Query("step_id") stepId: Long,
        @Query("user_id") userId: Long
    ): Response<Boolean>

    //31.	просмотр ответов на шаг
    @GET("get_answers_for_step")
    suspend fun getAnswersForStep(
        @Query("step_id") stepId: Long
    ): List<AnswersForStepResponse>

    //32.	просмотр ответа на шаг
    @GET("get_answer_for_step")
    suspend fun getAnswerForStep(
        @Query("answer_id") answerId: Long
    ): AnswerForStepResponse

    //33.	оценить ответ на шаг
    @POST("evaluate_answer_on_step")
    suspend fun evaluateAnswerOnStep(
        @Query("answer_user_id") answerUserId: Long,
        @Query("score") score: Long,
        @Query("comment_teacher") commentTeacher: String?
    ): Response<Boolean>

    //34.	просмотр статистики на курсе
    @GET("view_course_statistics")
    suspend fun viewCourseStatistics(
        @Query("course_id") courseId: Long
    ): List<ViewCourseStatisticsResponse>

    //36.	просмотр всех пользователей
    @GET("/get_all_users")
    suspend fun getAllUsers(): List<AllUserResponse>

    //37.	выдать предупреждение пользователю
    @POST("issue_warning")
    suspend fun issueWarning(
        @Query("user_id") userId: Long
    ): Response<Boolean>

    //38.	удалить страницу пользователя
    @POST("delete_user")
    suspend fun deleteUser(
        @Query("user_id") userId: Long
    ): Response<Boolean>

    //39.	выдать предупреждение на курс
    @POST("warn_on_course")
    suspend fun warnOnCourse(
        @Query("course_id") courseId: Long
    ): Response<Boolean>

    //40.	выдать предупреждение на шаг урока
    @POST("warn_on_step")
    suspend fun warnOnStep(
        @Query("step_id") stepId: Long
    ): Response<Boolean>

    //41.	поступление/отложить курс
    @POST("enroll_or_defer_course")
    suspend fun enrollOrDeferCourse(
        @Query("user_id") userId: Long,
        @Query("course_id") courseId: Long,
        @Query("status_name") statusName: String
    ): Response<Boolean>

    //42.	просмотр своих сертификатов
    @GET("get_user_sertificates")
    suspend fun getUserSertificates(
        @Query("user_id") userId: Long
    ): List<UserCertificatesResponse>

    //43.	просмотр конкретного сертификата
    @GET("view_certificate")
    suspend fun viewCertificate(
        @Query("cerf_id") certificateId: Long
    ): List<ViewCertificate>

    //44.	ответ на шаг урока
    @Multipart
    @POST("answer_lesson_step")
    suspend fun answerLessonStep(
        @Part("user_id") userId: RequestBody,
        @Part("step_lesson_id") stepLessonId: RequestBody,
        @Part("answer_text") answerText: RequestBody? = null,
        @Part("selected_option_ids") selectedOptionIds: RequestBody? = null,
        @Part("original_name") originalName: RequestBody? = null,
        @Part("mime_type") mimeType: RequestBody? = null,
        @Part("size_bytes") sizeBytes: RequestBody? = null,
        @Part("comment_student") commentStudent: RequestBody? = null,
        @Part file: MultipartBody.Part? = null
    ): Response<Boolean>

    //45.	изменение ответа на шаг урока
    @Multipart
    @POST("update_lesson_step_answer")
    suspend fun updateLessonStepAnswer(
        @Part("answer_id") answerId: RequestBody,
        @Part("user_id") userId: RequestBody,
        @Part("step_lesson_id") stepLessonId: RequestBody,
        @Part("answer_text") answerText: RequestBody? = null,
        @Part("selected_option_ids") selectedOptionIds: RequestBody? = null,
        @Part("original_name") originalName: RequestBody? = null,
        @Part("mime_type") mimeType: RequestBody? = null,
        @Part("size_bytes") sizeBytes: RequestBody? = null,
        @Part("comment_student") commentStudent: RequestBody? = null,
        @Part file: MultipartBody.Part? = null
    ): Response<Boolean>

    //46.1.	создание шага «Лекция»
    @Multipart
    @POST("create_lecture_step")
    suspend fun createLectureStep(
        @Part("lesson_id") lessonId: RequestBody,
        @Part("name") name: RequestBody,
        @Part("content") content: RequestBody,
        @Part("sequence_number") sequenceNumber: RequestBody,
        @Part("obligatory") obligatory: RequestBody,
        @Part("original_name") originalName: RequestBody? = null,
        @Part("mime_type") mimeType: RequestBody? = null,
        @Part("size_bytes") sizeBytes: RequestBody? = null,
        @Part file: MultipartBody.Part? = null
    ): Response<Boolean>

    //46.2.	создание шага «Вопрос без вариантов ответа»
    @POST("create_open_question_step")
    suspend fun createOpenQuestionStep(
        @Query("lesson_id") lessonId: Long,
        @Query("name") name: String,
        @Query("content") content: String,
        @Query("sequence_number") sequenceNumber: Long,
        @Query("time_passes") timePasses: String,
        @Query("obligatory") obligatory: Boolean,
        @Query("max_score") maxScore: Long? = null
    ): Response<Boolean>

    //46.3.	создание шага «Вопрос с вариантами ответа»
    @POST("create_multiple_choice_question_step")
    suspend fun createMultipleChoiceQuestionStep(
        @Query("lesson_id") lessonId: Long,
        @Query("name") name: String,
        @Query("content") content: String,
        @Query("sequence_number") sequenceNumber: Long,
        @Query("time_passes") timePasses: String,
        @Query("obligatory") obligatory: Boolean,
        @Query("max_score") maxScore: Long,
        @Query("text_options") textOptions: List<String>,
        @Query("correct") correct: List<Boolean>,
        @Query("scores") scores: List<Long>
    ): Response<Boolean>

    //46.4.	создание шага «Вопрос с приложением»
    @Multipart
    @POST("create_file_upload_question_step")
    suspend fun createFileUploadQuestionStep(
        @Part("lesson_id") lessonId: RequestBody,
        @Part("name") name: RequestBody,
        @Part("content") content: RequestBody,
        @Part("sequence_number") sequenceNumber: RequestBody,
        @Part("time_passes") timePasses: RequestBody,
        @Part("obligatory") obligatory: RequestBody,
        @Part("max_score") maxScore: RequestBody,
        @Part("original_name") originalName: RequestBody,
        @Part("mime_type") mimeType: RequestBody,
        @Part("size_bytes") sizeBytes: RequestBody,
        @Part file: MultipartBody.Part
    ): Response<Boolean>

    //47.1.	изменение шага «Лекция»
    @Multipart
    @POST("update_lecture_step")
    suspend fun updateLectureStep(
        @Part("step_id") stepId: RequestBody,
        @Part("name") name: RequestBody,
        @Part("content") content: RequestBody,
        @Part("sequence_number") sequenceNumber: RequestBody,
        @Part("obligatory") obligatory: RequestBody,
        @Part("original_name") originalName: RequestBody?,
        @Part("mime_type") mimeType: RequestBody?,
        @Part("size_bytes") sizeBytes: RequestBody?,
        @Part file: MultipartBody.Part?
    ): Response<Boolean>

    //47.2.	изменение шага «Вопрос без вариантов ответа»
    @POST("update_open_question_step")
    suspend fun updateOpenQuestionStep(
        @Query("step_id") stepId: Long,
        @Query("name") name: String,
        @Query("content") content: String,
        @Query("sequence_number") sequenceNumber: Long,
        @Query("time_passes") timePasses: String,
        @Query("obligatory") obligatory: Boolean,
        @Query("max_score") maxScore: Long
    ): Response<Boolean>

    //47.3.	изменение шага «Вопрос с вариантами ответа»
    @POST("update_multiple_choice_question_step")
    suspend fun updateMultipleChoiceQuestionStep(
        @Query("step_id") stepId: Long,
        @Query("name") name: String,
        @Query("content") content: String,
        @Query("sequence_number") sequenceNumber: Long,
        @Query("time_passes") timePasses: String,
        @Query("obligatory") obligatory: Boolean,
        @Query("max_score") maxScore: Long,
        @Query("text_options") textOptions: List<String>,
        @Query("correct") correct: List<Boolean>,
        @Query("scores") scores: List<Long>
    ): Response<Boolean>

    //47.4.	изменение шага «Вопрос с приложением»
    @Multipart
    @POST("update_file_upload_question_step")
    suspend fun updateFileUploadQuestionStep(
        @Part("step_id") stepId: RequestBody,
        @Part("name") name: RequestBody,
        @Part("content") content: RequestBody,
        @Part("sequence_number") sequenceNumber: RequestBody,
        @Part("time_passes") timePasses: RequestBody,
        @Part("obligatory") obligatory: RequestBody,
        @Part("max_score") maxScore: RequestBody,
        @Part("original_name") originalName: RequestBody,
        @Part("mime_type") mimeType: RequestBody,
        @Part("size_bytes") sizeBytes: RequestBody,
        @Part file: MultipartBody.Part
    ): Response<Boolean>

    //48.	добавить категории курсов
    @POST("add_course_category")
    suspend fun addCourseCategory(
        @Query("name") name: String,
        @Query("description") description: String
    ): Response<Boolean>

    //49.	изменить категорию курсов
    @POST("update_course_category")
    suspend fun updateCourseCategory(
        @Query("course_id") courseId: Long,
        @Query("name") name: String,
        @Query("description") description: String
    ): Response<Boolean>

    //50.	удалить категорию курсов
    @POST("delete_course_category")
    suspend fun deleteCourseCategory(
        @Query("course_id") courseId: Long
    ): Response<Boolean>

    //51.	добавить темы обращений
    @POST("add_topic_appeal")
    suspend fun addTopicAppeal(
        @Query("name") name: String,
        @Query("description") description: String
    ): Response<Boolean>

    //52.	изменить тему обращений
    @POST("update_topic_appeal")
    suspend fun updateTopicAppeal(
        @Query("id_") id: Long,
        @Query("name") name: String,
        @Query("description") description: String
    ): Response<Boolean>

    //53.	удалить тему обращения
    @POST("delete_topic_appeal")
    suspend fun deleteTopicAppeal(
        @Query("id_") id: Long
    ): Response<Boolean>

    //54.	зарегистрировать преподавателя
    @POST("register_teacher")
    suspend fun registerTeacher(
        @Query("login") login: String,
        @Query("mail") mail: String,
        @Query("password") password: String,
        @Query("last_name") lastName: String,
        @Query("first_name") firstName: String,
        @Query("patronymic") patronymic: String
    ): LongResponse

    //55.	Общая статистика платформы
    @GET("get_platform_statistics")
    suspend fun getPlatformStatistics(): List<GetPlatformStatisticsResponse>

    //56.	Активность пользователей
    @GET("get_user_activity_stats")
    suspend fun getUserActivityStats(
        @Query("days_back") daysBack: Int = 14
    ): List<UserActivityStatsItemResponse>

    //57.	Статистика по обращениям
    @GET("get_appeals_statistics")
    suspend fun getAppealsStatistics(): List<AppealStatisticsItemResponse>

    //58.	выдать сертификат студенту
    @POST("/issue_certificate")
    suspend fun issueCertificate(
        @Query("user_id") userId: Long,
        @Query("course_id") courseId: Long
    ): List<IssueCertificateResponse>
}