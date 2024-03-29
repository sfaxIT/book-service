package de.sfaxit.util;

import de.sfaxit.model.dto.SubscriberDTO;
import de.sfaxit.model.dto.enums.SubscriberRole;
import de.sfaxit.model.entity.Subscriber;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class SubscriberPersister {
	private static final Logger LOG = LoggerFactory.getLogger(SubscriberPersister.class);
	
	@Transactional
	public Subscriber addSubscriber(final String username, final String password, final SubscriberRole role) {
		try {
			final Subscriber subscriber = new Subscriber(username, password);
			subscriber.subscriberRole = role.name();
			subscriber.setSubscriberBooks(new HashSet<>());
		
			subscriber.persist();
			LOG.debug("Persisted subscriber {}", subscriber);
		
			return subscriber;
		} catch (final Exception e) {
			LOG.error("registerSubscriber Error {}", e.getMessage());
		}
		return null;
	}
	
	@Transactional
	public boolean update(final SubscriberDTO dto) {
		try {
			final Long id = dto.getUserId();
			final Subscriber entityToUpdate = Subscriber.findById(id);
			
			if (entityToUpdate != null) {
				entityToUpdate.subscriberName = dto.getUsername();
				entityToUpdate.subscriberPassword = dto.getPassword();
				
				entityToUpdate.persist();
			}
		} catch (final Exception e) {
			LOG.error("Error update subscriber with id " + dto.getUserId() + " {}", e.getMessage());
		}
		return false;
	}
	
	@Transactional
	public boolean delete(final Long subscriberId) {
		try {
			final Subscriber subscriberToDelete = Subscriber.findById(subscriberId);
			if (subscriberToDelete.isPersistent()) {
				subscriberToDelete.delete();
				return true;
			}
		} catch (final Exception e) {
			LOG.error("Error delete subscriber with id " + subscriberId + " {}", e.getMessage());
		}
		return false;
	}
	
	@Transactional
	public Subscriber banSubscriber(final String subscriberId) {
		try {
			final Subscriber subscriberToBan = Subscriber.findById(subscriberId);
			if (subscriberToBan.isPersistent()) {
				subscriberToBan.subscriberRole = SubscriberRole.BANNED.name();
				
				subscriberToBan.persist();
				
				return Subscriber.findById(subscriberToBan.subscriberId);
			}
		} catch (final Exception e) {
			LOG.error("Error ban subscriber with ID " + subscriberId + " {}", e.getMessage());
		}
		return null;
	}
	
}
