package constants

const val ao3_url: String = "https://archiveofourown.org"
const val ao3_login: String = "$ao3_url/users/login"

val ao3_search = { query: String, page: Int -> "$ao3_url/works/search?$query&page=$page" }

val ao3_sort_and_filter = { filter: String, page: Int -> "$ao3_url/works?$filter&page=$page" }

val ao3_chapter = { work_id: Int, chapter_id: Int -> "$ao3_url/works/$work_id/chapters/$chapter_id" }

val ao3_download = { work_id: Int, extension: DownloadType -> "$ao3_url/downloads/$work_id/a.${extension.extension}" }

val ao3_autocomplete = { action: String, term: String -> "$ao3_url/autocomplete/$action?term=$term" }

val ao3_chapter_navigation = { work_id: Int -> "$ao3_url/works/$work_id/navigate" }

const val ao3_session_cookie = "_otwarchive_session"
const val ao3_session_remember_user_token = "remember_user_token"
const val ao3_session_user_conditionals = "user_credentials"
