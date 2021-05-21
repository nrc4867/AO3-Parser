package constants

object AO3Constant {
    const val ao3_url: String = "https://archiveofourown.org"
    const val ao3_search: String = "/works/search?"
    const val ao3_login: String = "/users/login"

    val ao3_work = { work_id: Int, chapter_id: Int  -> "/works/$work_id/chapters/$chapter_id" }

    val ao3_download = {work_id : Int, extension : DownloadType -> "/downloads/$work_id/a.${extension.extension}"}

    val ao3_autocomplete = {action: String, term : String -> "/autocomplete/$action?term=$term"}
}
