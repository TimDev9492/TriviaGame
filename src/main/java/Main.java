import triviaapi.TriviaAPI;
import triviaapi.data.QuestionDifficulty;
import triviaapi.data.QuestionType;
import triviaapi.data.TriviaQuestionData;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        TriviaAPI triviaAPI = new TriviaAPI();

        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                System.out.println("What type of question do you want? [m/b]");
                String mode = sc.nextLine();
                if (!mode.equals("m") && !mode.equals("b")) continue;
                QuestionType type = mode.equals("m") ? QuestionType.MULTIPLE_CHOICE : QuestionType.TRUE_FALSE;

                System.out.println("Choose a difficulty: [(e)asy/(m)edium/(h)ard]");
                String level = sc.nextLine();
                if (!level.equals("e") && !level.equals("m") && !level.equals("h")) continue;
                QuestionDifficulty difficulty = switch (level) {
                    case "e" -> QuestionDifficulty.EASY;
                    case "m" -> QuestionDifficulty.MEDIUM;
                    case "h" -> QuestionDifficulty.HARD;
                    default -> throw new IllegalStateException("Unexpected value: " + level);
                };

                System.out.println("Choose a category:");
                int i = 0;
                for (String category : triviaAPI.getAvailableCategories()) {
                    System.out.println(String.format("%d - %s", i++, category));
                }
                String selection = sc.nextLine();
                String categoryName = null;
                if (!selection.isEmpty()) {
                    try {
                        int index = Integer.parseInt(selection);
                        categoryName = triviaAPI.getAvailableCategories().stream()
                                .skip(index)
                                .findFirst()
                                .orElse(null);
                    } catch (NumberFormatException e) {
                        continue;
                    }
                }

                TriviaQuestionData question = triviaAPI.getTriviaQuestions(1, categoryName, difficulty, type).getFirst();
                System.out.println(String.format("Question: %s", question.question()));
                switch (type) {
                    case TRUE_FALSE -> {
                        String answer = "";
                        while (!answer.equals("true") && !answer.equals("false")) {
                            System.out.println("Answer [true/false]:");
                            answer = sc.nextLine();
                        }
                        System.out.println(question.correctAnswer().toLowerCase().equals(answer) ? "Correct!" : "Wrong!");
                    }
                    case MULTIPLE_CHOICE -> {
                        String answer = "";
                        List<String> choices = question.choices();
                        Collections.shuffle(choices);
                        while (!answer.equals("A") && !answer.equals("B") && !answer.equals("C") && !answer.equals("D")) {
                            if (choices.size() != 4) throw new IllegalStateException(String.format("There are %d choices available!", choices.size()));
                            System.out.println(String.format("""
                                    A: %s
                                    B: %s
                                    C: %s
                                    D: %s""",
                                    choices.get(0),
                                    choices.get(1),
                                    choices.get(2),
                                    choices.get(3)));
                            System.out.println("Answer [A/B/C/D]:");
                            answer = sc.nextLine();
                        }
                        char ans = answer.toCharArray()[0];
                        boolean correct = question.correctAnswer().equals(choices.get(ans - 'A'));
                        System.out.println(correct ? "Correct!" : String.format("Wrong, the correct answer is: %s", question.correctAnswer()));
                    }
                }
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
