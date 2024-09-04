package example.springsecuritytistory.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.RSAKey.Builder;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.proc.SecurityContext;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

@Configuration
public class JwtBeanGenerator {
    @Bean
    public JwtDecoder jwtDecoder(KeyPair keyPair) {
        return NimbusJwtDecoder.withPublicKey((RSAPublicKey) keyPair.getPublic())
                               .build();
    }

    @Bean
    public JwtEncoder jwtEncoder(KeyPair keyPair) {
        RSAKey key = new Builder((RSAPublicKey) keyPair.getPublic()).privateKey(
                                                                        (RSAPrivateKey) keyPair.getPrivate())
                                                                    .keyID("bean")
                                                                    .build();
        ImmutableJWKSet<SecurityContext> jwkSet = new ImmutableJWKSet<>(new JWKSet(key));
        return new NimbusJwtEncoder(jwkSet);
    }

    @Bean
    public KeyPair keyPair(@Value("${spring.application.name:key}") String path)
        throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, JOSEException {
        var privateKeyFile = new File(path);
        String publicKeyPath = path + ".pub";
        var publicKeyFile = new File(publicKeyPath);
        if (privateKeyFile.exists() && publicKeyFile.exists()) {
            return readKeyPairFromFile(path, publicKeyPath);
        }
        KeyPair keyPair = generateNewKeyPair();
        saveFileKeyPair(keyPair, path, publicKeyPath);
        return keyPair;
    }

    private KeyPair generateNewKeyPair() throws JOSEException {
        RSAKeyGenerator rsaKeyGenerator = new RSAKeyGenerator(RSAKeyGenerator.MIN_KEY_SIZE_BITS);
        RSAKey generated = rsaKeyGenerator.generate();
        return new KeyPair(generated.toPublicKey(), generated.toPrivateKey());
    }

    private KeyPair readKeyPairFromFile(String path, String publicKeyPath)
        throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        KeyFactory factory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = factory.generatePrivate(
            new PKCS8EncodedKeySpec(Files.readAllBytes(Paths.get(path))));
        PublicKey publicKey = factory.generatePublic(
            new X509EncodedKeySpec(Files.readAllBytes(Paths.get(publicKeyPath))));
        return new KeyPair(publicKey, privateKey);
    }

    private void saveFileKeyPair(KeyPair keyPair, String path, String publicKeyPath)
        throws IOException {
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();
        try (FileOutputStream fos = new FileOutputStream(path)) {
            fos.write(privateKey.getEncoded());
        }
        try (FileOutputStream fos = new FileOutputStream(publicKeyPath)) {
            fos.write(publicKey.getEncoded());
        }
    }

}
