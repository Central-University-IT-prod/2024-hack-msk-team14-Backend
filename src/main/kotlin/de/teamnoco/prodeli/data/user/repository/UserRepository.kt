package de.teamnoco.prodeli.data.user.repository

import de.teamnoco.prodeli.data.user.model.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

/**
 * @author <a href="https://github.com/Neruxov">Neruxov</a>
 */
interface UserRepository : JpaRepository<User, Long> {
    fun findUserByCurrentToken(currentToken: String): Optional<User>
}