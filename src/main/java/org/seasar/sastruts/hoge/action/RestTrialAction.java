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

	@Resource
	protected HttpServletRequest httpServletRequest;
	@Resource
	protected HttpServletResponse httpServletResponse;
	@ActionForm
	public RestTrialForm restTrialForm;

	// データ
	public static HashMap<Integer, LinkedHashMap<String, String>> dataMap = new HashMap<Integer, LinkedHashMap<String, String>>();
	// シーケンス
	public static Integer sequence = 0;

	@Execute(urlPattern = "data/{id}", validator = false)
	public String data() {
		// idのバリデートは済んでいると仮定。
		Integer id = Integer.valueOf(restTrialForm.id);
		LinkedHashMap<String, String> resHashMap = new LinkedHashMap<String, String>();
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
			// 先にシーケンスを繰り上げる。
			// String body =
			// ReaderUtil.readText(httpServletRequest.getReader());
			System.out.println(restTrialForm.jsonBody);
			LinkedHashMap<String, String> decode = JSON
					.decode(restTrialForm.jsonBody);
			// 永続化
			dataMap.put(++sequence, decode);
			// idフィールドをセット
			decode.put("id", sequence.toString());
			// 戻り値を作成
			resHashMap = decode;
			break;
		case "PUT":
			// 更新。
			
			break;
		case "DELETE":
			
			
			break;
		default:
			// Method Not Allowed
			httpServletResponse.setStatus(405);
			break;
		}

		ResponseUtil.write(JSON.encode(resHashMap), "application/json");
		return null;
	}
}
