package no.nav.personbruker.dittnav.common.util.security

data class AuthenticatedUser(val ident: String, val loginLevel: Int, val token: String) {

    fun createAuthenticationHeader(): String {
        return "Bearer $token"
    }

    override fun toString(): String {
        return "AuthenticatedUser(ident='***', loginLevel=$loginLevel, token='***')"
    }

}
