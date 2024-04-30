import java.security.*;
import java.security.spec.*;
import java.util.Base64;

public class KeyPairFromString {
	
    public static void main(String[] args) {
        try {
        	KeyPairFromString keyPairFromString = new KeyPairFromString();
            // String representations of RSA keys
            String publicKeyStr = "-----BEGIN PUBLIC KEY-----\n" +
                                   "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmLpjE44FZUOm1ZRFK6Kvqa69jvXNJLSm0YHOSRdf25qnW1tuUIP2eWsDW/fpLeTWLU9x4NWDybZGD8w3/VluvjlF07gY5aT/Gis+9CtLEOHAJxE6PzhD1S7Po4syubrEnRlhyuHVg53tt4eQrhuO1ku5F3VQCvA5f9Jzg4RFneM6ndpTmt02Ouf9Jxq7cdm/eca6cz6l21eo1s6nA7d5j1XaVqCX9OFB5/WLDDWuV/k9Wc4snv3PHqOGGmMjP80K0cLniFPF77gqbzaOkF4fab25XBer1aO0ChUGr7c+qDaDJNSgzAW5HDeyYihWCClEvpcazeup3flWR8SXLwLPGwIDAQAB\n" +
                                   // Public key goes here
                                   "-----END PUBLIC KEY-----";

            String privateKeyStr = "-----BEGIN PRIVATE KEY-----\n" +
                                    "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCYumMTjgVlQ6bVlEUroq+prr2O9c0ktKbRgc5JF1/bmqdbW25Qg/Z5awNb9+kt5NYtT3Hg1YPJtkYPzDf9WW6+OUXTuBjlpP8aKz70K0sQ4cAnETo/OEPVLs+jizK5usSdGWHK4dWDne23h5CuG47WS7kXdVAK8Dl/0nODhEWd4zqd2lOa3TY65/0nGrtx2b95xrpzPqXbV6jWzqcDt3mPVdpWoJf04UHn9YsMNa5X+T1Zziye/c8eo4YaYyM/zQrRwueIU8XvuCpvNo6QXh9pvblcF6vVo7QKFQavtz6oNoMk1KDMBbkcN7JiKFYIKUS+lxrN66nd+VZHxJcvAs8bAgMBAAECggEAM3jFgUag7OLi7CIfObiHaTFZ8z9F1vQHMJ6Frj5xcOprMq/ausnunDcDl9AyRuRThku6/mIXGzkR3asex4SHfRaCQPtPcL2tt90RR2r7zoG2p7P3pGzK96LiGaUFAHFAaA2w1pZLxiLIhczrZrIk7nXwuEYyJq2VQ03i3lUKvU3dFtOmAf+fA88+AtfBsXIBrWupc2d84H3HH1qSVKKOnXf5i6r+f2gC1xKmAvCERj+qIqbTWKJ+t8eR0d8qc0URsnr0DeoV6tcH+JYRAO5mhauIiYUEcgDtoXZXcJ8kmC+kSG3y9sw1poMaCfP82ey/fl4GpXKfoI5+mk0dPf0lAQKBgQDBC8kkr6ZL9H5NK9hajhMiqUAvsloqeZkBPZfy/i81K/OfeJR6NPTIDLERcAwzv3kim0SJVVRDDqSwmwiovp0WmhZoDeA8xCco88k8zIrO/JkKw9VFtJKkjrXZldP3HF/lWIwSjk2XdXqJB/etGKtfCZttE9GgQ98tPcd8rbk/gQKBgQDKiLR78SQ+Ha5FSiihNAg5oSO4/7m4o1P07dEF7fJmEessADi28tuzn2t/1sHzJslUN9JGlNQmWjfFc3kqoRcL2xTFkVpCQpvnMAqzSaUe4zoZUdohhktCwP8Uio118HbuX9/gg3sLTtnOkxxg9ZfSCVg0pZVWMKln4p5y8IdcmwKBgQCFuXpOdXaG6wiKwLKQmBZdTkzuPWgEQFNi7p3Rb46YEKgkccy4kvJ1mYUXBT0Oo7Zv6Iqjb359LSmcROjMq635uCeJUQNiVKaj0aw296G4HHaPZHTaYmmqoEkMPqOka/1py9rMB9Nr9a86OVCykYI0xVzrRG1TzXBN0JvdnCHVAQKBgG2nGeBIrFCdDS8ieWDdpN5eqdKNUWXvt5LR9ur46tXsOaD29b3HOr/DPhZWlWcpxrK8DjJ8fWfOTIewfxdfY/TdRh+5FF7vZS0Dy6dJ5gYVC+2NbUXdSX34s6HRaclCyYFBkxog1UtIA5BeAW1VSj9DI+hNBc9GRVGjVhnws7ilAoGANmpdszl4e80ZAZjsdHUITdEdV6Wpu/VfKAFiS2pUVNcEmPnvdJuHYApgCdooa4EHHFu6vSZvwBzb6N0EFQZMSlYf2J4TgSEJZs7lQKJW9A0hgPjtzzDJsVugLUsx5XfjaLxIwXctCyF2wqYJnc6tQ2SM3WNWvoC2Gs9GbH9I+2g=\n" +
                                    // Private key goes here
                                    "-----END PRIVATE KEY-----";

            // Parse the string representations of keys into Key objects
            PublicKey publicKey = keyPairFromString.parsePublicKey(publicKeyStr);
            PrivateKey privateKey = keyPairFromString.parsePrivateKey(privateKeyStr);

            // Create a KeyPair object
            KeyPair keyPair = new KeyPair(publicKey, privateKey);

            // Now you can use the key pair for cryptographic operations
            System.out.println("KeyPair created successfully: " + keyPair);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Parse a string representation of a public key into a PublicKey object
    public PublicKey parsePublicKey(String publicKeyStr) throws Exception {
        publicKeyStr = publicKeyStr
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        byte[] keyBytes = Base64.getDecoder().decode(publicKeyStr);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

    // Parse a string representation of a private key into a PrivateKey object
    public PrivateKey parsePrivateKey(String privateKeyStr) throws Exception {
        privateKeyStr = privateKeyStr
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] keyBytes = Base64.getDecoder().decode(privateKeyStr);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }
}
