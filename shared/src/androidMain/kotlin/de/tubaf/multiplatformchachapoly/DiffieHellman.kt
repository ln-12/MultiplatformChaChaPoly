package de.tubaf.multiplatformchachapoly

import com.google.crypto.tink.subtle.Hkdf
import com.google.crypto.tink.subtle.X25519

actual object DiffieHellman {
    actual fun calculateSharedSecret(
        salt: ByteArray,
        publicKey: ByteArray,
        privateKey: ByteArray,
    ): ByteArray {
        val shared = X25519.computeSharedSecret(privateKey, publicKey)

        // derive encryption key from shared secret
        return Hkdf.computeHkdf("HMACSHA256", shared, salt, byteArrayOf(), 32)
    }

    actual fun getPrivateKey(): ByteArray {
        return X25519.generatePrivateKey()
    }

    actual fun getPublicKey(
        privateKey: ByteArray
    ): ByteArray {
        return X25519.publicFromPrivate(privateKey)
    }
}
