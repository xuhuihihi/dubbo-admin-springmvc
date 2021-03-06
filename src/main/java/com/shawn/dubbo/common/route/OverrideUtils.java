/*
 * Copyright 1999-2012 Alibaba Group.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.shawn.dubbo.common.route;

import com.alibaba.dubbo.common.Constants;
import com.shawn.dubbo.dao.Override;
import com.shawn.dubbo.dao.Provider;
import com.shawn.dubbo.dao.Consumer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class OverrideUtils {
	
	public static final Comparator<Override> OVERRIDE_COMPARATOR = new Comparator<Override>() {
		public int compare(Override o1, Override o2) {
			if (o1 == null && o2 == null) {
				return 0;
			}
			if (o1 == null) {
				return -1;
			}
			if (o2 == null) {
				return 1;
			}
			int cmp = cmp(o1.getAddress(), o2.getAddress());
			if (cmp != 0) {
				return cmp;
			}
			cmp = cmp(o1.getApplication(), o2.getApplication());
			if (cmp != 0) {
				return cmp;
			}
			return cmp(o1.getService(), o2.getService());
		}
		
		private int cmp(String s1, String s2) {
			if (s1 == null && s2 == null) {
				return 0;
			}
			if (s1 == null) {
				return -1;
			}
			if (s2 == null) {
				return 1;
			}
			if (s1.equals(s2)) {
				return 0;
			}
			if (isAny(s1)) {
				return 1;
			}
			if (isAny(s2)) {
				return -1;
			}
			return s1.compareTo(s2);
		}
		private boolean isAny(String s) {
			return s == null || s.length() == 0 || Constants.ANY_VALUE.equals(s) || Constants.ANYHOST_VALUE.equals(s);
		}
	};

	/**
	 * 动态配置消费者
	 * @param consumer
	 * @param overrides
     */
	public static void setConsumerOverrides(Consumer consumer, List<Override> overrides) {
		if (consumer == null || overrides == null) {
			return;
		}
		List<Override> result = new ArrayList<Override>(overrides.size());
		for(Override override : overrides){
    		if (! override.isEnabled()) {//动态配置不可用
				continue;
			}
    		if (override.isMatch(consumer)) {//override 匹配 消费者
    			result.add(override);
    		}
    		if (override.isUniqueMatch(consumer)) {//唯一匹配
    			consumer.setOverride(override);
    		}
    	}
		Collections.sort(result, OverrideUtils.OVERRIDE_COMPARATOR);//result 按照override规定 进行排序
		consumer.setOverrides(result);
	}
	
	public static void setProviderOverrides(Provider provider, List<Override> overrides) {
		if (provider == null || overrides == null) {
			return;
		}
		List<Override> result = new ArrayList<Override>(overrides.size());
		for(Override override : overrides){
    		if (! override.isEnabled()) {
				continue;
			}
    		if (override.isMatch(provider)) {
    			result.add(override);
    		}
    		if (override.isUniqueMatch(provider)) {
    			provider.setOverride(override);
    		}
    	}
		provider.setOverrides(overrides);
	}

}
