package de.teamnoco.prodeli.data.invite.exception

import de.teamnoco.prodeli.web.response.WebError
import de.teamnoco.prodeli.web.response.WebErrorException

@WebError(11)
object InviteNotFoundException : WebErrorException() {
    private fun readResolve(): Any = InviteNotFoundException
}