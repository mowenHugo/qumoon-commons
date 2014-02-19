package com.qumoon.commons.web.spring.csrf;

import freemarker.ext.servlet.FreemarkerServlet;
import freemarker.ext.servlet.ServletContextHashModel;
import freemarker.template.SimpleHash;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.support.RequestDataValueProcessor;
import org.springframework.web.servlet.view.freemarker.FreeMarkerView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author kevin
 */
public class CsrfFreeMarkerView extends FreeMarkerView {
    private CsrfTokenManager csrfTokenManager;

    protected SimpleHash buildTemplateModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) {
        SimpleHash fmModel = super.buildTemplateModel(model, request, response);
        Object csrfTokenManagerObj = getApplicationContext().getBean("csrfTokenManager");
        if (null != csrfTokenManagerObj && csrfTokenManagerObj instanceof CsrfTokenManager) {
            csrfTokenManager = (CsrfTokenManager) csrfTokenManagerObj;
        } else {
            logger.error("can not init CsrfTokenManager");
        }
        try {
            TemplateModel application = fmModel.get(FreemarkerServlet.KEY_APPLICATION);
            if (application instanceof ServletContextHashModel) {
                Object reqObj = model.get("requestContext");
                if (null != reqObj && reqObj instanceof RequestContext) {
                    RequestContext requestContext = (RequestContext) reqObj;
                    RequestDataValueProcessor processor = requestContext.getRequestDataValueProcessor();
                    if (processor != null) {
                        fmModel.put("csrfKey", CsrfTokenManager.CSRF_PARAM_NAME);
                        fmModel.put("csrfValue", csrfTokenManager.getTokenInSession(request.getSession().getId()));
                    }
                }
            }
        } catch (TemplateModelException e) {
        }
        return fmModel;
    }

    public void setCsrfTokenManager(CsrfTokenManager csrfTokenManager) {
        this.csrfTokenManager = csrfTokenManager;
    }
}
