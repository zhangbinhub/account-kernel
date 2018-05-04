package OLink.bpm.util.cache;

import java.lang.reflect.Method;

public class CacheKey {

	private static final long serialVersionUID = -7124787750194694248L;

	private Object o;

	private Method method;

	Object[] methodParameters;

	public CacheKey(Object o, Method method, Object[] methodParameters) {
		this.o = o;
		this.method = method;
		this.methodParameters = methodParameters;
	}

	// public native int hashCode();

	public String toString() {
		StringBuffer tmp = new StringBuffer();
		for (int i = 0; i < methodParameters.length; i++) {
			if (methodParameters[i] != null)
				tmp.append(methodParameters[i]).append(",");
		}
		return o + "&&" + method + tmp;
	}
	/*
	private boolean equalsArray() {
		return false;
	}
	*/

	public boolean equals(Object obj) {
		if (obj != null && obj instanceof CacheKey) {
			CacheKey ck = (CacheKey) obj;
			if ((method != null && method.equals(ck.method))
					|| (method == null && ck.method == null)) {
				if (methodParameters != null) {
					if (methodParameters.length == ck.methodParameters.length) {
						for (int i = 0; i < methodParameters.length; i++) {
							Object param1 = methodParameters[i];
							Object param2 = ck.methodParameters[i];
							if (param1 != null) {
								if (!param1.equals(param2))
									return false;
							} else {
								if (param2 != null)
									return false;
							}
						}
						return true;
					}
				}
			}
		}
		return false;
	}

	public int hashCode() {
		return method.hashCode();
	}

}
