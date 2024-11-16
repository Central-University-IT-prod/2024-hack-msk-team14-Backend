package de.teamnoco.prodeli.data.user.exception

import de.teamnoco.prodeli.web.response.WebError
import de.teamnoco.prodeli.web.response.WebErrorException

/**
 * @author <a href="https://github.com/Neruxov">Neruxov</a>
 */
@WebError(13)
object UserNotFoundException : WebErrorException() {
    private fun readResolve(): Any = UserNotFoundException
}