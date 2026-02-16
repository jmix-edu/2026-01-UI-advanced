package com.company.timesheets.view.pdfdemo;


import com.company.timesheets.view.main.MainView;
import com.vaadin.componentfactory.pdfviewer.PdfViewer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.streams.DownloadHandler;
import com.vaadin.flow.server.streams.DownloadResponse;
import io.jmix.core.Resources;
import io.jmix.flowui.view.StandardView;
import io.jmix.flowui.view.Subscribe;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

import java.io.ByteArrayInputStream;

@Route(value = "pdf-demo-view", layout = MainView.class)
@ViewController(id = "ts_PdfDemoView")
@ViewDescriptor(path = "pdf-demo-view.xml")
public class PdfDemoView extends StandardView {
    @Autowired
    private Resources resources;

    @Subscribe
    public void onInit(final InitEvent event) {
        PdfViewer pdfViewer = new PdfViewer();
        pdfViewer.setSizeFull();

        Resource resource = resources.getResource("META-INF/resources/files/example.pdf");

        DownloadHandler handler = DownloadHandler.fromInputStream(downloadEvent ->
                new DownloadResponse(
                        new ByteArrayInputStream(resource.getContentAsByteArray()),
                        "example.pdf",
                        "applition/octet-stream",
                        resource.contentLength()
                )
        );

        pdfViewer.setSrc(handler);
        getContent().add(pdfViewer);
    }
}