package ass02.parser.model.report;

import java.util.List;

public interface InterfaceReport extends ProjectElem {
    String getFullInterfaceName();

    String getSrcFullFileName();

    List<String> getAllMethodsName();
}
