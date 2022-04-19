package de.tubaf.multiplatformchachapoly

expect object ChachaPoly {
    // Returns:
    // nonce || ciphertext || authenticationTag
    fun encrypt(
        key: ByteArray,
        plaintext: ByteArray,
        tag: ByteArray
    ): ByteArray

    // Returns:
    // encrypted text
    fun decrypt(
        key: ByteArray,
        nonce: ByteArray,
        ciphertext: ByteArray,
        authenticatedData: ByteArray,
        tag: ByteArray
    ): ByteArray
}
