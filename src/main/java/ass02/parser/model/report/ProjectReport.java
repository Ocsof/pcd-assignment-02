package ass02.parser.model.report;

import com.github.javaparser.utils.Pair;

import java.util.List;

public interface ProjectReport {

    List<PackageReport> getPackageReport();

    List<Pair<String, String>> getPackageAndMain();
}
