package ass02.parser.model.report;

import ass02.parser.event.ProjectElem;

import java.util.List;

public interface ClassReport extends ProjectElem {
    String getFullClassName();

    String getSrcFullFileName();

    List<MethodInfo> getMethodsInfo();

    List<FieldInfo> getFieldsInfo();

}
