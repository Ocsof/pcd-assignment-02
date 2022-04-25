package ass02.parser.event.report;

import com.github.javaparser.utils.Pair;
import ass02.parser.event.ProjectElem;

import java.util.List;

public interface ProjectReport extends ProjectElem {

    List<PackageReport> getPackageReport();

    List<Pair<String, String>> getPackageAndMain();
}
