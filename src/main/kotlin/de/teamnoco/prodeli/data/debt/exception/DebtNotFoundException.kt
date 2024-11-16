package de.teamnoco.prodeli.data.debt.exception

import de.teamnoco.prodeli.web.response.WebError
import de.teamnoco.prodeli.web.response.WebErrorException

/**
 * @author <a href="https://github.com/Neruxov">Neruxov</a>
 */
@WebError(14)
object DebtNotFoundException : WebErrorException() {
    private fun readResolve(): Any = DebtNotFoundException
}