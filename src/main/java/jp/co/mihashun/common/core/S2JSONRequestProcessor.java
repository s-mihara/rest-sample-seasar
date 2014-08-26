package jp.co.mihashun.common.core;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.MultipartRequestHandler;
import org.seasar.framework.beans.IllegalPropertyRuntimeException;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;
import org.seasar.framework.util.ReaderUtil;
import org.seasar.struts.action.S2RequestProcessor;
import org.seasar.struts.config.S2ActionMapping;

public class S2JSONRequestProcessor extends S2RequestProcessor {

	@Override
	protected void processPopulate(HttpServletRequest request,
			HttpServletResponse response, ActionForm form, ActionMapping mapping)
			throws ServletException {

		// if (form == null) {
		// return;
		// }
		//
		// String contentType = request.getContentType();
		// String method = request.getMethod();
		// if (contentType != null && contentType.startsWith("application/json")
		// && "POST".equalsIgnoreCase(method)) {
		//
		// form.setServlet(servlet);
		// form.setMultipartRequestHandler(null);
		// processExecuteConfig(request, response, mapping);
		// form.reset(mapping, request);
		//
		// Object actionForm = S2ActionMappingUtil.getActionMapping()
		// .getActionForm();
		// try {
		//
		// String postBody = ReaderUtil.readText(request.getReader());
		// Object value = postBody;
		//
		// Beans.copy(value, actionForm).execute();
		//
		// } catch (IOException e) {
		// log.error(e.getMessage(), e);
		// throw new ServletException(e.getMessage(), e);
		// }
		//
		// } else {
		//
		// super.processPopulate(request, response, form, mapping);
		//
		// }
		if (form == null) {
			return;
		}
		form.setServlet(servlet);
		String contentType = request.getContentType();
		String method = request.getMethod();
		form.setMultipartRequestHandler(null);
		MultipartRequestHandler multipartHandler = null;
		if (contentType != null
				&& contentType.startsWith("multipart/form-data")
				&& method.equalsIgnoreCase("POST")) {
			multipartHandler = getMultipartHandler(mapping.getMultipartClass());
			if (multipartHandler != null) {
				multipartHandler.setServlet(servlet);
				multipartHandler.setMapping(mapping);
				multipartHandler.handleRequest(request);
				Boolean maxLengthExceeded = (Boolean) request
						.getAttribute(MultipartRequestHandler.ATTRIBUTE_MAX_LENGTH_EXCEEDED);
				if ((maxLengthExceeded != null)
						&& (maxLengthExceeded.booleanValue())) {
					form.setMultipartRequestHandler(multipartHandler);
					processExecuteConfig(request, response, mapping);
					return;
				}
				SingletonS2ContainerFactory.getContainer().getExternalContext()
						.setRequest(request);
			}
		}
		processExecuteConfig(request, response, mapping);
		form.reset(mapping, request);
		Map<String, Object> params = getAllParameters(request, multipartHandler);
		S2ActionMapping actionMapping = (S2ActionMapping) mapping;
		for (Iterator<String> i = params.keySet().iterator(); i.hasNext();) {
			String name = i.next();
			try {
				setProperty(actionMapping.getActionForm(), name,
						params.get(name));
			} catch (Throwable t) {
				throw new IllegalPropertyRuntimeException(actionMapping
						.getActionFormBeanDesc().getBeanClass(), name, t);
			}
		}
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