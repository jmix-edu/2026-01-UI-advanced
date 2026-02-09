package com.company.timesheets.view.task;

import com.company.timesheets.entity.Task;
import com.company.timesheets.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;
import io.jmix.tabbedmode.view.MultipleOpen;


@Route(value = "tasks-simple", layout = MainView.class)
@ViewController(id = "ts_TaskSimple.list")
@ViewDescriptor(path = "task-simple-list-view.xml")
@LookupComponent("tasksDataGrid")
@DialogMode(width = "64em")
public class TaskSimpleListView extends StandardListView<Task> {
}