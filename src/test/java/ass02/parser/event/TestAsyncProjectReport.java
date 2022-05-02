package ass02.parser.event;

import ass02.parser.model.report.ProjectReport;
import io.vertx.core.Future;

public class TestAsyncProjectReport {

    public static void main(String[] args) {
        ProjectAnalyzer projectAnalyzer = new ProjectAnalyzerImpl();
        Future<ProjectReport> reportFuture = projectAnalyzer.getProjectReport("src/main/java");
        reportFuture.onComplete(res -> System.out.println(res.result().toString()));
    }

}
