package de.tubaf.multiplatformchachapoly

import com.google.crypto.tink.subtle.ChaCha20Poly1305

actual object ChachaPoly {
    actual fun encrypt(
        key: ByteArray,
        plaintext: ByteArray,
        tag: ByteArray
    ): ByteArray {
        // Returns:
        // ciphertext with the following format: nonce || actual_ciphertext || tag
        return ChaCha20Poly1305(key).encrypt(plaintext, tag)
    }

    actual fun decrypt(
        key: ByteArray,
        nonce: ByteArray,
        ciphertext: ByteArray,
        authenticatedData: ByteArray,
        tag: ByteArray
    ): ByteArray {
        return ChaCha20Poly1305(key).decrypt(nonce + ciphertext + authenticatedData, tag)
    }
}
