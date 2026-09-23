package com.tekram.service;

import com.tekram.mapper.ItemMapper;
import com.tekram.mapper.OrderMapper;
import com.tekram.pojo.Item;
import com.tekram.service.impl.OrderTimeoutServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.SimpleTransactionStatus;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderTimeoutServiceTest {
    private final OrderMapper orders = mock(OrderMapper.class);
    private final ItemMapper items = mock(ItemMapper.class);
    private final PlatformTransactionManager transactions = mock(PlatformTransactionManager.class);
    private AnnotationConfigApplicationContext context;
    private OrderTimeoutService service;

    @Configuration
    @EnableTransactionManagement
    static class TransactionConfig { }

    @BeforeEach
    void setUp() {
        when(transactions.getTransaction(any())).thenAnswer(call -> new SimpleTransactionStatus());
        context = new AnnotationConfigApplicationContext();
        context.register(TransactionConfig.class, OrderTimeoutServiceImpl.class);
        context.registerBean(OrderMapper.class, () -> orders);
        context.registerBean(ItemMapper.class, () -> items);
        context.registerBean(PlatformTransactionManager.class, () -> transactions);
        context.refresh();
        service = context.getBean(OrderTimeoutService.class);
        Item item = new Item();
        item.setId(10L);
        item.setItemStatus((byte) 3);
        when(items.getItemByIdForUpdate(10L)).thenReturn(item);
        when(orders.cancelExpiredOrder(1L, 10L)).thenReturn(1);
        when(items.releaseOrderItem(10L)).thenReturn(1);
        when(orders.insertTimeoutStatusLog(1L)).thenReturn(1);
    }

    @AfterEach
    void tearDown() {
        context.close();
    }

    @Test
    void cancellationLocksItemFirstAndCommitsReleaseAndHistoryTogether() {
        assertTrue(service.cancelExpiredOrder(1L, 10L));
        var calls = inOrder(items, orders, transactions);
        calls.verify(items).getItemByIdForUpdate(10L);
        calls.verify(orders).cancelExpiredOrder(1L, 10L);
        calls.verify(items).releaseOrderItem(10L);
        calls.verify(orders).insertTimeoutStatusLog(1L);
        calls.verify(transactions).commit(any());
        verify(transactions).getTransaction(argThat(definition ->
                definition.getPropagationBehavior() == TransactionDefinition.PROPAGATION_REQUIRES_NEW));
    }

    @Test
    void repeatedOrAlreadyPaidOrderDoesNotReleaseItemOrWriteAnotherLog() {
        when(orders.cancelExpiredOrder(1L, 10L)).thenReturn(1, 0);
        assertTrue(service.cancelExpiredOrder(1L, 10L));
        assertFalse(service.cancelExpiredOrder(1L, 10L));
        verify(items, times(1)).releaseOrderItem(10L);
        verify(orders, times(1)).insertTimeoutStatusLog(1L);
        verify(transactions, times(2)).commit(any());
    }

    @Test
    void itemReleaseFailureRollsBackCancellation() {
        when(items.releaseOrderItem(10L)).thenReturn(0);
        assertThrows(IllegalStateException.class, () -> service.cancelExpiredOrder(1L, 10L));
        verify(transactions).rollback(any());
        verify(transactions, never()).commit(any());
        verify(orders, never()).insertTimeoutStatusLog(any());
    }

    @Test
    void historyFailureRollsBackBothOrderAndItemChanges() {
        when(orders.insertTimeoutStatusLog(1L)).thenThrow(new IllegalStateException("日志写入失败"));
        assertThrows(IllegalStateException.class, () -> service.cancelExpiredOrder(1L, 10L));
        verify(items).releaseOrderItem(10L);
        verify(transactions).rollback(any());
        verify(transactions, never()).commit(any());
    }

    @Test
    void legacyAvailableItemIsNotUpdatedAgain() {
        Item item = new Item();
        item.setItemStatus((byte) 1);
        when(items.getItemByIdForUpdate(10L)).thenReturn(item);
        assertTrue(service.cancelExpiredOrder(1L, 10L));
        verify(items, never()).releaseOrderItem(any());
        verify(orders).insertTimeoutStatusLog(1L);
    }

    @Test
    void soldItemIsNotResetToAvailable() {
        Item item = new Item();
        item.setItemStatus((byte) 4);
        when(items.getItemByIdForUpdate(10L)).thenReturn(item);
        assertThrows(IllegalStateException.class, () -> service.cancelExpiredOrder(1L, 10L));
        verify(items, never()).releaseOrderItem(any());
        verify(transactions).rollback(any());
    }
}
