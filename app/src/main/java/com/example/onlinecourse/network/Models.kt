package com.example.onlinecourse.network

import com.google.gson.annotations.SerializedName

data class LongResponse(
    val id: Long
)

// 1.	авторизация
data class AuthorizeResponse(
    @SerializedName("user_id") val userId: Long,
    @SerializedName("role_name") val roleName: String?
)

//3.	просмотр своих обращений
data class UserAppealsResponse(
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
    @SerializedName("patronymic") val patronymic: String?,
    @SerializedName("date_registration") val dateRegistration: String,
    @SerializedName("file_path") val filePath: String?,
    @SerializedName("original_name") val originalName: String?,
    @SerializedName("mime_type") val mimeType: String?,
    @SerializedName("size_bytes") val sizeBytes: Long?,
    @SerializedName("upload_date") val uploadDate: String?
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
    @SerializedName("teacher_patronymic") val teacherPatronymic: String?
)

//15.	просмотр конкретного курса
data class CourseDetailResponse(
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("date_publication") val datePublication: String,
    @SerializedName("category_name") val categoryName: String,
    @SerializedName("teacher_last_name") val teacherLastName: String,
    @SerializedName("teacher_first_name") val teacherFirstName: String,
    @SerializedName("teacher_patronymic") val teacherPatronymic: String?
)

//16.	просмотр курсов
data class UserCourseResponse(
    @SerializedName("course_id") val courseId: Long,
    @SerializedName("course_name") val courseName: String,
    @SerializedName("date_publication") val datePublication: String,
    @SerializedName("category_name") val categoryName: String,
    @SerializedName("teacher_last_name") val teacherLastName: String,
    @SerializedName("teacher_first_name") val teacherFirstName: String,
    @SerializedName("teacher_patronymic") val teacherPatronymic: String?,
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
data class StepsByLessons(
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
    @SerializedName("upload_date") val uploadDate: String?,
    @SerializedName("answer_option_ids") val answerOptionIds: List<Long>?,
    @SerializedName("answer_option_texts") val answerOptionTexts: List<String>?,
    @SerializedName("answer_option_scores") val answerOptionScores: List<Long>?,
    @SerializedName("user_answer_id") val userAnswerId: Long?,
    @SerializedName("user_answer_text") val userAnswerText: String?,
    @SerializedName("user_score") val userScore: Long?,
    @SerializedName("user_comment_student") val userCommentStudent: String?,
    @SerializedName("user_comment_ticher") val userCommentTeacher: String?,
    @SerializedName("user_date_answer") val userDateAnswer: String?
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

//24.	просмотр категорий курсов
data class CourseCategory(
    @SerializedName("category_id") val categoryId: Long,
    @SerializedName("category_name") val categoryName: String,
    @SerializedName("category_description") val categoryDescription: String
)

//31.	просмотр ответов на шаг
data class AnswersForStepResponse(
    @SerializedName("answer_id") val answerId: Long,
    @SerializedName("student_last_name") val studentLastName: String?,
    @SerializedName("student_first_name") val studentFirstName: String?,
    @SerializedName("student_patronymic") val studentPatronymic: String?,
    @SerializedName("answer_date") val answerDate: String?
)

//32.	просмотр ответа на шаг
data class AnswerForStepResponse(
    @SerializedName("student_last_name") val studentLastName: String?,
    @SerializedName("student_first_name") val studentFirstName: String?,
    @SerializedName("student_patronymic") val studentPatronymic: String?,
    @SerializedName("answer_text") val answerText: String?,
    @SerializedName("comment_student") val commentStudent: String?,
    @SerializedName("comment_teacher") val commentTeacher: String?,
    @SerializedName("score") val score: Long?,
    @SerializedName("option_text") val optionText: List<String>?,
    @SerializedName("is_correct") val isCorrect: List<Boolean>?,
    @SerializedName("option_score") val optionScore: List<Long>?,
    @SerializedName("file_path") val filePath: List<String>?
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
    @SerializedName("patronymic") val patronymic: String?,
    @SerializedName("date_registration") val dateRegistration: String
)

//36.	просмотр всех пользователей
data class AllUserResponse(
    @SerializedName("user_id") val userId: Long,
    @SerializedName("last_name") val lastName: String?,
    @SerializedName("first_name") val firstName: String?,
    @SerializedName("patronymic") val patronymic: String?,
    @SerializedName("date_registration") val dateRegistration: String?
)

//42.	просмотр своих сертификатов
data class UserCertificatesResponse(
    @SerializedName("course_name") val courseName: String,
    @SerializedName("sertificate_id") val sertificateId: Long,
    @SerializedName("upload_date") val uploadDate: String
)

//43.	просмотр конкретного сертификата
data class ViewCertificate(
    @SerializedName("file_path") val filePath: String,
    @SerializedName("original_name") val originalName: String,
    @SerializedName("mime_type") val mimeType: String
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
    @SerializedName("day") val day: String,
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
    @SerializedName("most_common_topic") val mostCommonTopic: String,
    @SerializedName("avg_files_per_appeal") val avgFilesPerAppeal: Double
)

//58.	выдать сертификат студенту
data class IssueCertificateResponse(
    @SerializedName("student_name") val studentName: String,
    @SerializedName("course_name") val courseName: String,
    @SerializedName("teacher_name") val teacherName: String,
    @SerializedName("average_score") val averageScore: Double,
    @SerializedName("completion_percent") val completionPercent: Double,
    @SerializedName("issue_date") val issueDate: String,
    @SerializedName("cert_id") val certId: Long
)