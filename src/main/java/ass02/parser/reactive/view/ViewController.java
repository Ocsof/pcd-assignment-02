package ass02.parser.reactive.view;

import ass02.parser.model.report.ProjectElem;
import ass02.parser.reactive.ProjectAnalyzerImpl;
import ass02.parser.reactive.view.ViewFrame;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ViewController {
    public static int CLASS_NUMBER = 0;
    public static int INTERFACE_NUMBER = 0;
    public static int PACKAGE_NUMBER = 0;
    private final JTextArea outputConsole;
    private final ViewFrame view;
    private final ProjectAnalyzerImpl projectAnalyzer;
    private Disposable disposable;

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
            this.clearTextArea();
            this.disposable = this.projectAnalyzer.analyzeProject(this.projectPath)
                    .subscribe(this::log);
        }
    }

    private void clearTextArea() {
        ViewController.CLASS_NUMBER = 0;
        ViewController.INTERFACE_NUMBER = 0;
        ViewController.PACKAGE_NUMBER = 0;
        this.view.getTextClass().setText("0");
        this.view.getTextInterface().setText("0");
        this.view.getTextPackage().setText("0");
        this.view.getConsoleTextArea().selectAll();
        this.view.getConsoleTextArea().replaceSelection("");
    }

    public void stopAnalysisPressed(ActionEvent e) {
        this.disposable.dispose();
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

    public void log(ProjectElem projectElem) {
        SwingUtilities.invokeLater(() -> {
            int indentation = 0;
            switch(projectElem.getType()){
                case CLASS:
                    this.increaseClassNumber();
                    indentation = 6;
                    break;
                case INTERFACE:
                    this.increaseInterfaceNumber();
                    indentation = 6;
                    break;
                case PACKAGE:
                    this.increasePackageNumber();
            }

            this.outputConsole.append(projectElem.toString().indent(indentation) + "\n");
        });
    }
}
