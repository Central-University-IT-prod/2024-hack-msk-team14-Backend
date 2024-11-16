package de.teamnoco.prodeli.service

import de.teamnoco.prodeli.data.user.model.User
import de.teamnoco.prodeli.data.user.repository.UserRepository
import de.teamnoco.prodeli.web.request.auth.WebAppAuthRequest
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

/**
 * @author <a href="https://github.com/Neruxov">Neruxov</a>
 */
@Service
class UserService(
    private val userRepository: UserRepository
) {

    fun me(user: User) = user

    fun getUserById(userId: Long) = userRepository.findById(userId).getOrNull()

    fun getUserByToken(token: String) = userRepository.findUserByCurrentToken(token).getOrNull()

    fun saveAndGetUser(request: WebAppAuthRequest, token: String): User {
        val optionalUser = userRepository.findById(request.userId)
        var isNew = false
        return userRepository.save(if (optionalUser.isPresent) {
            optionalUser.get()
                .copy(firstName = request.firstName, lastName = request.lastName, username = request.username, currentToken = token)
        } else {
            isNew = true
            User(
                request.userId, request.firstName, request.lastName, request.username, token
            )
        }).apply {
            this.isNew = isNew
        }
    }
}