package effective.interview.revolut

import kotlin.test.Test

class BalanceTransferServiceTest {

    val service =  BalanceTransferService();


    @Test
    fun `should_transfer_money_between_accounts`() {
        // given
        Account from;
        Account to;
        ?? amount; // change this!
        // when
        service.transfer(from, to, amount);
        // then
        // …
    }
}