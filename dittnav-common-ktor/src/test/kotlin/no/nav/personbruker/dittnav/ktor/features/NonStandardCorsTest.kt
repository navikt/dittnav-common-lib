package no.nav.personbruker.dittnav.ktor.features

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test

internal class NonStandardCorsTest {
    private val scheme = "https"
    private val subDomain = "subdomain"
    private val otherSubDomain = "subdomain.other"
    private val host = "test.host"
    private val otherHost = "other.host"

    @Test
    fun `Should work like standard CORS feature host is wildcard`() = withTestApplication<Unit>({
        testApi("*")
    }) {
        val nonCorsResponse = nonCorsCall()
        val hostOnlyResponse = corsCall("$scheme://$host")
        val otherHostResponse = corsCall("$scheme://$otherHost")
        val hostAndSubResponse = corsCall("$scheme://$subDomain.$host")
        val hostAndOtherSubResponse = corsCall("$scheme://$otherSubDomain.$host")

        nonCorsResponse.statusCode `should be equal to` HttpStatusCode.OK
        hostOnlyResponse.statusCode `should be equal to` HttpStatusCode.OK
        otherHostResponse.statusCode `should be equal to` HttpStatusCode.OK
        hostAndSubResponse.statusCode `should be equal to` HttpStatusCode.OK
        hostAndOtherSubResponse.statusCode `should be equal to` HttpStatusCode.OK
    }

    @Test
    fun `Should work like standard CORS feature when no subdomain is defined`() = withTestApplication<Unit>({
        testApi(host, listOf(scheme))
    }) {
        val nonCorsResponse = nonCorsCall()
        val hostOnlyResponse = corsCall("$scheme://$host")
        val otherHostResponse = corsCall("$scheme://$otherHost")
        val hostAndSubResponse = corsCall("$scheme://$subDomain.$host")
        val hostAndOtherSubResponse = corsCall("$scheme://$otherSubDomain.$host")

        nonCorsResponse.statusCode `should be equal to` HttpStatusCode.OK
        hostOnlyResponse.statusCode `should be equal to` HttpStatusCode.OK
        otherHostResponse.statusCode `should be equal to` HttpStatusCode.Forbidden
        hostAndSubResponse.statusCode `should be equal to` HttpStatusCode.Forbidden
        hostAndOtherSubResponse.statusCode `should be equal to` HttpStatusCode.Forbidden
    }

    @Test
    fun `Should work like standard CORS feature when subdomain is defined`() = withTestApplication<Unit>({
        testApi(host, listOf(scheme), listOf(subDomain))
    }) {
        val nonCorsResponse = nonCorsCall()
        val hostOnlyResponse = corsCall("$scheme://$host")
        val otherHostResponse = corsCall("$scheme://$otherHost")
        val hostAndSubResponse = corsCall("$scheme://$subDomain.$host")
        val hostAndOtherSubResponse = corsCall("$scheme://$otherSubDomain.$host")

        nonCorsResponse.statusCode `should be equal to` HttpStatusCode.OK
        hostOnlyResponse.statusCode `should be equal to` HttpStatusCode.OK
        otherHostResponse.statusCode `should be equal to` HttpStatusCode.Forbidden
        hostAndSubResponse.statusCode `should be equal to` HttpStatusCode.OK
        hostAndOtherSubResponse.statusCode `should be equal to` HttpStatusCode.Forbidden
    }

    @Test
    fun `Should allow for wildcard subdomains`() = withTestApplication<Unit>({
        testApi(host, listOf(scheme), listOf("*"))
    }) {
        val nonCorsResponse = nonCorsCall()
        val hostOnlyResponse = corsCall("$scheme://$host")
        val otherHostResponse = corsCall("$scheme://$otherHost")
        val hostAndSubResponse = corsCall("$scheme://$subDomain.$host")
        val hostAndOtherSubResponse = corsCall("$scheme://$otherSubDomain.$host")

        nonCorsResponse.statusCode `should be equal to` HttpStatusCode.OK
        hostOnlyResponse.statusCode `should be equal to` HttpStatusCode.OK
        otherHostResponse.statusCode `should be equal to` HttpStatusCode.Forbidden
        hostAndSubResponse.statusCode `should be equal to` HttpStatusCode.OK
        hostAndOtherSubResponse.statusCode `should be equal to` HttpStatusCode.OK
    }
    
    @Test
    fun `Wildcard subdomains should be defined and applied for each host`() = withTestApplication<Unit>({
        testApi(host, listOf(scheme), listOf("*")) {
            host(otherHost, listOf(scheme))
        }
    }) {
        val hostAndSubResponse = corsCall("$scheme://$subDomain.$host")
        val otherHostResponse = corsCall("$scheme://$otherSubDomain.$otherHost")

        hostAndSubResponse.statusCode `should be equal to` HttpStatusCode.OK
        otherHostResponse.statusCode `should be equal to` HttpStatusCode.Forbidden
    }

    @Test
    fun `Wilcard subdomains should not allow origin to be merely a substring of valid host`() = withTestApplication<Unit>({
        testApi(host, listOf(scheme), listOf("*"))
    }) {
        val maliciousPrefixCallResponse = corsCall("$scheme://malicious-$host")
        val maliciousSuffixCallResponse = corsCall("$scheme://$host-malicious")
        val maliciousCallResponse = corsCall("$scheme://malicious-$host-malicious")

        maliciousPrefixCallResponse.statusCode `should be equal to` HttpStatusCode.Forbidden
        maliciousSuffixCallResponse.statusCode `should be equal to` HttpStatusCode.Forbidden
        maliciousCallResponse.statusCode `should be equal to` HttpStatusCode.Forbidden
    }


    private fun Application.testApi(
        host: String,
        schemes: List<String> = emptyList(),
        subdomains: List<String> = emptyList(),
        config: (NonStandardCors.Configuration.() -> Unit)? = null
    ) {
        install(NonStandardCors) {
            host(host, schemes, subdomains)
            config?.invoke(this)
        }

        routing {
            get("/test") {
                call.respond(HttpStatusCode.OK, "Ok")
            }
        }
    }



    private fun TestApplicationEngine.nonCorsCall() =
        handleRequest(HttpMethod.Get, "/test")

    private fun TestApplicationEngine.corsCall(origin: String) =
        handleRequest(HttpMethod.Get, "/test") {
            addHeader(HttpHeaders.Origin, origin)
        }

    private val TestApplicationCall.statusCode get() = response.status()
}