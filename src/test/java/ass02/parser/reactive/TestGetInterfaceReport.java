package ass02.parser.reactive;

import ass02.parser.reactive.ProjectAnalyzer;
import ass02.parser.reactive.ProjectAnalyzerImpl;
import ass02.parser.model.report.InterfaceReport;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.vertx.core.Future;

import static java.lang.Thread.sleep;


public class TestGetInterfaceReport {

    public static void main(String[] args) {
        ProjectAnalyzer projectAnalyzer = new ProjectAnalyzerImpl();
        Maybe<InterfaceReport> d = projectAnalyzer.getInterfaceReport("src/main/java/ass02/parser/event/ProjectAnalyzer.java")
                .map(v -> { log("map 1 " + v); return v; })
                .subscribeOn(Schedulers.computation()); //mettendolo tutto quello sopra svolto da un thread dello scheduler
        /*
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        */


        d//.observeOn(Schedulers.computation())
                .map(v -> { log("map 2 " + v); return v; })
                .blockingSubscribe(report -> log("Subscribe: " + report));

        log("pippo");
    }

    static private void log(String msg) {
        System.out.println("[" + Thread.currentThread().getName() + "] " + msg);
    }

}
