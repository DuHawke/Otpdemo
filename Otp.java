import java.security.SecureRandom;
import java.util.Scanner;

public class Otp {

    private static final int LENGTH = 6;
    private static long EXPRIRE_TIME = 30_000;

    // generate OTP
    public static String generateOTP() {
        String numbers = "0123456789";
        SecureRandom secure = new SecureRandom();
        StringBuilder otp = new StringBuilder();

        for (int i = 0; i < LENGTH; i++) {
            otp.append(secure.nextInt(10));
        }
        return otp.toString();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int attempts = 0;

        String otp = generateOTP();
        long createdTime = System.currentTimeMillis();
        System.out.println("Your OTP is: " + otp);

        // verify OTP
        boolean success = false;
        for (int i = 1; i <= 5; i++) {
            System.out.print("Enter OTP: ");
            String input = scanner.nextLine().trim();

            // check expired otp
            /**
             * if current time - created time >= 30s (EXPRIRE_TIME)
             * -> reset time(created time) + overwrite old otp with new otp
             */
            if (System.currentTimeMillis() - createdTime >= EXPRIRE_TIME) {
                otp = generateOTP();
                createdTime = System.currentTimeMillis();
                attempts = 0;

                System.out.println("OTP expired. New OTP is: " + otp);
            }

            if (otp.equals(input)) {
                System.out.println("Successful");
                success = true;
                break;
            } else {
                if (i < 5) {
                    System.out.println("Wrong OTP. Attempts left: " + (5 - i));
                }
            }
        }

        if (!success) {
            System.out.println("Too many attempts. Please try again later.");
        }
        
    }
}
