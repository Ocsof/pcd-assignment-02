package ass02.parser.event.view;

import ass02.parser.event.ProjectAnalyzerImpl;
import io.vertx.core.Vertx;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Objects;

public class ViewController {
    public static int CLASS_NUMBER = 0;
    public static int INTERFACE_NUMBER = 0;
    public static int PACKAGE_NUMBER = 0;
    private final JTextArea outputConsole;
    private final ViewFrame view;
    private final ProjectAnalyzerImpl projectAnalyzer;

    private String projectPath;

    public ViewController() {
        this.projectAnalyzer = new ProjectAnalyzerImpl();
        this.view = new ViewFrame(this);
        this.outputConsole = view.getConsoleTextArea();
    }

    public void openProjectPressed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.showSaveDialog(fileChooser);
        String path = fileChooser.getSelectedFile().getPath();
        this.projectPath = path;
        view.getFileSelectedLabel().setText(path);
    }

    public void startAnalysisPressed(ActionEvent e) {
        if (!this.projectPath.isEmpty()) {
            this.view.getConsoleTextArea().selectAll();
            this.view.getConsoleTextArea().replaceSelection("");
            ViewController.CLASS_NUMBER = 0;
            ViewController.INTERFACE_NUMBER = 0;
            ViewController.PACKAGE_NUMBER = 0;
            this.view.getTextClass().setText("0");
            this.view.getTextInterface().setText("0");
            this.view.getTextPackage().setText("0");
            this.projectAnalyzer.analyzeProject(this.projectPath, (k) -> this.log(k.toString()));
        }
    }

    public void stopAnalysisPressed(ActionEvent e) {
        try {
            this.projectAnalyzer.getVertx().undeploy(projectAnalyzer.deploymentID());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void increasePackageNumber() {
        this.view.getTextPackage().setText(String.valueOf(++PACKAGE_NUMBER));
    }

    public void increaseClassNumber() {
        this.view.getTextClass().setText(String.valueOf(++CLASS_NUMBER));
    }

    public void increaseInterfaceNumber() {
        this.view.getTextInterface().setText(String.valueOf(++INTERFACE_NUMBER));
    }

    public void log(String message) {
        this.outputConsole.append(message + "\n");
    }
}
