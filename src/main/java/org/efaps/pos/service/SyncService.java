/*
 * Copyright © 2003 - 2024 The eFaps Team (-)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.efaps.pos.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.efaps.pos.client.EFapsClient;
import org.efaps.pos.config.ConfigProperties;
import org.efaps.pos.config.ConfigProperties.Company;
import org.efaps.pos.context.Context;
import org.efaps.pos.entity.Config;
import org.efaps.pos.entity.Employee;
import org.efaps.pos.entity.Pos;
import org.efaps.pos.entity.Printer;
import org.efaps.pos.entity.Sequence;
import org.efaps.pos.entity.SyncInfo;
import org.efaps.pos.entity.User;
import org.efaps.pos.entity.Warehouse;
import org.efaps.pos.pojo.StashId;
import org.efaps.pos.repository.PrinterRepository;
import org.efaps.pos.repository.SequenceRepository;
import org.efaps.pos.repository.UserRepository;
import org.efaps.pos.repository.WarehouseRepository;
import org.efaps.pos.util.Converter;
import org.efaps.pos.util.SyncServiceDeactivatedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
public class SyncService
{

    /** The Constant LOG. */
    private static final Logger LOG = LoggerFactory.getLogger(SyncService.class);

    private final MongoTemplate mongoTemplate;
    private final EFapsClient eFapsClient;
    private final SequenceRepository sequenceRepository;
    private final UserRepository userRepository;
    private final WarehouseRepository warehouseRepository;
    private final PrinterRepository printerRepository;
    private final ConfigProperties configProperties;
    private final ContactService contactService;
    private final DocumentService documentService;
    private final ExchangeRateService exchangeRateService;
    private final LogService logService;
    private final PromotionService promotionService;
    private final ProductService productService;
    private final PosFileService posFileService;
    private final UpdateService updateService;
    private final CategoryService categoryService;
    private final ImageService imageService;
    private final ReportService reportService;
    private final ReceiptService receiptService;

    private boolean deactivated;

    private final BalanceService balanceService;

    private final InvoiceService invoiceService;

    private final TicketService ticketService;

    private final CreditNoteService creditNoteService;

    private final OrderService orderService;

    private final WorkspaceService workspaceService;

    private final InventoryService inventoryService;

    @Autowired
    public SyncService(final MongoTemplate _mongoTemplate,
                       final SequenceRepository _sequenceRepository,
                       final UserRepository _userRepository,
                       final WarehouseRepository _warehouseRepository,
                       final PrinterRepository _printerRepository,
                       final EFapsClient _eFapsClient,
                       final ConfigProperties _configProperties,
                       final WorkspaceService workspaceService,
                       final ContactService contactService,
                       final DocumentService documentService,
                       final ExchangeRateService _exchangeRateService,
                       final LogService logService,
                       final PromotionService promotionService,
                       final ProductService productService,
                       final InventoryService inventoryService,
                       final CategoryService categoryService,
                       final PosFileService posFileService,
                       final UpdateService updateService,
                       final ImageService imageService,
                       final ReportService reportService,
                       final OrderService orderService,
                       final InvoiceService invoiceService,
                       final ReceiptService receiptService,
                       final TicketService ticketService,
                       final CreditNoteService creditNoteService,
                       final BalanceService balanceService)
    {
        mongoTemplate = _mongoTemplate;
        sequenceRepository = _sequenceRepository;
        userRepository = _userRepository;
        warehouseRepository = _warehouseRepository;
        printerRepository = _printerRepository;
        eFapsClient = _eFapsClient;
        configProperties = _configProperties;
        this.workspaceService= workspaceService;
        this.contactService = contactService;
        this.documentService = documentService;
        exchangeRateService = _exchangeRateService;
        this.logService = logService;
        this.promotionService = promotionService;
        this.productService = productService;
        this.inventoryService= inventoryService;
        this.posFileService = posFileService;
        this.updateService = updateService;
        this.categoryService = categoryService;
        this.imageService = imageService;
        this.reportService = reportService;
        this.orderService = orderService;
        this.invoiceService = invoiceService;
        this.receiptService = receiptService;
        this.ticketService = ticketService;
        this.creditNoteService =creditNoteService;
        this.balanceService = balanceService;
    }

    public void runSyncJob(final String _methodName)
        throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
        InvocationTargetException
    {
        final Method method = getClass().getMethod(_methodName);
        if (configProperties.getCompanies().size() > 0) {
            for (final Company company : configProperties.getCompanies()) {
                Context.get().setCompany(company);
                MDC.put("company", company.getTenant());
                method.invoke(this);
            }
        } else {
            method.invoke(this);
        }
    }

    public void syncPromotionInfos()
    {
        if (promotionService.syncPromotionInfos()) {
            registerSync(StashId.PROMOTIONINFOSYNC);
        }
    }

    public void syncPromotions()
    {
        if (promotionService.syncPromotions()) {
            registerSync(StashId.PROMOTIONSYNC);
        }
    }

    public void syncLogs()
    {
        LOG.info("Syncing Logs");
        logService.syncLogs();
    }

    public void syncProperties()
        throws SyncServiceDeactivatedException
    {
        if (isDeactivated()) {
            throw new SyncServiceDeactivatedException();
        }
        LOG.info("Syncing Properties");
        final Map<String, String> properties = eFapsClient.getProperties();
        if (!properties.isEmpty()) {
            final Config config = new Config().setId(Config.KEY).setProperties(properties);
            mongoTemplate.save(config);
        }
    }

    public void syncAllProducts()
        throws SyncServiceDeactivatedException
    {
        if (isDeactivated()) {
            throw new SyncServiceDeactivatedException();
        }
        productService.syncAllProducts();
        registerSync(StashId.PRODUCTSYNC);
    }

    public void syncProducts()
        throws SyncServiceDeactivatedException
    {
        if (isDeactivated()) {
            throw new SyncServiceDeactivatedException();
        }
        if (productService.syncProducts(getSync(StashId.PRODUCTSYNC))) {
            registerSync(StashId.PRODUCTSYNC);
        }
    }

    public void syncCategories()
        throws SyncServiceDeactivatedException
    {
        if (isDeactivated()) {
            throw new SyncServiceDeactivatedException();
        }
        categoryService.sync();
        registerSync(StashId.CATEGORYSYNC);
    }

    public void syncWorkspaces()
        throws SyncServiceDeactivatedException
    {
        if (isDeactivated()) {
            throw new SyncServiceDeactivatedException();
        }
        workspaceService.syncWorkspaces(getSync(StashId.WORKSPACESYNC));
        registerSync(StashId.WORKSPACESYNC);
    }

    public void syncWarehouses()
        throws SyncServiceDeactivatedException
    {
        if (isDeactivated()) {
            throw new SyncServiceDeactivatedException();
        }
        LOG.info("Syncing Warehouses");
        final List<Warehouse> warehouses = eFapsClient.getWarehouses().stream()
                        .map(Converter::toEntity)
                        .collect(Collectors.toList());
        if (!warehouses.isEmpty()) {
            warehouseRepository.deleteAll();
            warehouses.forEach(workspace -> warehouseRepository.save(workspace));
        }
        registerSync(StashId.WAREHOUSESYNC);
    }

    public void syncExchangeRates()
        throws SyncServiceDeactivatedException
    {
        if (isDeactivated()) {
            throw new SyncServiceDeactivatedException();
        }
        LOG.info("Syncing ExchangeRates");
        exchangeRateService.loadExchangeRate();
        registerSync(StashId.EXCHANGERATESSYNC);
    }

    public void syncInventory()
        throws SyncServiceDeactivatedException
    {
        if (isDeactivated()) {
            throw new SyncServiceDeactivatedException();
        }
        if (inventoryService.syncInventory(getSync(StashId.INVENTORYSYNC))) {
            registerSync(StashId.INVENTORYSYNC);
        }
    }

    public void syncPrinters()
        throws SyncServiceDeactivatedException
    {
        if (isDeactivated()) {
            throw new SyncServiceDeactivatedException();
        }
        LOG.info("Syncing Printers");
        final List<Printer> printers = eFapsClient.getPrinters().stream()
                        .map(Converter::toEntity)
                        .collect(Collectors.toList());
        if (!printers.isEmpty()) {
            printerRepository.deleteAll();
            printers.forEach(printer -> printerRepository.save(printer));
        }
        registerSync(StashId.PRINTERSYNC);
    }

    public void syncPOSs()
        throws SyncServiceDeactivatedException
    {
        if (isDeactivated()) {
            throw new SyncServiceDeactivatedException();
        }
        LOG.info("Syncing POSs");
        final List<Pos> poss = eFapsClient.getPOSs().stream()
                        .map(Converter::toEntity)
                        .collect(Collectors.toList());
        if (!poss.isEmpty()) {
            final List<Pos> existingPoss = mongoTemplate.findAll(Pos.class);
            existingPoss.forEach(existing -> {
                if (!poss.stream()
                                .filter(pos -> pos.getOid().equals(existing.getOid())).findFirst()
                                .isPresent()) {
                    mongoTemplate.remove(existing);
                }
            });
            poss.forEach(pos -> mongoTemplate.save(pos));
        }
        registerSync(StashId.POSSYNC);
    }

    public void syncUsers()
        throws SyncServiceDeactivatedException
    {
        if (isDeactivated()) {
            throw new SyncServiceDeactivatedException();
        }

        LOG.info("Syncing Users");
        final List<User> users = eFapsClient.getUsers().stream()
                        .map(Converter::toEntity)
                        .collect(Collectors.toList());
        if (!users.isEmpty()) {
            userRepository.deleteAll();
            users.forEach(user -> userRepository.save(user));
        }
        registerSync(StashId.USERSYNC);
    }

    public void syncPayables()
        throws SyncServiceDeactivatedException
    {
        final var syncedContacts = contactService.syncContactsUp();
        documentService.updateContactOids(syncedContacts);
        syncBalances();
        syncReceipts();
        syncInvoices();
        syncTickets();
        syncOrders();
        syncCreditNotes();
    }

    public void syncBalances()
        throws SyncServiceDeactivatedException
    {
        if (isDeactivated()) {
            throw new SyncServiceDeactivatedException();
        }

        if (balanceService.syncBalances(getSync(StashId.BALANCESYNC))) {
            registerSync(StashId.BALANCESYNC);
        }

    }

    public void syncOrders()
        throws SyncServiceDeactivatedException
    {
        if (isDeactivated()) {
            throw new SyncServiceDeactivatedException();
        }
        if (orderService.syncOrders(getSync(StashId.ORDERSYNC))) {
            registerSync(StashId.ORDERSYNC);
        }
    }

    public void syncReceipts()
        throws SyncServiceDeactivatedException
    {
        if (isDeactivated()) {
            throw new SyncServiceDeactivatedException();
        }
        if (receiptService.syncReceipts(getSync(StashId.RECEIPTSYNC))) {
            registerSync(StashId.RECEIPTSYNC);
        }
    }

    public void syncInvoices()
        throws SyncServiceDeactivatedException
    {
        if (isDeactivated()) {
            throw new SyncServiceDeactivatedException();
        }
        if (invoiceService.syncInvoices(getSync(StashId.INVOICESYNC))) {
            registerSync(StashId.INVOICESYNC);
        }
    }

    public void syncTickets()
        throws SyncServiceDeactivatedException
    {
        if (isDeactivated()) {
            throw new SyncServiceDeactivatedException();
        }
        if (ticketService.syncTickets(getSync(StashId.TICKETSYNC))) {
            registerSync(StashId.TICKETSYNC);
        }
    }

    public void syncCreditNotes()
        throws SyncServiceDeactivatedException
    {
        if (isDeactivated()) {
            throw new SyncServiceDeactivatedException();
        }
        if (creditNoteService.syncCreditNotes(getSync(StashId.CREDITNOTESYNC))) {
            registerSync(StashId.CREDITNOTESYNC);
        }
    }

    public void syncImages()
        throws SyncServiceDeactivatedException
    {
        if (isDeactivated()) {
            throw new SyncServiceDeactivatedException();
        }
        imageService.sync();
        registerSync(StashId.IMAGESYNC);
    }

    public void syncReports()
        throws SyncServiceDeactivatedException
    {
        if (isDeactivated()) {
            throw new SyncServiceDeactivatedException();
        }
        reportService.sync();
        registerSync(StashId.REPORTSYNC);
    }

    public void syncSequences()
        throws SyncServiceDeactivatedException
    {
        if (isDeactivated()) {
            throw new SyncServiceDeactivatedException();
        }
        LOG.info("Syncing Sequences");
        final List<Sequence> sequences = eFapsClient.getSequences().stream()
                        .map(Converter::toEntity)
                        .collect(Collectors.toList());
        for (final Sequence sequence : sequences) {
            LOG.debug("Syncing Sequence: {}", sequence);
            final Optional<Sequence> seqOpt = sequenceRepository.findByOid(sequence.getOid());
            if (seqOpt.isPresent()) {
                final Sequence es = seqOpt.get();
                if (es.getSeq() < sequence.getSeq()) {
                    es.setSeq(sequence.getSeq());
                    sequenceRepository.save(es);
                }
                if (!es.getFormat().equals(sequence.getFormat())) {
                    es.setFormat(sequence.getFormat());
                    sequenceRepository.save(es);
                }
            } else {
                sequenceRepository.save(sequence);
            }
        }
        registerSync(StashId.SEQUENCESYNC);
    }

    public void syncAllContacts()
        throws SyncServiceDeactivatedException
    {
        if (isDeactivated()) {
            throw new SyncServiceDeactivatedException();
        }
        LOG.info("Syncing All Contacts");
        final var syncedContacts = contactService.syncContactsUp();
        documentService.updateContactOids(syncedContacts);
        contactService.syncContacts(null);
    }

    public void syncContacts()
        throws SyncServiceDeactivatedException
    {
        if (isDeactivated()) {
            throw new SyncServiceDeactivatedException();
        }
        LOG.info("Syncing Contacts");
        final var syncedContacts = contactService.syncContactsUp();
        documentService.updateContactOids(syncedContacts);
        contactService.syncContacts(getSync(StashId.CONTACTSYNC));
        registerSync(StashId.CONTACTSYNC);
    }

    public void syncEmployees()
        throws SyncServiceDeactivatedException
    {
        if (isDeactivated()) {
            throw new SyncServiceDeactivatedException();
        }
        LOG.info("Syncing Employees");
        final List<Employee> employees = eFapsClient.getEmployees().stream()
                        .map(Converter::toEntity)
                        .collect(Collectors.toList());
        if (!employees.isEmpty()) {
            final List<Employee> existingEmployees = mongoTemplate.findAll(Employee.class);
            existingEmployees.forEach(existing -> {
                if (!employees.stream().filter(employee -> employee.getOid().equals(existing.getOid())).findFirst()
                                .isPresent()) {
                    mongoTemplate.remove(existing);
                }
            });
            employees.forEach(product -> mongoTemplate.save(product));
        }
        registerSync(StashId.EMPLOYEESYNC);
    }

    public void syncPosFiles()
        throws SyncServiceDeactivatedException
    {
        if (isDeactivated()) {
            throw new SyncServiceDeactivatedException();
        }
        if (posFileService.syncFiles()) {
            registerSync(StashId.POSFILESYNC);
        }
    }

    public void check4Update()
    {
        updateService.check4Update();
    }

    public void registerSync(final StashId _stashId)
    {
        registerSync(_stashId.getKey());
    }

    private void registerSync(final String id)
    {
        SyncInfo syncInfo = mongoTemplate.findById(id, SyncInfo.class);
        if (syncInfo == null) {
            syncInfo = new SyncInfo();
            syncInfo.setId(id);
        }
        syncInfo.setLastSync(LocalDateTime.now());
        mongoTemplate.save(syncInfo);
    }

    private SyncInfo getSync(final StashId _stashId)
    {
        return mongoTemplate.findById(_stashId.getKey(), SyncInfo.class);
    }

    public boolean isDeactivated()
    {
        return deactivated;
    }

    public void setDeactivated(final boolean deactivated)
    {
        this.deactivated = deactivated;
    }

    public void syncManual(List<SyncDirective> syncDirectives)
        throws SyncServiceDeactivatedException
    {
        if (!isDeactivated()) {
            for (final var syncDirective : syncDirectives) {
                switch (syncDirective) {
                    case PRODUCTS:
                    case PROMOTIONS:
                    case WORKSPACES:
                    case EXCHANGERATES:
                    case USERS:
                    case CATEGORIES:
                    case INVENTORY:
                    case POSFILES:
                        invokeSync(syncDirective.method);
                        break;
                    case ALL:
                    default:
                        syncProperties();
                        syncUsers();
                        syncExchangeRates();
                        syncPOSs();
                        syncWorkspaces();
                        syncAllProducts();
                        syncCategories();
                        syncPromotions();
                        syncBalances();
                        syncReceipts();
                        syncInvoices();
                        syncTickets();
                        syncSequences();
                        syncWarehouses();
                        syncInventory();
                        syncPrinters();
                        syncImages();
                        syncReports();
                        syncOrders();
                        syncAllContacts();
                        syncPosFiles();
                        check4Update();
                        syncPromotionInfos();
                        syncLogs();
                        syncInventory();
                        syncEmployees();
                }
            }
        }
    }

    private void invokeSync(final String methodName)
    {
        LOG.info("Invoking {}", methodName);
        try {
            final Method method = getClass().getMethod(methodName);
            method.invoke(this);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                        | InvocationTargetException e) {
            LOG.error("well....", e);
        }
    }

    public enum SyncDirective
    {

        ALL(null),
        PROMOTIONS("syncPromotions"),
        PRODUCTS("syncAllProducts"),
        WORKSPACES("syncWorkspaces"),
        EXCHANGERATES("syncExchangeRates"),
        USERS("syncUsers"),
        CATEGORIES("syncCategories"),
        INVENTORY("syncInventory"),
        POSFILES("syncPosFiles");

        String method;

        SyncDirective(String method)
        {
            this.method = method;
        }
    }
}
