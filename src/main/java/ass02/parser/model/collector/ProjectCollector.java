package ass02.parser.model.collector;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.utils.Pair;
import com.github.javaparser.utils.SourceRoot;
import ass02.parser.model.report.ClassReportImpl;
import ass02.parser.model.report.MethodInfo;
import ass02.parser.model.report.PackageReportImpl;
import ass02.parser.model.report.ProjectReportImpl;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ProjectCollector {

    public void visit(String srcProjectFolderPath, ProjectReportImpl projectReport) {
        SourceRoot sourceRoot = new SourceRoot(Paths.get(srcProjectFolderPath));
        sourceRoot.setParserConfiguration(new ParserConfiguration());
        List<ParseResult<CompilationUnit>> parseResultList;

        try {
            parseResultList = sourceRoot.tryToParse("");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<CompilationUnit> allCus = parseResultList.stream()
                .filter(ParseResult::isSuccessful)
                .filter(r -> r.getResult().isPresent())
                .map(r -> r.getResult().get()).toList();

        List<PackageDeclaration> cuPack = allCus.stream()
                .filter(c -> c.getPackageDeclaration().isPresent())
                .map(c -> c.getPackageDeclaration().get())
                .distinct().toList();

        PackageCollector packageCollector = new PackageCollector();
        List<PackageReportImpl> packageReportList = new ArrayList<>();

        for (PackageDeclaration pckDec : cuPack) {
            PackageReportImpl report = new PackageReportImpl();
            packageCollector.visit(pckDec, report);
            packageReportList.add(report);
        }

        projectReport.setPackageReports(packageReportList);


        ClassReportImpl classReport = new ClassReportImpl();
        ClassCollector classCollector = new ClassCollector();
        List<Pair<String, Boolean>> pairList = new ArrayList<>();

        for (CompilationUnit cu : allCus) {
            classCollector.visit(cu, classReport);
            Pair<String, Boolean> pair = new Pair<>(classReport.getSrcFullFileName(), classReport.getMethodsInfo().stream().anyMatch(MethodInfo::isMain));
            pairList.add(pair);
        }

        List<Pair<String, String>> pairs = new ArrayList<>();
        for (Pair<String, Boolean> booleanPair : pairList) {
            for (PackageDeclaration packageDeclaration : cuPack) {
                if (booleanPair.a.contains(packageDeclaration.getNameAsString())) {
                    if (booleanPair.b) {
                        Pair<String, String> stringStringPair = new Pair<>(packageDeclaration.getNameAsString(), booleanPair.a);
                        pairs.add(stringStringPair);
                    }
                }
            }
        }

        projectReport.setPairList(pairs);
    }
}
