package top.starrysea.hateoas;

public class StarryseaControllerHandle {

	private Class<?> clazz;
	private String methodName;

	private StarryseaControllerHandle(Class<?> clazz, String methodName) {
		this.clazz = clazz;
		this.methodName = methodName;
	}

	public static StarryseaControllerHandle of(Class<?> clazz, String methodName) {
		return new StarryseaControllerHandle(clazz, methodName);
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public String getMethodName() {
		return methodName;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof StarryseaControllerHandle)) {
			return false;
		}
		StarryseaControllerHandle other = (StarryseaControllerHandle) obj;
		return this.clazz == other.getClazz() && this.methodName.equals(other.getMethodName());
	}

	@Override
	public int hashCode() {
		return (clazz.toString() + "/" + methodName).hashCode();
	}

}
