package model.user

data class Session(val session_id: String? = null, val remember_user_token: String? = null, val userCredentials: String? = "1") {
    fun getCookie(): String {
        val stringBuilder = StringBuilder()

        session_id?.let { stringBuilder.append("_otwarchive_session=$it;") }
        remember_user_token?.let { stringBuilder.append("remember_user_token=$it;") }
        userCredentials?.let { stringBuilder.append("user_credentials=$it;") }

        return stringBuilder.toString()
    }
}