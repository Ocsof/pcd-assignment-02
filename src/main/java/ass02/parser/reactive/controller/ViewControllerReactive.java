package ass02.parser.reactive.controller;

import ass02.parser.reactive.view.ViewFrame;
import ass02.parser.reactive.model.ReactiveProjectAnalyzerImpl;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;


import javax.swing.*;
import java.awt.event.ActionEvent;

public class ViewControllerReactive {
    public static int CLASS_NUMBER = 0;
    public static int INTERFACE_NUMBER = 0;
    public static int PACKAGE_NUMBER = 0;
    private final JTextArea outputConsole;
    private final ViewFrame view;
    private final ReactiveProjectAnalyzerImpl projectAnalyzer;
    private boolean isStopped;
    private Scheduler.Worker workerProcess;
    private String projectPath;

    public ViewControllerReactive() {
        this.projectAnalyzer = new ReactiveProjectAnalyzerImpl();
        this.view = new ViewFrame(this);
        this.outputConsole = view.getConsoleTextArea();
        this.setObservers();
    }

    private void setObservers() {
        this.listenIncreasePackageNumber();
        this.listenIncreaseInterfaceNumber();
        this.listenIncreaseClassNumber();
        this.listenReportObserver();
    }

    private void listenReportObserver() {
        this.projectAnalyzer.getReportObservable()
                .subscribeOn(Schedulers.newThread())
                .subscribe(res -> {
                    if (!this.isStopped) {
                        view.getConsoleTextArea().append(res + "");
                    }
                });
    }

    private void listenIncreaseClassNumber() {
        this.projectAnalyzer.getClassNumberObservable().subscribeOn(Schedulers.computation())
                .subscribe(res -> this.increaseClassNumber());;
    }

    private void listenIncreaseInterfaceNumber() {
        this.projectAnalyzer.getInterfaceNumberObservable().subscribeOn(Schedulers.computation())
                .subscribe(res -> this.increaseInterfaceNumber());
    }

    private void listenIncreasePackageNumber() {
        this.projectAnalyzer.getPackageNumberObservable().subscribeOn(Schedulers.computation())
                .subscribe(res -> this.increasePackageNumber());
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
            this.workerProcess = Schedulers.computation().createWorker();
            this.workerProcess.schedule( () -> this.projectAnalyzer.analyzeProject(this.projectPath) );

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
        this.workerProcess.dispose();
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
