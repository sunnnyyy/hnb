package com.hnb.repsitories;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hnb.entities.Expense;
import com.hnb.enums.ExpenseType;
import com.hnb.forms.ExpenseSummaryDTO;
import com.hnb.forms.LastSixMonthSummaryDTO;
import com.hnb.forms.ProfitAndLossProjection;

@Repository
public interface ExpenseRepo extends JpaRepository<Expense, Integer> {
	// find the contact by user
	// custom finder method
//    Page<Customer> findByUser(User user, Pageable pageable);
	Page<Expense> findAll(Pageable pageable);

	@Query("SELECT e FROM Expense e WHERE " + "LOWER(e.expenseType) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
			+ "CAST(e.amount AS string) LIKE CONCAT('%', :keyword, '%') OR "
			+ "LOWER(e.notes) LIKE LOWER(CONCAT('%', :keyword, '%'))")
	Page<Expense> searchExpenses(@Param("keyword") String keyword, Pageable pageable);

	@Query("SELECT e FROM Expense e WHERE " + "(LOWER(e.expenseType) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
			+ "CAST(e.amount AS string) LIKE CONCAT('%', :keyword, '%') OR "
			+ "LOWER(e.notes) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND "
			+ "(:expenseType IS NULL OR e.expenseType = :expenseType)")
	Page<Expense> searchExpenses(@Param("keyword") String keyword, @Param("expenseType") ExpenseType expenseType,
			Pageable pageable);

// com.hnb.repository.ExpenseRepository.java

	@Query(value = """
			  SELECT
			      YEAR(e.created_at) AS year,
				  MONTHNAME(e.created_at) AS month,
			      SUM(e.amount) AS totalExpense,
			      SUM(CASE WHEN e.payment_mode = 'CASH' THEN e.amount ELSE 0 END) AS totalCashExpense,
			      SUM(CASE WHEN e.payment_mode = 'ONLINE' THEN e.amount ELSE 0 END) AS totalOnlineExpense,
			      COALESCE((
			          SELECT SUM(c.amount)
			          FROM customer c
			          WHERE YEAR(c.check_in) = YEAR(e.created_at)
			            AND MONTH(c.check_in) = MONTH(e.created_at)
			            AND c.payment_mode = 'CASH'
			      ), 0) AS totalCashRevenue,
			      COALESCE((
			          SELECT SUM(c.amount)
			          FROM customer c
			          WHERE YEAR(c.check_in) = YEAR(e.created_at)
			            AND MONTH(c.check_in) = MONTH(e.created_at)
			            AND c.payment_mode = 'ONLINE'
			      ), 0) AS totalOnlineRevenue,
			      COALESCE((
			          SELECT COUNT(*)
			          FROM customer c
			          WHERE YEAR(c.check_in) = YEAR(e.created_at)
			            AND MONTH(c.check_in) = MONTH(e.created_at)
			      ), 0) AS totalCustomers
			  FROM expense e
			  GROUP BY YEAR(e.created_at), MONTH(e.created_at)
			  ORDER BY YEAR(e.created_at) DESC, MONTH(e.created_at) DESC
			  """, countQuery = """
			SELECT COUNT(*) FROM (
			    SELECT 1
			    FROM expense e
			    GROUP BY YEAR(e.created_at), MONTH(e.created_at)
			) AS grouped
			""", nativeQuery = true)
	Page<ProfitAndLossProjection> getProfitAndLossReport(Pageable pageable);

	
	@Query(value = """
		    SELECT
		        -- Expenses
		        COALESCE((SELECT SUM(e.amount) FROM expense e), 0) AS totalExpense,
		        COALESCE((SELECT SUM(e.amount) FROM expense e WHERE e.payment_mode = 'CASH'), 0) AS totalCashExpense,
		        COALESCE((SELECT SUM(e.amount) FROM expense e WHERE e.payment_mode = 'ONLINE'), 0) AS totalOnlineExpense,

		        -- Withdrawals
			    COALESCE((
			        SELECT SUM(mw.cash) + SUM(mw.online)
			        FROM money_withdrawl mw
			        WHERE mw.cash IS NOT NULL OR mw.online IS NOT NULL
			    ), 0) AS totalWithdrawal,
		        COALESCE((SELECT SUM(mw.cash) FROM money_withdrawl mw WHERE mw.cash is NOT NULL), 0) AS totalCashWithdrawal,
		        COALESCE((SELECT SUM(mw.online) FROM money_withdrawl mw WHERE mw.online is NOT NULL), 0) AS totalOnlineWithdrawal,

		        -- Customer Revenue
		        COALESCE((SELECT SUM(c.amount) FROM customer c), 0) AS totalRevenue,
		        COALESCE((SELECT SUM(c.amount) FROM customer c WHERE c.payment_mode = 'CASH'), 0) AS totalCashRevenue,
		        COALESCE((SELECT SUM(c.amount) FROM customer c WHERE c.payment_mode = 'ONLINE'), 0) AS totalOnlineRevenue

		    """, nativeQuery = true)
		ExpenseSummaryDTO getFinancialSummary();

		@Query(value = """
				SELECT
				    YEAR(e.created_at) AS year,
					MONTHNAME(e.created_at) AS month,
				    SUM(e.amount) AS totalExpense,
				    COALESCE((
				        SELECT SUM(c.amount)
				        FROM customer c
				        WHERE YEAR(c.check_in) = YEAR(e.created_at)
				          AND MONTH(c.check_in) = MONTH(e.created_at)
				    ), 0) AS totalRevenue,
					(SELECT COALESCE(SUM(amount),0) FROM expense) AS totalExpenseAllMonths,
					(SELECT COALESCE(SUM(amount),0) FROM customer) AS totalRevenueAllMonths				FROM expense e
				WHERE e.created_at >= DATE_SUB(CURDATE(), INTERVAL 6 MONTH)
				GROUP BY YEAR(e.created_at), MONTH(e.created_at)
				ORDER BY YEAR(e.created_at) DESC, MONTH(e.created_at) DESC

						    """, nativeQuery = true)
		List<LastSixMonthSummaryDTO> getLastSixMonthSummary();
		
		@Query(value = """
				SELECT
			 		mw.owners AS owner, 
					SUM(COALESCE(mw.cash, 0)) + SUM(COALESCE(mw.online, 0)) AS total
				FROM money_withdrawl mw
				GROUP BY mw.owners
   				      """, nativeQuery = true)
		List<Map<String, Object>> getOwnerWithdrawMoney();

		
 
}
