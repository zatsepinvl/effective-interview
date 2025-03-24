BEGIN TRANSACTION;

UPDATE balances
SET operation_version = operation_version + 1
WHERE account_id='account_id1' or account_id='account_id2'

SELECT amount as amount1 FROM balances WHERE account_id='account_id1';

SELECT amount as amount2 FROM balances WHERE account_id='account_id2';

UPDATE balances
SET amount = amount1 - amount
WHERE account_id='account_id1'

UPDATE balances
SET amount = amount2 + amount
WHERE account_id='account_id2'

SELECT operation_version BALANCES WHERE account_id='account_id1' or account_id='account_id2'

    // operation version

COMMIT;