import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class OSEncryption {

	// Generate key
	private SecretKey secretKey = null;
	private Cipher cipher = null;
	private static final String RSA = "RSA";
	private String publicKey = null;
	private String privateKey = null;
	
	public OSLogger log = null;
	public KeyPair keypair = null; 
	
	public SecretKey getSecretKey() {
		return this.secretKey;
	}

	
	public void setSecretKey(SecretKey myKey) {
		this.secretKey = myKey;
	}
	
	public String getSecretKeyStr() {
		String tempSecretKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
		log.debug("Secret key as a string - " + tempSecretKey);
		return tempSecretKey;
	}
	
	public String getPrivateKey() {
		return this.privateKey;
	}

	public String getPublicKey() {
		return this.publicKey;
	}

	public synchronized SecretKey generateSecretKey() throws Exception {
		// Generate key
		//System.out.println("AES Format : " + KeyGenerator.getInstance("AES128").generateKey().getFormat());
		SecretKey newSecretKey = KeyGenerator.getInstance("AES").generateKey();
		return (newSecretKey);

		/*
		byte[] decodedKey = Base64.getDecoder().decode(tempSecretKey);
		SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
		
		//SecretKey convertedKey = getSecretKeyFromStr(tempSecretKey);
		tempSecretKey = Base64.getEncoder().encodeToString(originalKey.getEncoded());
		System.out.println("Secret key as a string - " + tempSecretKey);
		/*
	// create new key
SecretKey secretKey = KeyGenerator.getInstance("AES").generateKey();
// get base64 encoded version of the key
String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());

// decode the base64 encoded string
byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
// rebuild key using SecretKeySpec
SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES"); 
		 */
		// Generate key
		// SecretKey secretKeyTemp = KeyGenerator.getInstance("AES").


	}

	public OSEncryption() throws Exception {
		// Generate key
		secretKey = this.generateSecretKey();

		// Initialize Cipher
		cipher = Cipher.getInstance("AES");

		// Initialize log
		log = new OSLogger("OSEncryption");

		// Generate Private / Public Key
		generateRSAKeyPair();
	}

	public void setEncryptMode() throws Exception {
		cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
	}

	public void setDecryptMode() throws Exception {
		cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
	}

	public String encrypt(String originalMessage) throws Exception {
		//Set the encryption mode with the private key
		setEncryptMode();
		// Encrypt the message
		byte[] encryptedMessage = cipher.doFinal(originalMessage.getBytes(StandardCharsets.UTF_8));

		// Convert the encrypted message to Base64 encoded string
		String encodedMessage = Base64.getEncoder().encodeToString(encryptedMessage);

		// System.out.println("Original Message: " + originalMessage);
		// System.out.println("Encrypted Message: " + encodedMessage);

		return (encodedMessage);
	}

	public String decrypt(String encodedMessage) throws Exception {

		// Set the decrypt mode
		setDecryptMode();
		
		// Decrypt the message		
		byte[] decryptedMessage = cipher.doFinal(Base64.getDecoder().decode(encodedMessage));

		String originalMessage = new String(decryptedMessage, StandardCharsets.UTF_8);

		// System.out.println("Encrypted Message: " + encodedMessage);
		// System.out.println("Original Message: " + originalMessage);

		return (originalMessage);
	}

	public static void main2(String[] args) {
		try {
			
			OSEncryption encryption = new OSEncryption();
			encryption.setEncryptMode();
			String string = "Hare Krishna";
			String encryptedString = encryption.encrypt(string);
			encryption.setDecryptMode();
			String backward = encryption.decrypt(encryptedString);
			if (string.compareTo(backward) == 0) {
				System.out.println("Success");
			} else {
				System.out.println("Failure");
			}
			System.out.println("Public Key:" + encryption.getPublicKey());
			System.out.println("Private Key:" + encryption.getPrivateKey());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		try {
			OSEncryption encryption = new OSEncryption();
			SecretKey sec = encryption.secretKey;
			
			OSEncryption encryption1 = new OSEncryption();
			PublicKey pub = encryption1.keypair.getPublic();
			PrivateKey prv = encryption1.keypair.getPrivate();
			
			String mySecretKeyTest = encryption.getSecretKeyStr();
			System.out.println("mySecretKeyTest - " + mySecretKeyTest);
			String encryptedSecretKey = encryption1.do_RSAEncryptionKey(mySecretKeyTest, pub);
			System.out.println("encryptedSecretKey - " + encryptedSecretKey);
			
			String mySecretKeyTestDec = encryption1.do_RSADecryptionKey(encryptedSecretKey, prv);
			System.out.println("mySecretKeyTestDec - " + mySecretKeyTestDec);
			
			byte[] decodedKey = Base64.getDecoder().decode(mySecretKeyTestDec);
			SecretKey sec2 = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public static void main1(String[] args) throws Exception {

		// Generate key
		SecretKey secretKey = KeyGenerator.getInstance("AES").generateKey();

		// Original message
		String originalMessage = "Hello, world!";

		// Create Cipher instance and initialize it to ENCRYPT_MODE
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);

		// Encrypt the message
		byte[] encryptedMessage = cipher.doFinal(originalMessage.getBytes(StandardCharsets.UTF_8));

		// Convert the encrypted message to Base64 encoded string
		String encodedMessage = Base64.getEncoder().encodeToString(encryptedMessage);

		System.out.println("Original Message: " + originalMessage);
		System.out.println("Encrypted Message: " + encodedMessage);

		// Reinitialize the cipher to DECRYPT_MODE
		cipher.init(Cipher.DECRYPT_MODE, secretKey);

		// Decrypt the message
		byte[] decryptedMessage = cipher.doFinal(Base64.getDecoder().decode(encodedMessage));

		System.out.println("Decrypted Message: " + new String(decryptedMessage, StandardCharsets.UTF_8));
	}

	// Generating public and private keys using RSA algorithm.
	public void generateRSAKeyPair() throws Exception {

		// Generate the private/public key only once during each run
		if (this.privateKey == null && this.publicKey == null) {
			SecureRandom secureRandom = new SecureRandom();

			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA);

			keyPairGenerator.initialize(2048, secureRandom);

			keypair = keyPairGenerator.generateKeyPair();

			// System.out.println("Public Key is: " +
			// DatatypeConverter.printHexBinary(keypair.getPublic().getEncoded()));
			// Convert the encrypted message to Base64 encoded string
			publicKey = Base64.getEncoder().encodeToString(keypair.getPublic().getEncoded());
			log.debug("Public Key is:" + publicKey);
			// System.out.println("Public Key is:" + publicKey);

			// System.out.println("Private Key is: " +
			// DatatypeConverter.printHexBinary(keypair.getPrivate().getEncoded()));
			privateKey = Base64.getEncoder().encodeToString(keypair.getPrivate().getEncoded());
			log.debug("Private Key is:" + privateKey);
			// System.out.println("Private Key is:" + privateKey);

		}
	}

	// Encryption function which converts
	// the plainText into a cipherText
	// using private Key.
	public String do_RSAEncryption(String plainText, PublicKey publicKey) throws Exception {
		Cipher cipher = Cipher.getInstance(RSA);
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		byte[] cipherText = cipher.doFinal(plainText.getBytes());
		return (Base64.getEncoder().encodeToString(cipherText));
	}

	// Encryption function which converts
	// the plainText into a cipherText
	// using private Key.
	public String do_RSAEncryptionKey(String plainText, PublicKey publicKey) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.PUBLIC_KEY, publicKey);
		byte[] cipherText = cipher.doFinal(plainText.getBytes());
		return (Base64.getEncoder().encodeToString(cipherText));
	}
	
	
	public String do_RSADecryption(byte[] cipherText, PrivateKey privateKey) throws Exception {
		Cipher cipher = Cipher.getInstance(RSA);
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		byte[] result = cipher.doFinal(cipherText);
		return new String(result);
	}

	public String do_RSADecryptionKey(String cipherText, PrivateKey privateKey) throws Exception {
		byte[] encryptedSecretKeyDecoded = Base64.getDecoder().decode(cipherText);
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.PRIVATE_KEY, privateKey);
		byte[] result = cipher.doFinal(encryptedSecretKeyDecoded);
		return new String(result);
	}

	public SecretKey getSecretKey(String tempSecretKey) throws Exception {
		byte[] decodedKey = Base64.getDecoder().decode(tempSecretKey);
		SecretKey tempKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
		return (tempKey);
	}	

	//Do Not Use
	public SecretKey getSecretKeyFromStr(String aesKeyString) {
		SecretKey secretKey = null;

		try {
			// String aesKeyString = "mysecretkey"; // Replace with your AES key string

			// Generate AES key bytes from the string using a secure hash function (e.g.,
			// SHA-256)
			byte[] keyBytes = generateAESKeyBytes(aesKeyString);

			// Create a SecretKey object from the key bytes
			secretKey = new SecretKeySpec(keyBytes, "AES");

			// Now you can use the secretKey for AES encryption/decryption
			System.out.println("SecretKey created successfully: " + secretKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (secretKey);
	}

	//Do Not Use
	// Generate AES key bytes from a string using a secure hash function (e.g.,
	// SHA-256)
	private static byte[] generateAESKeyBytes(String aesKeyString) throws Exception {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		//MessageDigest digest = MessageDigest.getInstance("AES");
		byte[] keyBytes = digest.digest(aesKeyString.getBytes("UTF-8"));

		// Use only the first 128 bits (16 bytes) for AES-128, or the first 192 bits (24
		// bytes) for AES-192, etc.
		// Adjust as necessary depending on the desired AES key length
		int keySize = 128; // AES-128
		int keyLength = keySize / 8;
		return Arrays.copyOf(keyBytes, keyLength);
	}

}