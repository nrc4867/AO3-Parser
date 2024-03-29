package dev.chieppa.wrapper.constants

const val ao3_host = "archiveofourown.org"
const val ao3_url: String = "https://$ao3_host"
const val ao3_login: String = "$ao3_url/users/login"

val ao3_search = { query: String, page: Int -> "$ao3_url/works/search?$query&page=$page" }

val ao3_bookmark_search =
    { query: String, page: Int -> "$ao3_url/bookmarks/search?$query&page=$page" }

val ao3_people_search = {
    query: String, page: Int -> "$ao3_url/people/search?$query&page=$page"
}

val ao3_sort_and_filter = { filter: String, page: Int -> "$ao3_url/works?$filter&page=$page" }
val ao3_sort_and_filter_bookmarks = { filter: String, page: Int -> "$ao3_url/bookmarks?$filter&page=$page" }

val ao3_chapter = { chapter_id: Int -> "$ao3_url/chapters/$chapter_id" }
val ao3_firstChapter = { work_id: Int -> "$ao3_url/works/$work_id" }

val ao3_download = { work_id: Int, extension: DownloadType -> "$ao3_url/downloads/$work_id/a.${extension.extension}" }

val ao3_autocomplete = { action: String, term: String -> "$ao3_url/autocomplete/$action?term=$term" }

val ao3_chapter_navigation = { work_id: Int -> "$ao3_url/works/$work_id/navigate" }

val ao3_chapter_comment_location =
    { chapter_id: Int, page: Int -> "$ao3_url/comments/show_comments?chapter_id=$chapter_id&page=$page" }
val ao3_work_comment_location =
    { work_id: Int, page: Int -> "$ao3_url/comments/show_comments?work_id=$work_id&page=$page&view_full_work=true" }

val ao3_user_location = { user: String -> "$ao3_url/users/$user" }
val ao3_user_profile_location = { user: String -> "${ao3_user_location(user)}/profile" }
val ao3_user_gift_location = { user: String, page: Int -> "${ao3_user_location(user)}/gifts?page=$page" }

val ao3_tag_location = { tag: String -> "tags/$tag" }

const val ao3_session_cookie = "_otwarchive_session"
const val ao3_session_remember_user_token = "remember_user_token"
const val ao3_session_user_conditionals = "user_credentials"
