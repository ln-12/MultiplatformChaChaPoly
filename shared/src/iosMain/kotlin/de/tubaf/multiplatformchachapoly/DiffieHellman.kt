package de.tubaf.multiplatformchachapoly

import de.tubaf.multiplatformchachapoly.toData
import kotlinx.cinterop.autoreleasepool
import platform.Foundation.NSData
import swift.chachaPoly.*

actual object DiffieHellman {
    private fun DataResult.unwrap(): NSData {
        this.failure()?.let { throw ChachaPolyException(it.description ?: "unknown error") }
        return success() ?: error("Invalid result")
    }

    actual fun calculateSharedSecret(
        salt: ByteArray,
        publicKey: ByteArray,
        privateKey: ByteArray,
    ): ByteArray {
        autoreleasepool {
            val saltData = salt.toData()
            val publicKeyData = publicKey.toData()
            val privateKeyData = privateKey.toData()

            return SwiftChachaPoly.getSharedSecretWithSalt(saltData, privateKeyData, publicKeyData).unwrap().toByteArray()
        }
    }

    actual fun getPrivateKey(

    ): ByteArray {
        autoreleasepool {
            return SwiftChachaPoly.getPrivateKey().unwrap().toByteArray()
        }
    }

    actual fun getPublicKey(
        privateKey: ByteArray
    ): ByteArray {
        autoreleasepool {
            return SwiftChachaPoly.getPublicKeyWithPrivateKey(privateKey.toData()).unwrap().toByteArray()
        }
    }
}