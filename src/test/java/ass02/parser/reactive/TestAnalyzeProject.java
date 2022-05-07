package ass02.parser.reactive;


public class TestAnalyzeProject {
    public static void main(String[] args) {
        ProjectAnalyzer projectAnalyzer = new ProjectAnalyzerImpl();
        projectAnalyzer.analyzeProject("src/main/java")
                .blockingSubscribe(s -> log(s));
    }

    static private void log(String msg) {
        System.out.println("[" + Thread.currentThread().getName() + "] " + msg);
    }

}
