package com.company.timesheets.component.composite;

import com.company.timesheets.ColorPicker;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class ColorComponent extends Composite<HorizontalLayout> {

    private final ColorPicker colorPicker;

    public ColorComponent() {
        colorPicker = new ColorPicker();
        Span valueLabel = new Span(colorPicker.getValue());

        colorPicker.addValueChangeListener(event ->
                valueLabel.setText(event.getValue()));

        getContent().add(colorPicker, valueLabel);
    }

    @Override
    protected HorizontalLayout initContent() {
        HorizontalLayout root = super.initContent();
        root.setAlignItems(FlexComponent.Alignment.CENTER);

        return root;
    }

    public String getValue() {
        return colorPicker.getValue();
    }

    public void setValue(String value) {
        colorPicker.setValue(value);
    }
}
