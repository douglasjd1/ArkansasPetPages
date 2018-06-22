package models;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

public class Password
{
    public static byte[] hashPassword(final char[] password, final byte[] salt)
    {
        try
        {
            final int iterations = 500000;
            final int keyLength = 256;

            SecretKeyFactory skf = SecretKeyFactory.getInstance( "PBKDF2WithHmacSHA512" );
            PBEKeySpec spec = new PBEKeySpec( password, salt, iterations, keyLength );
            SecretKey key = skf.generateSecret( spec );
            return key.getEncoded( );

        } catch( NoSuchAlgorithmException | InvalidKeySpecException e )
        {
            throw new RuntimeException( e );
        }
    }

    public static byte[] getNewSalt() throws Exception
    {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] bytes = new byte[16];
        sr.nextBytes(bytes);
        return bytes;

    }
}