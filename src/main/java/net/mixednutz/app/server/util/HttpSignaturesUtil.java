package net.mixednutz.app.server.util;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

public class HttpSignaturesUtil {
	
	private static final Logger LOG = LoggerFactory.getLogger(HttpSignaturesUtil.class);
	
	private static final DateTimeFormatter httpDateFormatter =
			DateTimeFormatter
				.ofPattern("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US)
				.withZone(ZoneId.of("GMT"));

	public static KeyPair generateKeyPair() {
		KeyPairGenerator generator;
		try {
			generator = KeyPairGenerator.getInstance("RSA");
			generator.initialize(2048);
			return generator.generateKeyPair();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Unable to generate RSA keypair", e);
		}
	}
	
	private static byte[] publicKeyPemToBytes(String pem) {
		String publicKeyPEM = pem
			      .replace("-----BEGIN PUBLIC KEY-----", "")
			      .replaceAll(System.lineSeparator(), "")
			      .replace("-----END PUBLIC KEY-----", "");
		return Base64.getMimeDecoder().decode(publicKeyPEM);
	}
	
	public static String publicKeyBytesToPem(byte[] publicKey) {
		return new StringBuffer()
				.append("-----BEGIN PUBLIC KEY-----")
					.append(System.lineSeparator())
				.append(Base64.getMimeEncoder().encodeToString(publicKey))
					.append(System.lineSeparator())
				.append("-----END PUBLIC KEY-----")
				.toString();
	}
	
	private static KeyFactory getKeyFactory() {
		try {
			return KeyFactory.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Unable to create KeyFactory", e);
		}
	}
	
	public static String publicKeyToPem(PublicKey publicKey) {
		return publicKeyBytesToPem(publicKey.getEncoded());
	}
	
	public static PublicKey getPublicKeyFromPem(String pem) {
		EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(
				publicKeyPemToBytes(pem));
		try {
			return getKeyFactory().generatePublic(publicKeySpec);
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public static PrivateKey getPrivateKeyFromBytes(byte[] privateKey) {
		EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKey);
		try {
			return getKeyFactory().generatePrivate(privateKeySpec);
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public static void signRequest(
			URI destination,
			HttpMethod method, 
			HttpHeaders headers, 
			byte[] body,
			PrivateKey privateKey, 
			String keyId)  {
		
		// HOST & PATH
		String path = destination.getPath();
		String host = destination.getHost();
		if (destination.getPort()!=-1) {
			host+=":"+destination.getPort();
		}
		
		// DATE
		ZonedDateTime date = ZonedDateTime.now();
		headers.setDate(date);
		
		// DIGEST
		String digestHeader = null;
		if (body!=null) {
			digestHeader = "SHA-256=";
			try {
				digestHeader+=Base64.getEncoder().encodeToString(
						MessageDigest.getInstance("SHA-256").digest(body));
			}catch(NoSuchAlgorithmException ignore){}
		}
		
		if(digestHeader!=null) {
			headers.set("Digest", digestHeader);
		}
		
		// STRING TO SIGN
		StringBuffer strToSign = new StringBuffer()
			.append("(request-target): ")
				.append(method.name().toLowerCase()).append(" ")
				.append(path).append('\n')
			.append("host: ").append(host).append('\n')
			.append("date: ")
				.append(httpDateFormatter.format(date));			
		if(digestHeader!=null) {
			strToSign.append('\n')
			.append("digest: ").append(digestHeader);
		}
		
		// SIGN STRING
		Signature sig;
		byte[] signature;
		try {
			sig = Signature.getInstance("SHA256withRSA");
			sig.initSign(privateKey);
			LOG.info("Signature string: \n"+strToSign);
			sig.update(strToSign.toString().getBytes(StandardCharsets.UTF_8));
			signature = sig.sign();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		// SIGNATURE HEADER
		String sigHeader = new StringBuffer()
				.append("keyId=\""+keyId+"\",")
				.append("headers=\"(request-target) host date"+(digestHeader!=null ? " digest\"," : "\","))
				.append("algorithm=\"rsa-sha256\",")
				.append("signature=\"").append(Base64.getEncoder()
						.encodeToString(signature)).append("\"")
				.toString();
		LOG.info("Signature: "+ sigHeader);
		headers.set("Signature", sigHeader);
	}


	public static void verifyRequest(
			URI destination,
			HttpMethod method, 
			HttpHeaders headers, 
			Function<String,PublicKey> publicKeyForKeyId) {
		
		String sigHeader = headers.getFirst("Signature");
		if(sigHeader==null) {
			throw new RuntimeException("Request is missing Signature header");
		}
		String[] parts = sigHeader.split(",");
		if(parts.length<=1) {
			throw new RuntimeException("Signature header has invalid format");
		}
		
		Map<String, String> values = new HashMap<>();
		for (String part: parts) {
			String[] pair = part.split("=");
			String value = pair[1].replace("\"", "");
			values.put(pair[0], value);
		}
		
		if(!values.containsKey("algorithm") ||
				!"rsa-sha256".equalsIgnoreCase(values.get("algorithm"))) {
			throw new RuntimeException("Signature header is missing algorithm");
		}
		if(!values.containsKey("keyId"))
			throw new RuntimeException("Signature header is missing keyId field");
		if(!values.containsKey("signature"))
			throw new RuntimeException("Signature header is missing signature field");
		if(!values.containsKey("headers"))
			throw new RuntimeException("Signature header is missing headers field");
		
		//Get Public Key
		final PublicKey publicKey = publicKeyForKeyId.apply(values.get("keyId"));
		
		byte[] signature=Base64.getDecoder().decode(values.get("signature"));
		List<String> sigHeaders = Arrays.asList(values.get("headers").split(" "));
		
		if(!sigHeaders.contains("(request-target)"))
			throw new RuntimeException("(request-target) is not in signed headers");
		if(!sigHeaders.contains("date"))
			throw new RuntimeException("date is not in signed headers");
		if(!sigHeaders.contains("host"))
			throw new RuntimeException("host is not in signed headers");
		
		long unixtime=headers.getDate();
		long now=Instant.now().toEpochMilli();
		long diff=now-unixtime;
		if(diff>30000L)
			throw new RuntimeException("Date is too far in the future (difference: "+diff+"ms)");
		if(diff<-30000L)
			throw new RuntimeException("Date is too far in the past (difference: "+diff+"ms)");

		//TODO get live actor
		
		List<String> sigParts=new ArrayList<>();
		for(String header:sigHeaders){
			String value;
			if(header.equals("(request-target)")){
				value=method.name().toLowerCase()+" "+destination.getPath();
			}else{
				value=headers.getFirst(header);
			}
			sigParts.add(header+": "+value);
		}
		String sigStr=String.join("\n", sigParts);
		Signature sig;
		try {
			sig = Signature.getInstance("SHA256withRSA");
			sig.initVerify(publicKey);
			sig.update(sigStr.getBytes(StandardCharsets.UTF_8));
		} catch (Exception e) {
			throw new RuntimeException("Enable to create signature from PublicKey and header",e);
		}
		try {
			if(!sig.verify(signature)){
				LOG.info("Failed signature header: {}", sigHeader);
				LOG.info("Failed signature string: \n{}", sigStr);
				throw new RuntimeException("Signature failed to verify");
			}
		} catch (SignatureException e) {
			throw new RuntimeException("something is not configured properly with the signature", e);
		}
	}

	
}
