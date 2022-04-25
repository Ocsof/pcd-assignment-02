package ass02.parser.event;

import io.vertx.core.Future;
import it.unibo.pcd.assignment.event.report.PackageReport;
import it.unibo.pcd.assignment.event.*;

public class TestAsyncPackageReport {

    public static void main(String[] args) {
        ProjectAnalyzer projectAnalyzer = new ProjectAnalyzerImpl();
        Future<PackageReport> reportFuture = projectAnalyzer.getPackageReport("it.unibo.pcd.assignment.event.report");
        reportFuture.onComplete(res -> System.out.println(res.result().toString()));
    }

}
