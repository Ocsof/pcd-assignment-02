package ass02.reactiveparser.controller;

import ass02.reactiveparser.view.ViewFrame;
import ass02.reactiveparser.ReactiveProjectAnalyzer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ViewControllerReactive {
    public static int CLASS_NUMBER = 0;
    public static int INTERFACE_NUMBER = 0;
    public static int PACKAGE_NUMBER = 0;
    private final JTextArea outputConsole;
    private final ViewFrame view;
    private final ReactiveProjectAnalyzer projectAnalyzer;
    private boolean isStopped;
    private Disposable process;
    private String projectPath;

    public ViewControllerReactive() {
        this.projectAnalyzer = new ReactiveProjectAnalyzer();
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
            this.isStopped = false;
            this.clearTextArea();
            this.process = Schedulers.computation().scheduleDirect(() ->
                    this.projectAnalyzer.analyzeProject(this.projectPath)
            );
            //this.projectAnalyzer.analyzeProject(this.projectPath, (k) -> this.log(k.toString()));
        }
    }

    private void clearTextArea() {
        ViewControllerReactive.CLASS_NUMBER = 0;
        ViewControllerReactive.INTERFACE_NUMBER = 0;
        ViewControllerReactive.PACKAGE_NUMBER = 0;
        this.view.getTextClass().setText("0");
        this.view.getTextInterface().setText("0");
        this.view.getTextPackage().setText("0");
        this.view.getConsoleTextArea().selectAll();
        this.view.getConsoleTextArea().replaceSelection("");
    }

    public void stopAnalysisPressed(ActionEvent e) {
        this.isStopped = true;
        this.process.dispose();
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
        int indentation = 0;
        switch(message.substring(0,3)){
            case "Cla":
                    this.increaseClassNumber();
                    indentation = 6;
                    break;
            case "Int":
                    this.increaseInterfaceNumber();
                    indentation = 6;
                    break;
            case "Pac":
                    this.increasePackageNumber();
        }

        this.outputConsole.append(message.indent(indentation) + "\n");
    }
}
