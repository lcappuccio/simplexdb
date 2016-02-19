package org.systemexception.simplexdb.database;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author cappuccio
 * @date 19/02/16 15:51
 */
@Aspect
@Component
public class DatabaseCommitMonitor {

	@Autowired
	private DatabaseApi databaseService;

	@AfterReturning("execution(* org.systemexception.simplexdb.database.DatabaseService.save(..)) || " +
			"execution(* org.systemexception.simplexdb.database.DatabaseService.delete(..))")
	public void logCommit(JoinPoint joinPoint) {
		databaseService.commit();
	}
}
