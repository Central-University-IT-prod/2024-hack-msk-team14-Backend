package de.teamnoco.prodeli.data.debt.dto

import java.math.BigInteger

data class DebtInfoResponse(
    val debts: Map<Long, DebtInfo> // <user_id, debt_info>
) {

    data class DebtInfo(
        val youOwe: BigInteger,
        val theyOwe: BigInteger
    )

}
