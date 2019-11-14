/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.utils;

import io.litmusblox.server.service.MasterDataBean;
import io.sentry.Sentry;
import io.sentry.event.BreadcrumbBuilder;
import io.sentry.event.UserBuilder;

import java.util.Map;

/**
 * @author : Sumit
 * Date : 09/08/19
 * Time : 11:36 AM
 * Class Name : SentryUtil
 * Project Name : server
 */
public class SentryUtil {

    static {
        // You can also manually provide the DSN to the ``init`` method.
        Sentry.init(MasterDataBean.getInstance().getSentryDSN());
    }
    public static void logWithStaticAPI(String email, String message, Map<String, String> breadCrumb){

       // Sentry.init(MasterDataBean.getInstance().getSentryDSN());

        Sentry.getContext().clear();//Clear context each time, so fresh data is sent

        // Note that all fields set on the context are optional. Context data is copied onto
        // all future events in the current context (until the context is cleared).

        // Record a breadcrumb in the current context. By default the last 100 breadcrumbs are kept.
        //Breadcrumb is a map with key value pairs. Helps to send extra params to sentry to help troubleshoot
        if(breadCrumb != null && !breadCrumb.isEmpty()) {
            for (Map.Entry<String, String> entry : breadCrumb.entrySet()){
                if(null != entry.getValue() && null != entry.getKey()){
                    BreadcrumbBuilder breadcrumbBuilder = new BreadcrumbBuilder();
                    breadcrumbBuilder.setMessage(entry.getValue());
                    breadcrumbBuilder.setCategory(entry.getKey());
                    Sentry.getContext().recordBreadcrumb(breadcrumbBuilder.build());
                }
            }
        }

        if(null == email) {
            email = "System";
        }

        // Set the user in the current context.
        Sentry.getContext().setUser(
                new UserBuilder().setEmail(email).build()
        );

/*        // Add extra data to future events in this context.
        Sentry.getContext().addExtra("extra", "thing");*/

        // Add an additional tag to future events in this context.
        //Tag the environment the error came in
        Sentry.getContext().addTag("env", MasterDataBean.getInstance().getSentryDSN());


/*         This sends a simple event to Sentry using the statically stored instance
         that was created in the ``main`` method.*/

        Sentry.capture(message);
    }
}
