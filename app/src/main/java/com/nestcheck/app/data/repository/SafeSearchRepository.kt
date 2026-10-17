package com.nestcheck.app.data.repository

import com.nestcheck.app.data.model.SafeSearchConfig
import com.nestcheck.app.data.model.SafeSearchEngineRule
import com.nestcheck.app.data.model.SafeSearchQueryLog
import com.nestcheck.app.data.model.SearchEngineType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SafeSearchRepository @Inject constructor() {

    private val _config = MutableStateFlow(SafeSearchConfig())
    val config: StateFlow<SafeSearchConfig> = _config.asStateFlow()

    private val _logs = MutableStateFlow(
        listOf(
            SafeSearchQueryLog(
                id = UUID.randomUUID().toString(),
                query = "space shuttle launch photos",
                engine = SearchEngineType.GOOGLE,
                originalUrl = "https://www.google.com/search?q=space+shuttle+launch+photos",
                rewrittenUrl = "https://www.google.com/search?q=space+shuttle+launch+photos&safe=active",
                timestamp = System.currentTimeMillis() - 1000L * 60 * 12,
                isEnforced = true
            ),
            SafeSearchQueryLog(
                id = UUID.randomUUID().toString(),
                query = "minecraft redstone contraptions",
                engine = SearchEngineType.YOUTUBE,
                originalUrl = "https://www.youtube.com/results?search_query=minecraft+redstone+contraptions",
                rewrittenUrl = "https://www.youtube.com/results?search_query=minecraft+redstone+contraptions&safe=active",
                timestamp = System.currentTimeMillis() - 1000L * 60 * 38,
                isEnforced = true
            ),
            SafeSearchQueryLog(
                id = UUID.randomUUID().toString(),
                query = "dinosaur fossils discovery",
                engine = SearchEngineType.BING,
                originalUrl = "https://www.bing.com/search?q=dinosaur+fossils+discovery",
                rewrittenUrl = "https://www.bing.com/search?q=dinosaur+fossils+discovery&adlt=strict",
                timestamp = System.currentTimeMillis() - 1000L * 60 * 75,
                isEnforced = true
            ),
            SafeSearchQueryLog(
                id = UUID.randomUUID().toString(),
                query = "algebra quadratic formula examples",
                engine = SearchEngineType.DUCKDUCKGO,
                originalUrl = "https://duckduckgo.com/?q=algebra+quadratic+formula+examples",
                rewrittenUrl = "https://duckduckgo.com/?q=algebra+quadratic+formula+examples&kp=1",
                timestamp = System.currentTimeMillis() - 1000L * 60 * 150,
                isEnforced = true
            )
        )
    )
    val logs: StateFlow<List<SafeSearchQueryLog>> = _logs.asStateFlow()

    fun toggleEngineEnforcement(targetEngine: SearchEngineType, enabled: Boolean) {
        val current = _config.value
        val updatedEngines = current.engines.map { rule ->
            if (rule.engine == targetEngine) rule.copy(isEnforced = enabled) else rule
        }
        _config.value = current.copy(engines = updatedEngines)
    }

    fun setTamperLock(enabled: Boolean) {
        _config.value = _config.value.copy(isTamperLockEnabled = enabled)
    }

    fun setBlockIncognito(enabled: Boolean) {
        _config.value = _config.value.copy(blockIncognitoBrowsing = enabled)
    }

    fun setYouTubeRestricted(enabled: Boolean) {
        _config.value = _config.value.copy(enforceRestrictedYouTubeMode = enabled)
    }

    fun simulateSearch(rawQuery: String, engine: SearchEngineType): SafeSearchQueryLog {
        val query = rawQuery.trim().ifBlank { "science experiments for kids" }
        val encoded = try {
            URLEncoder.encode(query, StandardCharsets.UTF_8.toString())
        } catch (_: Exception) {
            query.replace(" ", "+")
        }

        val rule = _config.value.engines.find { it.engine == engine }
        val isEnforced = rule?.isEnforced ?: true

        val originalUrl = when (engine) {
            SearchEngineType.GOOGLE -> "https://www.google.com/search?q=$encoded"
            SearchEngineType.BING -> "https://www.bing.com/search?q=$encoded"
            SearchEngineType.YOUTUBE -> "https://www.youtube.com/results?search_query=$encoded"
            SearchEngineType.DUCKDUCKGO -> "https://duckduckgo.com/?q=$encoded"
            SearchEngineType.YAHOO -> "https://search.yahoo.com/search?p=$encoded"
            SearchEngineType.ECOSIA -> "https://www.ecosia.org/search?q=$encoded"
        }

        val rewrittenUrl = if (isEnforced) {
            when (engine) {
                SearchEngineType.GOOGLE -> "$originalUrl&safe=active"
                SearchEngineType.BING -> "$originalUrl&adlt=strict"
                SearchEngineType.YOUTUBE -> "$originalUrl&safe=active"
                SearchEngineType.DUCKDUCKGO -> "$originalUrl&kp=1"
                SearchEngineType.YAHOO -> "$originalUrl&vm=r"
                SearchEngineType.ECOSIA -> "$originalUrl&safesearch=2"
            }
        } else {
            originalUrl
        }

        val log = SafeSearchQueryLog(
            id = UUID.randomUUID().toString(),
            query = query,
            engine = engine,
            originalUrl = originalUrl,
            rewrittenUrl = rewrittenUrl,
            timestamp = System.currentTimeMillis(),
            isEnforced = isEnforced
        )

        val updatedEngines = _config.value.engines.map { r ->
            if (r.engine == engine) r.copy(requestsIntercepted = r.requestsIntercepted + 1) else r
        }
        _config.value = _config.value.copy(
            totalQueriesFiltered = _config.value.totalQueriesFiltered + 1,
            engines = updatedEngines
        )

        _logs.value = listOf(log) + _logs.value
        return log
    }
}
