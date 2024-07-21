package chaincode.util;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Calendar;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.json.JSONObject;

import chaincode.data.TicketDTO;

public class Cert {
    /**
     * @param ticketDTO
     * @return
     */
    public static JSONObject ticketDTOtoJsonObject(TicketDTO ticketDTO, String username) {
        JSONObject jsonForm = new JSONObject();
        jsonForm.put("id", ticketDTO.getId() == null ? null : ticketDTO.getId().toString());
        jsonForm.put("day", ticketDTO.getDay());
        jsonForm.put("hour", ticketDTO.getHour());
        jsonForm.put("month", ticketDTO.getMonth());
        jsonForm.put("year", ticketDTO.getYear());
        jsonForm.put("username", username);
        return jsonForm;
    }

    /**
     * @param ticketDTO
     * @param currentTime
     */
    public static void generateTicketDTO(TicketDTO ticketDTO, Calendar currentTime) {
        Calendar c = currentTime;
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int hour = c.get(Calendar.HOUR);
        int minute = c.get(Calendar.MINUTE);
        int year = c.get(Calendar.YEAR);
        int timeToSum = 1;
        // ? If time is superior than 10 minutes we add 2 hours instead of 1
        if (minute > 10) {
            timeToSum++;
        }
        // ? Case the hour becomes higher than 24
        if (hour + timeToSum > 24) {
            // ? We set the time for the difference of that result with the max hours
            hour = hour + timeToSum - 24;
            // ? Now we get the max number of days this month can have
            Calendar fakeCalender = currentTime;
            fakeCalender.set(Calendar.MONTH, fakeCalender.get(Calendar.MONTH) + 1);
            fakeCalender.set(Calendar.DAY_OF_MONTH, 1);
            fakeCalender.add(Calendar.DAY_OF_MONTH, -1);
            int maxNumberOfDays = fakeCalender.get(Calendar.DAY_OF_MONTH);
            if (c.get(Calendar.DAY_OF_MONTH) == maxNumberOfDays) {
                month++;
                if (month > 11) {
                    month = 0;
                    year++;
                }
            }
        } else {
            hour += timeToSum;
        }
        ticketDTO.setDay((short) day);
        ticketDTO.setHour((short) hour);
        ticketDTO.setMonth((short) (month + 1));
        ticketDTO.setYear((short) year);
    }

    /**
     * @param cert
     * @param ouValue
     * @return
     */
    public static boolean hasOrganizationalUnit(X509Certificate cert, String ouValue) {
        String subject = cert.getSubjectX500Principal().getName();
        return subject.contains("OU=" + ouValue);
    }

    /**
     * @param pemCertificate, a base64 string that we want to know if it is a public
     *                        key or not
     * @return boolean, true in case it is a valid public key otherwise false
     */
    public static boolean isItValidPublicKey(String pemCertificate) {
        // ? Check delimiters
        if (!pemCertificate.contains("-----BEGIN PUBLIC KEY-----")
                || !pemCertificate.contains("-----END PUBLIC KEY-----")) {
            return false;
        }
        // ? Remove the delimiters
        String base64key = removeX509Headers(pemCertificate);
        // ? Decode base64 to byte
        byte[] byte_form = Base64.getDecoder()
                .decode(base64key);
        // ? Try to generate a public key.. in case there are no error everything is
        // ? fine otherwise its a invalid public key
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(byte_form);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * @param ticket
     * @return
     * @throws java.security.InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     */
    public static String encryptTicket(String ticketJSONString, PublicKey pubKey)
            throws java.security.InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        byte[] encryptedBytes = cipher.doFinal(ticketJSONString.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * @param publicKeyPem, the public key in string format without the headers
     * @return
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     */
    public static PublicKey getPublicKeyFromKeyWithNoHeaders(String publicKeyPem)
            throws InvalidKeySpecException, NoSuchAlgorithmException {
        byte[] encoded = Base64.getDecoder().decode(publicKeyPem);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keyspec = new X509EncodedKeySpec(encoded);
        return keyFactory.generatePublic(keyspec);
    }

    /**
     * @param str the string we wish to convert to byte
     * @return
     */
    public static byte[] stringToByteArray(String str) {
        return str.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * @param byteArray, the bytearray that we wish to convert to a UTF_8 String
     * @return
     */
    public static String byteArrayToString(byte[] byteArray) {
        return new String(byteArray, StandardCharsets.UTF_8);
    }

    /**
     * @param pemCertificate, the certificate in string form
     * @return The actual string of the public or private key
     */
    public static String removeX509Headers(String pemCertificate) {
        return pemCertificate
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");
    }

}
