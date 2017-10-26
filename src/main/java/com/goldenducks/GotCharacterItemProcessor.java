package com.goldenducks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class GotCharacterItemProcessor implements ItemProcessor<GotCharacter, GotCharacter> {

	private static final Logger log = LoggerFactory.getLogger(GotCharacterItemProcessor.class);

	public GotCharacter process(GotCharacter gotCharacter) throws Exception {
		
		final String firstName = gotCharacter.getFirstName().toUpperCase();
        final String lastName = gotCharacter.getLastName().toUpperCase();

        final GotCharacter transformedPerson = new GotCharacter(firstName, lastName);

        log.info("Converting (" + gotCharacter + ") into (" + transformedPerson + ")");

        return transformedPerson;
	}

}
