package com.examples.demo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javax.persistence.Persistence;
import javax.transaction.Transactional;

import org.junit.Test;

import com.examples.demo.model.Child;
import com.examples.demo.model.Customer;
import com.examples.demo.model.Parent;

public class EntityManagerTest extends AbstractTest {
	
	@Test
	@Transactional
	public void shouldInitializeProxy_whenFind() {
		Customer c = new Customer("name");
		em.persist(c);
		em.flush();
		em.clear();
		
		Customer c1 = em.getReference(Customer.class, c.getId());
		assertThat(Persistence.getPersistenceUtil().isLoaded(c1)).isFalse();
		Customer c2 = em.find(Customer.class, c.getId());
		
		assertThat(Persistence.getPersistenceUtil().isLoaded(c2)).isTrue();
		assertThat(c1 == c2).isTrue();
		assertThat(Persistence.getPersistenceUtil().isLoaded(c1)).isTrue();
	}
	
	@Test
	public void queryIgnoresEagerFetching() {
		
		Parent p = new Parent("parent");
		p.addChild("c1");
		p.addChild("c2");
		
		runInJPA(()->em.persist(p));
		
		runInJPA(() -> {
			em.find(Child.class, Long.valueOf(1));
			em.find(Child.class, Long.valueOf(2));
		});
		
		runInJPA(() -> {
			List<Child> children = em.createQuery("from Child c", Child.class).getResultList();
			assertThat(children).hasSize(2);
		});
	}
}


