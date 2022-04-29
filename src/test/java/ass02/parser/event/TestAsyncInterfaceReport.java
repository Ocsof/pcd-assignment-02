package ass02.parser.event;

import ass02.parser.event.report.InterfaceReport;
import io.vertx.core.Future;


public class TestAsyncInterfaceReport {

    public static void main(String[] args) {
        ProjectAnalyzer projectAnalyzer = new ProjectAnalyzerImpl();
        Future<InterfaceReport> reportFuture = projectAnalyzer.getInterfaceReport("src/main/java/it/unibo/pcd/assignment/event/ProjectAnalyzer.java");
        reportFuture.onComplete(res -> System.out.println(res.result().toString()));
    }

}
