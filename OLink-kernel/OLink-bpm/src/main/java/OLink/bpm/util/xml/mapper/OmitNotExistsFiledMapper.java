package OLink.bpm.util.xml.mapper;

import com.thoughtworks.xstream.mapper.OuterClassMapper;
import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;

public class OmitNotExistsFiledMapper extends MapperWrapper {

	public OmitNotExistsFiledMapper(Mapper wrapped) {
		super(wrapped);
	}

	public OmitNotExistsFiledMapper(OuterClassMapper wrapped) {
		this((Mapper) wrapped);
	}

	/**
	 * @SuppressWarnings shouldSerializeMember方法不支持泛型
	 */
	public boolean shouldSerializeMember(Class definedIn, String fieldName) {
		return isFieldExists(definedIn, fieldName)
				&& super.shouldSerializeMember(definedIn, fieldName);
	}

	private boolean isFieldExists(Class<?> definedIn, String fieldName) {
		try {
			definedIn.getDeclaredField(fieldName);
		} catch (SecurityException e) {
			e.printStackTrace();
			return false;
		} catch (NoSuchFieldException e) {
			return false;
		}
		return true;
	}
}
