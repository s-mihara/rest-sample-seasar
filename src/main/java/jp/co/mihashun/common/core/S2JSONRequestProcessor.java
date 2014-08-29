package jp.co.mihashun.common.core;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.seasar.framework.beans.IllegalPropertyRuntimeException;
import org.seasar.framework.util.ReaderUtil;
import org.seasar.struts.action.S2RequestProcessor;
import org.seasar.struts.config.S2ActionMapping;

public class S2JSONRequestProcessor extends S2RequestProcessor {

	@Override
	protected void processPopulate(HttpServletRequest request,
			HttpServletResponse response, ActionForm form, ActionMapping mapping)
			throws ServletException {
		super.processPopulate(request, response, form, mapping);
		String contentType = request.getContentType();
		String method = request.getMethod();
		S2ActionMapping actionMapping = (S2ActionMapping) mapping;

		/*
		 * jsonの時の処理
		 */
		if (contentType != null
				&& contentType.startsWith("application/json")
				&& ("POST".equalsIgnoreCase(method)
						|| "PUT".equalsIgnoreCase(method) || "DELETE"
							.equalsIgnoreCase(method))) {
			try {

				String postBody = ReaderUtil.readText(request.getReader());
				try {
					setProperty(actionMapping.getActionForm(), "jsonBody",
							postBody);
				} catch (Throwable t) {
					throw new IllegalPropertyRuntimeException(actionMapping
							.getActionFormBeanDesc().getBeanClass(), postBody,
							t);
				}

			} catch (IOException e) {
				log.error(e.getMessage(), e);
				throw new ServletException(e.getMessage(), e);
			}

		}

	}

}