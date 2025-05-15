package com.example.onlinecourse.network

import com.google.gson.annotations.SerializedName

// 1.	авторизация
data class AuthorizeRequest(
    @SerializedName("p_login") val login: String,
    @SerializedName("p_password") val password: String
)
data class AuthorizeResponse(
    @SerializedName("id_user") val idUser: Long,
    @SerializedName("role_name") val roleName: String?
)

//2.   регистрация
data class RegisterRequest(
    @SerializedName("p_login") val login: String,
    @SerializedName("p_mail") val mail: String,
    @SerializedName("p_password") val password: String,
    @SerializedName("p_last_name") val lastName: String,
    @SerializedName("p_first_name") val firstName: String,
    @SerializedName("p_patronymic") val patronymic: String,
    @SerializedName("p_file_type") val fileType: String? = null,
    @SerializedName("p_file_path") val filePath: String? = null,
    @SerializedName("p_original_name") val originalName: String? = null,
    @SerializedName("p_mime_type") val mimeType: String? = null,
    @SerializedName("p_size_bytes") val sizeBytes: Long? = null
)

//3.	просмотр своих обращений
data class UserAppealResponse(
    @SerializedName("appeal_id") val appealId: Long,
    @SerializedName("heading_appeal") val headingAppeal: String,
    @SerializedName("topic_name") val topicName: String,
    @SerializedName("status_name") val statusName: String
)

//4.	просмотр конкретного обращения
data class AppealDetailResponse(
    @SerializedName("status_name") val statusName: String,
    @SerializedName("topic_name") val topicName: String,
    @SerializedName("topic_id") val topicId: Long,
    @SerializedName("heading_appeal") val headingAppeal: String,
    @SerializedName("text_appeal") val textAppeal: String,
    @SerializedName("admin_last_name") val adminLastName: String?,
    @SerializedName("admin_first_name") val adminFirstName: String?,
    @SerializedName("admin_patronymic") val adminPatronymic: String?,
    @SerializedName("text_answer") val textAnswer: String?,
    @SerializedName("date_answer") val dateAnswer: String?
)

//5.	добавить обращение
data class AddAppealRequest(
    @SerializedName("p_user_id") val userId: Long,
    @SerializedName("p_topic_appeal_id") val topicAppealId: Long,
    @SerializedName("p_heading_appeal") val headingAppeal: String,
    @SerializedName("p_text_appeal") val textAppeal: String,
    @SerializedName("p_file_type") val fileType: String? = null,
    @SerializedName("p_file_path") val filePath: String? = null,
    @SerializedName("p_original_name") val originalName: String? = null,
    @SerializedName("p_mime_type") val mimeType: String? = null,
    @SerializedName("p_size_bytes") val sizeBytes: Long? = null
)

//6.	добавить ответ на обращение
data class AddAnswerRequest(
    @SerializedName("p_appeal_id") val appealId: Long,
    @SerializedName("p_user_id") val userId: Long,
    @SerializedName("p_text_answer") val textAnswer: String
)

//7.	просмотр тем обращений
data class AppealTopicResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String
)

//8.	просмотр всех обращений
data class AllAppealResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("heading_appeal") val headingAppeal: String,
    @SerializedName("topic_name") val topicName: String,
    @SerializedName("status_appeal") val statusAppeal: String
)

//9.	просмотр данных пользователя
data class UserProfileResponse(
    @SerializedName("mail") val mail: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("patronymic") val patronymic: String,
    @SerializedName("date_registration") val dateRegistration: String,
    @SerializedName("file_path") val filePath: String?,
    @SerializedName("original_name") val originalName: String?,
    @SerializedName("mime_type") val mimeType: String?,
    @SerializedName("size_bytes") val sizeBytes: Long?,
    @SerializedName("upload_date") val uploadDate: String?
)

//10.	изменение данных пользователя
data class UpdateUserProfileRequest(
    @SerializedName("p_id") val userId: Long,
    @SerializedName("p_mail") val mail: String,
    @SerializedName("p_last_name") val lastName: String,
    @SerializedName("p_first_name") val firstName: String,
    @SerializedName("p_patronymic") val patronymic: String,
    @SerializedName("p_file_type") val fileType: String? = null,
    @SerializedName("p_file_path") val filePath: String? = null,
    @SerializedName("p_original_name") val originalName: String? = null,
    @SerializedName("p_mime_type") val mimeType: String? = null,
    @SerializedName("p_size_bytes") val sizeBytes: Long? = null
)

//11.	изменение пароля
data class UpdateUserPasswordRequest(
    @SerializedName("p_id") val userId: Long,
    @SerializedName("p_password") val currentPassword: String,
    @SerializedName("p_new_password") val newPassword: String
)

//12.	просмотр уведомлений
data class NotificationResponse(
    @SerializedName("heading_notification") val headingNotification: String,
    @SerializedName("date_create") val dateCreate: String
)

//13.	просмотр конкретного уведомления
data class NotificationDetailResponse(
    @SerializedName("heading_notification") val headingNotification: String,
    @SerializedName("text_notification") val textNotification: String,
    @SerializedName("date_create") val dateCreate: String
)

//14.	просмотр всех курсов
data class CourseResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("date_publication") val datePublication: String,
    @SerializedName("category_name") val categoryName: String,
    @SerializedName("teacher_last_name") val teacherLastName: String,
    @SerializedName("teacher_first_name") val teacherFirstName: String,
    @SerializedName("teacher_patronymic") val teacherPatronymic: String
)

//15.	просмотр конкретного курса
data class CourseDetailResponse(
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("date_publication") val datePublication: String,
    @SerializedName("category_name") val categoryName: String,
    @SerializedName("teacher_last_name") val teacherLastName: String,
    @SerializedName("teacher_first_name") val teacherFirstName: String,
    @SerializedName("teacher_patronymic") val teacherPatronymic: String
)

//16.	просмотр курсов
data class UserCourseRequest(
    @SerializedName("p_user_id") val userId: Long,
    @SerializedName("p_status_name") val statusName: String
)
data class UserCourseResponse(
    @SerializedName("course_id") val courseId: Long,
    @SerializedName("course_name") val courseName: String,
    @SerializedName("date_publication") val datePublication: String,
    @SerializedName("category_name") val categoryName: String,
    @SerializedName("teacher_last_name") val teacherLastName: String,
    @SerializedName("teacher_first_name") val teacherFirstName: String,
    @SerializedName("teacher_patronymic") val teacherPatronymic: String,
    @SerializedName("date_finishing") val dateFinishing: String?
)

//17.	просмотр уроков курса
data class LessonResponse(
    @SerializedName("lesson_id") val lessonId: Long,
    @SerializedName("lesson_name") val lessonName: String,
    @SerializedName("date_publication") val datePublication: String
)

//18.	просмотр урока курса
data class LessonDetailResponse(
    @SerializedName("lesson_name") val lessonName: String,
    @SerializedName("lesson_description") val lessonDescription: String,
    @SerializedName("date_publication") val datePublication: String
)

//19.	просмотр шагов урока
data class StepResponse(
    @SerializedName("step_id") val stepId: Long,
    @SerializedName("step_name") val stepName: String,
    @SerializedName("obligatory") val obligatory: Boolean,
    @SerializedName("date_publication") val datePublication: String
)

//20.	просмотр шага урока
data class StepDetailResponse(
    @SerializedName("step_name") val stepName: String,
    @SerializedName("step_content") val stepContent: String,
    @SerializedName("sequence_number") val sequenceNumber: Long,
    @SerializedName("time_passes") val timePasses: String,
    @SerializedName("obligatory") val obligatory: Boolean,
    @SerializedName("date_publication") val datePublication: String,
    @SerializedName("step_type_name") val stepTypeName: String,
    @SerializedName("file_id") val fileId: Long?,
    @SerializedName("file_type") val fileType: String?,
    @SerializedName("file_path") val filePath: String?,
    @SerializedName("original_name") val originalName: String?,
    @SerializedName("mime_type") val mimeType: String?,
    @SerializedName("size_bytes") val sizeBytes: Long?,
    @SerializedName("upload_date") val uploadDate: String?
)

//21.	просмотр статистики по дням
data class DailyStatisticsResponse(
    @SerializedName("date_day") val dateDay: String,
    @SerializedName("answers_count") val answersCount: Long
)

//22.	просмотр курсов, которые создал учитель
data class CourseByTeacherResponse(
    @SerializedName("course_id") val courseId: Long,
    @SerializedName("course_name") val courseName: String,
    @SerializedName("date_publication") val datePublication: String,
    @SerializedName("course_category_name") val courseCategoryName: String
)

//23.	создание курса
data class CreateCourseRequest(
    @SerializedName("p_course_category_id") val courseCategoryId: Long,
    @SerializedName("p_user_id") val userId: Long,
    @SerializedName("p_name") val name: String,
    @SerializedName("p_description") val description: String
)

//24.	просмотр категорий курсов
data class CourseCategory(
    @SerializedName("category_id") val categoryId: Long,
    @SerializedName("category_name") val categoryName: String,
    @SerializedName("category_description") val categoryDescription: String
)

//25.	создание урока
data class CreateLessonRequest(
    @SerializedName("course_id") val courseId: Long,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("sequence_number") val sequenceNumber: Long
)

//26.	изменение курса
data class UpdateCourseRequest(
    @SerializedName("course_id") val courseId: Long,
    @SerializedName("course_category_id") val courseCategoryId: Long,
    @SerializedName("user_id") val userId: Long,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String
)

//27.	изменение урока
data class UpdateLessonRequest(
    @SerializedName("lesson_id") val lessonId: Long,
    @SerializedName("course_id") val courseId: Long,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("sequence_number") val sequenceNumber: Long
)

//28.	удаление курса
data class DeleteCourseRequest(
    @SerializedName("course_id") val courseId: Long,
    @SerializedName("user_id") val userId: Long
)

//29.	удаление урока
data class DeleteLessonRequest(
    @SerializedName("lesson_id") val lessonId: Long,
    @SerializedName("user_id") val userId: Long
)

//30.	удаление шага
data class DeleteStepRequest(
    @SerializedName("step_id") val stepId: Long,
    @SerializedName("user_id") val userId: Long
)

//31.	просмотр ответов на шаг
data class AnswersForStepResponse(
    @SerializedName("answer_id") val answerId: Long,
    @SerializedName("student_last_name") val studentLastName: String,
    @SerializedName("student_first_name") val studentFirstName: String,
    @SerializedName("student_patronymic") val studentPatronymic: String,
    @SerializedName("answer_date") val answerDate: String
)

//32.	просмотр ответа на шаг
data class AnswerForStepResponse(
    @SerializedName("student_last_name") val studentLastName: String,
    @SerializedName("student_first_name") val studentFirstName: String,
    @SerializedName("student_patronymic") val studentPatronymic: String,
    @SerializedName("answer_text") val answerText: String,
    @SerializedName("comment_student") val commentStudent: String?,
    @SerializedName("comment_teacher") val commentTeacher: String?,
    @SerializedName("score") val score: Long,
    @SerializedName("option_text") val optionText: List<String>,
    @SerializedName("is_correct") val isCorrect: List<Boolean>,
    @SerializedName("option_score") val optionScore: List<Long>,
    @SerializedName("file_path") val filePath: List<String>
)

//33.	оценить ответ на шаг
data class EvaluateAnswerOnStepRequest(
    @SerializedName("answer_user_id") val answerUserId: Long,
    @SerializedName("score") val score: Long,
    @SerializedName("comment_teacher") val commentTeacher: String? = null
)

//34.	просмотр статистики на курсе
data class ViewCourseStatisticsResponse(
    @SerializedName("student_full_name") val studentFullName: String,
    @SerializedName("completed_tasks_count") val completedTasksCount: Long,
    @SerializedName("average_score") val averageScore: Double
)

//35.	новые пользователи
data class NewUserResponse(
    @SerializedName("user_id") val userId: Long,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("patronymic") val patronymic: String,
    @SerializedName("date_registration") val dateRegistration: String
)

//36.	просмотр всех пользователей
data class AllUserResponse(
    @SerializedName("user_id") val userId: Long,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("patronymic") val patronymic: String,
    @SerializedName("date_registration") val dateRegistration: String
)

//37.	выдать предупреждение пользователю
//38.	удалить страницу пользователя
//39.	выдать предупреждение на курс
//40.	выдать предупреждение на шаг урока


//41.	поступление/отложить курс
data class EnrollOrDeferCourseRequest(
    @SerializedName("p_user_id") val userId: Long,
    @SerializedName("p_course_id") val courseId: Long,
    @SerializedName("p_status_name") val statusName: String
)

//42.	просмотр своих сертификатов
data class UserCertificatesResponse(
    @SerializedName("course_name") val courseName: String,
    @SerializedName("sertificate_id") val sertificateId: Long,
    @SerializedName("upload_date") val uploadDate: String
)

//43.	просмотр конкретного сертификата	???


//44.	ответ на шаг урока
data class AnswerLessonStepRequest(
    @SerializedName("user_id") val userId: Long,
    @SerializedName("step_lesson_id") val stepLessonId: Long,
    @SerializedName("answer_text") val answerText: String? = null,
    @SerializedName("selected_option_ids") val selectedOptionIds: List<Long>? = null,
    @SerializedName("file_path") val filePath: String? = null,
    @SerializedName("original_name") val originalName: String? = null,
    @SerializedName("mime_type") val mimeType: String? = null,
    @SerializedName("size_bytes") val sizeBytes: Long? = null,
    @SerializedName("comment_student") val commentStudent: String? = null
)

//45.	изменение ответа на шаг урока
data class UpdateLessonStepAnswerRequest(
    @SerializedName("answer_id") val answerId: Long,
    @SerializedName("user_id") val userId: Long,
    @SerializedName("step_lesson_id") val stepLessonId: Long,
    @SerializedName("answer_text") val answerText: String? = null,
    @SerializedName("selected_option_ids") val selectedOptionIds: List<Long>? = null,
    @SerializedName("file_path") val filePath: String? = null,
    @SerializedName("original_name") val originalName: String? = null,
    @SerializedName("mime_type") val mimeType: String? = null,
    @SerializedName("size_bytes") val sizeBytes: Long? = null,
    @SerializedName("comment_student") val commentStudent: String? = null
)

//46.1.	создание шага «Лекция»
data class CreateLectureStepRequest(
    @SerializedName("lesson_id") val lessonId: Long,
    @SerializedName("name") val name: String,
    @SerializedName("content") val content: String,
    @SerializedName("sequence_number") val sequenceNumber: Long,
    @SerializedName("obligatory") val obligatory: Boolean,
    @SerializedName("file_path") val filePath: String? = null,
    @SerializedName("original_name") val originalName: String? = null,
    @SerializedName("mime_type") val mimeType: String? = null,
    @SerializedName("size_bytes") val sizeBytes: Long? = null
)

//46.2.	создание шага «Вопрос без вариантов ответа»
data class CreateOpenQuestionStepRequest(
    @SerializedName("lesson_id") val lessonId: Long,
    @SerializedName("name") val name: String,
    @SerializedName("content") val content: String,
    @SerializedName("sequence_number") val sequenceNumber: Long,
    @SerializedName("time_passes") val timePasses: String, // Время в формате HH:MM:SS
    @SerializedName("obligatory") val obligatory: Boolean,
    @SerializedName("max_score") val maxScore: Long? = null
)

//46.3.	создание шага «Вопрос с вариантами ответа»
data class CreateMultipleChoiceQuestionStepRequest(
    @SerializedName("lesson_id") val lessonId: Long,
    @SerializedName("name") val name: String,
    @SerializedName("content") val content: String,
    @SerializedName("sequence_number") val sequenceNumber: Long,
    @SerializedName("time_passes") val timePasses: String, // Время в формате HH:MM:SS
    @SerializedName("obligatory") val obligatory: Boolean,
    @SerializedName("max_score") val maxScore: Long,
    @SerializedName("text_options") val textOptions: List<String>,
    @SerializedName("correct") val correct: List<Boolean>,
    @SerializedName("scores") val scores: List<Long>
)

//46.4.	создание шага «Вопрос с приложением»
data class CreateFileUploadQuestionStepRequest(
    @SerializedName("lesson_id") val lessonId: Long,
    @SerializedName("name") val name: String,
    @SerializedName("content") val content: String,
    @SerializedName("sequence_number") val sequenceNumber: Long,
    @SerializedName("obligatory") val obligatory: Boolean,
    @SerializedName("max_score") val maxScore: Long,
    @SerializedName("file_path") val filePath: String,
    @SerializedName("original_name") val originalName: String,
    @SerializedName("mime_type") val mimeType: String,
    @SerializedName("size_bytes") val sizeBytes: Long
)

//47.1.	изменение шага «Лекция»
data class UpdateLectureStepRequest(
    @SerializedName("step_id") val stepId: Long,
    @SerializedName("name") val name: String,
    @SerializedName("content") val content: String,
    @SerializedName("sequence_number") val sequenceNumber: Long,
    @SerializedName("obligatory") val obligatory: Boolean,
    @SerializedName("file_path") val filePath: String? = null,
    @SerializedName("original_name") val originalName: String? = null,
    @SerializedName("mime_type") val mimeType: String? = null,
    @SerializedName("size_bytes") val sizeBytes: Long? = null
)

//47.2.	изменение шага «Вопрос без вариантов ответа»
data class UpdateOpenQuestionStepRequest(
    @SerializedName("step_id") val stepId: Long,
    @SerializedName("name") val name: String,
    @SerializedName("content") val content: String,
    @SerializedName("sequence_number") val sequenceNumber: Long,
    @SerializedName("time_passes") val timePasses: String,  // Time format: HH:MM:SS
    @SerializedName("obligatory") val obligatory: Boolean,
    @SerializedName("max_score") val maxScore: Long
)

//47.3.	изменение шага «Вопрос с вариантами ответа»
data class UpdateMultipleChoiceQuestionStepRequest(
    @SerializedName("step_id") val stepId: Long,
    @SerializedName("name") val name: String,
    @SerializedName("content") val content: String,
    @SerializedName("sequence_number") val sequenceNumber: Long,
    @SerializedName("time_passes") val timePasses: String,  // Time format: HH:MM:SS
    @SerializedName("obligatory") val obligatory: Boolean,
    @SerializedName("max_score") val maxScore: Long,
    @SerializedName("text_options") val textOptions: List<String>,
    @SerializedName("correct") val correct: List<Boolean>,
    @SerializedName("scores") val scores: List<Long>
)

//47.4.	изменение шага «Вопрос с приложением»
data class UpdateFileUploadQuestionStepRequest(
    @SerializedName("step_id") val stepId: Long,
    @SerializedName("name") val name: String,
    @SerializedName("content") val content: String,
    @SerializedName("sequence_number") val sequenceNumber: Long,
    @SerializedName("obligatory") val obligatory: Boolean,
    @SerializedName("max_score") val maxScore: Long,
    @SerializedName("file_path") val filePath: String,
    @SerializedName("original_name") val originalName: String,
    @SerializedName("mime_type") val mimeType: String,
    @SerializedName("size_bytes") val sizeBytes: Long
)

//48.	добавить категории курсов
data class AddCourseCategoryRequest(
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String
)

//49.	изменить категорию курсов
data class UpdateCourseCategoryRequest(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String
)

//50.	удалить категорию курсов


//51.	добавить темы обращений
data class AddTopicAppealRequest(
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String
)

//52.	изменить тему обращений
data class UpdateTopicAppealRequest(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String
)

//53.	удалить тему обращения


//54.	зарегистрировать преподавателя
data class RegisterTeacherRequest(
    @SerializedName("login") val login: String,
    @SerializedName("mail") val mail: String,
    @SerializedName("password") val password: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("patronymic") val patronymic: String
)

//55.	Общая статистика платформы
data class GetPlatformStatisticsResponse(
    @SerializedName("total_users") val totalUsers: Long,
    @SerializedName("active_users_7d") val activeUsers7d: Long,
    @SerializedName("new_users_today") val newUsersToday: Long,
    @SerializedName("total_courses") val totalCourses: Long,
    @SerializedName("published_courses") val publishedCourses: Long,
    @SerializedName("total_appeals") val totalAppeals: Long,
    @SerializedName("open_appeals") val openAppeals: Long,
    @SerializedName("course_enrollments") val courseEnrollments: Long,
    @SerializedName("completed_courses") val completedCourses: Long,
    @SerializedName("completion_rate") val completionRate: Double
)

//56.	Активность пользователей
data class UserActivityStatsItemResponse(
    @SerializedName("day") val day: String, // Дата в формате "YYYY-MM-DD"
    @SerializedName("active_users") val activeUsers: Long,
    @SerializedName("new_users") val newUsers: Long,
    @SerializedName("answered_questions") val answeredQuestions: Long,
    @SerializedName("new_enrollments") val newEnrollments: Long
)

//57.	Статистика по обращениям
data class AppealStatisticsItemResponse(
    @SerializedName("status") val status: String,
    @SerializedName("appeal_count") val appealCount: Long,
    @SerializedName("avg_response_hours") val avgResponseHours: Double,
    @SerializedName("overdue_appeals") val overdueAppeals: Long,
    @SerializedName("resolution_rate") val resolutionRate: Double,
    @SerializedName("most_common_topic") val mostCommonTopic: String?,
    @SerializedName("avg_files_per_appeal") val avgFilesPerAppeal: Double
)

//58.	выдать сертификат студенту	???
//59.	скачать сертификат	???
