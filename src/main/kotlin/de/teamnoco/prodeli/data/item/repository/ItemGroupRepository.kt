package de.teamnoco.prodeli.data.item.repository

import de.teamnoco.prodeli.data.item.model.ItemGroup
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface ItemGroupRepository : JpaRepository<ItemGroup, UUID> {
}