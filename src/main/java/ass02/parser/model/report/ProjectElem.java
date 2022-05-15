package ass02.parser.model.report;

public interface ProjectElem {
    enum Type {
        CLASS, INTERFACE, PACKAGE
    }

    Type getType();

}
