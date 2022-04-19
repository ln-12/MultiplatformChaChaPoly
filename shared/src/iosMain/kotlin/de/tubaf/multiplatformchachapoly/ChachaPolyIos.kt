package de.tubaf.multiplatformchachapoly

import kotlinx.cinterop.autoreleasepool
import platform.Foundation.NSData
import swift.chachaPoly.*

actual object ChachaPoly {
    private fun DataResult.unwrap(): NSData {
        this.failure()?.let { throw ChachaPolyException(it.description ?: "unknown error") }
        return success() ?: error("Invalid result")
    }

    actual fun encrypt(
        key: ByteArray,
        plaintext: ByteArray,
        tag: ByteArray
    ): ByteArray {
        autoreleasepool {
            val keyData = key.toData()
            val tagData = tag.toData()
            val plaintextData = plaintext.toData()

            return SwiftChachaPoly.encryptWithKey(keyData, tagData, plaintextData).unwrap().toByteArray()
        }
    }

    actual fun decrypt(
        key: ByteArray,
        nonce: ByteArray,
        ciphertext: ByteArray,
        authenticatedData: ByteArray,
        tag: ByteArray
    ): ByteArray {
        autoreleasepool {
            return SwiftChachaPoly.decryptWithKey(
                key.toData(),
                nonce.toData(),
                ciphertext.toData(),
                authenticatedData.toData(),
                tag.toData()
            ).unwrap().toByteArray()
        }
    }
}
