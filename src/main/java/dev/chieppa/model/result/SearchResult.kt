package dev.chieppa.model.result

import dev.chieppa.model.result.navigation.Navigation
import dev.chieppa.model.result.work.Work

data class SearchResult(val found: Int,
                        val navigation: Navigation,
                        val works: List<Work>)