@file:OptIn(ExperimentalStdlibApi::class)

package de.teamnoco.prodeli.service

import de.teamnoco.prodeli.data.user.model.User
import de.teamnoco.prodeli.util.random
import de.teamnoco.prodeli.web.request.auth.WebAppAuthRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.security.SecureRandom
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@Service
class AuthService(
//    @Value("\${ktgram.bot[0].token}")
//    private val botToken: String,
    private val userService: UserService
) {
    fun signIn(request: WebAppAuthRequest): User {
        // FIXME
//        val decodedParams = request.params.split("&").map { URLDecoder.decode(it, Charsets.UTF_8) }
//        val expectedHash = decodedParams.first { it.startsWith("hash=") }?.replaceFirst("hash=", "")
//        val decodedWithoutHash = decodedParams.filter { !it.startsWith("hash=") }
//        val dataCheckString = decodedWithoutHash.sorted().joinToString("\n")
//        val secretKey = calcHmacSha256(msg = botToken.toByteArray(), key = "WebAppData".toByteArray())
//        val dataHash = calcHmacSha256(dataCheckString.toByteArray(), secretKey).toHexString()
//
//        if (expectedHash != dataHash) {
//            throw HashCheckFailedException
//        }

        val token = generateToken()
        return userService.saveAndGetUser(request, token)
    }

    private fun ByteArray.toHexString(): String {
        return this.joinToString("") { "%02x".format(it) }
    }

    private fun calcHmacSha256(msg: ByteArray, key: ByteArray): ByteArray {
        val algorithm = "HmacSHA256"
        val mac = Mac.getInstance(algorithm)
        val secretKeySpec = SecretKeySpec(key, algorithm)
        mac.init(secretKeySpec)
        return mac.doFinal(msg)
    }

    fun generateToken() = buildString {
        repeat(128) {
            append(TOKEN_SYMBOLS.random(RANDOM))
        }
    }

    companion object {
        private val RANDOM = SecureRandom.getInstanceStrong()
        private val TOKEN_SYMBOLS = ('0'..'9') + ('a'..'z') + ('A'..'Z') + listOf('_', '.', '-')
    }
}