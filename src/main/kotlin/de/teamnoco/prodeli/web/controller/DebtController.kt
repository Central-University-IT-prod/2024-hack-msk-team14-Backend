package de.teamnoco.prodeli.web.controller

import de.teamnoco.prodeli.data.debt.dto.DebtUpdateRequest
import de.teamnoco.prodeli.data.user.model.User
import de.teamnoco.prodeli.service.DebtService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/debt")
class DebtController(private val debtService: DebtService) {

    @PutMapping("/{id}")
    @Operation(description = "Обновить инфромацию про долг (в текущем состоянии только отметить, что он оплачен)")
    fun update(@AuthenticationPrincipal user: User, @PathVariable id: UUID, @RequestBody request: DebtUpdateRequest) =
        debtService.updateDebt(user, id, request)

}