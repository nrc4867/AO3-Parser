package dev.chieppa.model.result

import dev.chieppa.model.result.work.Work

data class SearchResult(val found: Int,
                        val pages: Int,
                        val page: Int,
                        val works: List<Work>)