package playtech.client;
import java.util.Random;

public class RandomUserGenerator {

    private static final String[] userNames = {"john","jane"};
    private static final Random generator = new Random();
    private static final int RANGE = 100;

    public String getUserName()
    {
        return userNames[generator.nextInt(userNames.length)];
    }

    public String getBalanceChange()
    {
        return String.valueOf(generator.nextInt(RANGE) - generator.nextInt(RANGE)) + "." +
               String.valueOf(generator.nextInt(RANGE));
    }
}
