package com.qdocs.smartschool.utils;

public class Constants {
    public static final Boolean isDemoModeOn = false;
    public static final Boolean askUrlFromUser = true;
    public static final String clientService = "smartschool";
    public static final String authKey = "schoolAdmin@";
    public static final String appKey = "schoolAdmin@1234";
    public static final String contentType = "application/json";
    public static final String contentTypes = "application/x-www-form-urlencoded";
    public static final String domain = "";
    public static final String mainUrl = "https://schoolingpro.in/admin/api/";

    public static final String loginUrl = "auth/login";
    public static final String baseSUrl = "/app/api";
    public static final String baseUrl = "/api/";
    public static final String siteUrl = "admin";
    public static final String getStudentProfileUrl = "webservice/getStudentProfile";
    public static final String getHomeworkUrl = "webservice/getHomework";
    public static final String uploadHomeworkUrl = "webservice/addaa";
    public static final String getstudentcurrentlanguageUrl = "webservice/getstudentcurrentlanguage";
    public static final String get_currency_listUrl = "webservice/get_currency_list";
    public static final String updatestudentlanguage = "webservice/updatestudentlanguage";
    public static final String updatestudentcurrency = "webservice/updatestudentcurrency";
    public static final String getstudentsubjectUrl = "webservice/getstudentsubject";
    public static final String uploadDocumentUrl = "webservice/uploadDocument";
    public static final String getFeesUrl = "webservice/fees";
    public static final String getProcessingfeesUrl = "webservice/getProcessingfees";
    public static final String lock_student_panelUrl = "webservice/lock_student_panel";
    public static final String getMaintenanceModeStatusUrl = "webservice/getMaintenanceModeStatus";
    public static final String getDownloadsLinksByIdUrl = "webservice/getDownloadsLinksById";
    public static final String checkStudentStatusUrl = "webservice/checkStudentStatus";
    public static final String getStudentTimelineStatusUrl = "webservice/getStudentTimelineStatus";
    public static final String getOfflineBankPaymentStatusUrl = "webservice/getOfflineBankPaymentStatus";
    public static final String getOfflineBankPaymentInstructionUrl = "webservice/getOfflineBankPaymentInstruction";
    public static final String getgmeetsettingsUrl = "webservice/getgmeetsettings";
    public static final String getzoomsettingsUrl = "webservice/getzoomsettings";
    public static final String getClassScheduleUrl = "webservice/class_schedule";
    public static final String coursecurriculumUrl = "webservice/coursecurriculum";
    public static final String getCourseReviewsUrl = "webservice/getCourseReviews";

    public static final String courseperformanceUrl = "webservice/courseperformance";
    public static final String getlessonplanUrl = "webservice/getlessonplan";
    public static final String getExamListUrl = "webservice/getExamList";
    public static final String cbseexamresultUrl = "webservice/cbseexamresult";
    public static final String cbseexamtimetableUrl = "webservice/cbseexamtimetable";
    public static final String getExamScheduleListUrl = "webservice/examSchedule";
    public static final String getOnlineExamQuestionUrl = "webservice/getOnlineExamQuestion";
    public static final String getquestionbyquizidUrl = "webservice/getquestionbyquizid";
    public static final String saveOnlineExamUrl = "webservice/saveOnlineExam";
    public static final String saveanswerUrl = "webservice/saveanswer";
    public static final String submitquizUrl = "webservice/submitquiz";
    public static final String quizresultUrl = "webservice/quizresult";
    public static final String resetquizUrl = "webservice/resetquiz";
    public static final String coursedetailUrl = "webservice/coursedetail";
    public static final String getExamResultListUrl = "webservice/getExamResultList";
    public static final String getExamScheduleDetailsUrl = "webservice/getExamSchedule";
    public static final String getExamResultUrl = "webservice/getExamResult";
    public static final String getSubjectsLessonsUrl = "webservice/getSubjectsLessons";
    public static final String getNotificationsUrl = "webservice/getNotifications";
    public static final String getsyllabussubjectsUrl = "webservice/getsyllabussubjects";
    public static final String getSubjectListUrl = "webservice/getSubjectList";
    public static final String getSubjectTimetableUrl = "webservice/getSubjectTimetable";
    public static final String getTeacherListUrl = "webservice/getTeachersList";
    public static final String getTeacherSubjectUrl = "webservice/getTeacherSubject";
    public static final String addStaffRatingUrl = "webservice/addStaffRating";
    public static final String getLibraryBookListUrl = "webservice/getLibraryBooks";
    public static final String getLibraryBookIssuedListUrl = "webservice/getLibraryBookIssued";
    public static final String getTransportRouteListUrl = "webservice/gettransportroutes";
    public static final String getTransportVehicleDetailsUrl = "webservice/getTransportVehicleDetails";
    public static final String getHostelListUrl = "webservice/getHostelList";
    public static final String getHostelDetailUrl = "webservice/getHostelDetails";
    public static final String getDownloadsLinksUrl = "webservice/getDownloadsLinks";
    public static final String getVideoTutorialUrl = "webservice/getVideoTutorial";
    public static final String getAttendanceUrl = "webservice/getAttendenceRecords";
    public static final String forgotPasswordUrl = "webservice/forgot_password";
    public static final String logoutUrl = "webservice/logout";
    public static final String paymentGatewayUrl = "payment/index/";
    public static final String coursepaymentGatewayUrl = "course_payment/Course_payment/payment/";
    public static final String getDashboardUrl = "webservice/dashboard";
    public static final String getStudentCurrencyUrl = "webservice/getStudentCurrency";
    public static final String getDocumentUrl = "webservice/getDocument";
    public static final String getdailyassignmentUrl = "webservice/getdailyassignment";
    public static final String addeditdailyassignmentUrl = "webservice/addeditdailyassignment";
    public static final String deletedailyassignmentUrl = "webservice/deletedailyassignment";
    public static final String getTimelineUrl = "webservice/getTimeline";
    public static final String addedittimelineUrl = "webservice/addedittimeline";
    public static final String addCourseRatingandReviewUrl = "webservice/addCourseRatingandReview";
    public static final String createTaskUrl = "webservice/addTask";
    public static final String deleteTaskUrl = "webservice/deleteTask";
    public static final String markTaskUrl = "webservice/updateTask";
    public static final String getTaskUrl = "webservice/getTask";
    public static final String getELearningUrl = "webservice/getELearningModuleStatus";
    public static final String getCommunicateUrl = "webservice/getCommunicateModuleStatus";
    public static final String getAcademicsUrl = "webservice/getAcademicsModuleStatus";
    public static final String getOthersUrl = "webservice/getOthersModuleStatus";
    public static final String getOnlineExamUrl = "webservice/getOnlineExam";
    public static final String courselistUrl = "webservice/courselist";
    public static final String liveclassesUrl = "webservice/liveclasses";
    public static final String gmeetclassesUrl = "webservice/gmeetclasses";
    public static final String getstudentbehaviourUrl = "webservice/getstudentbehaviour";
    public static final String getOfflineBankPayments = "webservice/getOfflineBankPayments";
    public static final String livehistoryUrl = "webservice/livehistory";
    public static final String gmeethistoryUrl = "webservice/gmeethistory";
    public static final String getsyllabusUrl = "webservice/getsyllabus";
    public static final String addforummessageUrl = "webservice/addforummessage";
    public static final String addincidentcommentsUrl = "webservice/addincidentcomments";
    public static final String getforummessageUrl = "webservice/getforummessage";
    public static final String deleteforummessageUrl = "webservice/deleteforummessage";
    public static final String getincidentcommentsUrl = "webservice/getincidentcomments";
    public static final String deleteincidentcommentsUrl = "webservice/deleteincidentcomments";
    public static final String getOnlineExamResultUrl = "webservice/getOnlineExamResult";
    public static final String getApplyLeaveUrl = "webservice/getApplyLeave";            //
    public static final String getVisitorsUrl = "webservice/getVisitors";
    public static final String addofflinepaymentUrl = "webservice/addofflinepayment";
    public static final String addleaveUrl = "webservice/addleave";
    public static final String updateLeaveUrl = "webservice/updateLeave";
    public static final String deleteLeaveUrl = "webservice/deleteLeave";
    public static final String deletetimelineUrl = "webservice/deletetimeline";
    public static final String markAsCompleteUrl = "webservice/markascomplete";
    public static final String getSchoolDetailsUrl = "webservice/getSchoolDetails";
    public static final String parent_getStudentList = "webservice/Parent_GetStudentsList";
    public static final String getExamResultDetailsUrl = "webservice/getExamResultDetails";
    public static final String privacyPolicyUrl = "privacy-policy";
    public static final String downloadDirectory = "SmartSchool";
    public static final String defaultSecondaryColour = "#daf6fc";
    public static final String defaultPrimaryColour = "#2e4b5f";
    //SHARED PREFERENCE KEYS
    public static final String primaryColour = "primaryColour";
    public static final String secondaryColour = "secondaryColour";
    public static final String app_ver = "app_ver";
    public static final String apiUrl = "apiUrl";
    public static final String appLogo = "appLogo";
    //  public static final String apiUrl = "apiUrl";
    public static final String imagesUrl = "imagesUrl";
    public static final String classSection = "classSection";
    public static final String currency = "currencySymbol";
    public static final String currency_short_name = "currency_short_name";
    public static final String currency_price = "currency_price";
    public static final String classId = "classId";
    public static final String sectionId = "sectionId";
    public static final String studentId = "studentId";
    public static final String parentsId = "parentsId";
    public static final String admission_no = "admission_no";
    public static final String userId = "userId";
    public static final String permissionStatus = "permissionStatus";
    public static final String userName = "userName";
    public static final String userImage = "userImage";
    public static final String chatuserImage = "chatuserImage";
    public static final String loginType = "role";
    public static final String superadmin_restriction = "superadmin_restriction";
    public static final String student_session_id = "student_session_id";
    public static final String modulesArray = "modulesArray";
    public static final String isLoggegIn = "isLoggegIn";
    public static final String isLock = "isLock";
    public static final String showPaymentBtn = "showPaymentBtn";
    public static final String showCoursePaymentBtn = "showPaymentBtn";
    public static final String langCode = "langCode";
    public static final String appDomain = "appDomain";
    public static final String isLocaleSet = "isLocaleSet";
    public static final String currentLocale = "currentLocale";
    public static final String currencycode = "currencycode";
    public static final String parent_live_class = "parent_live_class";
    public static final String zoom_parent_live_class = "zoom_parent_live_class";
}


