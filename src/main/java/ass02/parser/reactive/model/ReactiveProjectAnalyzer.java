package ass02.parser.reactive.model;

import ass02.parser.model.report.ClassReport;
import ass02.parser.model.report.InterfaceReport;
import ass02.parser.model.report.PackageReport;
import ass02.parser.model.report.ProjectReport;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;

public interface ReactiveProjectAnalyzer {

    /**
     * Async method to retrieve the report about a specific interface,
     * given the full path of the interface source file
     *
     * @param srcInterfacePath
     * @return
     */
    Observable<InterfaceReport> getInterfaceReport(String srcInterfacePath);

    /**
     * Async method to retrieve the report about a specific class,
     * given the full path of the class source file
     *
     * @param srcClassPath
     * @return
     */
    Observable<ClassReport> getClassReport(String srcClassPath);

    /**
     * Async method to retrieve the report about a package,
     * given the full path of the package folder
     *
     * @param srcPackagePath
     * @return
     */
    Observable<PackageReport> getPackageReport(String srcPackagePath);

    /**
     * Async method to retrieve the report about a project
     * given the full path of the project folder
     *
     * @param srcProjectFolderPath
     * @return
     */
    Observable<ProjectReport> getProjectReport(String srcProjectFolderPath);

    /**
     * Async function that analyze a project given the full path of the project folder,
     * executing the callback each time a project element is found
     *
     * @param srcProjectFolderName
     * @param callback
     */
    void analyzeProject(String srcProjectFolderName);

    Flowable<Integer> getClassNumberObservable();

    Flowable<Integer> getInterfaceNumberObservable();

    Flowable<Integer> getPackageNumberObservable();

    Flowable<String> getReportObservable();
}
