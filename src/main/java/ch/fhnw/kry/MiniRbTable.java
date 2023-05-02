package ch.fhnw.kry;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HexFormat;

public class MiniRbTable {


//    private final int NUMBER_OF_CHAIN = 2000;
    private final int NUMBER_OF_CHAIN = 3202;

    private final int NUMBER_OF_PASSWORDS = 2000;


    MessageDigest md;
    private final int PASSWORDLENGTH = 7;

    HashMap<String, String> lastToFirst = new HashMap<>();

    char[] order = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
            'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u',
            'v', 'w', 'x', 'y', 'z'};
    char[] currentChars = {0, 0, 0, 0, 0, 0, 0};

    public MiniRbTable() {
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public void generateRainbowTable() {
        String lastPass = "";
        String currPassword = "";
        for (int i = 0; i < NUMBER_OF_PASSWORDS; i++) { //Loops through all passwort within the number of passwords.
            currPassword = getString(); //Get Passwort as String.
            System.out.println(currPassword);
            lastPass = calcChain(currPassword); //Calculates all 2000 Chains and gets the last one.
            System.out.println(lastPass);

            System.out.println("__________________________________________--");
            lastToFirst.put(lastPass, currPassword);//Saves it in the map
            incrementChars(); // Increments the pasword.
        }
    }

    private String getString() { //get String for the number array.
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < PASSWORDLENGTH; i++) {
            stringBuilder.append(order[currentChars[i]]);
        }
        return stringBuilder.toString();
    }


    public void incrementChars() { // increments the password by one
        for (int i = PASSWORDLENGTH - 1; i >= 0; i--) {
            int id = currentChars[i]++;

            if (id != order.length - 1) {
                break;
            }
            currentChars[i] = 0;

        }
    }

    private String calcChain(String start) {
        String curr = start;
        for (int i = 0; i < NUMBER_OF_CHAIN; i++) {//2000 chains
            md.update(curr.getBytes());
            byte[] hashBytes = md.digest(); //Hash with md5
            curr = reduction(hashBytes, i);//Reduce
        }
        return curr; //Return after 2000 Reduces
    }


    private String reduction(byte[] hash, int round) { //Reduce the hash

        BigInteger hashInt = new BigInteger(1, hash).add(BigInteger.valueOf(round)); //Get Hash as a Number and add Stuff

        StringBuilder redString = new StringBuilder();
        for (int i = 0; i < PASSWORDLENGTH; i++) { //Calc reduction
            BigInteger[] divmod = hashInt.divideAndRemainder(BigInteger.valueOf(order.length));
            BigInteger modVal = divmod[1];
            hashInt = divmod[0];
            redString.append(order[modVal.intValue()]);
        }
        return redString.reverse().toString(); //Reverse sb.append();
    }

    public String getPasswordOf(String hash) { // Gets the password of a hash based on the rbTable.
        byte[] startBytes = HexFormat.of().parseHex(hash); // get bytes of the Hash.
        for (int i = 0; i < NUMBER_OF_CHAIN; i++) { //loops through all chains.
            int startRound = NUMBER_OF_CHAIN-1 - i; //Start from behind.
            byte[] currBytes = startBytes;
            String reduction = "";
            for (int j = startRound; j < NUMBER_OF_CHAIN; j++) {
                reduction = reduction(currBytes, j); //Get Reduction
                md.update(reduction.getBytes()); //Hash if not
                currBytes = md.digest();

            }

            if (lastToFirst.containsKey(reduction)) { // If the reduction is in the rb -Table.
                String curr = lastToFirst.get(reduction);
                for (int j = 0; j <= NUMBER_OF_CHAIN - startRound; j++) {//Start from begingin to the found location to get the password.
                    md.update(curr.getBytes()); //Hashes the string.
                    byte [] currHash = md.digest();
                    if (Arrays.equals(currHash,startBytes)){ // If hash matches return password that lead to the hash.
                        return curr;
                    }
                    curr = reduction(currHash, j);
                }
            };
        }
        return null;
    }


    private static final byte[] HEX_ARRAY = "0123456789ABCDEF".getBytes(StandardCharsets.US_ASCII);

    public static String bytesToHex(byte[] bytes) { //Used for Debugging.
        byte[] hexChars = new byte[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars, StandardCharsets.UTF_8);
    }
}
