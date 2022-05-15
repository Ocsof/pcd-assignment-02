package ass02.parser.model.report;

import java.util.List;

public interface ClassReport extends ProjectElem {
    String getFullClassName();

    String getSrcFullFileName();

    List<MethodInfo> getMethodsInfo();

    List<FieldInfo> getFieldsInfo();

}
