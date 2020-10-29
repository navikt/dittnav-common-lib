/*
 * Copyright 2014-2019 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 *
 * Original source file was copied from https://github.com/ktorio/ktor
 * Modifications are licensed under MIT, copyright (c) 2020 NAV
 */

package no.nav.personbruker.dittnav.ktor.features


import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.util.pipeline.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.util.*

/**
 * CORS feature. Please read http://ktor.io/servers/features/cors.html first before using it.
 * Modifications have been made to allow wildcard subdomains
 */
class NonStandardCors(configuration: Configuration) {
    private val numberRegex = "[0-9]+".toRegex()

    val allowSameOrigin: Boolean = configuration.allowSameOrigin

    val allowsAnyHost: Boolean = "*" in configuration.hosts

    val allowCredentials: Boolean = configuration.allowCredentials

    val allHeaders: Set<String> = (configuration.headers + Configuration.CorsSimpleRequestHeaders).let { headers ->
        if (configuration.allowNonSimpleContentTypes) headers else headers.minus(HttpHeaders.ContentType)
    }

    val methods: Set<HttpMethod> = HashSet<HttpMethod>(configuration.methods + Configuration.CorsDefaultMethods)

    val allHeadersSet: Set<String> = allHeaders.map { it.toLowerCase() }.toSet()

    private val allowNonSimpleContentTypes: Boolean = configuration.allowNonSimpleContentTypes

    private val headersListHeaderValue =
        configuration.headers.filterNot { it in Configuration.CorsSimpleRequestHeaders }
            .let { if (allowNonSimpleContentTypes) it + HttpHeaders.ContentType else it }
            .sorted()
            .joinToString(", ")

    private val methodsListHeaderValue =
        methods.filterNot { it in Configuration.CorsDefaultMethods }
            .map { it.value }
            .sorted()
            .joinToString(", ")

    private val maxAgeHeaderValue = configuration.maxAgeInSeconds.let { if (it > 0) it.toString() else null }
    private val exposedHeaders = when {
        configuration.exposedHeaders.isNotEmpty() -> configuration.exposedHeaders.sorted().joinToString(", ")
        else -> null
    }

    private val hostsNormalized = HashSet<String>(configuration.hosts.map { normalizeOrigin(it) })

    private val wildCardSubDomainPatterns = configuration.wildcardSubDomainPatterns

    private val wildcardSubDomainCacheNormalized = HashSet<String>()


    suspend fun intercept(context: PipelineContext<Unit, ApplicationCall>) {
        val call = context.call

        if (!allowsAnyHost || allowCredentials) {
            call.corsVary()
        }

        val origin = call.request.headers.getAll(HttpHeaders.Origin)?.singleOrNull()
            ?.takeIf(this::isValidOrigin)
            ?: return

        if (allowSameOrigin && call.isSameOrigin(origin)) return

        if (!corsCheckOrigins(origin)) {
            context.respondCorsFailed()
            return
        }

        if (call.request.httpMethod == HttpMethod.Options) {
            call.respondPreflight(origin)
            // If nothing else responds to OPTIONS, we should respond with OK
            context.finish()
            return
        }

        if (!call.corsCheckCurrentMethod()) {
            context.respondCorsFailed()
            return
        }

        call.accessControlAllowOrigin(origin)
        call.accessControlAllowCredentials()

        if (exposedHeaders != null) {
            call.response.header(HttpHeaders.AccessControlExposeHeaders, exposedHeaders)
        }
    }

    private suspend fun ApplicationCall.respondPreflight(origin: String) {
        if (!corsCheckRequestMethod() || !corsCheckRequestHeaders()) {
            respond(HttpStatusCode.Forbidden)
            return
        }

        accessControlAllowOrigin(origin)
        accessControlAllowCredentials()
        if (methodsListHeaderValue.isNotEmpty()) {
            response.header(HttpHeaders.AccessControlAllowMethods, methodsListHeaderValue)
        }
        if (headersListHeaderValue.isNotEmpty()) {
            response.header(HttpHeaders.AccessControlAllowHeaders, headersListHeaderValue)
        }
        accessControlMaxAge()

        respond(HttpStatusCode.OK)
    }

    private fun ApplicationCall.accessControlAllowOrigin(origin: String) {
        if (allowsAnyHost && !allowCredentials) {
            response.header(HttpHeaders.AccessControlAllowOrigin, "*")
        } else {
            response.header(HttpHeaders.AccessControlAllowOrigin, origin)
        }
    }

    private fun ApplicationCall.corsVary() {
        val vary = response.headers[HttpHeaders.Vary]
        if (vary == null) {
            response.header(HttpHeaders.Vary, HttpHeaders.Origin)
        } else {
            response.header(HttpHeaders.Vary, vary + ", " + HttpHeaders.Origin)
        }
    }

    private fun ApplicationCall.accessControlAllowCredentials() {
        if (allowCredentials) {
            response.header(HttpHeaders.AccessControlAllowCredentials, "true")
        }
    }

    private fun ApplicationCall.accessControlMaxAge() {
        if (maxAgeHeaderValue != null) {
            response.header(HttpHeaders.AccessControlMaxAge, maxAgeHeaderValue)
        }
    }

    private fun ApplicationCall.isSameOrigin(origin: String): Boolean {
        val requestOrigin = "${this.request.origin.scheme}://${this.request.origin.host}:${this.request.origin.port}"
        return normalizeOrigin(requestOrigin) == normalizeOrigin(origin)
    }

    private fun corsCheckOrigins(origin: String): Boolean {
        if (allowsAnyHost) {
            return true
        }

        val normalizedOrigin = normalizeOrigin(origin)

        return normalizedOrigin in hostsNormalized
                || normalizedOrigin in wildcardSubDomainCacheNormalized
                || checkWildcardSubdomain(origin)
    }

    private fun checkWildcardSubdomain(origin: String): Boolean {
        return if (wildCardSubDomainPatterns.any { it.matches(origin) }) {
            wildcardSubDomainCacheNormalized.add(normalizeOrigin(origin))
            true
        } else {
            false
        }
    }

    private fun ApplicationCall.corsCheckRequestHeaders(): Boolean {
        val requestHeaders =
            request.headers.getAll(HttpHeaders.AccessControlRequestHeaders)?.flatMap { it.split(",") }?.map {
                it.trim().toLowerCase()
            } ?: emptyList()

        return requestHeaders.none { it !in allHeadersSet }
    }

    private fun ApplicationCall.corsCheckCurrentMethod(): Boolean {
        return request.httpMethod in methods
    }

    private fun ApplicationCall.corsCheckRequestMethod(): Boolean {
        val requestMethod = request.header(HttpHeaders.AccessControlRequestMethod)?.let { HttpMethod(it) }
        return requestMethod != null && requestMethod in methods
    }

    private suspend fun PipelineContext<Unit, ApplicationCall>.respondCorsFailed() {
        call.respond(HttpStatusCode.Forbidden)
        finish()
    }

    private fun isValidOrigin(origin: String): Boolean {
        if (origin.isEmpty()) {
            return false
        }
        if (origin == "null") {
            return true
        }
        if ("%" in origin) {
            return false
        }

        val protoDelimiter = origin.indexOf("://")
        if (protoDelimiter <= 0) {
            return false
        }

        // check proto
        for (index in 0 until protoDelimiter) {
            if (!origin[index].isLetter()) {
                return false
            }
        }

        var portIndex = origin.length
        for (index in protoDelimiter + 3 until origin.length) {
            val ch = origin[index]
            if (ch == ':' || ch == '/') {
                portIndex = index + 1
                break
            }
            if (ch == '?') return false
        }

        for (index in portIndex until origin.length) {
            if (!origin[index].isDigit()) {
                return false
            }
        }

        return true
    }

    private fun normalizeOrigin(origin: String) =
        if (origin == "null" || origin == "*") origin else StringBuilder(origin.length).apply {
            append(origin)

            if (!origin.substringAfterLast(":", "").matches(numberRegex)) {
                val port = when (origin.substringBefore(':')) {
                    "http" -> "80"
                    "https" -> "443"
                    else -> null
                }

                if (port != null) {
                    append(':')
                    append(port)
                }
            }
        }.toString()

    class Configuration {
        companion object {

            val CorsDefaultMethods: Set<HttpMethod> = setOf(HttpMethod.Get, HttpMethod.Post, HttpMethod.Head)

            val CorsSimpleRequestHeaders: Set<String> = caseInsensitiveSet(
                HttpHeaders.Accept,
                HttpHeaders.AcceptLanguage,
                HttpHeaders.ContentLanguage,
                HttpHeaders.ContentType
            )

            val CorsSimpleResponseHeaders: Set<String> = caseInsensitiveSet(
                HttpHeaders.CacheControl,
                HttpHeaders.ContentLanguage,
                HttpHeaders.ContentType,
                HttpHeaders.Expires,
                HttpHeaders.LastModified,
                HttpHeaders.Pragma
            )

            val CorsSimpleContentTypes: Set<ContentType> =
                setOf(
                    ContentType.Application.FormUrlEncoded,
                    ContentType.MultiPart.FormData,
                    ContentType.Text.Plain
                )
        }

        val hosts: MutableSet<String> = HashSet()

        val wildcardSubDomainPatterns: MutableSet<Regex> = HashSet()

        val headers: MutableSet<String> = CaseInsensitiveStringSet()

        val methods: MutableSet<HttpMethod> = HashSet()

        val exposedHeaders: MutableSet<String> = CaseInsensitiveStringSet()

        var allowCredentials: Boolean = false

        var maxAgeInSeconds: Long = CORS_DEFAULT_MAX_AGE
            set(newMaxAge) {
                check(newMaxAge >= 0L) { "maxAgeInSeconds shouldn't be negative: $newMaxAge" }
                field = newMaxAge
            }

        var allowSameOrigin: Boolean = true

        var allowNonSimpleContentTypes: Boolean = false

        fun anyHost() {
            hosts.add("*")
        }

        fun host(host: String, schemes: List<String> = listOf("http"), subDomains: List<String> = emptyList()) {
            if (host == "*") {
                return anyHost()
            }
            require("://" !in host) { "scheme should be specified as a separate parameter schemes" }

            for (schema in schemes) {
                hosts.add("$schema://$host")

                if ("*" in subDomains) {
                    val hostPattern = "^$schema://.+\\.${host.replace(".", "\\.")}$"
                    wildcardSubDomainPatterns.add(hostPattern.toRegex())
                } else {
                    for (subDomain in subDomains) {
                        hosts.add("$schema://$subDomain.$host")
                    }
                }

            }
        }

        fun exposeHeader(header: String) {
            if (header !in CorsSimpleResponseHeaders) {
                exposedHeaders.add(header)
            }
        }

        fun allowXHttpMethodOverride() {
            header(HttpHeaders.XHttpMethodOverride)
        }

        fun header(header: String) {
            if (header.equals(HttpHeaders.ContentType, ignoreCase = true)) {
                allowNonSimpleContentTypes = true
                return
            }

            if (header !in CorsSimpleRequestHeaders) {
                headers.add(header)
            }
        }

        /**
         * Please note that CORS operates ONLY with REAL HTTP methods
         * and will never consider overridden methods via `X-Http-Method-Override`.
         * However you can add them here if you are implementing CORS at client side from the scratch
         * that you generally don't need to do.
         */
        fun method(method: HttpMethod) {
            if (method !in CorsDefaultMethods) {
                methods.add(method)
            }
        }
    }


    companion object Feature : ApplicationFeature<ApplicationCallPipeline, Configuration, NonStandardCors> {

        const val CORS_DEFAULT_MAX_AGE: Long = 24L * 3600 // 1 day

        override val key: AttributeKey<NonStandardCors> = AttributeKey("NonStandardCors")
        override fun install(pipeline: ApplicationCallPipeline, configure: Configuration.() -> Unit): NonStandardCors {
            val cors = NonStandardCors(Configuration().apply(configure))
            pipeline.intercept(ApplicationCallPipeline.Features) { cors.intercept(this) }
            return cors
        }

        private fun caseInsensitiveSet(vararg elements: String): Set<String> =
            CaseInsensitiveStringSet().apply { addAll(elements) }
    }
}
