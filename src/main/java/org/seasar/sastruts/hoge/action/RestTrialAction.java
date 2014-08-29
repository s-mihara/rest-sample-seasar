/*
 * restクラス
 */
package org.seasar.sastruts.hoge.action;

import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.arnx.jsonic.JSON;

import org.seasar.sastruts.hoge.form.RestTrialForm;
import org.seasar.struts.annotation.ActionForm;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.util.ResponseUtil;

public class RestTrialAction {
	
	// データ
	public static HashMap<Integer, LinkedHashMap<String, String>> dataMap = new HashMap<Integer, LinkedHashMap<String, String>>();
	// シーケンス
	public static Integer sequence = 0;

	@Resource
	protected HttpServletRequest httpServletRequest;
	@Resource
	protected HttpServletResponse httpServletResponse;
	@ActionForm
	public RestTrialForm restTrialForm;

	@Execute(urlPattern = "data/{id}", validator = false)
	public String data() {
		// idのバリデートは済んでいると仮定。
		Integer id = Integer.valueOf(restTrialForm.id);
		LinkedHashMap<String, String> resHashMap = new LinkedHashMap<String, String>();
		LinkedHashMap<String, String> decode;
		switch (httpServletRequest.getMethod().toUpperCase()) {
		case "GET":
			// 取得
			if (dataMap.containsKey(id)) {
				resHashMap = dataMap.get(id);
			} else {
				httpServletResponse.setStatus(404);
				return null;
			}
			break;
		case "POST":
			// 登録。自動採番させる。
			decode = JSON.decode(restTrialForm.jsonBody);
			// 永続化
			dataMap.put(++sequence, decode);
			// idフィールドをセット
			decode.put("id", sequence.toString());
			// 戻り値を作成
			resHashMap = decode;
			httpServletResponse.setStatus(201);
			httpServletResponse.setHeader(
					"Location",
					"http://localhost:8080/sastrats/restTrial/data/"
							+ sequence.toString());
			break;
		case "PUT":
			// 更新。
			if (dataMap.containsKey(id)) {
				decode = JSON.decode(restTrialForm.jsonBody);
				// 永続化
				dataMap.put(id, decode);
				decode.put("id", id.toString());
				resHashMap = decode;
				httpServletResponse.setStatus(201);
				
			} else {
				httpServletResponse.setStatus(404);
				return null;
			}
			break;
		case "DELETE":
			// 削除
			if (dataMap.containsKey(id)) {
				resHashMap = dataMap.remove(id);
				;
			} else {
				httpServletResponse.setStatus(404);
				return null;
			}
			break;
		default:
			// Method Not Allowed
			httpServletResponse.setStatus(405);
			break;
		}
		ResponseUtil.write(JSON.encode(resHashMap), "application/json");
		return null;
	}

	@Execute(validator = false)
	public String dataFindAll() {
		if ("GET".equalsIgnoreCase(httpServletRequest.getMethod())) {
			ResponseUtil.write(JSON.encode(dataMap), "application/json");
		} else {
			httpServletResponse.setStatus(405);
		}
		return null;
	}
}
