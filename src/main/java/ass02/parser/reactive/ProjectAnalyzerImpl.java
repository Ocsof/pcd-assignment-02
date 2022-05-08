package ass02.parser.reactive;

import ass02.parser.model.collector.ClassCollector;
import ass02.parser.model.collector.InterfaceCollector;
import ass02.parser.model.report.*;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.utils.SourceRoot;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class ProjectAnalyzerImpl implements ProjectAnalyzer {

    private List<String> reportObservable;


    public ProjectAnalyzerImpl() {
        this.reportObservable = new LinkedList<>();
    }

    @Override
    public Maybe<InterfaceReport> getInterfaceReport(String srcInterfacePath) {
        return Maybe.create(emitter -> {
                    CompilationUnit compilationUnit;
                    try {
                        compilationUnit = StaticJavaParser.parse(new File(srcInterfacePath));
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    InterfaceReportImpl interfaceReport = new InterfaceReportImpl();
                    InterfaceCollector interfaceCollector = new InterfaceCollector();
                    interfaceCollector.visit(compilationUnit, interfaceReport);
                    emitter.onSuccess(interfaceReport);
                });
    }

    @Override
    public Maybe<ClassReport> getClassReport(String srcClassPath){
        return Maybe.create(emitter -> {
            CompilationUnit compilationUnit;
            try {
                compilationUnit = StaticJavaParser.parse(new File(srcClassPath));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            ClassReportImpl classReport = new ClassReportImpl();
            ClassCollector classCollector = new ClassCollector();
            classCollector.visit(compilationUnit, classReport);
            emitter.onSuccess(classReport);
        });
    }

    @Override
    public Observable<ProjectReport> getProjectReport(String srcProjectFolderPath) {
        return null;
    }

    @Override
    public Observable<String> analyzeProject(String srcProjectFolderName) {

        // mi prendo i vari package che compongono il progetto
        /*
        List<PackageDeclaration> allCus = parseResultList.stream()
                .filter(r -> r.getResult().isPresent() && r.isSuccessful())
                .map(r -> r.getResult().get())
                .filter(c -> c.getPackageDeclaration().isPresent())
                .map(c -> c.getPackageDeclaration().get())
                .distinct().toList();
        */
        return Observable.<PackageDeclaration>create(emitter -> {
                    SourceRoot sourceRoot = new SourceRoot(Paths.get(srcProjectFolderName)).setParserConfiguration(new ParserConfiguration());
                    List<ParseResult<CompilationUnit>> parseResultList;
                    parseResultList = sourceRoot.tryToParseParallelized("");
                    List<PackageDeclaration> allCus = parseResultList.stream()
                            .filter(r -> r.getResult().isPresent() && r.isSuccessful())
                            .map(r -> r.getResult().get())
                            .filter(c -> c.getPackageDeclaration().isPresent())
                            .map(c -> c.getPackageDeclaration().get())
                            .distinct().toList();
                    System.out.println("[CUS creation] " + Thread.currentThread());
                    allCus.forEach(emitter::onNext);
                })
                .subscribeOn(Schedulers.computation())
                .concatMap(packageDeclaration -> Observable.just(packageDeclaration)
                        .flatMap(p -> Observable.create(emitter -> {
                            System.out.println("[Package exploration] " + Thread.currentThread());
                            PackageReportImpl packageNameReport = new PackageReportImpl();
                            packageNameReport.setFullPackageName(p.getNameAsString());
                            emitter.onNext(packageNameReport.toString());

                            // classes/interfaces report
                            List<CompilationUnit> classesOrInterfacesUnit = this.createParsedFileList(p)
                                    .stream()
                                    .filter(r -> r.getResult().isPresent() && r.isSuccessful())
                                    .map(r -> r.getResult().get()).toList();

                            for (CompilationUnit cu : classesOrInterfacesUnit) {

                                List<ClassOrInterfaceDeclaration> declarationList = cu.getTypes().stream()
                                        .map(TypeDeclaration::asTypeDeclaration)
                                        .filter(BodyDeclaration::isClassOrInterfaceDeclaration)
                                        .map(x -> (ClassOrInterfaceDeclaration) x).toList();

                                for (ClassOrInterfaceDeclaration declaration : declarationList) {
                                    if (declaration.isInterface()) {
                                        this.getInterfaceReport("src/main/java/" + declaration.getFullyQualifiedName().get().replace(".", "/") + ".java")
                                                .blockingSubscribe(report -> {
                                                    emitter.onNext(report.toString());
                                                    System.out.println("[ReportInterfaceGenerated] " + Thread.currentThread());
                                                });
                                    } else {
                                        this.getClassReport("src/main/java/" + declaration.getFullyQualifiedName().get().replace(".", "/") + ".java")
                                                .blockingSubscribe(report -> emitter.onNext(report.toString()));
                                    }
                                }
                            }
                            emitter.onComplete();
                        })));
    }

    private void log(String msg) {
        System.out.println("[REACTIVE AGENT] " + Thread.currentThread() + msg);
    }

        private List<ParseResult<CompilationUnit>> createParsedFileList(PackageDeclaration dec) {
            SourceRoot sourceRoot = new SourceRoot(Paths.get("src/main/java/")).setParserConfiguration(new ParserConfiguration());
            List<ParseResult<CompilationUnit>> parseResultList;
            try {
                parseResultList = sourceRoot.tryToParse(dec.getNameAsString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return parseResultList;
        }


}
