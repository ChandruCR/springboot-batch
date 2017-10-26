package com.goldenducks;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

// Job execution listener
@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

	private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	// executed after job is completed
	@Override
	public void afterJob(JobExecution jobExecution) {
		if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
			log.info("!!! Job Finished. Checking database !!!");

			List<GotCharacter> results = jdbcTemplate.query("SELECT first_name, last_name FROM gotcharacters", new RowMapper<GotCharacter>() {
				@Override
				public GotCharacter mapRow(ResultSet rs, int row) throws SQLException {
					return new GotCharacter(rs.getString(1), rs.getString(2));
				}
			});

			for (GotCharacter gotCharacter : results) {
				log.info("Found <" + gotCharacter + "> in the database.");
			}

		}
	}
}