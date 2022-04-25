package ass02.parser.event.report;

public interface MethodInfo {

    boolean isMain();

    String getName();

    int getSrcBeginLine();

    int getEndBeginLine();

    ClassReport getParent();

}
