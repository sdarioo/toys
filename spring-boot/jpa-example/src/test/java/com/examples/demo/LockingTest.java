package com.examples.demo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.persistence.OptimisticLockException;

import org.junit.Assert;
import org.junit.Test;

import com.examples.demo.model.Customer;

public class LockingTest extends AbstractTest {

	
	@Test
	public void testOptimisticLocking() throws InterruptedException {
		
		Customer c = new Customer("Jan Kowalski");
		runInJPA(() -> {
			em.persist(c);
		});
		
		final CyclicBarrier barrier = new CyclicBarrier(2);
		final AtomicBoolean lockException = new AtomicBoolean(false);
		
		Runnable modifyCustomer = () -> {
			try {
				Customer c1 = em.find(Customer.class, c.getId());
				barrier.await();
				
				c1.setName("Bruce Lee");
				em.flush();

			} catch (OptimisticLockException e) {
				lockException.set(true); // Expected
			} catch (Throwable t) {
				Assert.fail(t.toString());
			}
		};
		
		ExecutorService executor = Executors.newFixedThreadPool(2);
		runInJPA(executor, modifyCustomer);
		runInJPA(executor, modifyCustomer);
		
		executor.shutdown();
		executor.awaitTermination(2, TimeUnit.SECONDS);
		
		assertThat(lockException.get()).isTrue();
	}
	

}
