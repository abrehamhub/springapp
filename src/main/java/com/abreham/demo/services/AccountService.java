package com.abreham.demo.services;

import static com.abreham.demo.exceptions.AccountException.ACCOUNT_NOT_FOUND_EXCEPTION;
import static com.abreham.demo.exceptions.AccountException.INVALID_ACCOUNT_EXCEPTION;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.abreham.demo.domains.Account;
import com.abreham.demo.exceptions.AccountException;
import com.abreham.demo.repositories.AccountRepository;

import java.sql.SQLException;

/**
 * Account Service accepts requests from the Controller and communicates with the repository to save and retrieve data.
 *
 * @author Biniam Asnake
 */
@Service
public class AccountService {

	private final AccountRepository accountRepository;

	@Autowired
	public AccountService(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	public Iterable<Account> getAllAccounts() {
		return accountRepository.findAll();
	}

	public Account createAccount(Account account) throws Exception {
		return accountRepository.save(account);
	}

	public Account getAccountById(Long id) {
		return accountRepository.findById(id)
				.orElseThrow(() -> new AccountException(ACCOUNT_NOT_FOUND_EXCEPTION));
	}

	/**
	 * Updates Account if it is available in the database
	 *
	 * @param accountId: ID of the account to be updated
	 * @param account: Account to be updated
	 * @return Nothing
	 */
	public void updateAccount(Long accountId, Account account) throws SQLException {

		accountRepository.updateAccount(accountId, account.getFirstName(), account.getLastName(), account.getEmail(), account.getPhoneNumber(), account.getPin());
	}

	/**
	 * Updates the balance of an Account if it is available in the {code}accounts{code} map
	 *
	 * @param account: Account to be updated
	 * @param newBalance: the new balance that should be saved
	 * @return The updated Account object
	 */
	public Account updateAccountBalance(Account account, Double newBalance) {

		if (account == null) {
			throw new AccountException(INVALID_ACCOUNT_EXCEPTION);
		}

		account.setBalance(newBalance);

		return accountRepository.save(account);
	}

	public Boolean deleteById(Long accountId) {
		try {
			accountRepository.deleteById(accountId);
			return Boolean.TRUE;
		} catch(Exception e) {
			e.printStackTrace();
			return Boolean.FALSE;
		}
	}
}
