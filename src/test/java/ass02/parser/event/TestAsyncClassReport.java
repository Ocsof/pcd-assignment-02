package ass02.parser.event;

import ass02.parser.model.report.ClassReport;
import io.vertx.core.Future;


public class TestAsyncClassReport {

    public static void main(String[] args) {
        ProjectAnalyzer projectAnalyzer = new ProjectAnalyzerImpl();
        System.out.println(projectAnalyzer);
        Future<ClassReport> reportFuture = projectAnalyzer.getClassReport("src/main/java/it/unibo/pcd/assignment/event/report/MethodInfoImpl.java");
        reportFuture.onComplete(res -> System.out.println(res.result().toString()));
    }

}
