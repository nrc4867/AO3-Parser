package model.user

data class Session(var session_id: String? = null, var remember_user_token: String? = null, var userCredentials: String? = "1") {
    fun getCookie(): String {
        val stringBuilder = StringBuilder()

        session_id?.let { stringBuilder.append("_otwarchive_session=$it;") }
        remember_user_token?.let { stringBuilder.append("remember_user_token=$it;") }
        userCredentials?.let { stringBuilder.append("user_credentials=$it;") }

        return stringBuilder.toString()
    }
}