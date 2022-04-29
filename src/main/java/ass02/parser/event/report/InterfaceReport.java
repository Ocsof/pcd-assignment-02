package ass02.parser.event.report;

import ass02.parser.event.ProjectElem;

import java.util.List;

public interface InterfaceReport extends ProjectElem {
    String getFullInterfaceName();

    String getSrcFullFileName();

    List<String> getAllMethodsName();
}