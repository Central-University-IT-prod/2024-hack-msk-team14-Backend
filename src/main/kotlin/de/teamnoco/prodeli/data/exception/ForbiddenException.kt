package de.teamnoco.prodeli.data.exception

import de.teamnoco.prodeli.web.response.WebError
import de.teamnoco.prodeli.web.response.WebErrorException

/**
 * @author <a href="https://github.com/Neruxov">Neruxov</a>
 */
@WebError(12)
object ForbiddenException : WebErrorException() {
    private fun readResolve(): Any = ForbiddenException
}