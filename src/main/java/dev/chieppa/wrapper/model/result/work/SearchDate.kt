package dev.chieppa.wrapper.model.result.work

data class SearchDate(val year: Int, val month: Int, val day: Int) {
    override fun toString(): String {
        return "$year-$month-$day"
    }
}
