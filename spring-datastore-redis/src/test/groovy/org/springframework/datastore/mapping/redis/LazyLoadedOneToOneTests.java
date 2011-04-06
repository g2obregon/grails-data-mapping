package org.springframework.datastore.mapping.redis;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import javassist.util.proxy.ProxyObject;

import org.junit.Test;
import org.springframework.datastore.mapping.core.Session;
import org.springframework.datastore.mapping.proxy.EntityProxy;

public class LazyLoadedOneToOneTests extends AbstractRedisTest {

    @Test
    public void testLazyLoadedOneToOne() {
        ds.getMappingContext().addPersistentEntity(Person.class);
        Session conn = ds.connect();

        Person p = new Person();
        p.setName("Bob");
        Address a = new Address();
        a.setNumber("22");
        a.setPostCode("308420");
        p.setAddress(a);
        conn.persist(p);
        conn.flush();

        conn.clear();

        p = conn.retrieve(Person.class, p.getId());

        Address proxy = p.getAddress();

        assertTrue(proxy instanceof ProxyObject);
        assertTrue(proxy instanceof EntityProxy);

        EntityProxy ep = (EntityProxy) proxy;
        assertFalse(ep.isInitialized());
        assertEquals(a.getId(), proxy.getId());

        assertFalse(ep.isInitialized());
        assertEquals("22", a.getNumber());
    }

    @Test
    public void testProxyMethod() {
        ds.getMappingContext().addPersistentEntity(Person.class);
        Session conn = ds.connect();

        Person p = new Person();
        p.setName("Bob");
        Address a = new Address();
        a.setNumber("22");
        a.setPostCode("308420");
        p.setAddress(a);
        conn.persist(p);

        Person personProxy = conn.proxy(Person.class, p.getId());

        EntityProxy proxy = (EntityProxy) personProxy;

        assertFalse(proxy.isInitialized());
        assertEquals(p.getId(), personProxy.getId());

        assertFalse(proxy.isInitialized());

        assertEquals("Bob", personProxy.getName());

        assertTrue(proxy.isInitialized());
    }
}
