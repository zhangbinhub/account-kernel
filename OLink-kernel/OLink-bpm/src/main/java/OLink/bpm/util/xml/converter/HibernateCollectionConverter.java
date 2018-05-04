package OLink.bpm.util.xml.converter;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.thoughtworks.xstream.converters.collections.CollectionConverter;
import com.thoughtworks.xstream.mapper.Mapper;

public class HibernateCollectionConverter extends CollectionConverter {
	public HibernateCollectionConverter(Mapper mapper) {
		super(mapper);
	}

	/**
	 * @SuppressWarnings canConvert方法不支持泛型
	 */
	@SuppressWarnings("unchecked")
	public boolean canConvert(Class type) {
		Collection<Class> interfaces = Arrays
				.asList(type.getInterfaces());
		return super.canConvert(type) || interfaces.contains(List.class)
				|| interfaces.contains(Set.class);
	}
}
