package com.company.timesheets.view.uiasynctasks;


import com.company.timesheets.view.main.MainView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.asynctask.UiAsyncTasks;
import io.jmix.flowui.component.textfield.TypedTextField;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.lang.Thread.sleep;

@Route(value = "ui-async-tasks-view", layout = MainView.class)
@ViewController(id = "ts_UiAsyncTasksView")
@ViewDescriptor(path = "ui-async-tasks-view.xml")
public class UiAsyncTasksView extends StandardView {
    @Autowired
    private UiAsyncTasks uiAsyncTasks;
    @Autowired
    private Notifications notifications;
    @ViewComponent
    private TypedTextField<Object> inputField;

    @Subscribe(id = "woResultBtn", subject = "clickListener")
    public void onWoResultBtnClick(final ClickEvent<JmixButton> event) {
        uiAsyncTasks.runnableConfigurer(this::voidMethod)
                .withResultHandler(() -> {
                    notifications.show("Action completed!");
                })
                .runAsync();
    }

    private void voidMethod() {
        try {
            sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted", e);
        }
    }

    @Subscribe(id = "performChanges", subject = "clickListener")
    public void onPerformChangesClick(final ClickEvent<JmixButton> event) {
        String input = inputField.getValue();

        uiAsyncTasks.supplierConfigurer(() -> changeString(input))
                .withResultHandler(resultString -> {
                    notifications.show(resultString);
                })
                .withTimeout(3, TimeUnit.SECONDS)
                .withExceptionHandler(ex -> {
                    if (ex instanceof TimeoutException) {
                        notifications.create("Timeout exception!")
                                .withType(Notifications.Type.WARNING)
                                .show();
                    } else {
                        notifications.create("Unknown error: ", ex.getMessage())
                                .withType(Notifications.Type.WARNING)
                                .show();
                    }
                })
                .supplyAsync();
    }

    private String changeString(String input) {
        try {
            sleep(4000);
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted", e);
        }

        return (input + "changed").toUpperCase();
    }
}