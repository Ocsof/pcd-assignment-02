package ass02.parser.event;

import ass02.parser.model.collector.ClassCollector;
import ass02.parser.model.collector.InterfaceCollector;
import ass02.parser.model.collector.PackageCollector;
import ass02.parser.model.collector.ProjectCollector;
import ass02.parser.model.report.ClassReportImpl;
import ass02.parser.model.report.InterfaceReportImpl;
import ass02.parser.model.report.PackageReportImpl;
import ass02.parser.model.report.ProjectReportImpl;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class TestSyncReports {

    @Test
    public void testClassReport() {
        CompilationUnit cu;
        try {
            cu = StaticJavaParser.parse(new File("src/main/java/it/unibo/pcd/assignment/event/report/MethodInfoImpl.java"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        ClassReportImpl classReport = new ClassReportImpl();
        ClassCollector classCollector = new ClassCollector();
        classCollector.visit(cu, classReport);
        System.out.println(classReport);
    }

    @Test
    public void testInterfaceReport() {
        CompilationUnit cu;
        try {

            cu = StaticJavaParser.parse(new File("src/main/java/it/unibo/pcd/assignment/event/ProjectReport.java"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        InterfaceReportImpl interfaceReport = new InterfaceReportImpl();
        InterfaceCollector interfaceCollector = new InterfaceCollector();
        interfaceCollector.visit(cu, interfaceReport);
        System.out.println(interfaceReport);
    }

    @Test
    public void testPackageReport() {
        PackageDeclaration cu;
        cu = StaticJavaParser.parsePackageDeclaration("package it.unibo.pcd.assignment.event.report;");
        // cu = StaticJavaParser.parseResource("src/main/java/it/unibo/pcd/assignment/event/");
        PackageReportImpl packageReport = new PackageReportImpl();
        PackageCollector packageCollector = new PackageCollector();
        packageCollector.visit(cu, packageReport);
        System.out.println(packageReport);
    }

    @Test
    public void testProjectReport() {
        ProjectCollector projectCollector = new ProjectCollector();
        ProjectReportImpl projectReport = new ProjectReportImpl();
        projectCollector.visit("src/main/java", projectReport);
        System.out.println("Package and Main: ");
        System.out.println(projectReport.getPackageAndMain());
        System.out.println();
        System.out.println();
        System.out.println("Packages report: ");
        System.out.println(projectReport.getPackageReport());
    }

}
