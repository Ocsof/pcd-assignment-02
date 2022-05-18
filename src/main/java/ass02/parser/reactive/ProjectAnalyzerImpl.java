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
                    System.out.println("[Interface visit] " + Thread.currentThread());
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
            System.out.println("[Class visit] " + Thread.currentThread());
            emitter.onSuccess(classReport);
        });
    }
    @Override
    public Observable<ProjectElem> analyzeProject(String srcProjectFolderName) {

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
                        .subscribeOn(Schedulers.computation())
                        .flatMap(p -> Observable.create(emitter -> {
                            System.out.println("[Package exploration] " + Thread.currentThread());
                            PackageReportImpl packageNameReport = new PackageReportImpl();
                            packageNameReport.setFullPackageName(p.getNameAsString());
                            emitter.onNext(packageNameReport);

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
                                    if(this.isRightPackage(packageNameReport.getFullPackageName(), declaration)){
                                        if (declaration.isInterface()) {
                                            this.getInterfaceReport("src/main/java/" + declaration.getFullyQualifiedName().get().replace(".", "/") + ".java")
                                                    .blockingSubscribe(emitter::onNext);
                                        } else {
                                            this.getClassReport("src/main/java/" + declaration.getFullyQualifiedName().get().replace(".", "/") + ".java")
                                                    .blockingSubscribe(emitter::onNext);
                                        }
                                    }
                                }
                            }
                            emitter.onComplete();
                        })));
    }

    private boolean isRightPackage(String packageName, ClassOrInterfaceDeclaration declaration) {
        String classFullName = declaration.getFullyQualifiedName().isPresent() ? declaration.getFullyQualifiedName().get() : "ERROR!";
        String className = declaration.getNameAsString();
        classFullName = classFullName.replace("." + className, "");
        return classFullName.equals(packageName);
    }

        private List<ParseResult<CompilationUnit>> createParsedFileList(PackageDeclaration dec) {
            SourceRoot sourceRoot = new SourceRoot(Paths.get("src/main/java/")).setParserConfiguration(new ParserConfiguration());
            List<ParseResult<CompilationUnit>> parseResultList;
            parseResultList = sourceRoot.tryToParseParallelized(dec.getNameAsString());
            return parseResultList;
        }


}
