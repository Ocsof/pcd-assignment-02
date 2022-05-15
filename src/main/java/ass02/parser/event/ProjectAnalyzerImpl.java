package ass02.parser.event;

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
import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import ass02.parser.model.collector.ClassCollector;
import ass02.parser.model.collector.InterfaceCollector;
import ass02.parser.model.collector.PackageCollector;
import ass02.parser.model.collector.ProjectCollector;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static java.lang.Thread.sleep;

public class ProjectAnalyzerImpl extends AbstractVerticle implements ProjectAnalyzer {

    public ProjectAnalyzerImpl() {
        Vertx.vertx().deployVerticle(this);
    }

    @Override
    public Future<InterfaceReport> getInterfaceReport(String srcInterfacePath) {
        return this.getVertx().executeBlocking(promise -> {
            this.log("Starting on getInterfaceReport");
            CompilationUnit compilationUnit;
            try {
                compilationUnit = StaticJavaParser.parse(new File(srcInterfacePath));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            InterfaceReportImpl interfaceReport = new InterfaceReportImpl();
            InterfaceCollector interfaceCollector = new InterfaceCollector();
            interfaceCollector.visit(compilationUnit, interfaceReport);
            promise.complete(interfaceReport);
        }, false);
    }

    @Override
    public Future<ClassReport> getClassReport(String srcClassPath) {
        return this.getVertx().executeBlocking(promise -> {
            this.log("Starting on getClassReport");
            CompilationUnit compilationUnit;
            try {
                compilationUnit = StaticJavaParser.parse(new File(srcClassPath));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            ClassReportImpl classReport = new ClassReportImpl();
            ClassCollector classCollector = new ClassCollector();
            classCollector.visit(compilationUnit, classReport);
            promise.complete(classReport);
        }, false);
    }

    @Override
    public Future<PackageReport> getPackageReport(String srcPackagePath) {
        return this.getVertx().executeBlocking(promise -> {
            this.log("Starting on getPackageReport");
            PackageDeclaration packageDeclaration;
            packageDeclaration = StaticJavaParser.parsePackageDeclaration("package " + srcPackagePath + ";");
            PackageReportImpl packageReport = new PackageReportImpl();
            PackageCollector packageCollector = new PackageCollector();
            packageCollector.visit(packageDeclaration, packageReport);
            promise.complete(packageReport);
        });
    }

    @Override
    public Future<ProjectReport> getProjectReport(String srcProjectFolderPath) {
        return this.getVertx().executeBlocking(promise -> {
            this.log("Starting on getProjectReport");
            ProjectCollector projectCollector = new ProjectCollector();
            ProjectReportImpl projectReport = new ProjectReportImpl();
            projectCollector.visit(srcProjectFolderPath, projectReport);
            promise.complete(projectReport);
        });
    }

    @Override
    public void analyzeProject(String srcProjectFolderName, Consumer<ProjectElem> callback) {
        this.getVertx().executeBlocking(promise -> {
            this.log("Starting on analyzeProject");
            SourceRoot sourceRoot = new SourceRoot(Paths.get(srcProjectFolderName)).setParserConfiguration(new ParserConfiguration());
            List<ParseResult<CompilationUnit>> parseResultList;

            try {
                parseResultList = sourceRoot.tryToParse("");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // mi prendo i vari package che compongono il progetto
            List<PackageDeclaration> allCus = parseResultList.stream()
                    .filter(r -> r.getResult().isPresent() && r.isSuccessful())
                    .map(r -> r.getResult().get())
                    .filter(c -> c.getPackageDeclaration().isPresent())
                    .map(c -> c.getPackageDeclaration().get())
                    .distinct().toList();

            for (PackageDeclaration packageDeclaration : allCus) {
                // genero report fittizi dei package con solo il nome, chiamando la callback per ciascuno
                PackageReportImpl packageNameReport = new PackageReportImpl();
                packageNameReport.setFullPackageName(packageDeclaration.getNameAsString());

                // classes/interfaces report
                List<CompilationUnit> classesOrInterfacesUnit = this.createParsedFileList(packageDeclaration)
                        .stream()
                        .filter(r -> r.getResult().isPresent() && r.isSuccessful())
                        .map(r -> r.getResult().get()).toList();

                List<Future> packageElements = new ArrayList<>();
                for (CompilationUnit cu : classesOrInterfacesUnit) {
                    List<ClassOrInterfaceDeclaration> declarationList = cu.getTypes().stream()
                            .map(TypeDeclaration::asTypeDeclaration)
                            .filter(BodyDeclaration::isClassOrInterfaceDeclaration)
                            .map(x -> (ClassOrInterfaceDeclaration) x).toList();


                    for (ClassOrInterfaceDeclaration declaration : declarationList) {
                        if (this.isRightPackage(packageNameReport.getFullPackageName(), declaration)) {
                            if (declaration.isInterface()) {

                                packageElements.add(this.getInterfaceReport("src/main/java/" + declaration.getFullyQualifiedName().get().replace(".", "/") + ".java"));
                                /*
                                this.getInterfaceReport("src/main/java/" + declaration.getFullyQualifiedName().get().replace(".", "/") + ".java").onComplete(e -> {
                                            if (e.succeeded()) {
                                                if (isFirst) {
                                                    callback.accept(packageNameReport);
                                                }
                                                callback.accept(e.result());
                                            }
                                        }
                                );*/
                            } else {
                                packageElements.add(this.getClassReport("src/main/java/" + declaration.getFullyQualifiedName().get().replace(".", "/") + ".java"));
                                /*
                                this.getClassReport("src/main/java/" + declaration.getFullyQualifiedName().get().replace(".", "/") + ".java").onComplete(e -> {
                                            if (e.succeeded()) {
                                                if (isFirst) {
                                                    callback.accept(packageNameReport);
                                                }
                                                callback.accept(e.result());
                                            }
                                        }
                                );*/
                            }
                        }
                    }
                }
                CompositeFuture.all(packageElements).onSuccess(res -> {
                    callback.accept(packageNameReport);
                    res.list().forEach(e -> callback.accept((ProjectElem) e));
                });
            }
        });
    }

    private boolean isRightPackage(String packageName, ClassOrInterfaceDeclaration declaration) {
        String classFullName = declaration.getFullyQualifiedName().isPresent() ? declaration.getFullyQualifiedName().get() : "ERROR!";
        String className = declaration.getNameAsString();
        classFullName = classFullName.replace("." + className, "");
        return classFullName.equals(packageName);
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
