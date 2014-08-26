/*
 * echoアプリのフォーム
 */
package org.seasar.sastruts.hoge.util;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.seasar.framework.container.annotation.tiger.Component;
import org.seasar.framework.container.annotation.tiger.InstanceType;

//@Component(instance = InstanceType.APPLICATION)
public class RestTrialUtils {
	
	// データ
	public static HashMap<Integer, LinkedHashMap<String, String>> dataMap = new HashMap<Integer, LinkedHashMap<String, String>>();
	// シーケンス
	public static Integer sequence ;
	
}
