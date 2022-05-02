package ass02.parser.model.report;

import ass02.parser.event.ProjectElem;

import java.util.List;

public interface PackageReport extends ProjectElem {

    String getFullPackageName();

    List<ClassReport> getClassesReport();

    List<InterfaceReport> getInterfacesReport();

}
