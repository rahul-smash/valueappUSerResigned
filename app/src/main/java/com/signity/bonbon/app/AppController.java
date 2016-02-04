package com.signity.bonbon.app;

import android.content.Context;

/**
 * Created by rajesh on 19/1/16.
 */
public class AppController {

    public Context context;
    public static AppController cInstance;
    public ViewController viewController;
    public FunctionalController funcController;


    public AppController(Context context) {
        this.context = context;
    }

    /* Static 'instance' method */
    public static AppController getInstance() {
        return cInstance;
    }

    public static void initInstance(Context context) {
        if (cInstance == null) {
            cInstance = new AppController(context);
        }
    }

    public ViewController getViewController() {
        if (viewController == null) {
            viewController = new ViewController(this.context);
        }
        return viewController;
    }

    public FunctionalController getfuncController() {
        if (funcController == null) {
            funcController = new FunctionalController(this.context);
        }
        return funcController;
    }
}
