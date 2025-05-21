package com.example.onlinecourse.network

import RetrofitClient
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Response

//главная страница
class DailyStatisticsViewModel : ViewModel() {
    var isLoading by mutableStateOf(false)
        private set
    var dailyStatistics by mutableStateOf<List<DailyStatisticsResponse>>(emptyList())
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun loadDailyStatistics(userId: Long) {
        viewModelScope.launch {
            isLoading = true
            try {
                dailyStatistics = RetrofitClient.instance.getDailyStatistics(userId)
                errorMessage = null
            } catch (e: Exception) {
                errorMessage = "Ошибка при загрузке статистики по дням: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}

// регистрация
class RegisterUserViewModel : ViewModel() {
    var isLoading by mutableStateOf(false)
        private set
    var registrationResult by mutableStateOf<String?>(null)
        private set
    var userId by mutableStateOf<Long?>(null)
        private set

    fun registerUser(
        login: String,
        email: String,
        password: String,
        lastName: String,
        firstName: String,
        patronymic: String,
        fileType: String? = null,
        originalName: String? = null,
        mimeType: String? = null,
        sizeBytes: String? = null,
        file: MultipartBody.Part? = null,
        onSuccess: (Long) -> Unit
    ) {
        viewModelScope.launch {
            isLoading = true
            try {
                val rb = { str: String -> str.toRequestBody("text/plain".toMediaTypeOrNull()) }

                val responseId = RetrofitClient.instance.registerUser(
                    login = rb(login),
                    email = rb(email),
                    password = rb(password),
                    lastName = rb(lastName),
                    firstName = rb(firstName),
                    patronymic = rb(patronymic),
                    fileType = fileType?.toRequestBody("text/plain".toMediaTypeOrNull()),
                    originalName = originalName?.toRequestBody("text/plain".toMediaTypeOrNull()),
                    mimeType = mimeType?.toRequestBody("text/plain".toMediaTypeOrNull()),
                    sizeBytes = sizeBytes?.toRequestBody("text/plain".toMediaTypeOrNull()),
                    file = file
                ).id

                userId = responseId
                onSuccess(userId!!)
            } catch (e: Exception) {
                registrationResult = "Ошибка регистрации: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}

// авторизация
class LoginViewModel : ViewModel() {
    var isLoading by mutableStateOf(false)
        private set
    var loginResult by mutableStateOf<String?>(null)
        private set
    var userId by mutableStateOf<Long?>(null)
        private set
    var roleName by mutableStateOf<String?>(null)
        private set

    fun clearError() {
        loginResult = null
    }

    fun login(
        login: String,
        password: String,
        onSuccess: (userId: Long, roleName: String) -> Unit
    ) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.instance.login(login, password)
                userId = response.userId
                roleName = response.roleName
                loginResult = "Вход выполнен"
                if (userId != null && roleName != null) {
                    onSuccess(userId!!, roleName!!)
                } else {
                    loginResult = "Неверный логин или пароль"
                }
            } catch (e: Exception) {
                loginResult = "Ошибка авторизации: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}

// изменение пароля
class ChangePasswordViewModel : ViewModel() {
    var newPassword = mutableStateOf("")
        private set
    var currentPassword = mutableStateOf("")
        private set
    var errorMessage = mutableStateOf("")
        private set
    var successMessage = mutableStateOf("")
        private set
    var isLoading = mutableStateOf(false)
        private set
    var passwordUpdated = mutableStateOf(false)
        private set

    fun onNewPasswordChanged(newPass: String) {
        newPassword.value = newPass
    }

    fun currentPasswordChanged(currentPass: String) {
        currentPassword.value = currentPass
    }

    fun updateUserPassword(userId: Long, currentPassword: String, newPassword: String) {
        errorMessage.value = ""
        successMessage.value = ""
        passwordUpdated.value = false

        viewModelScope.launch {
            isLoading.value = true
            try {
                val result = RetrofitClient.instance.updateUserPassword(
                    userId = userId,
                    currentPassword = currentPassword,
                    newPassword = newPassword
                )
                if (result.body() == true) {
                    successMessage.value = "Пароль успешно обновлён"
                    passwordUpdated.value = true
                } else {
                    errorMessage.value = "Не удалось обновить пароль. Проверьте текущий пароль"
                    passwordUpdated.value = false
                }
            } catch (e: Exception) {
                errorMessage.value = "Ошибка при смене пароля: ${e.message}"
                passwordUpdated.value = false
                Log.e("ChangePasswordViewModel", "Ошибка смены пароля", e)
            } finally {
                isLoading.value = false
            }
        }
    }
}

// просмотр всех уведомлений
class UserNotificationsViewModel : ViewModel() {
    var isLoading by mutableStateOf(false)
        private set
    var notifications by mutableStateOf<List<NotificationResponse>>(emptyList())
        private set
    var loadResult by mutableStateOf<String?>(null)
        private set

    fun loadNotifications(userId: Long) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.instance.getUserNotifications(userId)
                notifications = response
                loadResult = if (response.isNotEmpty()) {
                    "Уведомления загружены: ${response.size}"
                } else {
                    "Уведомлений нет"
                }
                Log.d("UserNotificationsViewModel", "Загружено уведомлений: ${response.size}")
            } catch (e: Exception) {
                loadResult = "Ошибка загрузки уведомлений: ${e.message}"
                Log.e("UserNotificationsViewModel", "Ошибка загрузки", e)
            } finally {
                isLoading = false
            }
        }
    }
}

// просмотр конкретного уведомления
class NotificationDetailViewModel : ViewModel() {
    var isLoading by mutableStateOf(false)
        private set
    var notificationDetail by mutableStateOf<NotificationDetailResponse?>(null)
        private set
    var loadResult by mutableStateOf<String?>(null)
        private set

    fun loadNotificationDetail(notifId: Long) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.instance.getNotificationDetails(notifId)
                if (response.isNotEmpty()) {
                    notificationDetail = response[0]
                    loadResult = "Детали уведомления загружены"
                } else {
                    loadResult = "Уведомление не найдено"
                    notificationDetail = null
                }
                Log.d("NotificationDetailViewModel", "Загружено уведомление id=$notifId")
            } catch (e: Exception) {
                loadResult = "Ошибка загрузки уведомления: ${e.message}"
                Log.e("NotificationDetailViewModel", "Ошибка загрузки", e)
                notificationDetail = null
            } finally {
                isLoading = false
            }
        }
    }
}

// зарегистрировать преподавателя
class RegisterTeacherViewModel : ViewModel() {
    var isLoading by mutableStateOf(false)
        private set
    var registrationResult by mutableStateOf<String?>(null)
        private set

    fun registerTeacher(
        login: String,
        mail: String,
        password: String,
        lastName: String,
        firstName: String,
        patronymic: String,
        onSuccess: (Long) -> Unit
    ) {
        viewModelScope.launch {
            isLoading = true
            try {
                val userId = RetrofitClient.instance.registerTeacher(
                    login = login,
                    mail = mail,
                    password = password,
                    lastName = lastName,
                    firstName = firstName,
                    patronymic = patronymic
                )
                registrationResult = "Преподаватель успешно зарегистрирован. ID: $userId"
                onSuccess(userId.id)
                Log.d("RegisterTeacherViewModel", "Преподаватель зарегистрирован с ID: $userId")
            } catch (e: Exception) {
                registrationResult = "Ошибка регистрации: ${e.message}"
                Log.e("RegisterTeacherViewModel", "Ошибка регистрации", e)
            } finally {
                isLoading = false
            }
        }
    }
}

//просмотр страницы пользователя
class UserProfileViewModel : ViewModel() {
    var isLoading by mutableStateOf(false)
        private set
    var userProfile by mutableStateOf<UserProfileResponse?>(null)
        private set
    var fileResponse by mutableStateOf<Response<ResponseBody>?>(null)
        private set
    var deletionResult by mutableStateOf<String?>(null)
        private set
    var updateResult by mutableStateOf<String?>(null)
        private set
    var warningResult by mutableStateOf<String?>(null)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set
    var saveResult by mutableStateOf<Boolean?>(null)
        private set
    fun clearSaveResult() {
        saveResult = null
    }

    fun loadUserProfile(userId: Long) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.instance.getUserProfile(userId)
                userProfile = response.firstOrNull()
                if (userProfile != null) {
                    errorMessage = null
                    Log.d("UserProfileViewModel", "Профиль пользователя загружен")
                } else {
                    errorMessage = "Профиль пользователя не найден"
                }
            } catch (e: Exception) {
                errorMessage = "Ошибка загрузки профиля: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun downloadFile(filePath: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.instance.getFile(filePath)
                fileResponse = response
                if (!response.isSuccessful) {
                    errorMessage = "Ошибка при загрузке файла: ${response.message()}"
                }
            } catch (e: Exception) {
                errorMessage = "Ошибка при получении файла: ${e.message}"
                Log.e("UserProfileViewModel", "Ошибка загрузки файла", e)
            } finally {
                isLoading = false
            }
        }
    }

    fun deleteUser(userId: Long, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            try {
                val result = RetrofitClient.instance.deleteUser(userId)
                if (result.body() == true) {
                    deletionResult = "Пользователь удалён"
                    onSuccess()
                } else {
                    deletionResult = "Не удалось удалить пользователя"
                }
            } catch (e: Exception) {
                deletionResult = "Ошибка при удалении: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun updateUserProfile(
        userId: Long,
        email: String,
        lastName: String,
        firstName: String,
        patronymic: String,
        fileType: String? = null,
        originalName: String? = null,
        mimeType: String? = null,
        sizeBytes: Long? = null,
        file: MultipartBody.Part? = null
    ) {
        viewModelScope.launch {
            try {
                isLoading = true
                val rb = { str: String -> str.toRequestBody("text/plain".toMediaTypeOrNull()) }

                val response = RetrofitClient.instance.updateUserProfile(
                    userId = rb(userId.toString()),
                    email = rb(email),
                    lastName = rb(lastName),
                    firstName = rb(firstName),
                    patronymic = rb(patronymic),
                    fileType = fileType?.toRequestBody("text/plain".toMediaTypeOrNull()),
                    originalName = originalName?.toRequestBody("text/plain".toMediaTypeOrNull()),
                    mimeType = mimeType?.toRequestBody("text/plain".toMediaTypeOrNull()),
                    sizeBytes = sizeBytes.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                    file = file
                )
                saveResult = response.body() == true
            } catch (e: Exception) {
                saveResult = false
                errorMessage = "Ошибка при обновлении: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun issueWarning(userId: Long) {
        viewModelScope.launch {
            isLoading = true
            try {
                val result = RetrofitClient.instance.issueWarning(userId)
                warningResult = if (result.body() == true) {
                    "Предупреждение успешно выдано"
                } else {
                    "Не удалось выдать предупреждение"
                }
            } catch (e: Exception) {
                warningResult = "Ошибка при выдаче предупреждения: ${e.message}"
                Log.e("UserProfileViewModel", "Ошибка предупреждения", e)
            } finally {
                isLoading = false
            }
        }
    }
}

// обращения
class AppealViewModel : ViewModel() {
    var isLoading by mutableStateOf(false)
        private set
    var userAppeals by mutableStateOf<List<UserAppealsResponse>>(emptyList())
        private set
    var allAppeals by mutableStateOf<List<AllAppealResponse>>(emptyList())
        private set
    var appealDetail by mutableStateOf<AppealDetailResponse?>(null)
        private set
    var topics by mutableStateOf<List<AppealTopicResponse>>(emptyList())
        private set
    var operationSuccess by mutableStateOf<Boolean?>(null)
    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun loadUserAppeals(userId: Long) {
        viewModelScope.launch {
            isLoading = true
            try {
                userAppeals = RetrofitClient.instance.getUserAppeals(userId)
                errorMessage = null
            } catch (e: Exception) {
                errorMessage = "Ошибка при загрузке обращений: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun loadAppealDetail(appealId: Long) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.instance.getAppealDetail(appealId)
                appealDetail = response.firstOrNull()
                errorMessage = null
            } catch (e: Exception) {
                errorMessage = "Ошибка при получении деталей обращения: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun loadTopics() {
        viewModelScope.launch {
            isLoading = true
            try {
                topics = RetrofitClient.instance.getTopicsAppeals()
                errorMessage = null
            } catch (e: Exception) {
                errorMessage = "Ошибка при загрузке тем: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun loadAllAppeals() {
        viewModelScope.launch {
            isLoading = true
            try {
                allAppeals = RetrofitClient.instance.getAllAppeals()
                errorMessage = null
            } catch (e: Exception) {
                errorMessage = "Ошибка при загрузке всех обращений: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun submitAppeal(
        userId: Long,
        topicAppealId: Long,
        heading: String,
        text: String,
        fileType: String? = null,
        originalName: String? = null,
        mimeType: String? = null,
        sizeBytes: Int? = null,
        file: MultipartBody.Part? = null
    ) {
        viewModelScope.launch {
            isLoading = true
            try {
                val rb = { str: String -> str.toRequestBody("text/plain".toMediaTypeOrNull()) }

                operationSuccess = RetrofitClient.instance.addAppealWithFile(
                    rb(userId.toString()),
                    rb(topicAppealId.toString()),
                    rb(heading),
                    rb(text),
                    fileType?.let { rb(it) },
                    originalName?.let { rb(it) },
                    mimeType?.let { rb(it) },
                    sizeBytes?.toString()?.let { rb(it) },
                    file
                ).body()
                errorMessage = null
            } catch (e: Exception) {
                errorMessage = "Ошибка при отправке обращения: ${e.message}"
                operationSuccess = false
            } finally {
                isLoading = false
            }
        }
    }

    fun submitAnswer(appealId: Long, userId: Long, text: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                operationSuccess = RetrofitClient.instance.addAnswerToAppeal(
                    appealId,
                    userId,
                    text
                ).body()
                errorMessage = null
            } catch (e: Exception) {
                errorMessage = "Ошибка при отправке ответа: ${e.message}"
                operationSuccess = false
            } finally {
                isLoading = false
            }
        }
    }
}

//просмотр всех курсов
class CourseDataViewModel : ViewModel() {
    var isLoading by mutableStateOf(false)
        private set
    var courseCategories by mutableStateOf<List<CourseCategory>>(emptyList())
        private set
    var coursesByTeacher by mutableStateOf<List<CourseByTeacherResponse>>(emptyList())
        private set
    var allCourses by mutableStateOf<List<CourseResponse>>(emptyList())
        private set
    var coursesByUserAndStatus by mutableStateOf<List<UserCourseResponse>>(emptyList())
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun loadCourseCategories() {
        viewModelScope.launch {
            isLoading = true
            try {
                courseCategories = RetrofitClient.instance.getCourseCategories()
                errorMessage = null
            } catch (e: Exception) {
                errorMessage = "Ошибка загрузки категорий курсов: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun loadCoursesByTeacher(teacherId: Long) {
        viewModelScope.launch {
            isLoading = true
            try {
                coursesByTeacher = RetrofitClient.instance.getCoursesByTeacher(teacherId)
                errorMessage = null
            } catch (e: Exception) {
                errorMessage = "Ошибка загрузки курсов учителя: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun loadAllCourses() {
        viewModelScope.launch {
            isLoading = true
            try {
                allCourses = RetrofitClient.instance.getAllCourses()
                errorMessage = null
            } catch (e: Exception) {
                errorMessage = "Ошибка загрузки всех курсов: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun loadCoursesByUserAndStatus(userId: Long, statusName: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                coursesByUserAndStatus = RetrofitClient.instance.getCoursesByUserAndStatus(userId, statusName)
                errorMessage = null
            } catch (e: Exception) {
                errorMessage = "Ошибка загрузки курсов пользователя по статусу: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}

//просмотр курса
class CourseDetailsViewModel : ViewModel() {
    var isLoading by mutableStateOf(false)
        private set
    var courseDetail by mutableStateOf<CourseDetailResponse?>(null)
        private set
    var lessons by mutableStateOf<List<LessonResponse>>(emptyList())
        private set
    var enrollmentResult by mutableStateOf<String?>(null)
        private set
    var updateResult by mutableStateOf<String?>(null)
        private set
    var warningResult by mutableStateOf<String?>(null)
        private set
    var deleteResult by mutableStateOf<String?>(null)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun loadCourseDetails(courseId: Long) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.instance.getCourseDetails(courseId)
                courseDetail = response.firstOrNull()
                if (courseDetail != null) {
                    errorMessage = null
                } else {
                    errorMessage = "Курс не найден"
                }
            } catch (e: Exception) {
                errorMessage = "Ошибка при загрузке курса: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun loadLessons(courseId: Long) {
        viewModelScope.launch {
            isLoading = true
            try {
                lessons = RetrofitClient.instance.getLessonsByCourse(courseId)
                errorMessage = null
            } catch (e: Exception) {
                errorMessage = "Ошибка при загрузке уроков: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun enrollOrDeferCourse(userId: Long, courseId: Long, statusName: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                val result = RetrofitClient.instance.enrollOrDeferCourse(userId, courseId, statusName)
                enrollmentResult = if (result.body() == true) {
                    if (statusName == "поступление") "Вы успешно записались на курс"
                    else "Курс отложен"
                } else {
                    "Не удалось выполнить действие"
                }
            } catch (e: Exception) {
                enrollmentResult = "Ошибка при выполнении действия: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun updateCourse(
        courseId: Long,
        courseCategoryId: Long,
        userId: Long,
        name: String,
        description: String
    ) {
        viewModelScope.launch {
            isLoading = true
            try {
                val result = RetrofitClient.instance.updateCourse(courseId, courseCategoryId, userId, name, description)
                updateResult = if (result.body() == true) {
                    "Курс успешно обновлён"
                } else {
                    "Не удалось обновить курс"
                }
            } catch (e: Exception) {
                updateResult = "Ошибка при обновлении курса: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun warnOnCourse(courseId: Long) {
        viewModelScope.launch {
            isLoading = true
            try {
                val result = RetrofitClient.instance.warnOnCourse(courseId)
                warningResult = if (result.body() == true) {
                    "Предупреждение выдано"
                } else {
                    "Не удалось выдать предупреждение"
                }
            } catch (e: Exception) {
                warningResult = "Ошибка при выдаче предупреждения: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun deleteCourse(courseId: Long, userId: Long) {
        viewModelScope.launch {
            isLoading = true
            try {
                val result = RetrofitClient.instance.deleteCourse(courseId, userId)
                deleteResult = if (result.body() == true) {
                    "Курс успешно удалён"
                } else {
                    "Не удалось удалить курс"
                }
            } catch (e: Exception) {
                deleteResult = "Ошибка при удалении курса: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}

//просмотр урока
class LessonDetailsViewModel : ViewModel() {
    var isLoading by mutableStateOf(false)
        private set
    var lessonDetail by mutableStateOf<LessonDetailResponse?>(null)
        private set
    var steps by mutableStateOf<List<StepsByLessons>>(emptyList())
        private set
    var updateResult by mutableStateOf<String?>(null)
        private set
    var warningResult by mutableStateOf<String?>(null)
        private set
    var deleteResult by mutableStateOf<String?>(null)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun loadLessonDetails(lessonId: Long) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.instance.getLessonDetails(lessonId)
                lessonDetail = response.firstOrNull()
                if (lessonDetail != null) {
                    errorMessage = null
                } else {
                    errorMessage = "Урок не найден"
                }
            } catch (e: Exception) {
                errorMessage = "Ошибка при загрузке урока: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun loadStepsByLesson(lessonId: Long) {
        viewModelScope.launch {
            isLoading = true
            try {
                steps = RetrofitClient.instance.getStepsByLesson(lessonId)
                errorMessage = null
            } catch (e: Exception) {
                errorMessage = "Ошибка при загрузке шагов: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun updateLesson(
        lessonId: Long,
        courseId: Long,
        name: String,
        description: String,
        sequenceNumber: Long
    ) {
        viewModelScope.launch {
            isLoading = true
            try {
                val result = RetrofitClient.instance.updateLesson(
                    lessonId = lessonId,
                    courseId = courseId,
                    name = name,
                    description = description,
                    sequenceNumber = sequenceNumber
                )
                updateResult = if (result.body() == true) {
                    "Урок успешно обновлён"
                } else {
                    "Не удалось обновить урок"
                }
            } catch (e: Exception) {
                updateResult = "Ошибка при обновлении урока: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun warnOnStep(stepId: Long) {
        viewModelScope.launch {
            isLoading = true
            try {
                val result = RetrofitClient.instance.warnOnStep(stepId)
                warningResult = if (result.body() == true) {
                    "Предупреждение по шагу выдано"
                } else {
                    "Не удалось выдать предупреждение"
                }
            } catch (e: Exception) {
                warningResult = "Ошибка при выдаче предупреждения: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun deleteLesson(lessonId: Long, userId: Long) {
        viewModelScope.launch {
            isLoading = true
            try {
                val result = RetrofitClient.instance.deleteLesson(lessonId, userId)
                deleteResult = if (result.body() == true) {
                    "Урок успешно удалён"
                } else {
                    "Не удалось удалить урок"
                }
            } catch (e: Exception) {
                deleteResult = "Ошибка при удалении урока: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}

//просмотр шага
class LessonStepAnswerViewModel : ViewModel() {
    var isLoading by mutableStateOf(false)
        private set
    var result by mutableStateOf<Boolean?>(null)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set
    var stepDetails by mutableStateOf<List<StepDetailResponse>>(emptyList())
        private set
    var fileDownloadLoading by mutableStateOf(false)
        private set
    var fileDownloadError by mutableStateOf<String?>(null)
        private set
    var deleteResult by mutableStateOf<String?>(null)
        private set

    fun answerLessonStep(
        userId: Long,
        stepLessonId: Long,
        answerText: String? = null,
        selectedOptionIds: List<Long>? = null,
        originalName: String? = null,
        mimeType: String? = null,
        sizeBytes: Long? = null,
        commentStudent: String? = null,
        file: MultipartBody.Part? = null,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            isLoading = true
            try {
                val rb = { str: String -> str.toRequestBody("text/plain".toMediaTypeOrNull()) }
                val selectedOptionsString = selectedOptionIds?.joinToString(separator = ",")
                val response = RetrofitClient.instance.answerLessonStep(
                    userId = rb(userId.toString()),
                    stepLessonId = rb(stepLessonId.toString()),
                    answerText = answerText?.let { rb(it) } ?: rb(""),
                    selectedOptionIds = selectedOptionsString?.let { rb(it) } ?: rb(""),
                    originalName = originalName?.let { rb(it) },
                    mimeType = mimeType?.let { rb(it) },
                    sizeBytes = sizeBytes?.toString()?.let { rb(it) },
                    commentStudent = commentStudent?.let { rb(it) },
                    file = file
                )
                result = response.body()
                if (response.body() == true) {
                    errorMessage = null
                    onSuccess()
                } else {
                    errorMessage = "Не удалось отправить ответ на шаг урока"
                }
            } catch (e: Exception) {
                errorMessage = "Ошибка при отправке ответа: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun updateLessonStepAnswer(
        answerId: Long,
        userId: Long,
        stepLessonId: Long,
        answerText: String? = null,
        selectedOptionIds: List<Long>? = null,
        originalName: String? = null,
        mimeType: String? = null,
        sizeBytes: Long? = null,
        commentStudent: String? = null,
        file: MultipartBody.Part? = null,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            isLoading = true
            try {
                val rb = { str: String -> str.toRequestBody("text/plain".toMediaTypeOrNull()) }
                val selectedOptionsString = selectedOptionIds?.joinToString(",") ?: ""

                val response = RetrofitClient.instance.updateLessonStepAnswer(
                    answerId = rb(answerId.toString()),
                    userId = rb(userId.toString()),
                    stepLessonId = rb(stepLessonId.toString()),
                    answerText = answerText?.let { rb(it) } ?: rb(""),
                    selectedOptionIds = rb(selectedOptionsString),
                    originalName = originalName?.let { rb(it) },
                    mimeType = mimeType?.let { rb(it) },
                    sizeBytes = sizeBytes?.toString()?.let { rb(it) },
                    commentStudent = commentStudent?.let { rb(it) },
                    file = file
                )
                result = response.body()
                if (response.body() == true) {
                    errorMessage = null
                    onSuccess()
                } else {
                    errorMessage = "Не удалось обновить ответ на шаг урока"
                }
            } catch (e: Exception) {
                errorMessage = "Ошибка при обновлении ответа: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun updateLectureStep(
        stepId: Long,
        name: String,
        content: String,
        sequenceNumber: Long,
        obligatory: Boolean,
        originalName: String? = null,
        mimeType: String? = null,
        sizeBytes: Long? = null,
        file: MultipartBody.Part? = null,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            isLoading = true
            try {
                val rb = { str: String -> str.toRequestBody("text/plain".toMediaTypeOrNull()) }

                val response = RetrofitClient.instance.updateLectureStep(
                    stepId = rb(stepId.toString()),
                    name = rb(name),
                    content = rb(content),
                    sequenceNumber = rb(sequenceNumber.toString()),
                    obligatory = rb(obligatory.toString()),
                    originalName = originalName?.let { rb(it) },
                    mimeType = mimeType?.let { rb(it) },
                    sizeBytes = sizeBytes?.toString()?.let { rb(it) },
                    file = file
                )
                result = response.body()
                if (response.body() == true) {
                    errorMessage = null
                    onSuccess()
                } else {
                    errorMessage = "Не удалось обновить шаг «Лекция»"
                }
            } catch (e: Exception) {
                errorMessage = "Ошибка при обновлении шага «Лекция»: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun updateOpenQuestionStep(
        stepId: Long,
        name: String,
        content: String,
        sequenceNumber: Long,
        timePasses: String,
        obligatory: Boolean,
        maxScore: Long,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.instance.updateOpenQuestionStep(
                    stepId,
                    name,
                    content,
                    sequenceNumber,
                    timePasses,
                    obligatory,
                    maxScore
                )
                result = response.body()
                if (response.body() == true) {
                    errorMessage = null
                    onSuccess()
                } else {
                    errorMessage = "Не удалось обновить шаг «Открытый вопрос»"
                }
            } catch (e: Exception) {
                errorMessage = "Ошибка при обновлении шага «Открытый вопрос»: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun updateMultipleChoiceQuestionStep(
        stepId: Long,
        name: String,
        content: String,
        sequenceNumber: Long,
        timePasses: String,
        obligatory: Boolean,
        maxScore: Long,
        textOptions: List<String>,
        correct: List<Boolean>,
        scores: List<Long>,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.instance.updateMultipleChoiceQuestionStep(
                    stepId,
                    name,
                    content,
                    sequenceNumber,
                    timePasses,
                    obligatory,
                    maxScore,
                    textOptions,
                    correct,
                    scores
                )
                result = response.body()
                if (response.body() == true) {
                    errorMessage = null
                    onSuccess()
                } else {
                    errorMessage = "Не удалось обновить шаг «Вопрос с вариантами ответа»"
                }
            } catch (e: Exception) {
                errorMessage = "Ошибка при обновлении шага «Вопрос с вариантами ответа»: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun updateFileUploadQuestionStep(
        stepId: Long,
        name: String,
        content: String,
        sequenceNumber: Long,
        timePasses: String,
        obligatory: Boolean,
        maxScore: Long,
        originalName: String,
        mimeType: String,
        sizeBytes: Long,
        file: MultipartBody.Part,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            isLoading = true
            try {
                val rb = { str: String -> str.toRequestBody("text/plain".toMediaTypeOrNull()) }

                val response = RetrofitClient.instance.updateFileUploadQuestionStep(
                    stepId = rb(stepId.toString()),
                    name = rb(name),
                    content = rb(content),
                    sequenceNumber = rb(sequenceNumber.toString()),
                    timePasses = rb(timePasses),
                    obligatory = rb(obligatory.toString()),
                    maxScore = rb(maxScore.toString()),
                    originalName = rb(originalName),
                    mimeType = rb(mimeType),
                    sizeBytes = rb(sizeBytes.toString()),
                    file = file
                )
                result = response.body()
                if (response.body() == true) {
                    errorMessage = null
                    onSuccess()
                } else {
                    errorMessage = "Не удалось обновить шаг «Вопрос с приложением»"
                }
            } catch (e: Exception) {
                errorMessage = "Ошибка при обновлении шага «Вопрос с приложением»: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun getStepDetails(
        stepId: Long,
        userId: Long,
        onSuccess: (List<StepDetailResponse>) -> Unit = {}
    ) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.instance.getStepDetails(stepId, userId)
                stepDetails = response
                errorMessage = null
                onSuccess(response)
            } catch (e: Exception) {
                errorMessage = "Ошибка при получении деталей шага: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun downloadFile(
        filePath: String,
        onSuccess: (ResponseBody) -> Unit,
        onFailure: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            fileDownloadLoading = true
            fileDownloadError = null
            try {
                val response = RetrofitClient.instance.getFile(filePath)
                if (response.isSuccessful && response.body() != null) {
                    onSuccess(response.body()!!)
                } else {
                    val message = "Ошибка при скачивании файла: ${response.code()} ${response.message()}"
                    fileDownloadError = message
                    onFailure(message)
                }
            } catch (e: Exception) {
                val message = "Ошибка при скачивании файла: ${e.message}"
                fileDownloadError = message
                onFailure(message)
            } finally {
                fileDownloadLoading = false
            }
        }
    }

    fun deleteStep(stepId: Long, userId: Long) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.instance.deleteStep(stepId, userId)
                deleteResult = if (response.body() == true) {
                    "Шаг успешно удалён"
                } else {
                    "Не удалось удалить шаг"
                }
            } catch (e: Exception) {
                deleteResult = "Ошибка при удалении шага: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}

//создать курс
class CourseCreateViewModel : ViewModel() {
    var isLoading by mutableStateOf(false)
        private set
    var categories by mutableStateOf<List<CourseCategory>>(emptyList())
        private set
    var createCourseResult by mutableStateOf<Long?>(null)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun loadCourseCategories() {
        viewModelScope.launch {
            isLoading = true
            try {
                categories = RetrofitClient.instance.getCourseCategories()
                errorMessage = null
            } catch (e: Exception) {
                errorMessage = "Ошибка при загрузке категорий: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun createCourse(
        courseCategoryId: Long,
        userId: Long,
        name: String,
        description: String,
        onSuccess: (Long) -> Unit
    ) {
        viewModelScope.launch {
            isLoading = true
            try {
                val courseId = RetrofitClient.instance.createCourse(
                    courseCategoryId = courseCategoryId,
                    userId = userId,
                    name = name,
                    description = description
                )
                createCourseResult = courseId.id
                errorMessage = null
                onSuccess(courseId.id)
            } catch (e: Exception) {
                errorMessage = "Ошибка при создании курса: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}

//создать урок
class LessonCreateViewModel : ViewModel() {
    var isLoading by mutableStateOf(false)
        private set
    var createLessonResult by mutableStateOf<Long?>(null)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun createLesson(
        courseId: Long,
        name: String,
        description: String,
        sequenceNumber: Long,
        onSuccess: (Long) -> Unit
    ) {
        viewModelScope.launch {
            isLoading = true
            try {
                val lessonId = RetrofitClient.instance.createLesson(
                    courseId = courseId,
                    name = name,
                    description = description,
                    sequenceNumber = sequenceNumber
                )
                createLessonResult = lessonId.id
                errorMessage = null
                onSuccess(lessonId.id)
            } catch (e: Exception) {
                errorMessage = "Ошибка при создании урока: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}

//создание шага
class StepCreationViewModel : ViewModel() {
    var isLoading by mutableStateOf(false)
        private set
    var creationResult by mutableStateOf<Boolean?>(null)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun createLectureStep(
        lessonId: Long,
        name: String,
        content: String,
        sequenceNumber: Long,
        obligatory: Boolean,
        originalName: String? = null,
        mimeType: String? = null,
        sizeBytes: Long? = null,
        file: MultipartBody.Part? = null,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            isLoading = true
            try {
                val rb = { str: String -> str.toRequestBody("text/plain".toMediaTypeOrNull()) }

                val result = RetrofitClient.instance.createLectureStep(
                    lessonId = rb(lessonId.toString()),
                    name = rb(name),
                    content = rb(content),
                    sequenceNumber = rb(sequenceNumber.toString()),
                    obligatory = rb(obligatory.toString()),
                    originalName = originalName?.let { rb(it) },
                    mimeType = mimeType?.let { rb(it) },
                    sizeBytes = sizeBytes?.toString()?.let { rb(it) },
                    file = file
                )
                creationResult = result.body()
                if (result.body() == true) {
                    errorMessage = null
                    onSuccess()
                } else {
                    errorMessage = "Не удалось создать шаг «Лекция»"
                }
            } catch (e: Exception) {
                errorMessage = "Ошибка при создании шага «Лекция»: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun createOpenQuestionStep(
        lessonId: Long,
        name: String,
        content: String,
        sequenceNumber: Long,
        timePasses: String,
        obligatory: Boolean,
        maxScore: Long? = null,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            isLoading = true
            try {
                val result = RetrofitClient.instance.createOpenQuestionStep(
                    lessonId, name, content, sequenceNumber, timePasses, obligatory, maxScore
                )
                creationResult = result.body()
                if (result.body() == true) {
                    errorMessage = null
                    onSuccess()
                } else {
                    errorMessage = "Не удалось создать шаг «Открытый вопрос»"
                }
            } catch (e: Exception) {
                errorMessage = "Ошибка при создании шага «Открытый вопрос»: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun createMultipleChoiceQuestionStep(
        lessonId: Long,
        name: String,
        content: String,
        sequenceNumber: Long,
        timePasses: String,
        obligatory: Boolean,
        maxScore: Long,
        textOptions: List<String>,
        correct: List<Boolean>,
        scores: List<Long>,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            isLoading = true
            try {
                val result = RetrofitClient.instance.createMultipleChoiceQuestionStep(
                    lessonId, name, content, sequenceNumber, timePasses,
                    obligatory, maxScore, textOptions, correct, scores
                )
                creationResult = result.body()
                if (result.body() == true) {
                    errorMessage = null
                    onSuccess()
                } else {
                    errorMessage = "Не удалось создать шаг «Множественный выбор»"
                }
            } catch (e: Exception) {
                errorMessage = "Ошибка при создании шага «Множественный выбор»: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun createFileUploadStep(
        lessonId: Long,
        name: String,
        content: String,
        sequenceNumber: Long,
        timePasses: String,
        obligatory: Boolean,
        maxScore: Long,
        originalName: String,
        mimeType: String,
        sizeBytes: Long,
        file: MultipartBody.Part?
    ) {
        viewModelScope.launch {
            isLoading = true
            try {
                val rb = { str: String -> str.toRequestBody("text/plain".toMediaTypeOrNull()) }

                val result = file?.let {
                    RetrofitClient.instance.createFileUploadQuestionStep(
                        lessonId = rb(lessonId.toString()),
                        name = rb(name),
                        content = rb(content),
                        sequenceNumber = rb(sequenceNumber.toString()),
                        timePasses = rb(timePasses),
                        obligatory = rb(obligatory.toString()),
                        maxScore = rb(maxScore.toString()),
                        originalName = rb(originalName),
                        mimeType = rb(mimeType),
                        sizeBytes = rb(sizeBytes.toString()),
                        file = it
                    )
                }
                if (result != null) {
                    if (result.body() == true) {
                        errorMessage = "Шаг успешно создан"
                    } else {
                        errorMessage = "Не удалось создать шаг"
                    }
                }
            } catch (e: Exception) {
                errorMessage = "Ошибка: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}

// ответы на шаг
class StepAnswersViewModel : ViewModel() {
    var isLoading by mutableStateOf(false)
        private set
    var answersForStep by mutableStateOf<List<AnswersForStepResponse>>(emptyList())
        private set
    var selectedAnswer by mutableStateOf<AnswerForStepResponse?>(null)
        private set
    var operationSuccess by mutableStateOf<Boolean?>(null)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun loadAnswersForStep(stepId: Long) {
        viewModelScope.launch {
            isLoading = true
            try {
                answersForStep = RetrofitClient.instance.getAnswersForStep(stepId)
                errorMessage = null
            } catch (e: Exception) {
                errorMessage = "Ошибка при загрузке ответов: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun loadAnswerDetail(answerId: Long) {
        viewModelScope.launch {
            isLoading = true
            try {
                selectedAnswer = RetrofitClient.instance.getAnswerForStep(answerId)
                errorMessage = null
            } catch (e: Exception) {
                errorMessage = "Ошибка при загрузке ответа: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun evaluateAnswer(answerUserId: Long, score: Long, commentTeacher: String?) {
        viewModelScope.launch {
            isLoading = true
            try {
                operationSuccess = RetrofitClient.instance.evaluateAnswerOnStep(
                    answerUserId, score, commentTeacher
                ).body()
                errorMessage = null
            } catch (e: Exception) {
                operationSuccess = false
                errorMessage = "Ошибка при оценивании ответа: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}

//статистика по обращениям
class AppealStatisticsViewModel : ViewModel() {
    var isLoading by mutableStateOf(false)
        private set
    var statistics by mutableStateOf<List<AppealStatisticsItemResponse>>(emptyList())
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun loadAppealStatistics() {
        viewModelScope.launch {
            isLoading = true
            try {
                statistics = RetrofitClient.instance.getAppealsStatistics()
                errorMessage = null
            } catch (e: Exception) {
                errorMessage = "Ошибка при загрузке статистики: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}

//статистика платформы
class PlatformStatisticsViewModel : ViewModel() {
    var isLoading by mutableStateOf(false)
        private set
    var statistics by mutableStateOf<GetPlatformStatisticsResponse?>(null)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun loadPlatformStatistics() {
        viewModelScope.launch {
            isLoading = true
            try {
                statistics = RetrofitClient.instance.getPlatformStatistics()
                errorMessage = null
            } catch (e: Exception) {
                errorMessage = "Ошибка при загрузке статистики: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}

//статистика по курсам
class TeacherCoursesViewModel : ViewModel() {
    var isLoading by mutableStateOf(false)
        private set
    var courses by mutableStateOf<List<CourseByTeacherResponse>>(emptyList())
        private set
    var selectedCourseStatistics by mutableStateOf<List<ViewCourseStatisticsResponse>>(emptyList())
        private set
    var issuedCertificate by mutableStateOf<List<IssueCertificateResponse>>(emptyList())
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun loadCoursesByTeacher(teacherId: Long) {
        viewModelScope.launch {
            isLoading = true
            try {
                courses = RetrofitClient.instance.getCoursesByTeacher(teacherId)
                errorMessage = null
            } catch (e: Exception) {
                errorMessage = "Ошибка при загрузке курсов: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun loadCourseStatistics(courseId: Long) {
        viewModelScope.launch {
            isLoading = true
            try {
                selectedCourseStatistics = RetrofitClient.instance.viewCourseStatistics(courseId)
                errorMessage = null
            } catch (e: Exception) {
                errorMessage = "Ошибка при загрузке статистики курса: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun issueCertificate(userId: Long, courseId: Long) {
        viewModelScope.launch {
            isLoading = true
            try {
                issuedCertificate = RetrofitClient.instance.issueCertificate(userId, courseId)
                errorMessage = null
            } catch (e: Exception) {
                errorMessage = "Ошибка при выдаче сертификата: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}

//активность пользователей
class UserActivityStatsViewModel : ViewModel() {
    var isLoading by mutableStateOf(false)
        private set
    var activityStats by mutableStateOf<List<UserActivityStatsItemResponse>>(emptyList())
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun loadUserActivityStats(daysBack: Int = 14) {
        viewModelScope.launch {
            isLoading = true
            try {
                activityStats = RetrofitClient.instance.getUserActivityStats(daysBack)
                errorMessage = null
            } catch (e: Exception) {
                errorMessage = "Ошибка при загрузке статистики активности: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}

//просмотр пользователей
class UserStatisticsViewModel : ViewModel() {
    var isLoading by mutableStateOf(false)
        private set
    var newUsers by mutableStateOf<List<NewUserResponse>>(emptyList())
        private set
    var allUsers by mutableStateOf<List<AllUserResponse>>(emptyList())
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set


    fun loadNewUsers() {
        viewModelScope.launch {
            isLoading = true
            try {
                newUsers = RetrofitClient.instance.getNewUsers()
                errorMessage = null
            } catch (e: Exception) {
                errorMessage = "Ошибка загрузки новых пользователей: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun loadAllUsers() {
        viewModelScope.launch {
            isLoading = true
            try {
                allUsers = RetrofitClient.instance.getAllUsers()
                errorMessage = null
            } catch (e: Exception) {
                errorMessage = "Ошибка загрузки всех пользователей: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}

//темы обращений
class AppealTopicViewModel : ViewModel() {
    var isLoading by mutableStateOf(false)
        private set
    var topics by mutableStateOf<List<AppealTopicResponse>>(emptyList())
        private set
    var operationResult by mutableStateOf<Boolean?>(null)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun loadTopics() {
        viewModelScope.launch {
            isLoading = true
            try {
                topics = RetrofitClient.instance.getTopicsAppeals()
                errorMessage = null
            } catch (e: Exception) {
                errorMessage = "Ошибка при загрузке тем обращений: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun addTopic(name: String, description: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                operationResult = RetrofitClient.instance.addTopicAppeal(name, description).body()
                errorMessage = null
                if (operationResult == true) loadTopics()
            } catch (e: Exception) {
                errorMessage = "Ошибка при добавлении темы: ${e.message}"
                operationResult = false
            } finally {
                isLoading = false
            }
        }
    }

    fun updateTopic(id: Long, name: String, description: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                operationResult = RetrofitClient.instance.updateTopicAppeal(id, name,
                    description).body()
                errorMessage = null
                if (operationResult == true) loadTopics()
            } catch (e: Exception) {
                errorMessage = "Ошибка при обновлении темы: ${e.message}"
                operationResult = false
            } finally {
                isLoading = false
            }
        }
    }

    fun deleteTopic(id: Long) {
        viewModelScope.launch {
            isLoading = true
            try {
                operationResult = RetrofitClient.instance.deleteTopicAppeal(id).body()
                errorMessage = null
                if (operationResult == true) loadTopics()
            } catch (e: Exception) {
                errorMessage = "Ошибка при удалении темы: ${e.message}"
                operationResult = false
            } finally {
                isLoading = false
            }
        }
    }
}

//категории курсов
class CourseCategoryViewModel : ViewModel() {
    var isLoading by mutableStateOf(false)
        private set
    var categories by mutableStateOf<List<CourseCategory>>(emptyList())
        private set
    var operationResult by mutableStateOf<Boolean?>(null)
    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun loadCategories() {
        viewModelScope.launch {
            isLoading = true
            try {
                categories = RetrofitClient.instance.getCourseCategories()
                errorMessage = null
            } catch (e: Exception) {
                errorMessage = "Ошибка при загрузке категорий курсов: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun addCategory(name: String, description: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                operationResult = RetrofitClient.instance.addCourseCategory(name,
                    description).body()
                errorMessage = null
                if (operationResult == true) loadCategories()
            } catch (e: Exception) {
                errorMessage = "Ошибка при добавлении категории: ${e.message}"
                operationResult = false
            } finally {
                isLoading = false
            }
        }
    }

    fun updateCategory(courseId: Long, name: String, description: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                operationResult = RetrofitClient.instance.updateCourseCategory(courseId, name,
                    description).body()
                errorMessage = null
                if (operationResult == true) loadCategories()
            } catch (e: Exception) {
                errorMessage = "Ошибка при обновлении категории: ${e.message}"
                operationResult = false
            } finally {
                isLoading = false
            }
        }
    }

    fun deleteCategory(courseId: Long) {
        viewModelScope.launch {
            isLoading = true
            try {
                RetrofitClient.instance.deleteCourseCategory(courseId)
                operationResult = true
                errorMessage = null
                if (operationResult == true) loadCategories()
            } catch (e: Exception) {
                errorMessage = "Ошибка при удалении категории: ${e.message}"
                operationResult = false
            } finally {
                isLoading = false
            }
        }
    }
}

// сертификаты
class CertificatesViewModel : ViewModel() {
    var isLoading by mutableStateOf(false)
        private set
    var userCertificates by mutableStateOf<List<UserCertificatesResponse>>(emptyList())
        private set
    var certificateDetails by mutableStateOf<List<ViewCertificate>>(emptyList())
        private set
    var downloadError by mutableStateOf<String?>(null)
        private set
    var generalError by mutableStateOf<String?>(null)
        private set

    fun loadUserCertificates(userId: Long) {
        viewModelScope.launch {
            isLoading = true
            try {
                userCertificates = RetrofitClient.instance.getUserSertificates(userId)
                generalError = null
            } catch (e: Exception) {
                generalError = "Ошибка загрузки сертификатов: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun loadCertificateDetails(certificateId: Long) {
        viewModelScope.launch {
            isLoading = true
            try {
                certificateDetails = RetrofitClient.instance.viewCertificate(certificateId)
                generalError = null
            } catch (e: Exception) {
                generalError = "Ошибка загрузки данных сертификата: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    suspend fun downloadFile(filePath: String): Response<ResponseBody>? {
        return try {
            RetrofitClient.instance.getFile(filePath)
        } catch (e: Exception) {
            downloadError = "Ошибка загрузки файла: ${e.message}"
            null
        }
    }
}
