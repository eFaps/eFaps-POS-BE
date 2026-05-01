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

import org.efaps.pos.config.ConfigProperties;
import org.efaps.pos.config.ConfigProperties.Company;
import org.efaps.pos.context.Context;
import org.efaps.pos.entity.SyncInfo;
import org.efaps.pos.pojo.StashId;
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

    private final UserService userService;

    private final PrintService printService;

    private final SequenceService sequenceService;

    private final PosService posService;

    private final EmployeeService employeeService;

    private final ConfigService configService;

    @Autowired
    public SyncService(final MongoTemplate mongoTemplate,
                       final ConfigProperties configProperties,
                       final WorkspaceService workspaceService,
                       final ContactService contactService,
                       final DocumentService documentService,
                       final ExchangeRateService exchangeRateService,
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
                       final BalanceService balanceService,
                       final UserService userService,
                       final PrintService printService,
                       final SequenceService sequenceService,
                       final PosService posService,
                       final EmployeeService employeeService,
                       final ConfigService configService)
    {
        this.mongoTemplate = mongoTemplate;
        this.configProperties = configProperties;
        this.workspaceService = workspaceService;
        this.contactService = contactService;
        this.documentService = documentService;
        this.exchangeRateService = exchangeRateService;
        this.logService = logService;
        this.promotionService = promotionService;
        this.productService = productService;
        this.inventoryService = inventoryService;
        this.posFileService = posFileService;
        this.updateService = updateService;
        this.categoryService = categoryService;
        this.imageService = imageService;
        this.reportService = reportService;
        this.orderService = orderService;
        this.invoiceService = invoiceService;
        this.receiptService = receiptService;
        this.ticketService = ticketService;
        this.creditNoteService = creditNoteService;
        this.balanceService = balanceService;
        this.userService = userService;
        this.printService = printService;
        this.sequenceService= sequenceService;
        this.posService = posService;
        this.employeeService = employeeService;
        this.configService = configService;
    }

    public void runSyncJob(final String methodName)
        throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
        InvocationTargetException
    {
        final Method method = getClass().getMethod(methodName);
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
        if (configService.syncProperties(getSync(StashId.PROPERTIESSYNC))) {
            registerSync(StashId.PROPERTIESSYNC);
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
        if (inventoryService.syncWarehouses(getSync(StashId.WAREHOUSESYNC))) {
            registerSync(StashId.WAREHOUSESYNC);
        }
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
        if (printService.syncPrinters(getSync(StashId.PRINTERSYNC))) {
            registerSync(StashId.PRINTERSYNC);
        }
    }

    public void syncPOSs()
        throws SyncServiceDeactivatedException
    {
        if (isDeactivated()) {
            throw new SyncServiceDeactivatedException();
        }
        if (posService.syncPOSs(getSync(StashId.POSSYNC))) {
            registerSync(StashId.POSSYNC);
        }
    }

    public void syncUsers()
        throws SyncServiceDeactivatedException
    {
        if (isDeactivated()) {
            throw new SyncServiceDeactivatedException();
        }
        if (userService.syncUsers(getSync(StashId.USERSYNC))) {
            registerSync(StashId.USERSYNC);
        }
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

        if (sequenceService.syncSequences(getSync(StashId.SEQUENCESYNC))) {
            registerSync(StashId.SEQUENCESYNC);
        }
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
        contactService.syncAllContacts();
        registerSync(StashId.PRODUCTSYNC);
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
        if (employeeService.syncEmployees(getSync(StashId.EMPLOYEESYNC))) {
            registerSync(StashId.EMPLOYEESYNC);
        }
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

    public void check4Updates()
    {
        updateService.check4Updates();
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
                    case PRODUCTS, PROMOTIONS, WORKSPACES, EXCHANGERATES, USERS, CATEGORIES, INVENTORY, POSFILES ->
                        invokeSync(syncDirective.method);
                    case ALL -> {
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
                        check4Updates();
                        syncPromotionInfos();
                        syncLogs();
                        syncInventory();
                        syncEmployees();
                    }
                    default -> {
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
                        check4Updates();
                        syncPromotionInfos();
                        syncLogs();
                        syncInventory();
                        syncEmployees();
                    }
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
