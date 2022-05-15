package ass02.parser.model.report;

import java.util.List;

public interface PackageReport extends ProjectElem {

    String getFullPackageName();

    List<ClassReport> getClassesReport();

    List<InterfaceReport> getInterfacesReport();

}
