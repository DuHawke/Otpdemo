import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import java.security.SecureRandom;
import java.util.Scanner;
import java.util.Arrays;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Otp {
    private static final int LENGTH = 6;
    private static final long EXPRIRE_TIME = 30_000;
	private static String sessionKey;
    private static long createdTime;
    private static String currentOTP;
	private static boolean created;

	public static void createNewSession() {
		sessionKey = "";
		var rng = new SecureRandom();

		for(var i = 0; i < 10; i++) sessionKey += rng.nextInt(10);
	}

    public static void generateOTP() {
		try {
			created = true;
        	var slots = new byte[LENGTH];
			Arrays.fill(slots, (byte) 0);
			var mac = Mac.getInstance("HmacSHA256");
			var keySpec = new SecretKeySpec(sessionKey.getBytes(), "HmacSHA256");
			mac.init(keySpec);
			var generatedData = mac.doFinal(("" + System.currentTimeMillis()).getBytes()); //current time assume synced
			currentOTP = "";

			for(var i = 0; i < generatedData.length; i++) slots[i % LENGTH] = (byte) ((slots[i % LENGTH] + generatedData[i]) % 10);
			for(var i = 0; i < LENGTH; i++) currentOTP += Math.abs(slots[i]);

			createdTime = System.currentTimeMillis();
		} catch(NoSuchAlgorithmException | InvalidKeyException e) {
			System.out.println("Failed to create OTP! Try again");
			created = false;
		}
    }

    public static boolean isExpired() {
        return System.currentTimeMillis() - createdTime >= EXPRIRE_TIME;
    }

    private static VerifyResult isVerify(String input) {
		if(currentOTP == null) return new VerifyResult(false, "OTP used or invalid");

        if(isExpired())
            return new VerifyResult(false, "OTP expired");

        if(input.equals(currentOTP)) {
            currentOTP = null;
            return new VerifyResult(true, "Valid OTP");
        }

        return new VerifyResult(false, "Invalid OTP");
    }

   
    record VerifyResult(boolean success, String message) {}

    public static void menu() {
        System.out.println("====================");
        System.out.println("        MENU        ");
		System.out.println("1. Create new session");
        System.out.println("2. Generate OTP");
        System.out.println("3. Verify OTP");
        System.out.println("4. Exit");
    }

    public static void main(String[] args) {
		created = false;
		createNewSession();
        Scanner scanner = new Scanner(System.in);
        int choice = -1;
        while(choice != 0) {
            menu();
            System.out.print("Your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
				case 1 -> createNewSession();
                case 2 -> {
                    generateOTP();
                    System.out.println("Your OTP is: " + currentOTP);
                }
                case 3 -> {
					if(!created) {
						System.out.println("OTP is not created");

						break;
					}

                    System.out.print("Enter OTP: ");
                    String input = scanner.nextLine();
                    VerifyResult result = isVerify(input);
                    System.out.println(result.message);
                }
                case 4 -> {
                    scanner.close();
                    System.exit(0);
                }
            }
        }
    }
}
