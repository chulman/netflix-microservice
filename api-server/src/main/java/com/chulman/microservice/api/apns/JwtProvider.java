package com.chulman.microservice.api.apns;

import com.chulman.microservice.api.exception.TokenCrteateException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.util.CharsetUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

/**
 * @author chulman
 */

@Slf4j
@Component
public class JwtProvider {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Data
    @NoArgsConstructor
    class JwtHeader {
        private String alg;
        private String kid;

        public JwtHeader(String alg, String kid) {
            this.alg = alg;
            this.kid = kid;
        }
    }

    @Data
    @NoArgsConstructor
    class JwtPayload {
        private String iss;
        private long iat;

        public JwtPayload(String iss, long iat) {
            this.iss = iss;
            this.iat = iat;
        }
    }

    /**
     * Parameter is All Registered Claim
     *
     * @param keyID
     * @param teamID
     * @param secret {String Data in AuthKey.p8 from Apns Development Site}
     * @return
     * @throws Exception
     */
    public String createToken(String keyID, String teamID, String secret) throws Exception {

        long nowMillis = System.currentTimeMillis() / 1000;
        String alg = "ES256";
        String header = objectMapper.writeValueAsString(new JwtHeader(alg,keyID));
        String payload = objectMapper.writeValueAsString(new JwtPayload(teamID, nowMillis));

        String encoded = encoding(header, payload);
        String token = signature(encoded, secret);
        log.info("Create JWT Token. {}", token);
        return token;

    }

    private String encoding(String header, String payload) {

        String base64Header = new String(Base64.getEncoder().encode(header.getBytes(StandardCharsets.UTF_8)));
        String base64Payload = new String(Base64.getEncoder().encode(payload.getBytes(StandardCharsets.UTF_8)));

        StringBuffer buffer = new StringBuffer();
        buffer.append(base64Header);
        buffer.append(".");
        buffer.append(base64Payload);

        return buffer.toString();
    }

    private String signature(String encoded, String secret) {

        StringBuffer buffer = new StringBuffer();

        buffer.append(encoded);
        buffer.append(".");
        buffer.append(ES256(secret, encoded));

        return buffer.toString();
    }


    private String ES256(String secret, String data) {

        KeyFactory kf = null;
        PrivateKey key = null;
        Signature sha256withECDSA = null;
        byte[] signed = null;

        try {
            kf = KeyFactory.getInstance("EC");
            KeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(secret));

            key = kf.generatePrivate(keySpec);

            sha256withECDSA = Signature.getInstance("SHA256withECDSA");
            sha256withECDSA.initSign(key);

            sha256withECDSA.update(data.getBytes(CharsetUtil.UTF_8));
            signed = sha256withECDSA.sign();

        } catch (NoSuchAlgorithmException e) {
            log.error("JWT Signature Fail. {}" + e);
            throw new TokenCrteateException(e);
        } catch (SignatureException e) {
            log.error("JWT Signature Fail. {}" + e);
            throw new TokenCrteateException(e);
        } catch (InvalidKeyException e) {
            log.error("JWT Signature Fail. {}" + e);
            throw new TokenCrteateException(e);
        } catch (InvalidKeySpecException e) {
            log.error("JWT Signature Fail. {}" + e);
            throw new TokenCrteateException(e);
        } finally {
            if (signed == null) {
                return null;
            }

            return new String(Base64.getEncoder().encode(signed), CharsetUtil.UTF_8);
        }
    }


}