package de.teamnoco.prodeli.data.auth.exception

import de.teamnoco.prodeli.web.response.WebError
import de.teamnoco.prodeli.web.response.WebErrorException

@WebError(9)
data object HashCheckFailedException : WebErrorException() {
    private fun readResolve(): Any = HashCheckFailedException
}