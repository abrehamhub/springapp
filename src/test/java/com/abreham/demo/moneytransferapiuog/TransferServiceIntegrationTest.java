package com.abreham.demo.moneytransferapiuog;

import com.abreham.demo.domains.Transfer;
import com.abreham.demo.exceptions.TransferException;
import com.abreham.demo.services.TransferService;
import com.abreham.demo.wrappers.TransferRequest;
import lombok.extern.java.Log;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
@Log
@AutoConfigureTestDatabase
public class TransferServiceIntegrationTest {

    @Autowired
    private TransferService transferService;

    private TransferRequest transferRequest;

    @Before
    public void setUp() {

        //given
        transferRequest = TransferRequest.builder()
                .senderId(1L)
                .receiverId(2L)
                .amount(300.00)
                .reason("test reason")
                .build();
    }

    @Test
    public void shouldTransferMoneyFromOneValidAccountToAnotherOne() {

        //when
        Transfer createdTransfer = transferService.transferMoney(transferRequest);

        //then
        assertEquals(transferRequest.getSenderId(), createdTransfer.getSender().getId());
        assertEquals(transferRequest.getReceiverId(), createdTransfer.getReceiver().getId());
        assertEquals(transferRequest.getAmount(), createdTransfer.getAmount());
        assertEquals(transferRequest.getReason(), createdTransfer.getReason());
    }

    @Test
    public void shouldFailToTransferMoneyIfBalanceIsInsufficient() {
        Exception exception = assertThrows(TransferException.class, () -> {
            //given
            transferRequest.setAmount(1000000.00);

            //when
            transferService.transferMoney(transferRequest);
        });

        String expectedMessage = "Insufficient balance";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test(expected = TransferException.class)
    public void shouldFailToTransferMoneyToTheSameAccount() {

        //given
        transferRequest.setReceiverId(1L);

        //when
        transferService.transferMoney(transferRequest);
    }

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();
    @Test
    public void shouldFailToTransferMoneyToTheSameAccount_anotherImplementation() {
        exceptionRule.expect(TransferException.class);
        exceptionRule.expectMessage("Both sender and receiver cannot be the same account");

        //given
        transferRequest.setReceiverId(1L);

        //when
        transferService.transferMoney(transferRequest);
    }
}