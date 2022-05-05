package ass02.parser.reactive;


import ass02.parser.model.report.ClassReport;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class TestGetClassReport {
    public static void main(String[] args) {
        ProjectAnalyzer projectAnalyzer = new ProjectAnalyzerImpl();
        Observable<ClassReport> classReportObservable = projectAnalyzer.getClassReportObservable();

        projectAnalyzer.requestClassReportGeneration("src/main/java/ass02/parser/event/ProjectAnalyzerImpl.java");
        classReportObservable
                .subscribeOn(Schedulers.computation())
                .blockingSubscribe(report -> log("Subscribe: " + report.getFullClassName()));
    }

    static private void log(String msg) {
        System.out.println("[" + Thread.currentThread().getName() + "] " + msg);
    }
}
