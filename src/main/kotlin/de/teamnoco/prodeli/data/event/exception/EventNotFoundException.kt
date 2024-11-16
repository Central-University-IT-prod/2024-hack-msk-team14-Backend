package de.teamnoco.prodeli.data.event.exception

import de.teamnoco.prodeli.web.response.WebError
import de.teamnoco.prodeli.web.response.WebErrorException

/**
 * @author <a href="https://github.com/Neruxov">Neruxov</a>
 */
@WebError(10)
object EventNotFoundException : WebErrorException() {
    private fun readResolve(): Any = EventNotFoundException
}