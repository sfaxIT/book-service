package de.sfaxit.service;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

import de.sfaxit.model.dto.LoginDTO;
import de.sfaxit.model.dto.SearchResultHolderDTO;
import de.sfaxit.model.dto.SubscriberDTO;
import de.sfaxit.model.dto.enums.SubscriberRole;
import de.sfaxit.model.entity.Subscriber;
import de.sfaxit.model.entity.Book;
import de.sfaxit.util.SubscriberPersister;
import de.sfaxit.util.TokenGenerator;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class SubscriberService {
	private static final Logger LOG = LoggerFactory.getLogger(SubscriberService.class);
	
	@Inject
	TokenGenerator tokenGenerator;
	
	@Inject
	SubscriberPersister persister;
	
	public boolean subscriberExists(@NotNull final String subscriberName) {
		return Subscriber.findByName(subscriberName) != null;
	}
	
	public Subscriber findBySubscriberName(@NotNull final String subscriberName) {
		return Subscriber.findByName(subscriberName);
	}
	
	public boolean isValidPassword(@NotNull final Subscriber subscriber, @NotNull final String pwd) {
		return StringUtils.equals(subscriber.subscriberPassword, pwd);
	}
	
	public Subscriber registerSubscriber(final String subscriberName, final String subscriberPassword, final SubscriberRole subscriberRole) {
		return this.persister.addSubscriber(subscriberName, subscriberPassword, subscriberRole);
	}
	
	public LoginDTO getSubscriberToken(@NotNull final Subscriber subscriber) {
		return this.tokenGenerator.generateSubscriberJWT(subscriber);
	}
	
	public Long subscribersCount() {
		return Subscriber.count();
	}
	
	public boolean isBookSubscriberValid(@NotNull final Book bookEntity, @NotNull final Long subscriberId) {
		if (bookEntity.subscriber != null) {
			final Long entityId = bookEntity.subscriber.subscriberId;
			return subscriberId.equals(entityId);
		}
		return false;
	}
	
	public SearchResultHolderDTO findAllSubscribersByPage(final Integer pageSize) {
		PanacheQuery<Subscriber> subscribersPage = Subscriber.findAll(Sort.by("subscriberId", Sort.Direction.Descending))
		                                                     .page(Page.ofSize(pageSize));
		
		final List<Subscriber> result = new ArrayList<>(subscribersPage.list());
		
		while (subscribersPage.hasNextPage()) {
			subscribersPage = subscribersPage.nextPage();
			
			result.addAll(subscribersPage.list());
		}
		LOG.info("Subscriber result count {}", result.size());
		
		final List<SubscriberDTO> subscribers = new ArrayList<>();
		emptyIfNull(result).forEach(entity -> {
			final SubscriberDTO dto = this.mapSubscriberEntityToDto(entity);
			subscribers.add(dto);
		});
		LOG.info("SubscriberDTO count {}", subscribers.size());
		
		return SearchResultHolderDTO.builder()
		                            .currentPage(subscribersPage.page().index)
		                            .pageCount(subscribersPage.pageCount())
		                            .pageSize(subscribersPage.page().size)
		                            .totalCount(subscribersPage.count())
		                            .users(subscribers)
		                            .build();
	}
	
	public SubscriberDTO banSubscriber(final String id) {
		final Subscriber bannedEntity = this.persister.banSubscriber(id);
		
		if (bannedEntity != null) {
			return this.mapSubscriberEntityToDto(bannedEntity);
		}
		return null;
	}
	
	private SubscriberDTO mapSubscriberEntityToDto(final Subscriber entity) {
		if (entity != null) {
			return SubscriberDTO.builder()
			                    .userId(entity.subscriberId)
			                    .username(entity.subscriberName)
			                    .password(entity.subscriberPassword)
			                    .subscriberRole(SubscriberRole.of(entity.subscriberRole))
			                    .build();
		}
		LOG.info("mapSubscriberEntityToDto: Couldn't map Subscriber entity to dto");
		return null;
	}
	
}
