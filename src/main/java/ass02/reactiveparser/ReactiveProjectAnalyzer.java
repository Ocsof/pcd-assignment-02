package ass02.reactiveparser;

import ass02.parser.event.ProjectAnalyzer;
import ass02.parser.event.ProjectElem;
import ass02.parser.model.report.ClassReport;
import ass02.parser.model.report.InterfaceReport;
import ass02.parser.model.report.PackageReport;
import ass02.parser.model.report.ProjectReport;
import io.vertx.core.Future;

import java.util.function.Consumer;

public class ReactiveProjectAnalyzer implements ProjectAnalyzer {
    @Override
    public Future<InterfaceReport> getInterfaceReport(String srcInterfacePath) {
        return null;
    }

    @Override
    public Future<ClassReport> getClassReport(String srcClassPath) {
        return null;
    }

    @Override
    public Future<PackageReport> getPackageReport(String srcPackagePath) {
        return null;
    }

    @Override
    public Future<ProjectReport> getProjectReport(String srcProjectFolderPath) {
        return null;
    }

    @Override
    public void analyzeProject(String srcProjectFolderName, Consumer<ProjectElem> callback) {

    }
}
