package de.teamnoco.prodeli.util.model

interface EntityConverter<K, V> {
    fun V.asModel(): K

    fun K.asEntity(): V
}