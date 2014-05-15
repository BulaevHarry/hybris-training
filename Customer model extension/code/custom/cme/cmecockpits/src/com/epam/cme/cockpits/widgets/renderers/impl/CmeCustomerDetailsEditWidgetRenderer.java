package com.epam.cme.cockpits.widgets.renderers.impl;

import de.hybris.platform.cockpit.model.meta.TypedObject;
import de.hybris.platform.cockpit.widgets.InputWidget;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.cscockpit.utils.LabelUtils;
import de.hybris.platform.cscockpit.widgets.controllers.CustomerController;
import de.hybris.platform.cscockpit.widgets.models.impl.CustomerItemWidgetModel;
import de.hybris.platform.cscockpit.widgets.renderers.impl.CustomerDetailsEditWidgetRenderer;
import de.hybris.platform.servicelayer.user.UserService;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.api.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;

import com.epam.cme.cockpits.widgets.controllers.impl.CmeCallContextController;
import com.epam.cme.core.model.BlockableCustomerModel;

public class CmeCustomerDetailsEditWidgetRenderer extends CustomerDetailsEditWidgetRenderer {

    private static final Logger LOG = Logger.getLogger(CmeCustomerDetailsEditWidgetRenderer.class);
    private UserService userService;

    @Override
    protected HtmlBasedComponent createContentInternal(
            final InputWidget<CustomerItemWidgetModel, CustomerController> widget,
            final HtmlBasedComponent rootContainer) {
        final Div content = new Div();

        final TypedObject customer = widget.getWidgetModel().getCustomer();
        if ((customer != null) && (customer.getObject() instanceof CustomerModel)) {
            loadAndCreateEditors(widget, content, customer);

            final Div afterContent = new Div();
            afterContent.setParent(content);
            afterContent.setClass("cmeCsCustomerUnlockButton");

            final Div row = new Div();
            row.setParent(content);
            row.setSclass("csCustomerFooterRow");
            renderFooter(row, widget);

            final Div right = new Div();
            right.setParent(row);
            right.setSclass("csCustomerRefreshButton");

            if (isUserCmeAdmin()) {
                final Object customerObject = customer.getObject();
                if (customerObject instanceof BlockableCustomerModel) {
                    final BlockableCustomerModel bc = (BlockableCustomerModel) customerObject;
                    if (bc.getBlockedStatus().booleanValue()) {
                        createUnlockButton(widget, afterContent, "unlockcustomer");
                    } else {
                        createLockButton(widget, afterContent, "lockcustomer");
                    }
                }
            }
            createRefreshButton(widget, right, "refresh");
        } else {
            content.appendChild(new Label(LabelUtils.getLabel(widget, "noCustomerSelected", new Object[0])));
        }
        return content;
    }

    protected void createUnlockButton(final InputWidget<CustomerItemWidgetModel, CustomerController> widget,
            final Div container, final String buttonLabelName) {
        final Button button = new Button(LabelUtils.getLabel(widget, buttonLabelName, new Object[0]));
        button.setParent(container);

        button.addEventListener("onClick", new EventListener() {

            @Override
            public void onEvent(final Event event) throws Exception {
                CmeCustomerDetailsEditWidgetRenderer.this.handleUnlockCustomerEvent(widget, event);
            }
        });

    }

    protected void createLockButton(final InputWidget<CustomerItemWidgetModel, CustomerController> widget,
            final Div container, final String buttonLabelName) {
        final Button button = new Button(LabelUtils.getLabel(widget, buttonLabelName, new Object[0]));
        button.setParent(container);

        button.addEventListener("onClick", new EventListener() {

            @Override
            public void onEvent(final Event event) throws Exception {
                CmeCustomerDetailsEditWidgetRenderer.this.handleLockCustomerEvent(widget, event);
            }
        });

    }

    protected void handleUnlockCustomerEvent(final InputWidget<CustomerItemWidgetModel, CustomerController> widget,
            final Event event) {
        ((CmeCallContextController) widget.getWidgetController()).unblockCustomer();
    }

    protected void handleLockCustomerEvent(final InputWidget<CustomerItemWidgetModel, CustomerController> widget,
            final Event event) {
        ((CmeCallContextController) widget.getWidgetController()).blockCustomer();
    }

    private boolean isUserCmeAdmin() {
        final UserModel currentUser = userService.getCurrentUser();
        final UserGroupModel cmeAdminGroup = userService.getUserGroupForUID("csadmingroup");
        return userService.isMemberOfGroup(currentUser, cmeAdminGroup);
    }

    public void setUserService(final UserService userService) {
        this.userService = userService;
    }

}