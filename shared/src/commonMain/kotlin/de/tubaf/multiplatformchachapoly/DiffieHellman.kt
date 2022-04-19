package de.tubaf.multiplatformchachapoly

expect object DiffieHellman {
    fun calculateSharedSecret(
        salt: ByteArray,
        publicKey: ByteArray,
        privateKey: ByteArray,
    ): ByteArray

    fun getPrivateKey(): ByteArray

    fun getPublicKey(
        privateKey: ByteArray
    ): ByteArray
}
