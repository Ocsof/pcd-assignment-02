package ass02.parser.event;

import ass02.parser.model.report.PackageReport;
import io.vertx.core.Future;


public class TestAsyncPackageReport {

    public static void main(String[] args) {
        ProjectAnalyzer projectAnalyzer = new ProjectAnalyzerImpl();
        Future<PackageReport> reportFuture = projectAnalyzer.getPackageReport("it.unibo.pcd.assignment.event.report");
        reportFuture.onComplete(res -> System.out.println(res.result().toString()));
    }

}
