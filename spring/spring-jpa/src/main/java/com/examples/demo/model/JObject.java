package com.examples.demo.model;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.util.ClassUtils;

@MappedSuperclass
public abstract class JObject implements Persistable<Integer> {
	
	private static final long serialVersionUID = 1L;

	protected JObject() {}
	

	public abstract Integer getId();

	protected abstract void setId(Integer id);

	/**
	 * Must be {@link Transient} in order to ensure that no JPA provider complains because of a missing setter.
	 * 
	 * @see DATAJPA-622
	 * @see org.springframework.data.domain.Persistable#isNew()
	 */
	@Transient
	public boolean isNew() {
		return null == getId();
	}

	@Override
	public String toString() {
		return String.format("Entity of type %s with id: %s", this.getClass().getName(), getId());
	}

	@Override
	public boolean equals(Object obj) {

		if (null == obj) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		if (!getClass().equals(ClassUtils.getUserClass(obj))) {
			return false;
		}

		AbstractPersistable<?> that = (AbstractPersistable<?>) obj;

		return null == this.getId() ? false : this.getId().equals(that.getId());
	}

	@Override
	public int hashCode() {

		int hashCode = 17;

		hashCode += null == getId() ? 0 : getId().hashCode() * 31;

		return hashCode;
	}
	
}
