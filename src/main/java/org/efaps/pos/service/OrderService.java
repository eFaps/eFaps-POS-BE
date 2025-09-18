package org.efaps.pos.service;

import java.util.Collection;
import java.util.Optional;

import org.efaps.pos.client.EFapsClient;
import org.efaps.pos.dto.DocStatus;
import org.efaps.pos.dto.OrderDto;
import org.efaps.pos.entity.Order;
import org.efaps.pos.entity.SyncInfo;
import org.efaps.pos.repository.OrderRepository;
import org.efaps.pos.util.Converter;
import org.efaps.pos.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OrderService extends PayablesService
{

    private static final Logger LOG = LoggerFactory.getLogger(OrderService.class);
    private final OrderRepository orderRepository;

    public OrderService(final EFapsClient eFapsClient,
                        final DocumentHelperService documentHelperService,
                          final ContactService contactService,
                          final BalanceService balanceService,
                          final OrderRepository orderRepository)
    {
        super(eFapsClient,documentHelperService, contactService, balanceService);
        this.orderRepository = orderRepository;
    }

    public boolean syncOrders(final SyncInfo syncInfo)
    {
        final boolean ret = false;
        LOG.info("Syncing Canceled Orders");
        final Collection<Order> tosync = orderRepository.findByOidIsNullAndStatus(DocStatus.CANCELED);
        for (final Order order : tosync) {
            if (order.getContactOid() == null || validateContact(order)) {
                LOG.debug("Syncing Order: {}", order);
                final OrderDto recDto = getEFapsClient().postOrder(Converter.toOrderDto(order));
                LOG.debug("received Order: {}", recDto);
                if (recDto.getOid() != null) {
                    final Optional<Order> orderOpt = orderRepository.findById(recDto.getId());
                    if (orderOpt.isPresent()) {
                        final Order retOrder = orderOpt.get();
                        retOrder.setOid(recDto.getOid());
                        orderRepository.save(retOrder);
                    }
                }
            }
        }
        LOG.info("Syncing Closed Orders");
        final Collection<Order> tosync2 = orderRepository.findByOidIsNullAndStatus(DocStatus.CLOSED);
        for (final Order order : tosync2) {
            if (order.getContactOid() == null || validateContact(order)) {
                boolean sync = true;
                if (order.getPayableOid() != null && !Utils.isOid(order.getPayableOid())) {
                    final var payableOpt = getDocumentHelperService().getPayableById(order.getPayableOid());
                    if (payableOpt.isPresent() && Utils.isOid(payableOpt.get().getOid())) {
                        order.setPayableOid(payableOpt.get().getOid());
                        orderRepository.save(order);
                    } else {
                        sync = false;
                    }
                }
                if (sync) {
                    LOG.debug("Syncing Order: {}", order);
                    final OrderDto recDto = getEFapsClient().postOrder(Converter.toOrderDto(order));
                    LOG.debug("received Order: {}", recDto);
                    if (recDto.getOid() != null) {
                        final Optional<Order> orderOpt = orderRepository.findById(recDto.getId());
                        if (orderOpt.isPresent()) {
                            final Order retOrder = orderOpt.get();
                            retOrder.setOid(recDto.getOid());
                            orderRepository.save(retOrder);
                        }
                    }
                } else {
                    LOG.info("skipped Order: {}", order);
                }
            }
        }
        return ret;
    }
}
