import java.io.*;
import java.util.*;

public class TypingTest {

    private static String lastInput = "";
    private static Scanner scanner = new Scanner(System.in);
    private static List<Boolean> resultList = new ArrayList<>();

    public static class InputRunnable implements Runnable {
        @Override
        public void run() {
            if (scanner.hasNextLine()) {
                lastInput = scanner.nextLine();
            }
        }
    }

    public static void testWord(String wordToTest) throws InterruptedException {
        System.out.println("Type: " + wordToTest);
        lastInput = "";

        Thread inputThread = new Thread(new InputRunnable());
        inputThread.start();

        inputThread.join(4000); // 4 seconds timeout

        System.out.println("You typed: " + lastInput);
        if (lastInput.equals(wordToTest)) {
            System.out.println("Correct ✅");
            resultList.add(true);
        } else {
            System.out.println("Incorrect ❌");
            resultList.add(false);
        }

        System.out.println();
    }

    public static void typingTest(List<String> inputList) throws InterruptedException {
        for (String word : inputList) {
            testWord(word);
            Thread.sleep(1500);
        }

        System.out.println("Typing test finished!");
        System.out.println("Correct answers: " + Collections.frequency(resultList, true) + "/" + resultList.size());
    }

    public static List<String> loadWords(String path) throws IOException {
        List<String> words = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                words.add(line);
            }
        }
        return words;
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        List<String> words = loadWords("data/Words.txt");
        typingTest(words);

        System.out.println("Press enter to exit.");
        scanner.nextLine();
    }
}
