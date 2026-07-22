package util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Hash y verificación de contraseñas con PBKDF2-HMAC-SHA256 (JDK, sin dependencias extra).
 * Formato almacenado: {@code $pbkdf2$<iter>$<saltB64>$<hashB64>}
 */
public final class PasswordHasher {

	private static final String PREFIJO = "$pbkdf2$";
	private static final int ITERACIONES = 120_000;
	private static final int SALT_BYTES = 16;
	private static final int KEY_BITS = 256;
	private static final SecureRandom RANDOM = new SecureRandom();

	private PasswordHasher() {
	}

	public static String hash(String contrasenaPlana) {
		if (contrasenaPlana == null || contrasenaPlana.isBlank()) {
			throw new IllegalArgumentException("La contraseña es obligatoria.");
		}
		byte[] salt = new byte[SALT_BYTES];
		RANDOM.nextBytes(salt);
		byte[] digesto = pbkdf2(contrasenaPlana.toCharArray(), salt, ITERACIONES);
		return PREFIJO + ITERACIONES + "$"
				+ Base64.getEncoder().encodeToString(salt) + "$"
				+ Base64.getEncoder().encodeToString(digesto);
	}

	public static boolean coincide(String contrasenaPlana, String almacenada) {
		if (contrasenaPlana == null || almacenada == null || almacenada.isBlank()) {
			return false;
		}
		if (esHash(almacenada)) {
			return verificarPbkdf2(contrasenaPlana, almacenada);
		}
		// Compatibilidad con cuentas creadas en texto plano
		return MessageDigest.isEqual(
				contrasenaPlana.getBytes(StandardCharsets.UTF_8),
				almacenada.getBytes(StandardCharsets.UTF_8));
	}

	public static boolean esHash(String valor) {
		return valor != null && valor.startsWith(PREFIJO);
	}

	private static boolean verificarPbkdf2(String contrasenaPlana, String almacenada) {
		try {
			String[] partes = almacenada.split("\\$");
			// "", "pbkdf2", iter, salt, hash
			if (partes.length != 5) {
				return false;
			}
			int iteraciones = Integer.parseInt(partes[2]);
			byte[] salt = Base64.getDecoder().decode(partes[3]);
			byte[] esperado = Base64.getDecoder().decode(partes[4]);
			byte[] actual = pbkdf2(contrasenaPlana.toCharArray(), salt, iteraciones);
			return MessageDigest.isEqual(esperado, actual);
		} catch (RuntimeException e) {
			return false;
		}
	}

	private static byte[] pbkdf2(char[] password, byte[] salt, int iteraciones) {
		try {
			PBEKeySpec spec = new PBEKeySpec(password, salt, iteraciones, KEY_BITS);
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
			return factory.generateSecret(spec).getEncoded();
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new IllegalStateException("No se pudo calcular el hash de la contraseña.", e);
		}
	}
}
