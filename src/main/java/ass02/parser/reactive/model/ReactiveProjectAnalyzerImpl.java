package ass02.parser.reactive.model;

import ass02.parser.model.report.*;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.utils.SourceRoot;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class ReactiveProjectAnalyzerImpl implements ReactiveProjectAnalyzer {

    private Flowable<String> reportObservable;
    private Flowable<Integer> packageNumberObservable;
    private Flowable<Integer> interfaceNumberObservable;
    private Flowable<Integer> classNumberObservable;

    public ReactiveProjectAnalyzerImpl() {

    }

    @Override
    public Observable<InterfaceReport> getInterfaceReport(String srcInterfacePath) {
        return null;
    }

    @Override
    public Observable<ClassReport> getClassReport(String srcClassPath) {
        return null;
    }

    @Override
    public Observable<PackageReport> getPackageReport(String srcPackagePath) {
        return null;
    }

    @Override
    public Observable<ProjectReport> getProjectReport(String srcProjectFolderPath) {
        return null;
    }

    @Override
    public void analyzeProject(String srcProjectFolderName) {
        SourceRoot sourceRoot = new SourceRoot(Paths.get(srcProjectFolderName)).setParserConfiguration(new ParserConfiguration());
        List<ParseResult<CompilationUnit>> parseResultList;
        parseResultList = sourceRoot.tryToParseParallelized("");
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
            //callback.accept(packageNameReport);


            this.reportObservable.just(packageDeclaration.getNameAsString());  //todo: osservabile del report view
            this.packageNumberObservable.just(1); //todo: osservabile del numero dei package

            // classes/interfaces report
            List<CompilationUnit> classesOrInterfacesUnit = this.createParsedFileList(packageDeclaration)
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
                        this.reportObservable.just(declaration.getFullyQualifiedName().get());
                        interfaceNumberObservable.just(1);
                    } else {
                        reportObservable.just(declaration.getFullyQualifiedName().get());
                        classNumberObservable.just(1);
                    }
                }
            }
        }

    }

    @Override
    public Flowable<Integer> getClassNumberObservable() {
        return this.classNumberObservable;
    }

    @Override
    public Flowable<Integer> getInterfaceNumberObservable() {
        return this.interfaceNumberObservable;
    }

    @Override
    public Flowable<Integer> getPackageNumberObservable() {
        return this.packageNumberObservable;
    }

    @Override
    public Flowable<String> getReportObservable() {
        return this.reportObservable;
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
