package de.teamnoco.prodeli.util.model

interface SerializableModel {
    fun serialize(): Map<String, Any>
}
