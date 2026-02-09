package com.company.timesheets.view.client;

import com.company.timesheets.entity.Client;
import com.company.timesheets.view.main.MainView;
import com.google.common.base.Strings;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.AnchorTarget;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.dom.ElementFactory;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.streams.DownloadHandler;
import com.vaadin.flow.server.streams.DownloadResponse;
import com.vaadin.flow.theme.lumo.LumoUtility;
import io.jmix.core.AccessManager;
import io.jmix.core.MessageTools;
import io.jmix.core.Messages;
import io.jmix.core.accesscontext.InMemoryCrudEntityContext;
import io.jmix.core.metamodel.model.MetaClass;
import io.jmix.flowui.UiComponents;
import io.jmix.flowui.ViewNavigators;
import io.jmix.flowui.accesscontext.UiEntityContext;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.util.RemoveOperation;
import io.jmix.flowui.view.*;
import io.jmix.tabbedmode.view.TabbedModeViewProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;

import java.io.ByteArrayInputStream;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Route(value = "clients", layout = MainView.class)
@ViewController("ts_Client.list")
@ViewDescriptor("client-list-view.xml")
@DialogMode(width = "64em")
@TabbedModeViewProperties(forceDialog = true)
public class ClientListView extends StandardView {
    @Autowired
    private UiComponents uiComponents;
    @ViewComponent
    private JmixButton createBtn;

    @ViewComponent
    private CollectionContainer<Client> clientsDc;
    @Autowired
    private AccessManager accessManager;
    @Autowired
    private ViewNavigators viewNavigators;
    @Autowired
    private MessageTools messageTools;

    private UiEntityContext uiEntityContext;
    private InMemoryCrudEntityContext crudEntityContext;
    @Autowired
    private Messages messages;
    @Autowired
    private RemoveOperation removeOperation;

    @Subscribe
    public void onInit(final InitEvent event) {
        createBtn.setEnabled(getEntityContext().isCreatePermitted());
    }

    private UiEntityContext getEntityContext() {
        if (uiEntityContext == null) {
            MetaClass metaClass = clientsDc.getEntityMetaClass();
            uiEntityContext = new UiEntityContext(metaClass);
            accessManager.applyRegisteredConstraints(uiEntityContext);
        }
        return uiEntityContext;
    }

    @Subscribe(id = "createBtn", subject = "clickListener")
    public void onCreateBtnClick(final ClickEvent<JmixButton> event) {
        viewNavigators.detailView(this, Client.class)
                .newEntity()
                .withBackwardNavigation(true)
                .navigate();
    }

    @Supply(to = "clientsList", subject = "renderer")
    private Renderer<Client> clientsListRenderer() {
        return new ComponentRenderer<>(client -> {
            HorizontalLayout layout = uiComponents.create(HorizontalLayout.class);
            layout.setPadding(true);
            layout.addClassNames(LumoUtility.BorderRadius.MEDIUM, LumoUtility.Margin.SMALL, LumoUtility.BoxShadow.SMALL);

            layout.add(
                    createAvatar(client.getName(), client.getImage(), "var(--lumo-size-xl)"),
                    createInfoLayout(client),
                    createActionsLayout(client)
            );

            return layout;
        });
    }

    private Avatar createAvatar(String name, byte[] image, String size) {
        Avatar avatar = uiComponents.create(Avatar.class);
        avatar.setName(name);

        if (image != null) {
            DownloadHandler handler = DownloadHandler.fromInputStream(downloadEvent ->
                    new DownloadResponse(
                            new ByteArrayInputStream(image),
                            "avatar.img",
                            "application/octet-stream",
                            image.length
                    ));
            avatar.setImageHandler(handler);
        }

        avatar.setWidth(size);
        avatar.setHeight(size);

        return avatar;
    }

    private Component createInfoLayout(Client client) {
        VerticalLayout infoBox = uiComponents.create(VerticalLayout.class);
        infoBox.getElement().appendChild(
                ElementFactory.createStrong(client.getName())
        );
        if (client.getContactInformation() != null) {
            infoBox.add(createCILayout(client));
        }

        return infoBox;
    }

    private Component createCILayout(Client client) {
        VerticalLayout ciLayout = uiComponents.create(VerticalLayout.class);

        createCIItem(
                client.getContactInformation().getEmail(),
                VaadinIcon.ENVELOPE.create(),
                Text::new
        ).ifPresent(ciLayout::add);

        createCIItem(
                client.getContactInformation().getPhone(),
                VaadinIcon.PHONE.create(),
                Text::new
        ).ifPresent(ciLayout::add);

        createCIItem(
                client.getContactInformation().getAddress(),
                VaadinIcon.MAP_MARKER.create(),
                Text::new
        ).ifPresent(ciLayout::add);

        createCIItem(
                client.getContactInformation().getUrl(),
                VaadinIcon.LINK.create(),
                text -> new Anchor(text, text, AnchorTarget.BLANK)
        ).ifPresent(ciLayout::add);

        Details ciDetails = uiComponents.create(Details.class);
        ciDetails.setOpened(true);
        ciDetails.setSummaryText(
                messageTools.getPropertyCaption(clientsDc.getEntityMetaClass(),
                        "contactInformation")
        );
        ciDetails.setWidthFull();
        ciDetails.add(ciLayout);

        return ciDetails;
    }

    private Optional<Component> createCIItem(@Nullable String value, Icon icon, Function<String, Component> valueRenderer) {
        return Strings.isNullOrEmpty(value)
                ? Optional.empty()
                : Optional.of(new HorizontalLayout(icon, valueRenderer.apply(value)));
    }

    private Component createActionsLayout(Client client) {
        VerticalLayout actionsBox = uiComponents.create(VerticalLayout.class);
        actionsBox.setPadding(false);
        actionsBox.setWidth("AUTO");

        Button editBtn = createEditButton(client);
        Button removeBtn = createRemoveButton(client);
        actionsBox.add(editBtn, removeBtn);

        return actionsBox;
    }

    private Button createEditButton(Client client) {
        boolean editPermitted = editPermitted(client);

        Button btn = uiComponents.create(Button.class);
        btn.setTooltipText(messages.getMessage(editPermitted ? "actions.Edit" : "actions:Read"));
        btn.setIcon(editPermitted ? VaadinIcon.PENCIL.create() : VaadinIcon.EYE.create());

        btn.addClickListener(event -> editClient(client));

        return btn;
    }

    private void editClient(Client client) {
        viewNavigators.detailView(this, Client.class)
                .editEntity(client)
                .withBackwardNavigation(true)
                .navigate();
    }

    private boolean editPermitted(Client client) {
        return getEntityContext().isEditPermitted()
                && (getCrudEntityContext().updatePredicate() == null
                || getCrudEntityContext().isUpdatePermitted(client));
    }

    private InMemoryCrudEntityContext getCrudEntityContext() {
        if (crudEntityContext == null) {
            crudEntityContext = new InMemoryCrudEntityContext(clientsDc.getEntityMetaClass(), getApplicationContext());
            accessManager.applyRegisteredConstraints(crudEntityContext);
        }

        return crudEntityContext;
    }

    private Button createRemoveButton(Client client) {
        Button btn = uiComponents.create(Button.class);
        btn.setTooltipText(messages.getMessage("actions.Remove"));
        btn.setIcon(VaadinIcon.TRASH.create());
        btn.addThemeVariants(ButtonVariant.LUMO_ERROR);
        btn.addClickListener(event -> removeClient(client));

        btn.setVisible(deletePermitted(client));

        return btn;
    }

    private boolean deletePermitted(Client client) {
        return getEntityContext().isDeletePermitted()
                && (getCrudEntityContext().updatePredicate() == null
                || getCrudEntityContext().isDeletePermitted(client));
    }

    private void removeClient(Client client) {
        removeOperation.builder(Client.class, this)
                .withConfirmation(true)
                .withItems(List.of(client))
                .withContainer(clientsDc)
                .remove();
    }
}
