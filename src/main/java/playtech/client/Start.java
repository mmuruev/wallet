package playtech.client;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class Start {
    private static final String BASE_URL = "http://localhost:8080/wallet/service";
    private static final String METHOD_NAME = "balance";

    public static void main(String[] args) throws IOException {
        System.out.println("Press Enter for finish");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (!br.ready()) {
            int threadsCount = 10;
            if (args.length > 1) {
                threadsCount = Integer.parseInt(args[0]);
            }

            List<Thread> threads = new ArrayList<>();

            for (int i = 0; i < threadsCount; i++) {
                Runnable task = new WebClient(BASE_URL, METHOD_NAME);
                Thread worker = new Thread(task);
                worker.setName(String.valueOf(i));
                worker.start();
                threads.add(worker);
            }

            try {
                for (Thread thread : threads) {
                     thread.join();
                }
                System.out.println("Done. Press Enter for finish");
                Thread.sleep(5000);

            } catch (InterruptedException ie) {
                System.out.println(ie.getMessage());
            }

        }
    }


}
