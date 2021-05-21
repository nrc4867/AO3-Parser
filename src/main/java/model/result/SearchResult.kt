package model.result

import model.work.Work

data class SearchResult(val found: Int,
                        val pages: Int,
                        val page: Int,
                        val works: List<Work>)