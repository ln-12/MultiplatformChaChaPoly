import Foundation
import CryptoKit


@objc public class DataResult: NSObject {
    
    @objc public private(set) var success: Data?
    @objc public private(set) var failure: Error?
    
    private init(_ success: Data?, _ failure: Error?) {
        super.init()
        self.success = success
        self.failure = failure
    }
    
    public convenience init(success: Data) {
        self.init(success, nil)
    }
    public convenience init(failure: Error) {
        self.init(nil, failure)
    }
    public convenience init(_ block: () throws -> Data) {
        do {
            let data = try block()
            self.init(success: data)
        } catch {
            self.init(failure: error)
        }
    }
}

@objc public class SwiftChachaPoly : NSObject {
    
    @objc public class func encrypt(key: Data, tag: Data, plaintext: Data) -> DataResult {
        DataResult {
            let symKey = SymmetricKey(data: key)
            let chaNonce: ChaChaPoly.Nonce = .init()
            let sealedBox: ChaChaPoly.SealedBox = try ChaChaPoly.seal(plaintext, using: symKey, nonce: chaNonce, authenticating: tag)

            // Returns:
            // ciphertext with the following format: nonce || actual_ciphertext || tag
            return sealedBox.nonce + sealedBox.ciphertext + sealedBox.tag
        }
    }

    @objc public class func decrypt(key: Data, nonce: Data, ciphertext: Data, authenticatedData: Data, tag: Data) -> DataResult {
        DataResult {
            let symKey = SymmetricKey(data: key)
            let chaNonce: ChaChaPoly.Nonce = try ChaChaPoly.Nonce(data: nonce)
            let sealedBox: ChaChaPoly.SealedBox = try ChaChaPoly.SealedBox(nonce: chaNonce, ciphertext: ciphertext, tag: authenticatedData)
            return try ChaChaPoly.open(sealedBox, using: symKey, authenticating: tag)
        }
    }
    
    @objc public class func getPrivateKey() -> DataResult {
        DataResult {
            return Curve25519.KeyAgreement.PrivateKey().rawRepresentation
        }
    }

    @objc public class func getPublicKey(privateKey: Data) -> DataResult {
        DataResult {
            return try! Curve25519.KeyAgreement.PrivateKey(rawRepresentation: privateKey).publicKey.rawRepresentation
        }
    }
    
    @objc public class func getSharedSecret(salt: Data, privateKey: Data, publicKey: Data) -> DataResult {
        DataResult {
            let privateKeyObject = try! Curve25519.KeyAgreement.PrivateKey(rawRepresentation: privateKey)
            let publicKeyObject = try! Curve25519.KeyAgreement.PublicKey(rawRepresentation: publicKey)
            let sharedSecret = try! privateKeyObject.sharedSecretFromKeyAgreement(with: publicKeyObject)
            let encryptionKey = sharedSecret.hkdfDerivedSymmetricKey(using: SHA256.self , salt: salt, sharedInfo: Data(), outputByteCount: 32)
            
            var keyData = Data()
            
            encryptionKey.withUnsafeBytes { keyData = Data(Array($0)) }
            
            return keyData
        }
    }
}
