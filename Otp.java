import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Otp {

    private static final int LENGTH = 6;
    private static final long EXPRIRE_TIME = 30_000;
    private static long createdTime;
    private static String currentOTP;
    private static final Set<String> usedOTP = new HashSet<>(); // save otp used

    // generate OTP
    public static String generateOTP() {
        SecureRandom secure = new SecureRandom();
        StringBuilder otp = new StringBuilder();

        for (int i = 0; i < LENGTH; i++) {
            otp.append(secure.nextInt(10));
        }
        currentOTP = otp.toString();
        createdTime = System.currentTimeMillis();
        return otp.toString();
    }

    // check otp expired
    public static boolean isExpired() {
        return currentOTP == null || System.currentTimeMillis() - createdTime >= EXPRIRE_TIME;
    }

    public static VerifyResult isVerify(String input) {
        if(isExpired())
            return new VerifyResult(false, "OTP expired");
        if(usedOTP.contains(input))
            return new VerifyResult(false, "OTP used");

        if(input.equals(currentOTP)) {
            usedOTP.add(input);
            return new VerifyResult(true, "Valid OTP");
        }

        return new VerifyResult(false, "Invalid or expired OTP");
    }

    static class VerifyResult {
        boolean success;
        String message;
        
        VerifyResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }

    public static void menu() {
        System.out.println("====================");
        System.out.println("        MENU        ");
        System.out.println("1. Generate OTP");
        System.out.println("2. Verify OTP");
        System.out.println("3. Validate OTP");
        System.out.println("4. Exit");
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int choice = -1;
        while(choice != 0) {
            menu();
            System.out.print("Your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1:
                    String otp = generateOTP();
                    System.out.println("Your OTP is: " + otp);
                    break;
                case 2:
                    System.out.print("Enter OTP: ");
                    String input = scanner.nextLine();
                    VerifyResult result = isVerify(input);
                    System.out.println(result.message);
                    break;
                case 3:
                    if(currentOTP == null) {
                        System.out.println("There is no OTP. Generate new OTP");
                        break;
                    }
                    if(isExpired())
                        System.out.println("OTP expired. Generate new OTP");
                    else
                        System.out.println("OTP still valid");
                    break;
                case 4:
                    scanner.close();
                    System.exit(0);
                    break;
            }
        }
    }
}
